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

byte data[512];

void setup() {
  Serial.begin(9600);
  DmxSimple.usePin(11);
  
  for(int i=0;i<512;i++){ //init data
    data[i] = 0;
  }
}

int value = 0;
int channel;
int readInt();

byte n, last;

void loop() {
    
    while(!Serial.available()){
      for (int i=0;i<512;i++){
        DmxSimple.write(i,data[i]);
      }
    }
    
      n = Serial.read();
      
      if (n == 255 && last == 255) { // start flag is consecutive 255
        Serial.println("!!!data recieved!!!");
        channel = readInt();
        while(!Serial.available());  //wait for food
        value = Serial.read(); // take a byte
        data[channel] = value;
        Serial.println("!channel "+channel);
        Serial.println("!value "+value);
        n=0;
      }
      last = n;
}

//=====================================================//

int readInt(){
  //Serial.println("reading int");
  
  while(!Serial.available()); //wait for byte
  byte high = Serial.read();
  
  while(!Serial.available()); //wait for byte
  byte low = Serial.read();
  
  int x = (int) word(high,low); //stick together
  
  //if (x==512) Serial.println("512 recieved! we have integer!");
  
  return x;
}

void printInt(int i){
  Serial.write(lowByte(i));
  Serial.write(highByte(i));
}
