const int TRIG_PIN = 2;
const int ECHO_PIN = 3;
const int TRIG_PIN2 = 4;
const int ECHO_PIN2 = 5;
const int SMER_A1 = 8;
const int SMER_A2 = 11;
const int RYCHLOST_A = 9;
const int SMER_B1 = 12;
const int SMER_B2 = 13;
const int RYCHLOST_B = 10;
const int PL_SMEROVKA = 29;
const int PP_SMEROVKA = 47;
const int ZL_SMEROVKA = 27;
const int ZP_SMEROVKA = 32;
const int L_STRETAVACIE = 45;
const int P_STRETAVACIE = 44;
const int P_DIALKOVE = 28;
const int L_DIALKOVE = 35;
const int P_ZADNE = 23;
const int L_ZADNE = 50;
const int P_BRZDNE = 22;
const int L_BRZDNE = 51;
const int SPIATOCKA = 49;
const int SILA_DENNYCH_SVETIEL = 20;

void zapniAkcelerometer();
void merajZrychlenie();

void setup() {
  Serial.begin(115200);
  Serial.println("Seriovy port nastartovany.");

  pinMode(TRIG_PIN, OUTPUT);
  pinMode(ECHO_PIN, INPUT);
  pinMode(TRIG_PIN2, OUTPUT);
  pinMode(ECHO_PIN2, INPUT);
  pinMode(SMER_A1, OUTPUT);
  pinMode(SMER_A2, OUTPUT);
  pinMode(RYCHLOST_A, OUTPUT);
  pinMode(SMER_B1, OUTPUT);
  pinMode(SMER_B2, OUTPUT);
  pinMode(RYCHLOST_B, OUTPUT);
  pinMode(PP_SMEROVKA, OUTPUT);
  pinMode(PL_SMEROVKA, OUTPUT);
  pinMode(ZP_SMEROVKA, OUTPUT);
  pinMode(ZL_SMEROVKA, OUTPUT);
  pinMode(L_STRETAVACIE, OUTPUT);
  pinMode(P_STRETAVACIE, OUTPUT);
  pinMode(P_DIALKOVE, OUTPUT);
  pinMode(L_DIALKOVE, OUTPUT);
  pinMode(P_ZADNE, OUTPUT);
  pinMode(L_ZADNE, OUTPUT);
  pinMode(P_BRZDNE, OUTPUT);
  pinMode(L_BRZDNE, OUTPUT);
  pinMode(SPIATOCKA, OUTPUT);
  Serial.println("Piny nastavene.");

  Serial.println("Svetla zapnute.");
  zapniAkcelerometer();  
}

const int DOPREDU = 1;
const int DOZADU = 2;
const int DOPRAVA = 3;
const int DOLAVA = 4;
const int STAT = 0;


long vypnutBrzdoveSvetla = 0;

int smer = STAT;

boolean zapnutyAsistentNarazu = 1;
int vyhybaniePrekazke = 0;              // 0 alebo DOPRAVA alebo DOLAVA
int ktoraRychlost = 0;                  // "tempomat" od 0 (plná rýchlosť) do 8 (najpomalšie)

float distance;                         // vzdialenost od prekazky - z predného senzora (ak sa nejde dozadu)
float distanceS = 0;                    // vzdialenost od prekazky z minuleho cyklu - používa sa na meranie rýchlosti
float distanceDZ = 0;                   // vzdialenosť od prekážky - zo zadného senzora (ak sa ide dozadu)

int kedyZastat = 10;                    // kedy má robot začať brzdiť pred prekážkou (v cm)
long rychlostMerania = 102;             // rýchlosť opakovania celého cyklu (v ms)

float volantV = 0;
float pedaleP = 0;
float akcelerometerX = 0;               // zrýchlenie v osiach X, Y, Z
float akcelerometerY = 0;
float akcelerometerZ = 0;

float airbagDZ = 8;                     //nastavenie najnizsej hodnoty pre airbagy pri smere dozadu
float airbagDO = -8;                    //nastavenie najnizsej hodnoty pre airbagy pri smere dopredu
long vypnutAirbag = 0;

boolean zastaveny = false;

int spiatocka = 0;

void loop() {

  unsigned long casStart = millis();
  svetla();
  merajZrychlenie();
  
  distance = 0;
  spracujPrikazZTabletu();
  akcelerometer();
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
    volant();
  }
  unsigned long presielCas = millis() - casStart;
  long esteCakat = rychlostMerania - presielCas;
  if (esteCakat < 0) {
    Serial.print("Zaporna doba cakania: ");
    Serial.println(esteCakat);
    esteCakat = 10;
  }
  Serial.print("V:");
  Serial.print(volantV);
  Serial.print(" P:");
  Serial.print(pedaleP);
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

void akcelerometer() {
  if (smer == DOZADU && akcelerometerY > airbagDZ) {
    Serial.print("Naraz pri jazde dozadu. Zrychlenie = ");
    Serial.println(akcelerometerY);
    zastav();
  } else if (smer == DOPREDU && akcelerometerY < airbagDO) {
    Serial.print("Naraz pri jazde dopredu. Zrychlenie = ");
    Serial.println(akcelerometerY);
    zastav();
  }
}

