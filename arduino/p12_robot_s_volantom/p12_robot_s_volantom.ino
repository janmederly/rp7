const int TRIG_PIN = 2;
const int ECHO_PIN = 3;
const int SMER_A1 = 8;
const int SMER_A2 = 11;
const int RYCHLOST_A = 9;
const int SMER_B1 = 12;
const int SMER_B2 = 13;
const int RYCHLOST_B = 10;
const int ZL_SMEROVKA = 27;
const int ZP_SMEROVKA = 32;
const int P_ZADNE = 23;
const int L_ZADNE = 50;
const int P_BRZDNE = 22;
const int L_BRZDNE = 51;
const int SPIATOCKA = 49;

void zapniAkcelerometer();
void merajZrychlenie();

void setup() {
  Serial.begin(115200);
  Serial.println("Seriovy port nastartovany.");

  pinMode(TRIG_PIN, OUTPUT);
  pinMode(ECHO_PIN, INPUT);
  pinMode(SMER_A1, OUTPUT);
  pinMode(SMER_A2, OUTPUT);
  pinMode(RYCHLOST_A, OUTPUT);
  pinMode(SMER_B1, OUTPUT);
  pinMode(SMER_B2, OUTPUT);
  pinMode(RYCHLOST_B, OUTPUT);
  pinMode(ZP_SMEROVKA, OUTPUT);
  pinMode(ZL_SMEROVKA, OUTPUT);
  pinMode(P_ZADNE, OUTPUT);
  pinMode(L_ZADNE, OUTPUT);
  pinMode(P_BRZDNE, OUTPUT);
  pinMode(L_BRZDNE, OUTPUT);
  pinMode(SPIATOCKA, OUTPUT);
  Serial.println("Piny nastavene.");

  digitalWrite(P_ZADNE, HIGH);
  digitalWrite(L_ZADNE, HIGH);
  digitalWrite(ZP_SMEROVKA, LOW);
  digitalWrite(ZL_SMEROVKA, LOW);
  digitalWrite(P_BRZDNE, LOW);
  digitalWrite(L_BRZDNE, LOW);
  digitalWrite(SPIATOCKA, LOW);
  Serial.println("Svetla zapnute.");
  
  zapniAkcelerometer();  
}

boolean zapnutyAsistentNarazu = 1;

float distance;                         // vzdialenost od prekazky - z predného senzora (ak sa nejde dozadu)

float volant = 0;
float pedale = 0;
float akcelerometerX = 0;               // zrýchlenie v osiach X, Y, Z
float akcelerometerY = 0;
float akcelerometerZ = 0;

boolean zastaveny = false;

int spiatocka = 0;
int rychlostMerania = 102;

void loop() {

  unsigned long casStart = millis();

  merajZrychlenie();
  
  distance = 0;
  spracujPrikazZBluetooth();
  if (zapnutyAsistentNarazu) {
    zistiVzdialenost();
    if (distance <= 30 && distance != 0) {
      zastav();
      zastaveny = true;
    }
  } else {
    zastaveny = false;
  }
  if (!zastaveny) {
    nastavMotoryASvetla();
  } else {
    digitalWrite(P_BRZDNE, HIGH);
    digitalWrite(L_BRZDNE, HIGH);
  }
  digitalWrite(SPIATOCKA, spiatocka);
  
  unsigned long presielCas = millis() - casStart;
  long esteCakat = rychlostMerania - presielCas;
  if (esteCakat < 0) {
    Serial.print("Zaporna doba cakania: ");
    Serial.println(esteCakat);
    esteCakat = 10;
  }
  Serial.print("V:");
  Serial.print(volant);
  Serial.print(" P:");
  Serial.print(pedale);
  Serial.print(" AN:");
  Serial.print(zapnutyAsistentNarazu);
  Serial.print(" D:");
  Serial.print(distance);
  Serial.print(" Z:");
  Serial.print(zastaveny);
  Serial.print(" AX:");
  Serial.print(akcelerometerX);
  Serial.print(" AY:");
  Serial.print(akcelerometerY);
  Serial.print(" AZ:");
  Serial.print(akcelerometerZ);
  Serial.println();
}

void spracujPrikazZBluetooth() {
  Serial.print(".");
  while (Serial.available() > 0) {
    int znak = Serial.read();
    switch (znak) {
      case 'F': zapnutyAsistentNarazu = true;
        Serial.println("Asistent narazu zapnuty");
        break;
      case 'G': zapnutyAsistentNarazu = false;
        Serial.println("Asistent narazu vypnuty");
        break;
      case 'R':
       spiatocka = 0;
       Serial.println("Spiatocka vypnuta");
       break;
      case 'S':
       spiatocka = 1;
       Serial.println("Spiatocka zapnuta");
       break;
      case 'X':  {
          char b[5];
          long c = millis();
          while (Serial.available() < 5 && millis() - c < 1000) {
          }
          if (Serial.available() >= 5) {
            b[0] = Serial.read();
            b[1] = Serial.read();
            b[2] = Serial.read();
            b[3] = Serial.read();
            b[4] = Serial.read();
            volant = retazecNaCislo(b);
            volant = volant / 4;
            Serial.print("V = ");
            Serial.println(volant);
          }
        }
        break;
      case 'Y':
        {
          long c = millis();
          char a[5];
          while (Serial.available() < 5 && millis() - c < 1000) {
          }
          if (Serial.available() >= 5) {
            a[0] = Serial.read();
            a[1] = Serial.read();
            a[2] = Serial.read();
            a[3] = Serial.read();
            a[4] = Serial.read();
            pedale = retazecNaCislo(a);
            pedale = pedale / 4;
            Serial.print("P = ");
            Serial.println(pedale);
          }
        }
        break;
    }
  }
}

