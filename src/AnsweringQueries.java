import java.awt.desktop.QuitResponse;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Vector;

public class AnsweringQueries {
	private int sum = 0;
	private int mul = 0;
	private double answer = 0.0;


	public int getSum() {
		return sum;
	}
	public int getMul() {
		return mul;
	}
	public double getAnswer() {
		return answer;
	}

	/**
	@Constructor
	@input: A query that fits algorithm 1 and Nodes.
	@description: A constructor that calculating the queries: the number of connections,
	the number of multiples and the answer to the query.
	 **/
	public AnsweringQueries(String query, Nodes nodes) {

		double answer2 = 0;
		String [] element = query.split(",|\\|");
		Nodes.Node query_var = null;

		Vector<Nodes.Node> vars = nodes.getNodes();
		for (int i = 0; i < vars.size(); i++) {
			if(vars.get(i).getName() == element[0].charAt(0))
				query_var = vars.get(i);
			for (int j = 0; j < element.length; j++) {
				if(vars.get(i).getName() == element[j].charAt(0)) {
					vars.get(i).setTag(element[j].substring(2));
				}
			}

		}	
		if(check_null(vars) == null) {
			answer = find_wparents(query_var);
			System.out.println("answer: "+answer);
		}
		else {
		double answer1 = find_vars(vars);
		sum--;

		Vector<Nodes.Node> normal = nodes.getNodes();
		for (int i = 0; i < normal.size(); i++) {
			for (int k = 0; k < element.length; k++) {
				if(normal.get(i).getName() == element[k].charAt(0)) {
					normal.get(i).setTag(element[k].substring(2));
				}
			}
			if(normal.get(i).getName() == element[0].charAt(0)) {
				for (int j = 0; j < normal.get(i).getValues().length; j++) {
					if(!normal.get(i).getTag().equals(normal.get(i).getValues()[j])) {
						normal.get(i).setTag(normal.get(i).getValues()[j]);
						answer2 += find_vars(normal);
						sum--;
					}
				}
			}
		}

		DecimalFormat df = new DecimalFormat("#0.#####");
		answer = Double.valueOf(df.format(answer1/(answer1 + answer2)));
		sum++;
		}
		for (int i = 0; i < vars.size(); i++) {
			vars.get(i).setTag("null");
		}
	}

	/**
	@input: Nodes vector.
	@description: A function that check the Node's tag and return null if there are no Nodes whose tag is null.
	 **/

	public Nodes.Node check_null(Vector<Nodes.Node> vars) {
		for (int i = 0; i < vars.size(); i++) {
			if(vars.get(i).getTag().equals("null"))
				return vars.get(i);
		}
		return null;
	}
	/**
	@input: Nodes vector.
	@description: A function that builds the calculates lines and sent them to calculation.
	 **/

	public double find_vars(Vector<Nodes.Node> vars) {
		double ans = 0.0;

		Nodes.Node n = check_null(vars);
		if(n == null) {
			sum++;
			return calculation(vars);
		}

		for (int j = 0; j < n.getValues().length; j++) {
			n.setTag(n.getValues()[j]);
			ans += find_vars(vars);
			n.setTag("null");
		}
		return ans;
	}

	/**
	@input: Nodes vector.
	@description: A function that calculates lines (search for them in the CPT).
	 **/

	public double calculation(Vector<Nodes.Node> vars) {
		double ans = 1.0;
		for (int i = 0; i < vars.size(); i++) {

			if(vars.get(i).getParents() == null) {

				int j = -1;
				boolean col = false;
				while(!col) {
					j++;
					col = vars.get(i).getValues()[j].equals(vars.get(i).getTag());
				}
				if(col) {
					ans *= Double.valueOf(vars.get(i).getCpt().getCPT_values()[1][j]);
					mul++;
				}
			}
			else {//has parents
				ans *= find_wparents(vars.get(i));
				mul++;
			}
		}
		mul--;
		return ans;
	}
	/**
	@input: Node that has parents.
	@description: A function that calculates the node's parents.
	 **/
	private double find_wparents(Nodes.Node node) {
		String [] parents = new String[node.getParents().size()];
		for (int i = 0; i < node.getParents().size(); i++) {
			parents[i] = node.getParents().get(i).getTag();
		}
		for (int i = 1; i < node.getCpt().getCPT_values().length; i++) {
			int numOfPar = 0;
			for (int j = 0; j < parents.length; j++) {
				if(node.getCpt().getCPT_values()[i][j].equals(parents[j])) {
					numOfPar++;
				}
			}
			if(numOfPar == parents.length) {
				for (int k = 0; k < node.getValues().length; k++) {
					if(node.getCpt().getCPT_values()[0][k+parents.length].equals(node.getTag())) {
						return Double.valueOf(node.getCpt().getCPT_values()[i][k+parents.length]);
					}
				}
			}
		}
		return 0;
	}
}



