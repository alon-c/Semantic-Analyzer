public class Token {

	/* properties */
	private String tokenType, tokenAttribute;
	private int tokenLine;

	public Token(String tokenType, String tokenAttribute, int tokenLine) {
		this.tokenType = tokenType;
		this.tokenAttribute = tokenAttribute;
		this.tokenLine = tokenLine;
	}

	public String toString() {
		return tokenType + ";" + tokenAttribute + ";" + tokenLine + "\r\n";
	}

	public boolean isEOF() {
		if (tokenType == "EOF") {
			return true;
		}
		return false;
	}

	/* getters and setters */

	public String getTokenType() {
		return tokenType;
	}

	public String getTokenAttribute() {
		return tokenAttribute;
	}

	public int getTokenLine() {
		return tokenLine;
	}

}
