package m.yao.bfpro;

import java.util.ArrayList;
import java.util.List;

import m.yao.bfpro.compile.Lexer;
import m.yao.bfpro.compile.Optimizer;
import m.yao.bfpro.compile.Parser;
import m.yao.bfpro.compile.Token;

public class Compiler {
	public static byte[] compile(String src) {
		final List<Token> tokens = Lexer.getTokens(src);
		final List<Integer> vmCodes = Parser.parse(tokens);
		final List<Integer> optimizedCodes = Optimizer.optimize(vmCodes);
		final int optimizedLen = optimizedCodes.size();
		final int bytesLen = (optimizedLen + 1) >>> 1;
		final byte[] result = new byte[bytesLen];
		final int loopMax = optimizedLen >>> 1;
		for (int i = 0; i < loopMax; i++) {
			final int base = i << 1;
			result[i] = (byte) ((optimizedCodes.get(base) << 4) |
					optimizedCodes.get(base + 1));
		}
		if (bytesLen != loopMax)
			result[bytesLen - 1] = (byte) (optimizedCodes.get(optimizedLen - 1) << 4);
		return result;
	}
}
