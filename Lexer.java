package m.yao.bfpro.compile;

import java.util.ArrayList;
import java.util.List;

public class Lexer {
	public static List<Token> getTokens(String src) {
		final int len = src.length();
		final List<Token> result = new ArrayList<>();
		for (int i = 0; i < len; i++) {
			char c = src.charAt(i);
			switch (c) {
				case '<': result.add(Token.MOV_LEFT); break;
				case '>': result.add(Token.MOV_RIGHT); break;
				case '+': result.add(Token.ADD); break;
				case '-': result.add(Token.SUB); break;
				case '.': result.add(Token.OUT); break;
				case ',': result.add(Token.IN); break;
				case '[': result.add(Token.LOOP_START); break;
				case ']': result.add(Token.LOOP_END); break;
			}
		}
		return result;
	}
}
