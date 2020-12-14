
public class Ex1 {

	public static void main(String[] args) throws Exception {

		StringBuilder lines = Parser.readLines("input2.txt");

		String [] two = lines.toString().split("Queries\n");
		Nodes nodes = new Nodes(two[0]);

		new Queries(two[1], nodes);


	}

}
