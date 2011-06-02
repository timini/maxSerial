package lzrdm.comms;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import gnu.io.CommPortIdentifier; 
//import gnu.io.SerialPort;
import gnu.io.SerialPortEvent; 
import gnu.io.SerialPortEventListener; 
import java.util.Enumeration;

import com.cycling74.max.MaxObject;
 

public class SerialPort implements SerialPortEventListener {
	gnu.io.SerialPort serialPort;
        /** The port we're normally going to use. */
	private static final String PORT_NAMES[] = { 
			"/dev/tty.usbserial-A9007UX1", // Mac OS X
			"/dev/ttyUSB0", // Linux
			"COM3", // Windows
			};
	/** Buffered input stream from the port */
	private InputStream input;
	/** The output stream to the port */
	private OutputStream output;
	/** Milliseconds to block while waiting for port open */
	private static final int TIME_OUT = 2000;
	/** Default bits per second for COM port. */
	private static final int DATA_RATE = 9600;
	
	private MaxObject max;
	
	boolean active;
	
	byte[] startFlag;

	public void initialize() {
		//System.out.println(System.getProperty("java.library.path")); // debug classpath
		
		CommPortIdentifier portId = null;
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

		// iterate through, looking for the port
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			for (String portName : PORT_NAMES) {
				if (currPortId.getName().equals(portName)) {
					portId = currPortId;
					break;
				}
			}
		}
		
		if (portId == null) {
			System.out.println("Could not find COM port.");
			return;
		}

		try {
			// open serial port, and use class name for the appName.
			serialPort = (gnu.io.SerialPort) portId.open(this.getClass().getName(),TIME_OUT);

			// set port parameters
			serialPort.setSerialPortParams(DATA_RATE,
					gnu.io.SerialPort.DATABITS_8,
					gnu.io.SerialPort.STOPBITS_1,
					gnu.io.SerialPort.PARITY_NONE);

			// open the streams
			input = serialPort.getInputStream();
			output = serialPort.getOutputStream();

			// add event listeners
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
		} catch (Exception e) {
			System.err.println(e.toString());
		}
		active = true;
	}
	public boolean isActive(){
		return active;
	}
	/**
	 * This should be called when you stop using the port.
	 * This will prevent port locking on platforms like Linux.
	 */
	public synchronized void close() {
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
		}
		active = false;
	}

	/**
	 * Handle an event on the serial port. Read the data and print it.
	 */
	public synchronized void serialEvent(SerialPortEvent oEvent) {
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				int available = input.available();
				byte chunk[] = new byte[available];
				input.read(chunk, 0, available);

				// Displayed results are codepage dependent
				//System.out.print(new String(chunk));
				max.post(new String(chunk));
			} catch (Exception e) {
				System.err.println(e.toString());
			}
		}
		// Ignore all the other eventTypes, but you should consider the other ones.
	}
	/**
	 * Write short to serial port
	 * @throws IOException 
	 */
	public void write(short s) throws IOException{
		byte low = (byte)(s & 0xff);
		byte high = (byte)((s >> 8) & 0xff);
		
		ByteBuffer bb = ByteBuffer.allocate(2);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.put(high);
		bb.put(low);
		
		
		//startFlag();
		output.write(bb.array());
		max.post("write int" + s);
	}
	/**
	 * Write byte to serial port
	 * @throws IOException 
	 */
	public void writeByte(short s) throws IOException{
		byte unsignedByte = (byte)(s & 0xff);
		
		ByteBuffer bb = ByteBuffer.allocate(1);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.put(unsignedByte);
		
		
		//startFlag();
		output.write(bb.array());
		max.post("write byte" + unsignedByte);
	}
	/**
	 * Write byte to serial port
	 * @throws IOException 
	 */
	public void writeByte(int s) throws IOException{
		byte unsignedByte = (byte)(s & 0xff);
		
		ByteBuffer bb = ByteBuffer.allocate(1);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.put(unsignedByte);
		
		
		//startFlag();
		output.write(bb.array());
		max.post("write byte" + unsignedByte);
	}
	public void setStartFlag(byte[] sf){
		startFlag = sf;
	}
	public void startFlag() throws IOException{
		output.write(startFlag);
		max.post("write start flag");
	}
	public void writeByte(byte b) throws IOException{
		output.write(b);
		max.post("write byte " + b);

	}
	public static void main(String[] args) throws Exception {
		SerialPort main = new SerialPort();
		main.initialize();
		short x = 0;
		while(x<100){
			main.write(x);
			x++;
		}
		System.out.println("Started");
	}
	public void setMax(MaxObject m){
		max = m;
	}
	
}