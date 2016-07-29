import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;


public class LexicalAnalyzer {

	/* properties */
	private String buffer; // holds the text in the file to work with
	private int line;
	private static final String[][] SAVED_WORDS = {{"int[\\s]", "INT"}, {"if[\\s]", "IF"}, {"function[\\s]", "FUNC"}, {"main[\\s]", "MAIN"}, {"then[\\s]", "THEN"}, {"else[\\s]", "ELSE"}};
	private static final String[][] SAVED_SYMBOLS = {{"=", "ASSIGN"}, {"+", "PLUS"}, {"-", "MINUS"}, {"*", "MULT"}, {"/", "DIV"}, {"&&", "AND"}, {"||", "OR"}, {";", "SC"}, {"(", "LP"}, {")", "RP"}, {"{", "LC"}, {"}", "RC"}};
	private static final String[] REL = {"==", "!=", "<=", ">=", "<", ">"};
	private static final String[][] REGEX_WORDS = {{"0|[1-9][0-9]*", "NUM"}, {"[a-z][a-z, A-Z, 0-9]*", "ID"}, {"[A-Z][a-z, A-Z, 0-9]*", "FID"}};
	private static final String WHITE = "\\s";

	public LexicalAnalyzer(String fileName) {
		this.buffer = null;
		this.line = 1;

		try {
			this.buffer = new Scanner(new File(fileName)).useDelimiter("\\Z").next();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("cannot find the file: " + fileName);
			e.printStackTrace();
			this.buffer = null;
		}
	}

	public Token yylex() {
		if (this.buffer.length() == 0) {
			return new Token("EOF", "", line);
		}

		String tmp;
		if (this.buffer.length() >= 1) {
			tmp = this.buffer.substring(0, 1);
			if (tmp.matches(WHITE) == true) {
				if (tmp.equals("\n")) {
					line++;
				}
				this.buffer = this.buffer.substring(1, this.buffer.length());
				return null;
			}
		}

		if (this.buffer.length() >=2) {
			tmp = this.buffer.substring(0, 2);
			if (tmp.matches("//") == true) {
				int i = 0;
				for (; i< this.buffer.length(); i++) {
					if (this.buffer.charAt(i) == '\n'){
						this.buffer = this.buffer.substring(i, this.buffer.length());
						return null;
					}
				}

				if (i == this.buffer.length()) {
					this.buffer = "";
					return null;
				}
			}

			if (tmp.equals("/*") == true) {
				int j = 0;
				for (; j < this.buffer.length() -1; j++) {
					if (this.buffer.charAt(j) == '\n') {
						line++;
					}
					if (this.buffer.charAt(j) == '*' && this.buffer.charAt(j +1) == '/') {
						this.buffer = this.buffer.substring(j +2, this.buffer.length());
						return null;
					}
				}
			}
		}

		for (int k = 0; k < SAVED_WORDS.length; k++) {
			if ((SAVED_WORDS[k][0].length() -3) <= this.buffer.length()) {
				tmp = this.buffer.substring(0, SAVED_WORDS[k][0].length() -3);
				boolean isMatched = false;
				for (int l = 0; l < SAVED_SYMBOLS.length; l++){
					if (tmp.charAt(tmp.length() -1) == SAVED_SYMBOLS[l][0].charAt(0)) 
						isMatched = true;
				}
				for (int m = 0; m < REL.length; m++) {
					if (tmp.charAt(tmp.length() -1) == REL[m].charAt(0)) 
						isMatched = true;
				}
				if (tmp.matches(SAVED_WORDS[k][0]) == true || tmp.startsWith(SAVED_WORDS[k][0].substring(0, SAVED_WORDS[k][0].length() -4)) == true && isMatched == true) {
					this.buffer = this.buffer.substring(SAVED_WORDS[k][0].length() -4, this.buffer.length());
					return new Token(new String(SAVED_WORDS[k][1]), "", line);
				}
			}
		}

		for (int k = 0; k< REL.length; k++) {
			if (this.buffer.length() >= REL[k].length()) {
				tmp = this.buffer.substring(0, REL[k].length());
				if (tmp.equals(REL[k]) == true) {
					this.buffer = this.buffer.substring(REL[k].length(), this.buffer.length());
					return new Token("REL", tmp, line);
				}
			}
		}

		for (int k = 0; k < SAVED_SYMBOLS.length; k++) {
			if (this.buffer.length() >= SAVED_SYMBOLS[k][0].length()) {
				tmp = this.buffer.substring(0, SAVED_SYMBOLS[k][0].length());
				if (tmp.equals(SAVED_SYMBOLS[k][0]) == true) {
					this.buffer = this.buffer.substring(SAVED_SYMBOLS[k][0].length(), this.buffer.length());
					return new Token(new String(SAVED_SYMBOLS[k][1]), "", line);
				}
			}
		}

		tmp = "";
		for (int k = 0; k < this.buffer.length(); k++) {
			if (this.buffer.substring(k, k +1).matches("[a-z]|[A-Z]|[0-9]") == true) {
				tmp += this.buffer.substring(k, k +1);
			}
			else {
				break;
			}
		}
		if (tmp.length() == 0 && this.buffer.length() > 0) {
			this.buffer = this.buffer.substring(1, this.buffer.length());
			System.out.println("lex dosen't know what that is!");
			return null;
		}
		if (tmp.charAt(0) == '0') {
			this.buffer = this.buffer.substring(1, this.buffer.length());
			return new Token("NUM", "0", line);
		}
		for (int k = 0; k < REGEX_WORDS.length; k++) {
			if (tmp.matches(REGEX_WORDS[k][0]) == true) {
				this.buffer = this.buffer.substring(tmp.length(), this.buffer.length());
				return new Token(new String(REGEX_WORDS[k][1]), tmp, line);
			}
		}

		this.buffer = this.buffer.substring(1, this.buffer.length());
		return null;
	}
	
	public Vector<Token> startAnalyze() {
		Vector<Token> tokens = new Vector<Token>(1);

		Token token = null;
		do {
			token = yylex();
			if (token !=null) {
				tokens.add(token);
			}
		} while((token != null && token.isEOF() == false) || (token == null));
		
		return tokens;
	}

}
