package m.yao.bfpro.compile;

import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import m.yao.bfpro.BFByteCodeVM;

public class Parser {
	// 注意：每个 Integer 实际上仅使用 4 位
	public static List<Integer> parse(List<Token> tokens) {
		final List<Integer> cache = new ArrayList<>();
		final Deque<Integer> stack = new ArrayDeque<>();
		int pointer4 = 0;
		for (Token tk : tokens) {
			switch (tk) {
				case MOV_LEFT:
					cache.add(BFByteCodeVM.WRI_N1MEM);
					for (int i : Utils.spilt8(-1))
						cache.add(i);
					cache.add(BFByteCodeVM.PMOV);
					pointer4 += 10;
					break;
				case MOV_RIGHT:
					cache.add(BFByteCodeVM.WRI_N1MEM);
					for (int i : Utils.spilt8(1))
						cache.add(i);
					cache.add(BFByteCodeVM.PMOV);
					pointer4 += 10;
					break;
				case ADD:
					cache.add(BFByteCodeVM.WRI_N1MEM);
					for (int i : Utils.spilt8(1))
						cache.add(i);
					cache.add(BFByteCodeVM.OP);
					pointer4 += 10;
					break;
				case SUB:
					cache.add(BFByteCodeVM.WRI_N1MEM);
					for (int i : Utils.spilt8(-1))
						cache.add(i);
					cache.add(BFByteCodeVM.OP);
					pointer4 += 10;
					break;
				case OUT:
					cache.add(BFByteCodeVM.OUT);
					pointer4++;
					break;
				case IN:
					cache.add(BFByteCodeVM.IN);
					pointer4++;
					break;
				case LOOP_START:
					stack.push(pointer4);

					cache.add(BFByteCodeVM.WRI_N1MEM);
					for (int i : Utils.spilt8(0xdeadbeef))
						cache.add(i);
					cache.add(BFByteCodeVM.JUMP_EQZ);
					pointer4 += 10;
					break;
				case LOOP_END:
					if (stack.isEmpty()) throw new RuntimeException("循环不匹配");
					int loopStart = stack.pop();
					int loopEnd = pointer4 + 10;

					cache.add(BFByteCodeVM.WRI_N1MEM);
					for (int i : Utils.spilt8(loopStart))
						cache.add(i);
					cache.add(BFByteCodeVM.JUMP_NEZ);

					int fillPos = loopStart + 1;
					for (int i = 0; i < 8; i++)
						cache.set(fillPos + i, Utils.spilt8(loopEnd)[i]);
					pointer4 += 10;
					break;
			}
		}
		return cache;
	}
}
