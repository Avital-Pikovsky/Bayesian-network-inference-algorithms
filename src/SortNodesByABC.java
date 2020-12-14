import java.util.Comparator;

public class SortNodesByABC  implements Comparator<Nodes.Node> {

	@Override
	public int compare(Nodes.Node n1, Nodes.Node n2) {
		return n1.getName().charAt(0) - n2.getName().charAt(0);
	}

}
