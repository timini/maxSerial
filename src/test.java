import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public class test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("short: ");
		short s = -3499;
		System.out.println(s);
		
		byte low = (byte)(s & 0xff);
		byte high = (byte)((s >> 8) & 0xff);
		
		System.out.println(low);
		System.out.println(high);
		
		ByteBuffer bb = ByteBuffer.allocate(2);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.put(low);
		bb.put(high);
		short recovered = bb.getShort(0);
		
		System.out.println(recovered);
	}

}
