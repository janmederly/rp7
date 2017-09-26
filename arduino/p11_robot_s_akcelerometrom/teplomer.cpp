#include <OneWire.h>
#include <DallasTemperature.h>
#include <Wire.h>
#define ONE_WIRE_BUS 31

OneWire oneWire(ONE_WIRE_BUS);
DallasTemperature teplomer(&oneWire);
DeviceAddress adresaTeplomera;

extern float teplota;

void zapniTeplomer() {
  teplomer.begin();
  teplomer.setCheckForConversion(true);
  Serial.println("Teplomer nastaveny.");
  
  teplomer.getAddress(adresaTeplomera, 0);
  Serial.print("Parasite = ");
  Serial.print(teplomer.isParasitePowerMode());
  Serial.print("  checkForConversion = ");
  Serial.print(teplomer.getCheckForConversion());
  Serial.print("  resolution = ");
  Serial.println(teplomer.getResolution(adresaTeplomera));
  teplomer.setWaitForConversion(false);
}

void zacniMeratTeplotu() {
  teplomer.requestTemperaturesByAddress(adresaTeplomera);
}

void zistiOdmeranuTeplotu() { 
  if (teplomer.isConversionAvailable(adresaTeplomera)) {
    teplota = teplomer.getTempCByIndex(0);
    teplomer.requestTemperatures();  
  }
}

