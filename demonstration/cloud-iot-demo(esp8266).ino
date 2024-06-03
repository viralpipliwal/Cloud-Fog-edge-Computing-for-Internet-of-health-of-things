#include "arduino_secrets.h"
#include "thingProperties.h"
#include<SoftwareSerial.h>
#include<ESP8266WiFi.h>

SoftwareSerial NodeMCU(D2, D3); //rx, tx

void setup() {
  Serial.begin(9600);
  NodeMCU.begin(4800);

  pinMode(D2, INPUT);
  pinMode(D3, OUTPUT);

  initProperties();
  ArduinoCloud.begin(ArduinoIoTPreferredConnection);
  setDebugMessageLevel(0);
  ArduinoCloud.printDebugInfo();
}

void loop() {
  ArduinoCloud.update();

  while(NodeMCU.available()){
    ArduinoCloud.update();

    BPM=NodeMCU.parseFloat();
    if(NodeMCU.read()=='\n') Serial.println(BPM);

    SpO2=NodeMCU.parseFloat();
    if(NodeMCU.read()=='\n') Serial.println(SpO2);
  }
  delay(1000);
}