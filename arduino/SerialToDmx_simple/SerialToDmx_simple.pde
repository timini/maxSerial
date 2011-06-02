/* This program allows you to set DMX channels over the serial port.
**
** After uploading to Arduino, switch to Serial Monitor and set the baud rate
** to 9600. You can then set DMX channels using these commands:
**
** <number>c : Select DMX channel
** <number>v : Set DMX channel to new value
**
** These can be combined. For example:
** 100c355w : Set channel 100 to value 255.
**
** For more details, and compatible Processing sketch,
** visit http://code.google.com/p/tinkerit/wiki/SerialToDmx
**
** Help and support: http://groups.google.com/group/dmxsimple       */

#include <DmxSimple.h>
byte data[254];

void setup() {
  Serial.begin(9600);
  DmxSimple.usePin(11);
  for (int i=0;i<254;i++){
    data[i] = 0;
  }
}

byte value = 0;
byte channel;
int readInt();

byte n, last;

void loop() {
    
    while(!Serial.available()){
      for (int i=0;i<254;i++){
        DmxSimple.write(i,data[i]);  
      }
    }
    
      n = Serial.read();
      
      if (n == 255) { // start flag
        while(!Serial.available());
        channel = Serial.read();
        while(!Serial.available());
        value = Serial.read();
        Serial.print("channel:");
        Serial.println(channel,DEC);
        Serial.println(value,DEC);
        data[channel] = value; // correct for sign
        n=0;
      }
      //last = n;
    //Serial.println(last);
    //Serial.println(n);
}