void spracujPrikazZTabletu() {
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
      case 'H': ktoraRychlost = 1;
        if (smer != STAT) {
          nastavRychlostMotorov(0);
        }
        Serial.println("Nastaveny tempomat \"najrychlejsie\"");
        break;
      case 'I': ktoraRychlost = 2;
        if (smer != STAT) {
          nastavRychlostMotorov(0);
        }
        Serial.println("Nastaveny tempomat \"velmi rychlo\"");
        break;
      case 'J': ktoraRychlost = 3;
        if (smer != STAT) {
          nastavRychlostMotorov(0);
        }
        Serial.println("Nastaveny tempomat \"rychlo\"");
        break;
      case 'K': ktoraRychlost = 4;
        if (smer != STAT) {
          nastavRychlostMotorov(0);
        }
        Serial.println("Nastaveny tempomat \"viac nez stredne\"");
        break;
      case 'L': ktoraRychlost = 5;
        if (smer != STAT) {
          nastavRychlostMotorov(0);
        }
        Serial.println("Nastaveny tempomat \"stredne\"");
        break;
      case 'M': ktoraRychlost = 6;
        if (smer != STAT) {
          nastavRychlostMotorov(0);
        }
        Serial.println("Nastaveny tempomat \"menej nez stredne\"");
        break;
      case 'N': ktoraRychlost = 7;
        if (smer != STAT) {
          nastavRychlostMotorov(0);
        }
        Serial.println("Nastaveny tempomat \"pomaly\"");
        break;
      case 'O': ktoraRychlost = 8;
        if (smer != STAT) {
          nastavRychlostMotorov(0);
        }
        Serial.println("Nastaveny tempomat \"najpomalsie\"");
        break;
      case 'R':
       spiatocka = 0;
        break;
      case 'S':
       spiatocka = 1;
       Serial.println("Spiatocka");
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
            volantV = retazecNaCislo(b);
            volantV = volantV / 4;
            Serial.print("V = ");
            Serial.println(volantV);
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
            pedaleP = retazecNaCislo(a);
            pedaleP = pedaleP / 4;
            pedaleP = pedaleP - pedaleP + pedaleP;
            Serial.print("P = ");
            Serial.println(pedaleP);
          }
        }
        break;
    }
  }
}

