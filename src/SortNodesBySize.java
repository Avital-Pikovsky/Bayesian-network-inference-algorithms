import java.util.Comparator;

public class SortNodesBySize implements Comparator<Nodes.Node> {

	@Override
	public int compare(Nodes.Node n1, Nodes.Node n2) {
		return n1.getCpt().getCPT_values().length - n2.getCpt().getCPT_values().length;
	}

}
