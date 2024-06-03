#include <Wire.h>
#include<SoftwareSerial.h>
#include "MAX30100_PulseOximeter.h"

SoftwareSerial ArduinoUno(3, 4); //rx, tx
PulseOximeter pox;

const unsigned long int interval=1000;
unsigned long int prevTime=0;

void setup(){
  Serial.begin(9600);
  ArduinoUno.begin(4800);
  
  pinMode(3, INPUT);
  pinMode(4, OUTPUT);
  
  Serial.print("Initializing pulse oximeter..");
  while (!pox.begin()) {
      Serial.println("FAILED");
  }
  else Serial.println("SUCCESS");
  pox.setIRLedCurrent(MAX30100_LED_CURR_7_6MA);
}

void loop(){
  pox.update();
  float BPM=0, SpO2=0;

  if (millis() - prevTime > interval) {
    BPM = pox.getHeartRate();
    ArduinoUno.print(BPM);
    ArduinoUno.println("\n");

    SpO2 = pox.getSpO2();
    ArduinoUno.print(SpO2);
    ArduinoUno.println("\n");

    //output for debugging
    Serial.println(BPM);
    Serial.println(SpO2);

    prevTime = millis();
  }
}