void nastavMotoryASvetla() {
  if (pedale >= 20) {
    if (volant == 250 || volant == -250) {
      if (volant == 250) {
        doprava();
        digitalWrite(ZP_SMEROVKA, HIGH);
        digitalWrite(ZL_SMEROVKA, LOW);
        digitalWrite(P_BRZDNE, LOW);
        digitalWrite(L_BRZDNE, LOW);
        nastavRychlostMotorov(pedale);
      } else {
        dolava();
        digitalWrite(ZL_SMEROVKA, HIGH);
        digitalWrite(ZP_SMEROVKA, LOW);
        digitalWrite(P_BRZDNE, LOW);
        digitalWrite(L_BRZDNE, LOW);
        nastavRychlostMotorov(pedale);
      }
    } else if (volant != 250 && volant >= 10 && pedale >= 20) {
      rychlostA(pedale);
      rychlostB(pedale - volant); 
      digitalWrite(ZP_SMEROVKA, HIGH);
      digitalWrite(ZL_SMEROVKA, LOW);
      digitalWrite(P_BRZDNE, LOW);
      digitalWrite(L_BRZDNE, LOW);
    } else if (volant <= -20 && volant != -250) {
      rychlostA(pedale + volant);
      rychlostB(pedale);
      digitalWrite(ZL_SMEROVKA, HIGH);
      digitalWrite(ZP_SMEROVKA, LOW);
      digitalWrite(P_BRZDNE, LOW);
      digitalWrite(L_BRZDNE, LOW);
    } else if (spiatocka == 0) {
      dopredu();
      rychlostA(pedale);
      rychlostB(pedale);
      digitalWrite(ZP_SMEROVKA, LOW);
      digitalWrite(ZL_SMEROVKA, LOW);
      digitalWrite(P_BRZDNE, LOW);
      digitalWrite(L_BRZDNE, LOW);
    } else if (spiatocka == 1) {
      dozadu();
      rychlostA(pedale);
      rychlostB(pedale);
      digitalWrite(ZP_SMEROVKA, LOW);
      digitalWrite(ZL_SMEROVKA, LOW);
      digitalWrite(P_BRZDNE, LOW);
      digitalWrite(L_BRZDNE, LOW);
    }
  } else {
    zastav();
  }
}

int retazecNaCislo(char* a) {
  int b = a[4] - 48; 
  int c = (a[3] - 48) * 10; 
  int d = (a[2] - 48) * 100;
  int e = (a[1] - 48) * 1000;
  int vratCislo = b + c + d + e;
  if (a[0] == '-') {
    vratCislo = -vratCislo;
  } 
  return vratCislo;
}

void dopredu() {
  smerA(1);
  smerB(1);
}

void dozadu() {
  smerA(0);
  smerB(0);
}

void doprava() {
  smerA(1);
  smerB(0);
}

void dolava() {
  smerA(0);
  smerB(1);
}

void nastavRychlostMotorov(int rychlost) {
  rychlostA(rychlost); 
  rychlostB(rychlost);
}

void zistiVzdialenost() {
  digitalWrite(TRIG_PIN, LOW);
  delayMicroseconds(2);
  digitalWrite(TRIG_PIN, HIGH);
  delayMicroseconds(10);
  digitalWrite(TRIG_PIN, LOW);
  long duration = pulseIn(ECHO_PIN, HIGH, 25000);
  distance = (duration / 2) * 0.034;
}

void zastav() {
  vypniA();
  vypniB();
  digitalWrite(ZP_SMEROVKA, LOW);
  digitalWrite(ZL_SMEROVKA, LOW);
  digitalWrite(P_BRZDNE, HIGH);
  digitalWrite(L_BRZDNE, HIGH);
}

void smerA(int vpred) {      // vpred = 1 ak chces ist dopredu, vpred = 0 ak chces ist dozadu
  if (vpred != 0) {
    digitalWrite(SMER_A1, LOW);
    digitalWrite(SMER_A2, HIGH);
  } else {
    digitalWrite(SMER_A1, HIGH);
    digitalWrite(SMER_A2, LOW);
  }
}

void vypniA() {
  digitalWrite(RYCHLOST_A, LOW);
}

void rychlostA(int kolko) {
  analogWrite(RYCHLOST_A, kolko);
}

void smerB(int vpred) {
  if (vpred != 0) {
    digitalWrite(SMER_B1, LOW);
    digitalWrite(SMER_B2, HIGH);
  } else {
    digitalWrite(SMER_B1, HIGH);
    digitalWrite(SMER_B2, LOW);
  }
}

void vypniB() {
  digitalWrite(RYCHLOST_B, LOW);
}

void rychlostB(int kolko) {
  analogWrite(RYCHLOST_B, kolko);
}

