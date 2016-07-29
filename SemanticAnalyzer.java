import java.io.FileWriter;
import java.io.IOException;
import java.util.Stack;
import java.util.Vector;

public class SemanticAnalyzer {
	/* properties */
	private String outputFile;
	private SyntaxAnalyzer syntaxAnalyzer;
	private Vector<Token> tokens = null;
	
	public SemanticAnalyzer(String grammarFile, String inputFile) {
		LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer(inputFile);
		this.tokens = lexicalAnalyzer .startAnalyze();
		syntaxAnalyzer = new SyntaxAnalyzer(grammarFile, inputFile, tokens);

		for (int i = inputFile.length() -1; i >= 0; i--) {
			if (inputFile.charAt(i) == '.') {
				inputFile = inputFile.substring(0, i);
				break;
			}
		}
		this.outputFile = inputFile + ".sem";
	}
	
	public void startAnalyze() {
		if (syntaxAnalyzer.startAnalyze() == false) {
			try {
				FileWriter fw = new FileWriter(outputFile);
					fw.write("Syntax mismatch");
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return;
		}
		
		Vector<RedecNode> reDec = new Vector<RedecNode>(1);
		Vector<RefNode> refrances = new Vector<RefNode>(1);
		int blockNum = 0;
		boolean isDec = false;
		boolean found = false;
		boolean isRedec = false;
		Stack<Integer> stack = new Stack<Integer>();
		stack.push(blockNum);
		
		for (int i = 0; i < tokens.size(); i++) {
			if (tokens.get(i).getTokenType().equals("LC")) {
				blockNum++;
				int tmp = blockNum;
				stack.push(tmp);
			}
			if (tokens.get(i).getTokenType().equals("RC")) {
				  stack.pop();
			}
			if (tokens.get(i).getTokenType().equals("INT")) {
				isDec = true;
			}
			if ((isDec == true) && (tokens.get(i).getTokenType().equals("SC"))) {
				isDec = false;
			}
			
			if (tokens.get(i).getTokenType().equals("ID")) {
				if (isDec == true) {
					for (int j = reDec.size() -1; j >= 0; j--) {
						if (stack.peek()== reDec.get(j).blockNum) {
						for (int k = 0; k < reDec.get(j).redecs.size(); k++) {
							if (reDec.get(j).redecs.get(k).name.equals(tokens.get(i).getTokenAttribute())) {
								found = true;
								reDec.get(j).redecs.get(k).lines.add(tokens.get(i).getTokenLine());
								reDec.get(j).isDooplicate = true;
								if (isRedec == false)
									isRedec = true;
								
								break;
							}
						}
						if (found == false) {
							SubRedecNode newSub = new SubRedecNode();
							newSub.name = tokens.get(i).getTokenAttribute();
							newSub.lines.add(tokens.get(i).getTokenLine());
							reDec.get(j).redecs.add(newSub);
							found = true;
							break;
						}
						if (found = true)
							break;
						}
					}
					if(found == false) {
						SubRedecNode newSub = new SubRedecNode();
						newSub.name = tokens.get(i).getTokenAttribute();
						newSub.lines.add(tokens.get(i).getTokenLine());
						RedecNode newrdec = new RedecNode();
						newrdec.blockNum = stack.peek();
						newrdec.redecs.add(newSub);
						reDec.add(newrdec);
					}
					
			}
			else { // not a declaration
				RefNode newRef = new RefNode();
				newRef.token = tokens.get(i);
				int tmp = stack.peek();
				newRef.blockNum = tmp; //reDec.get(j).blockNum;
				for (int j = reDec.size() -1; j>= 0; j--) {
					if (stack.search(reDec.get(j).blockNum) != -1) {
					for (int k = 0; k < reDec.get(j).redecs.size(); k++) {
						if (reDec.get(j).redecs.get(k).name.equals(tokens.get(i).getTokenAttribute())) {
							newRef.decLine = reDec.get(j).redecs.get(k).lines.get(0);
							found = true;
							break;
						}
					}
					if (found == true) 
						break;
					}
				}
				refrances.add(newRef);
			}
				
				found = false;
			}
		}
		
		try {		
			FileWriter fw = new FileWriter(outputFile);
			if (isRedec == false) {
				fw.write("no redeclarations\r\n");
			}
			else { // there are redeclerations
				fw.write("redeclarations\r\n\r\n");
				for(int i = 0; i < reDec.size();i++) {
					if (reDec.get(i).isDooplicate == true) {
						fw.write(reDec.get(i).toString());
						fw.write("\r\n");
					}
				}
			}
			if (refrances.size() == 0) {
				fw.write("no references\r\n");
			}
			else { //there are refrances
				fw.write("\r\nreferences\r\n\r\n");
				for(int i = 0; i < refrances.size(); i++) {
					fw.write(refrances.get(i).toString());
					fw.write("\r\n");
				}
			}
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public class RedecNode {
		public int blockNum;
		public boolean isDooplicate = false;
		public Vector<SubRedecNode> redecs = new Vector<SubRedecNode>();
		
		public String toString() {
			String ret = "B" + blockNum + ":\r\n";
			
				for (int i = 0; i < redecs.size(); i++) {
					ret += redecs.get(i).toString();
				}
				
				return ret;
		}
	}
	
	public class SubRedecNode {
		public String name ="";
		public Vector<Integer> lines = new Vector<Integer>();
		
		public String toString() {
			String ret = "";
			if (lines.size() > 1) {
				ret = name + ": ";
			
ret += lines.get(0);
				for (int i = 1; i < lines.size(); i++) {
					ret += ", " + lines.get(i);
				}
			ret += "\r\n";
			}
			return ret;
	}
	}
	
	public class RefNode {
		public Token token;
		public int blockNum;
		public int decLine = -1;
		
		public String toString() {
			if (decLine == -1)
				return token.getTokenAttribute() + ":\r\nref line: " + token.getTokenLine() + "\r\nref block: " + blockNum + "\r\ndec line: undeclared \r\n";
			
			return token.getTokenAttribute() + ":\r\nref line: " + token.getTokenLine() + "\r\nref block: " + blockNum + "\r\ndec line: " + decLine + "\r\n";
		}
	}

}
