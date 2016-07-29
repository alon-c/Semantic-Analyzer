public class ParserMain {

	public static void main(String args[]) {
		if (args.length != 2) {
			System.out.println("Usage: ParserMain <LL(1) table file> <text to analize file>");
			System.exit(-1);
		}

		String path = "";
		if ((System.getProperty("os.name").equals("Linux")) || (System.getProperty("os.name").equals("Mac OS X")))
			path = System.getProperty("user.dir") + "/";
		else
			path = System.getProperty("user.dir") + "\\";

		String tableFile = path + args[0], textFile = path + args[1];

		SemanticAnalyzer semantic = new SemanticAnalyzer(tableFile, textFile);
		semantic.startAnalyze();
	}

}
