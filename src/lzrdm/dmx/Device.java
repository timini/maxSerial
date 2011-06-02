package lzrdm.dmx;

import java.io.IOException;

import lzrdm.comms.SerialPort;

import com.cycling74.max.DataTypes;
import com.cycling74.max.MaxObject;
import processing.serial.*;


public class Device extends MaxObject{
	//private static final int[] INLETS = new int[]{ DataTypes.ALL };
	//private static final int[] OUTLETS = new int[] { DataTypes.ALL, };
	
	private int serial_rate;
	private byte start_address; //start DMX address
	
	private int[] channel_data;
	
	private SerialPort port;
	
	public Device(){
		this(9600, 0, 8); //default rate / address
	}
	public Device(int sr){
		this(sr, 0, 8);
	}
	public Device(int sr, int start_add){
		this(sr, start_add, 8);
	}
	public Device(int sr, int start_add,int channels){
		serial_rate = sr;
		start_address = (byte) start_add;
		
		declareIO(channels,1);
		//declareInlets(INLETS);
		//declareOutlets(OUTLETS);
		
		//==--- max info labels ---==//
		//createInfoOutlet(false); //info off
		setInletAssist(new String[] {"DMX channel "+start_address,"DMX channel "+(start_address+1),
				"DMX channel "+(start_address+1),"DMX channel "+(start_address+3),
				"DMX channel "+(start_address+4),"DMX channel "+(start_address+5),
				"DMX channel "+(start_address+6),"DMX channel "+(start_address+7)
				});
		setOutletAssist(new String[] {"blah"});
		
		//== serial ==//
		port = new SerialPort();
		port.initialize();
		port.setMax(this);
		
		//set the start flag (what's on arduino?)
		byte llllll = (byte) 0xff;
		//port.setStartFlag(new byte[] {hi,hi});
		port.setStartFlag(new byte[] {llllll});
	}
	public void inlet(int in){
		int clipped = clip(in);
		post("recieved: " + clipped);
		try {
			mySerialProtocol(start_address+getInlet(),clipped);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	void mySerialProtocol(int channel,int value) throws IOException{
		port.startFlag();
		port.writeByte(channel);
		port.writeByte(value);
		post("sent datum");
	}
	public void setAllChannels(int val){
		val = clip(val);
		for (int i : channel_data) channel_data[i] = val;
	}
	int clip(int v){
		v = (v < 0) ? 0 : v;
		v = ((v > 254) ? 254 : v); //!!!!! 255 reserved for start
		return v;
	}
	public void bang(){
		if (port.isActive()) {port.close();post("serial terminated");}
		else {port.initialize();post("serial started");}
	}
	public void writeByte(byte b) throws IOException{
		port.writeByte(b);
	}
	public void send(short i) throws IOException{
		port.write(i);
	}
//	public void send(short[] dada) throws IOException{
//		for (short s : dada) port.write(s);
//		post("array short");
//	}
}
