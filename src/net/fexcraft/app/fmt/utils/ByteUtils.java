package net.fexcraft.app.fmt.utils;

public class ByteUtils {
	
	public static int M1 = 256;
	public static int M2 = M1 * M1;
	public static int M3 = M2 * M1;
	
	/* Non-Negative Integer from Byte */
	public static int get(byte bite){
		return bite < 0 ? bite + 256 : bite;
	}

	/* Non-Negative Integer from 3 RGB Bytes */
	public static int getRGB(byte[] rgb){
		return (get(rgb[0]) * M2) + (get(rgb[1]) * M1) + get(rgb[2]);
	}

}