void volant(){
  float NBA;

  if (pedaleP >= 20){
    NBA = pedaleP;
    if (volantV == 250 || volantV == -250) {
      if (volantV == 250) {
        doprava();
        nastavRychlostMotorov(NBA);
      } else {
        dolava();
        nastavRychlostMotorov(NBA);
      }
    } else if (volantV != 250 && volantV >= 10 && pedaleP >= 20) {
      rychlostA(NBA);
      rychlostB(NBA - volantV); 
      digitalWrite(PP_SMEROVKA, HIGH);
      digitalWrite(ZP_SMEROVKA, HIGH);
      digitalWrite(PL_SMEROVKA, LOW);
      digitalWrite(ZL_SMEROVKA, LOW);
      digitalWrite(P_BRZDNE, LOW);
      digitalWrite(L_BRZDNE, LOW);
      digitalWrite(SPIATOCKA, LOW);
    } else if (volantV <= -20 && volantV != -250) {
      rychlostA(NBA + volantV);
      rychlostB(NBA);
      digitalWrite(PL_SMEROVKA, HIGH);
      digitalWrite(ZL_SMEROVKA, HIGH);
      digitalWrite(PP_SMEROVKA, LOW);
      digitalWrite(ZP_SMEROVKA, LOW);
      digitalWrite(P_BRZDNE, LOW);
      digitalWrite(L_BRZDNE, LOW);
      digitalWrite(SPIATOCKA, LOW);
    } else if (spiatocka == 0) {
      dopredu();
      rychlostA(NBA);
      rychlostB(NBA);
      digitalWrite(PP_SMEROVKA, LOW);
      digitalWrite(PL_SMEROVKA, LOW);
      digitalWrite(ZP_SMEROVKA, LOW);
      digitalWrite(ZL_SMEROVKA, LOW);
      digitalWrite(P_BRZDNE, LOW);
      digitalWrite(L_BRZDNE, LOW);
      digitalWrite(SPIATOCKA, LOW);
    } else if (spiatocka == 1){
      dozadu();
      rychlostA(NBA);
      rychlostB(NBA);
      digitalWrite(SPIATOCKA, HIGH);
      digitalWrite(PP_SMEROVKA, LOW);
      digitalWrite(PL_SMEROVKA, LOW);
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

//void testujVzdialenost() {
//  zistiVzdialenost();
//  if (distance >= 400 || distance <= 0) {
////    rychlost = 0;
//  } else if (distance < 50) {
//     zastav();
//  } else {
//     if (distance < kedyZastat && smer == DOPREDU) {
//        zastav();
//     }
//  }
//}

//void testujVzdialenostDZ() {
//  zistiVzdialenostDZ();
//  if (distanceDZ > 0 && distanceDZ < 50) {
//    Serial.print(distanceDZ);
//    Serial.println(" cm POZOR PREKAZKA BLIZSIE AKO 50 CM BRZDIT ALEBO ZMENIT SMER ");
//    if (distanceDZ < kedyZastat) {
//      zastav();
//    }
//  }
//}


void svetla() {
  digitalWrite(P_ZADNE, HIGH);
  digitalWrite(L_ZADNE, HIGH);
  digitalWrite(P_DIALKOVE, LOW);
  digitalWrite(L_DIALKOVE, LOW);
  digitalWrite(PP_SMEROVKA, LOW);
  digitalWrite(PL_SMEROVKA, LOW);
  digitalWrite(ZP_SMEROVKA, LOW);
  digitalWrite(ZL_SMEROVKA, LOW);
  digitalWrite(P_BRZDNE, LOW);
  digitalWrite(L_BRZDNE, LOW);
  digitalWrite(SPIATOCKA, LOW);
}

void dopredu() {
  smerA(1);
  smerB(1);
  nastavRychlostMotorov(255);
  digitalWrite(PP_SMEROVKA, LOW);
  digitalWrite(PL_SMEROVKA, LOW);
  digitalWrite(ZP_SMEROVKA, LOW);
  digitalWrite(ZL_SMEROVKA, LOW);
  digitalWrite(P_BRZDNE, LOW);
  digitalWrite(L_BRZDNE, LOW);
  digitalWrite(SPIATOCKA, LOW);
}

void dozadu() {
  smerA(0);
  smerB(0);
  nastavRychlostMotorov(255);
  digitalWrite(SPIATOCKA, HIGH);
  digitalWrite(PP_SMEROVKA, LOW);
  digitalWrite(PL_SMEROVKA, LOW);
  digitalWrite(ZP_SMEROVKA, LOW);
  digitalWrite(ZL_SMEROVKA, LOW);
  digitalWrite(P_BRZDNE, LOW);
  digitalWrite(L_BRZDNE, LOW);
}

void doprava() {
  smerA(1);
  smerB(0);
  nastavRychlostMotorov(150);
  digitalWrite(PP_SMEROVKA, HIGH);
  digitalWrite(ZP_SMEROVKA, HIGH);
  digitalWrite(PL_SMEROVKA, LOW);
  digitalWrite(ZL_SMEROVKA, LOW);
  digitalWrite(P_BRZDNE, LOW);
  digitalWrite(L_BRZDNE, LOW);
  digitalWrite(SPIATOCKA, LOW);
}

void dolava() {
  smerA(0);
  smerB(1);
  nastavRychlostMotorov(150);
  digitalWrite(PL_SMEROVKA, HIGH);
  digitalWrite(ZL_SMEROVKA, HIGH);
  digitalWrite(PP_SMEROVKA, LOW);
  digitalWrite(ZP_SMEROVKA, LOW);
  digitalWrite(P_BRZDNE, LOW);
  digitalWrite(L_BRZDNE, LOW);
  digitalWrite(SPIATOCKA, LOW);
}

void nastavRychlostMotorov(int rychlostBezTempomatu) {
  switch (ktoraRychlost) {
    case 1: rychlostA(255); rychlostB(255); break;
    case 2: rychlostA(225); rychlostB(225); break;
    case 3: rychlostA(200); rychlostB(200); break;
    case 4: rychlostA(175); rychlostB(175); break;
    case 5: rychlostA(150); rychlostB(150); break;
    case 6: rychlostA(125); rychlostB(125); break;
    case 7: rychlostA(100); rychlostB(100); break;
    case 8: rychlostA(75); rychlostB(75); break;
    case 0: rychlostA(rychlostBezTempomatu); rychlostB(rychlostBezTempomatu); break;
  }
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

void zistiVzdialenostDZ() {
  digitalWrite(TRIG_PIN2, LOW);
  delayMicroseconds(2);
  digitalWrite(TRIG_PIN2, HIGH);
  delayMicroseconds(10);
  digitalWrite(TRIG_PIN2, LOW);
  long durationDZ = pulseIn(ECHO_PIN2, HIGH, 25000);
  distanceDZ = (durationDZ / 2) * 0.034;
}

void zastav() {
  Serial.println("Zastavujem");
  vypniA();
  vypniB();
  digitalWrite(SPIATOCKA, LOW);
  digitalWrite(PP_SMEROVKA, LOW);
  digitalWrite(PL_SMEROVKA, LOW);
  digitalWrite(ZP_SMEROVKA, LOW);
  digitalWrite(ZL_SMEROVKA, LOW);
  digitalWrite(P_BRZDNE, HIGH);
  digitalWrite(L_BRZDNE, HIGH);
  vypnutBrzdoveSvetla = millis() + 1500;
  smer = STAT;
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

void zapniA() {
  digitalWrite(RYCHLOST_A, HIGH);
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

void zapniB() {
  digitalWrite(RYCHLOST_B, HIGH);
}

void vypniB() {
  digitalWrite(RYCHLOST_B, LOW);
}
void rychlostB(int kolko) {
  analogWrite(RYCHLOST_B, kolko);
}



