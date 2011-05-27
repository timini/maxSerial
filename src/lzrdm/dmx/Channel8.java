package lzrdm.dmx;

import lzrdm.comms.SerialPort;

import com.cycling74.max.DataTypes;
import com.cycling74.max.MaxObject;
import processing.serial.*;


public class Channel8 extends MaxObject{
	//private static final int[] INLETS = new int[]{ DataTypes.ALL };
	//private static final int[] OUTLETS = new int[] { DataTypes.ALL, };
	
	private int serial_rate;
	private int start_address; //start DMX address
	
	private int[] channel_data;
	
	public Channel8(){
		this(9600, 0); //default rate
	}
	public Channel8(int start_add){
		this(9600, start_add);
	}
	public Channel8(int sr, int start_add){
		serial_rate = sr;
		start_address = start_add;
		
		declareIO(8,1);
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
		SerialPort port = new SerialPort();
		port.initialize();
		port.setMax(this);
		//port.write(512);
	}
	public void setAllChannels(int val){
		val = bound(val);
		for (int i : channel_data) channel_data[i] = val;
	}
	public int bound(int v){
		v = (v < 0) ? 0 : v;
		v = (v > 255) ? 255 : v;
		return v;
	}
}
