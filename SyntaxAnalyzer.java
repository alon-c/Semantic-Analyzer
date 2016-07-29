import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.Stack;
import java.util.Vector;

public class SyntaxAnalyzer {
	/* properties */
	private Vector<Token> tokens = null;
	private String[][][] llOneTable;

	public SyntaxAnalyzer(String grammarFile, String inputFile, Vector<Token> tokens) {
		this.tokens = tokens;
		
		llOneTableGenerator(grammarFile);
	}

	private void llOneTableGenerator(String grammarFile) {
		String buffer;
		try {
			buffer = new Scanner(new File(grammarFile)).useDelimiter("\\Z").next();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("cannot find the file: " + grammarFile);
			e.printStackTrace();
			buffer = null;
			return;
		}

		String[] bufferLines = buffer.split("\r\n");
		int i = 0;
		for (i = 0; i < bufferLines[0].length(); i++) {
			if (bufferLines[0].charAt(i) == '=')
				break;
		}
		bufferLines[0] = bufferLines[0].substring(i +1, bufferLines[0].length());

		int rows = 0, columns = 2;
		for (i = 0; i< bufferLines[0].length(); i++) {
			if (bufferLines[0].charAt(i) == ',')
				columns++;
		}

		for (i = 0; i < bufferLines.length; i++) {
			if (bufferLines[i] == "" || bufferLines[i] == null || bufferLines[i].charAt(0) == '#') {
				continue;
			}
			rows++;
		}

		String[][][] tableTmp = new String[rows][columns][];

		tableTmp[0][0] = new String[1];
		tableTmp[0][0][0] = "";

		// parse none-terminals
		int rowTmp = 1;
		for (int k = 1; k < bufferLines.length; k++) {
			if (bufferLines[k] == "" || bufferLines[k] == null || bufferLines[k].charAt(0) == '#')
				continue;

			int id = 0;
			for (id =0; id < bufferLines[k].length(); id++) {
				if (bufferLines[k].charAt(id) == '=')
					break;
			}
			String[] nT = new String[1];
			nT[0] = bufferLines[k].substring(0, id);
			tableTmp[rowTmp][0] = nT;
			bufferLines[k] = bufferLines[k].substring(id+1, bufferLines[k].length());
			rowTmp++;
		}

		int line = 0;
		for (i = 0; i < bufferLines.length; i++) {
			if (bufferLines[i] == "" || bufferLines[i] == null || bufferLines[i].charAt(0) == '#') {
				continue;
			}
			int start = 0, col = 1;
			for (int j = 0; j < bufferLines[i].length(); j++) {
				if (bufferLines[i].charAt(j) == ',') {
					String tmp = bufferLines[i].substring(start, j);
					if (tmp == null || tmp == "" || tmp.length() == 0)
						tmp = "eps";

					tableTmp[line][col] = tmp.split(";");

					start = j +1;
					col++;
				}
			}
			String tmp0 = "";
			if (start == bufferLines[i].length()) {
				tmp0 = "eps";
			}
			else {
				tmp0 = bufferLines[i].substring(start, bufferLines[i].length());
				if (tmp0 == null || tmp0 == "" || tmp0.length() == 0)
					tmp0 = "eps";
			}

			tableTmp[line][col] = tmp0.split(";");

			line++;
		}

		this.llOneTable = tableTmp;
	}

	public Node yyLL1Parse() {
		//start analyzing
		Stack<Node> stack = new Stack<Node>();
		int id = 1;
		Node root = new Node();
		root.id = id;
		root.data = new String(llOneTable[1][0][0]);
		id++;
		stack.push(root);
		Node pNode;
		int tokenId = 0;
		do {
			pNode = stack.peek();
			if (pNode.data.equals("eps")) {
				stack.pop();
				continue;
			}
			if (tokens.get(tokenId).getTokenType().equals(pNode.data) == true) {
				Token tokenTmp = new Token(tokens.get(tokenId).getTokenType(), tokens.get(tokenId).getTokenAttribute(), tokens.get(tokenId).getTokenLine());
				pNode.token = tokenTmp;
				tokenId++;
				stack.pop();
			}
			else {
				int i = 1, j = 1;
				for (i = 1; i < llOneTable.length; i++) {
					if (llOneTable[i][0][0].equals(pNode.data)) {
						for (j = 1; j < llOneTable[0].length; j++) {
							if (llOneTable[0][j][0].equals(tokens.get(tokenId).getTokenType())) {
								stack.pop();
								id += llOneTable[i][j].length; 
								for (int k = llOneTable[i][j].length -1; k >= 0; k--) {
									Node newNode = new Node();
									newNode.id = id - (llOneTable[i][j].length - k);
									newNode.data = llOneTable[i][j][k];
									stack.push(newNode);
									pNode.edges.add(newNode);
								}
								break;
							}
						}
						break;
					}
				}
				if (i == llOneTable.length || j == llOneTable[0].length)
					break;
				//notFound = true;
			}
			//if (stack.empty() == true)
			//break;
		} while(tokenId < tokens.size() && stack.empty() == false);

		if (stack.empty() == false || tokenId  < tokens.size()) {
			return null;
		}

		return root;
	}

	public boolean startAnalyze() {
		Node root = yyLL1Parse();
		if (root == null) {
			//System.out.println("input is wrong according to LL(1) table");
			return false;
		}

		
		return true;
		/*
		Stack<Node> stack = new Stack<Node>();

		if (root != null)
			stack.push(root);

		try {
			Node pNode;
			FileWriter fw = new FileWriter(outputFile);
			if (root != null) {
				fw.write("digraph G {\r\n");
				while(stack.empty() == false) {
					pNode = stack.pop();
					fw.write(pNode.toString());
					for (int k = 0; k < pNode.edges.size(); k++) {
						stack.push(pNode.edges.get(k));
					}
				}
				fw.write("}\r\n");
			}
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}

}
