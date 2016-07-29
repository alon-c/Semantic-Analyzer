import java.util.Vector;

public class Node {
	/* properties */
	public int id;
	public String data;
	public Token token = null;
	public Vector<Node> edges = new Vector<Node>(1);

	public String toString() {
		String ret = "";
		for (int k = edges.size( )-1; k >= 0; k--)
			ret += data + "_" +id + " -> " + edges.get(k).data + "_" + edges.get(k).id + "\r\n";

		return ret;
	}

}
