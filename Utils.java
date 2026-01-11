package m.yao.bfpro.compile;

public class Utils {
	public static int[] spilt8(int i32) {
		int[] result = new int[8];

		result[0] = (i32 >>> 28) & 0xf;
		result[1] = (i32 >>> 24) & 0xf;
		result[2] = (i32 >>> 20) & 0xf;
		result[3] = (i32 >>> 16) & 0xf;
		result[4] = (i32 >>> 12) & 0xf;
		result[5] = (i32 >>> 8) & 0xf;
		result[6] = (i32 >>> 4) & 0xf;
		result[7] = i32 & 0xf;

		return result;
	}
}