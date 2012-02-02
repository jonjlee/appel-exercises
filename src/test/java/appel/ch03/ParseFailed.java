package appel.ch03;

import appel.ch03.node.Switch;
import appel.ch03.node.Token;
import appel.ch03.parser.ParserException;

class ParseFailed extends Token {
	public String input;
	public Exception e;
	public ParseFailed(String input, Exception e) {
		this.input = input;
		this.e = e;
	}
	public String getMessage() {
		String indent = "       ";
		StringBuilder s = new StringBuilder("\n").append(indent).append(e.getMessage()).append(":\n");
		s.append(indent).append("1   .    10   .    20   .    30   .    40   .    50   .    60   .    70   .\n");
		if (e instanceof ParserException) {
			int col = Integer.parseInt(e.getMessage().split("\\[|\\]|,")[2]);
			s.append(indent);
			for (int i = 0; i < col-1; i++) {
				s.append(" ");
			}
			s.append("v\n");
		}

		String[] lines = input.split("\n");
		for (int i = 0; i < lines.length; i++) {
			s.append(indent.substring(3)).append(i+1).append(": ").append(lines[i]).append('\n');
		}
		return s.toString();
	}
	@Override public void apply(Switch sw) { throw new UnsupportedOperationException(); }
	@Override public Object clone() { throw new UnsupportedOperationException(); }
}