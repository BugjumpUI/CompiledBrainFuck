package m.yao.bfpro;

public class BFByteCodeVM implements Runnable {
	public static final int
			NOP = 0,
			PMOV = 1,
			OP = 2,
			OUT = 3,
			IN = 4,
			JUMP_EQZ = 5,
			JUMP_NEZ = 6,
			WRI_N1MEM = 7;

	// 16 位对齐
	// 注意：一个 int 有 8 个命令
	private final int[] mBFByteCodes;
	private int mPtr;
	private int mPtrVM; // 4 比特一格
	private int mNeg1MemVal32 = 0;
	private final byte[] mMem;

	private IBFIn mIn;
	private IBFOut mOut;

	public BFByteCodeVM(byte[] bcByte) {
		this(bcByte, 30000);
	}

	public BFByteCodeVM(byte[] bcByte, int memLen) {
		int bcByLen = bcByte.length;
		int numNibbles = bcByLen << 1;
		int[] intArray = new int[(numNibbles + 7) >>> 3];

		for (int i = 0; i < bcByLen; i++) {
			byte b = bcByte[i];
			int nibble1 = (b >> 4) & 0xF;
			int nibble2 = b & 0xF;

			int nibbleIndex = i << 1;
			int intIndex = nibbleIndex >>> 3;
			int bitPos = (7 - (nibbleIndex & 0x7)) << 2;

			intArray[intIndex] |= nibble1 << bitPos;

			nibbleIndex++;
			intIndex = nibbleIndex >>> 3;
			bitPos = (7 - (nibbleIndex & 0x7)) << 2;

			intArray[intIndex] |= nibble2 << bitPos;
		}

		mBFByteCodes = intArray;
		mMem = new byte[memLen];
		mPtrVM = 0;
		mPtr = 0;
	}

	public void setIO(IBFIn in, IBFOut out) {
		mIn = in;
		mOut = out;
	}

	@Override
	public void run() {
		int totalNibbles = mBFByteCodes.length * 8; // 每个int有8个4位命令
		while (mPtrVM < totalNibbles) {
			int operate = read4_bc();
			exec4_bc(operate);
		}
	}

	// 读取后会自动往后移动
	private int read4_bc() {
		if (mPtrVM >= mBFByteCodes.length * 8) {
			return NOP;
		}

		int intIndex = mPtrVM >>> 3;
		int bitPos = (7 - (mPtrVM & 0x7)) << 2;
		int operate = (mBFByteCodes[intIndex] >> bitPos) & 0xF;

		mPtrVM++;
		return operate;
	}

	private void exec4_bc(int operate) {
		switch (operate) {
			case NOP:
				break;
			case PMOV:
				mPtr += mNeg1MemVal32;
				if (mPtr < 0) {
					mPtr = 0;
				} else if (mPtr >= mMem.length) {
					mPtr = mMem.length - 1;
				}
				break;
			case OP:
				int newValue = (mMem[mPtr] & 0xFF) + mNeg1MemVal32;
				if (newValue < 0)
					newValue = 256 - ((-newValue) % 256);

				mMem[mPtr] = (byte) (newValue % 256);
				break;
			case OUT:
				out(mMem[mPtr]);
				break;
			case IN:
				mMem[mPtr] = in();
				break;
			case JUMP_EQZ:
				if ((mMem[mPtr] & 0xFF) == 0)
					mPtrVM = mNeg1MemVal32;
				break;
			case JUMP_NEZ:
				if ((mMem[mPtr] & 0xFF) != 0)
					mPtrVM = mNeg1MemVal32;
				break;
			case WRI_N1MEM:
				next32N1M_bc();
				break;
			default:
				break;
		}
	}

	private void next32N1M_bc() {
		int value = 0;

		for (int i = 0; i < 8; i++) {
			int nibble = read4_bc();
			value = (value << 4) | (nibble & 0xF);
		}
		mNeg1MemVal32 = value;
	}


	private void out(byte outVar) {
		mOut.out(outVar);
	}

	private byte in() {
		return mIn.in();
	}
}
