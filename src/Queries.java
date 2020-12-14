import java.io.IOException;
import java.util.Vector;

public class Queries {
	/**
	@constructor
	@input: The queries String and nodes.
	@description: A function that sends every query to the right algorithm  and finally sent the answers to output file.
	**/

	public Queries(String queries, Nodes nodes) throws IOException {
		Vector<String> answer = new Vector<String>();

		String [] lines = queries.toString().split("\n"); //lines
		for (int i = 0; i < lines.length; i++) {
			//			System.out.println("line: "+lines[i]);
			char num = lines[i].charAt(lines[i].length()-1);
			String line = lines[i].substring(2, lines[i].length()-3);
			if(num == '1') {
				//add to vector
				System.out.println("\n***Algorithm 1 \n");
				AnsweringQueries aq = new AnsweringQueries(line, nodes);
				answer.add(String.format("%.5f", aq.getAnswer())+","+aq.getSum()+","+aq.getMul());
			}
			if(num == '2' || num == '3') { 
				if(num == '2')System.out.println("\n***Algorithm 2 \n");
				if(num == '3')System.out.println("\n***Algorithm 3 \n");

				VariableElimination ve = new VariableElimination(line, nodes, num);
				answer.add(String.format("%.5f", ve.getAnswer())+","+ve.getSum()+","+ve.getMul());
			}
		}
		

		Parser.output(answer);


	}
}
