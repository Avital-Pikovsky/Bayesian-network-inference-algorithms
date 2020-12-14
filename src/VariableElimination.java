import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;


public class VariableElimination{
	private int sum = 0;
	private int mul = 0;
	private double answer = 0.0;
	Nodes.Node query_node = null;

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
	@input: The Query, nodes and number of algorithm (2 or 3).
	@description: Building the factors, sort them and send them to the right functions.
	**/
	public VariableElimination(String query, Nodes nodes, char num) {

		String [] element = query.split(",|\\|");

		Vector<Nodes.Node> vars = nodes.getNodes();

		for (int i = 0; i < vars.size(); i++) {
			for (int j = 0; j < element.length; j++) {
				if(vars.get(i).getName().equals(element[0].substring(0, element[0].indexOf("="))))
					query_node = vars.get(i);
				if(vars.get(i).getName().equals(element[j].substring(0, element[0].indexOf("=")))) {
					find_parents(vars.get(i));
				}
			}
		}
		Vector<Nodes.Node> removes = new Vector<>();

		for (int i = 0; i < vars.size(); i++) {
			if(vars.get(i).getTag().equals("null")) {
				removes.add(vars.get(i));
				vars.remove(i);
			}
		}
		Vector<Nodes.Node> hidden_vector = new Vector<>();

		for (int i = 0; i < vars.size(); i++) {
			for (int j = 0; j < element.length; j++) {
				if(vars.get(i).getName().equals(element[j].substring(0, element[j].indexOf("=")))) {

					vars.get(i).setTag(element[j].substring(element[j].indexOf("=") + 1));
					System.out.println(vars.get(i).getName()+" "+(element[j].substring(element[j].indexOf("=") + 1)));
				}
			}
			if(vars.get(i).getTag().equals("not_null")) {
				hidden_vector.add(vars.get(i));
			}

		}
		//algorithm 2: sort by ABC.
		if(num == '2') {
			Collections.sort(hidden_vector, new SortNodesByABC());
		}

		//algorithm 3: sort by number of rows.
		if(num == '3') {
			Collections.sort(hidden_vector, new SortNodesBySize());
		}
		Factors f = new Factors(vars, query_node);
		f.printFactors();
		if(hidden_vector.isEmpty()) {
			for (int i = 1; i < f.getFactors().firstElement().getFactor_values().length; i++) {
				if(f.getFactors().firstElement().getFactor_values()[i][0].equals(query_node.getTag())) {
					answer = Double.valueOf(f.getFactors().firstElement().getFactor_values()[i][1]);
				}
			}
		}
		else {

			for (int i = 0; i < hidden_vector.size(); i++) {
				Factors.Factor new_factor = (pre_elimination(hidden(f, hidden_vector.get(i)), hidden_vector.get(i)));
				f.getFactors().add(new_factor);
			}

			if(f.getFactors().size() > 1)
				answer = normalization(last_join(f));
			else answer = normalization(f.getFactors().firstElement());
			System.out.println("answerrrrrrrrrrrrrrrr: "+answer);
		}
		f.getFactors().removeAllElements();
		//return the removes nodes
		for (int i = 0; i < removes.size(); i++) {
			vars.add(removes.get(i));
		}

		//init tags to null after the algorithm
		for (int i = 0; i < vars.size(); i++) {
			vars.get(i).setTag("null");
		}

	}
	/**
	@input: Node.
	@description: A function that find the ancestor query or evidence nodes
	and tag them by 'not_null'.
	**/
	public void find_parents(Nodes.Node node) {
		node.setTag("not_null");

		if(node.getParents() != null) {
			for (int j = 0; j < node.getParents().size(); j++) {
				find_parents(node.getParents().get(j));
			}
		}
	}
	/**
	@input: Factor and Node.
	@description: A function that choose every time hidden Factor, send the Factors that the hidden Factor
	existing in them to right functions to build new Factor from all this factors.
	@output: new Factor.
	**/
	public Factors.Factor hidden(Factors f, Nodes.Node node) {
		System.out.println("\nStart print factors");
		f.printFactors();
		System.out.println("\nFinish print factors\n");

		Set<Factors.Factor> hidden_factors = new HashSet<Factors.Factor>();

		for (int i = 0; i < f.getFactors().size(); i++) {
			for (int j = 0; j < f.getFactors().get(i).getFactor_values()[0].length; j++) {
				if(f.getFactors().get(i).getFactor_values()[0][j] != null && f.getFactors().get(i).getFactor_values()[0][j].equals(String.valueOf(node.getName()))) {
					hidden_factors.add(f.getFactors().get(i));
				}
			}
		}
		f.getFactors().removeAll(hidden_factors);

		List<Factors.Factor> list_hidden = new ArrayList<Factors.Factor>();
		list_hidden.addAll(hidden_factors);

		Factors.Factor new_fac;
		while(list_hidden.size() > 0) {

			if(list_hidden.size() == 1)
				return list_hidden.get(0);

			else if(list_hidden.size() == 2) {
				new_fac = pre_join(list_hidden.get(0), list_hidden.get(1));
				list_hidden.removeAll(list_hidden);
				return new_fac;
			}
			else {//more than 2 factors in the list
				new_fac = permutations(list_hidden);
				list_hidden.add(new_fac);
			}
		}
		return null;
	}
	/**
	@input: Hidden Factors list.
	@description: A function that choose every time two Factors that the return Factor will be the smallest one 
	(if there is more then one Factor - choose the Factors by smallest ASCII value of the return Factor's variables).
	@output: new Factor.
	**/
	private Factors.Factor permutations(List<Factors.Factor> list_hidden) {
		for (int i = 0; i < list_hidden.size(); i++) {
			System.out.println("print all factors: " + i +" "+list_hidden.get(i).getName());
		}
		int min = Integer.MAX_VALUE;
		Factors.Factor a = null, b = null;
		for (int i = 0; i < list_hidden.size() - 1; i++) {
			for (int j = i+1; j < list_hidden.size(); j++) {
				if(check_common(list_hidden.get(i),list_hidden.get(j)) < min) {
					min = check_common(list_hidden.get(i),list_hidden.get(j));
					a = list_hidden.get(i);
					b = list_hidden.get(j);
				}
				if(check_common(list_hidden.get(i),list_hidden.get(j)) == min) {
					if(ASCII_sum(a, b, list_hidden.get(i), list_hidden.get(j)) == 1) {
						min = check_common(list_hidden.get(i),list_hidden.get(j));
						a = list_hidden.get(i);
						b = list_hidden.get(j);
					}
				}
			}
		}
		list_hidden.remove(a);
		list_hidden.remove(b);

		return pre_join(a, b);
	}
	/**
	@input: 4 Factors.
	@description: A function that choose two Factors by smallest ASCII value of the return Factor's variables).
	@output: Chosen two Factors.
	**/
	public int ASCII_sum(Factors.Factor a1, Factors.Factor a2, Factors.Factor b1, Factors.Factor b2) {
		int ascii1 = 0, ascii2 = 0;
		for (int i = 0; i < a1.getFactor_values()[0].length; i++) {
			if(a1.getFactor_values()[0][i] != null) {
				ascii1 += (int)a1.getFactor_values()[0][i].charAt(0);
			}
		}
		for (int i = 0; i < a2.getFactor_values()[0].length; i++) {
			if(a2.getFactor_values()[0][i] != null) {
				ascii1 += (int)a2.getFactor_values()[0][i].charAt(0);
			}
		}
		for (int j = 0; j < b1.getFactor_values()[0].length; j++) {
			if(b1.getFactor_values()[0][j] != null) {
				ascii2 += (int)b1.getFactor_values()[0][j].charAt(0);
			}
		}
		for (int j = 0; j < b2.getFactor_values()[0].length; j++) {
			if(b2.getFactor_values()[0][j] != null) {
				ascii2 += (int)b2.getFactor_values()[0][j].charAt(0);
			}
		}
		if(ascii1 < ascii2)
			return 0;
		else return 1;
	}
	/**
	@input: 2 Factors.
	@description: A function that sum the variables of the two Factors and sum the rows number of each variable.
	@output: Number of row.
	**/
	public int check_common(Factors.Factor a, Factors.Factor b) {
		int rows = 1;
		Set<String> together = new HashSet<String>();

		Vector<String> title_a = new Vector<String>();
		Vector<String> title_b = new Vector<String>();

		for (int i = 0; i < a.getFactor_values()[0].length - 1; i++) { 
			title_a.add(a.getFactor_values()[0][i]);
		}
		for (int j = 0; j < b.getFactor_values()[0].length - 1; j++) {
			title_b.add(b.getFactor_values()[0][j]);
		}

		together.addAll(title_a);
		together.addAll(title_b);

		List<String> together_list = new ArrayList<String>();
		together_list.addAll(together);

		for (int i = 0; i < together_list.size(); i++) {
			rows *= Nodes.convert(together_list.get(i)).getValues().length;
		}			
		return rows;
	}
	/**
	@input: 2 Factors.
	@description: A function that builds the new Factor before join the 2 old Factors and send them to join function.
	@output: New Factor after join.
	**/
	public Factors.Factor pre_join(Factors.Factor a, Factors.Factor b) {
		Set<String> title = new HashSet<String>();

		Vector<String> title_a = new Vector<String>();
		Vector<String> title_b = new Vector<String>();

		//add a vars
		for (int i = 0; i < a.getFactor_values()[0].length - 1; i++) {
			title_a.add(a.getFactor_values()[0][i]);
		}
		//add b vars
		for (int i = 0; i < b.getFactor_values()[0].length - 1; i++) {
			title_b.add(b.getFactor_values()[0][i]);
		}
		title.addAll(title_a);
		title.addAll(title_b);
		List<String> title_list = new ArrayList<String>();
		title_list.addAll(title);

		//new factor rows size
		int rows = 1;
		for (int i = 0; i < title.size(); i++) {
			rows *= Nodes.convert(title_list.get(i)).getValues().length;
		}
		rows++;
		int cols = title.size() + 1;

		Factors.Factor join_factor = new Factors.Factor(a.getName()+b.getName(), rows, cols);

		//title
		for (int i = 0; i < title_list.size(); i++) {
			join_factor.getFactor_values()[0][i] = title_list.get(i);
		}
		//body
		int timess = join_factor.getFactor_values().length;

		for (int j = 0; j < join_factor.getFactor_values()[0].length - 1; j++) {
			int currRow = 1;
			int values_num = Nodes.convert(join_factor.getFactor_values()[0][j]).getValues().length;
			timess /= values_num;
			int run = (join_factor.getFactor_values().length - 1)/(values_num*timess);
			for (int r = 0; r < run; r++) {
				for (int k = 0; k < values_num; k++) {
					for (int t = 0; t < timess; t++) {
						join_factor.getFactor_values()[currRow++][j] = Nodes.convert(join_factor.getFactor_values()[0][j]).getValues()[k];

					}
				}
			}
		}
		//multiply the values
		HashMap<String, String> search = new HashMap<>();
		for (int j = 0; j < title_list.size(); j++) {
			search.put(title_list.get(j), "");
		}

		for (int i = 1; i < join_factor.getFactor_values().length; i++) {
			for (int j = 0; j < join_factor.getFactor_values()[0].length; j++) {

				search.replace(join_factor.getFactor_values()[0][j], join_factor.getFactor_values()[i][j]);
			}

			double ans = join(a, b, search);
			join_factor.getFactor_values()[i][join_factor.getFactor_values()[0].length-1] = String.valueOf(ans);
		}
		//print the new factor
		System.out.println();
		System.out.println(join_factor.getName());
		for (int i = 0; i < join_factor.getFactor_values().length; i++) {
			for (int j = 0; j < join_factor.getFactor_values()[0].length; j++) {
				System.out.print(join_factor.getFactor_values()[i][j]+ " , ");
			}
			System.out.println();
		}
		return join_factor;
	}
	/**
	@input: 2 Factors and HashMap.
	@description: A function that builds the values column (for every row).
	@output: value for each row.
	**/
	public double join(Factors.Factor a, Factors.Factor b, HashMap<String, String> search) {
		HashMap<String, String> search_a = new HashMap<>();
		HashMap<String, String> search_b = new HashMap<>();
		double ans = 1;
		boolean flag_a = true ;
		for (int i = 1; i < a.getFactor_values().length; i++) {
			flag_a = true ;
			for (int j = 0; j < a.getFactor_values()[0].length-1; j++) {
				search_a.put(a.getFactor_values()[0][j], a.getFactor_values()[i][j]);
			}
			for(Entry<String, String> entry : search_a.entrySet()) {
				if(!(entry.getValue().equals(search.get(entry.getKey())))){

					flag_a=false;
				}
			}
			if(flag_a) {
				ans = Double.valueOf(a.getFactor_values()[i][a.getFactor_values()[0].length-1]);			
				flag_a=false;
			}
		}
		boolean flag_b=true ;
		for (int i = 1; i < b.getFactor_values().length; i++) {
			flag_b = true ;
			for (int j = 0; j < b.getFactor_values()[0].length -1 && flag_b; j++) {
				search_b.put(b.getFactor_values()[0][j], b.getFactor_values()[i][j]);
			}
			for(Entry<String, String> entry : search_b.entrySet()) {
				if(!(entry.getValue().equals(search.get(entry.getKey())))){
					flag_b = false;
				}
			}
			if(flag_b) {
				ans *= Double.valueOf(b.getFactor_values()[i][b.getFactor_values()[0].length-1]);
				mul++;
				return Double.valueOf(ans);
			}
		}
		return 0;
	}
	/**
	@input: Factor and Node.
	@description: A function that builds the new Factor before eliminition the old Factor and send it to elimination function.
	@output: New Factor after elimination.
	**/
	public Factors.Factor pre_elimination(Factors.Factor factor, Nodes.Node node) {

		int rows = (factor.getFactor_values().length - 1)/(node.getValues().length) + 1;
		int cols = factor.getFactor_values()[0].length - 1;

		Factors.Factor elimination_factor = new Factors.Factor(factor.getName(), rows, cols);

		int col = 0;
		for (int i = 0; i < factor.getFactor_values()[0].length - 1; i++) {
			if(factor.getFactor_values()[0][i].equals(node.getName()))
				col = i;
		}
		for (int i = 0; i < elimination_factor.getFactor_values()[0].length; i++) {
			if(i < col) {
				elimination_factor.getFactor_values()[0][i] = factor.getFactor_values()[0][i];
			}
			else elimination_factor.getFactor_values()[0][i] = factor.getFactor_values()[0][i+1];
		}
		int times = elimination_factor.getFactor_values().length;

		for (int j = 0; j < elimination_factor.getFactor_values()[0].length - 1; j++) {
			int currRow = 1;
			int values_num = Nodes.convert(elimination_factor.getFactor_values()[0][j]).getValues().length;
			times /= values_num;
			int run = (elimination_factor.getFactor_values().length - 1)/(values_num*times);
			for (int r = 0; r < run; r++) {
				for (int k = 0; k < values_num; k++) {
					for (int t = 0; t < times; t++) {
						elimination_factor.getFactor_values()[currRow++][j] = Nodes.convert(elimination_factor.getFactor_values()[0][j]).getValues()[k];
					}
				}
			}
		}
		HashMap<String, String> search = new HashMap<>();
		for (int j = 0; j < elimination_factor.getFactor_values()[0].length-1; j++) {
			search.put(elimination_factor.getFactor_values()[0][j], "");
		}
		for (int i = 1; i < elimination_factor.getFactor_values().length; i++) {
			for (int j = 0; j < elimination_factor.getFactor_values()[0].length; j++) {
				search.replace(elimination_factor.getFactor_values()[0][j], elimination_factor.getFactor_values()[i][j]);
			}
			double ans = elimination(factor, search);
			elimination_factor.getFactor_values()[i][elimination_factor.getFactor_values()[0].length-1] = String.valueOf(ans);
		}

		//print the new factor
		System.out.println();
		System.out.println(elimination_factor.getName());
		for (int i = 0; i < elimination_factor.getFactor_values().length; i++) {
			for (int j = 0; j < elimination_factor.getFactor_values()[0].length; j++) {
				System.out.print(elimination_factor.getFactor_values()[i][j]+ " , ");
			}
			System.out.println();
		}
		return elimination_factor;
	}
	/**
	@input: Factor and HashMap.
	@description: A function that builds the values column (for every row).
	@output: value for each row.
	**/
	public double elimination(Factors.Factor fac, HashMap<String, String> search) {
		HashMap<String, String> search_fac = new HashMap<>();
		double ans = 0;

		boolean flag_a = true ;
		for (int i = 1; i < fac.getFactor_values().length; i++) {
			flag_a = true ;
			for (int j = 0; j < fac.getFactor_values()[0].length-1; j++) {
				search_fac.put(fac.getFactor_values()[0][j], fac.getFactor_values()[i][j]);
			}
			for(Entry<String, String> entry : search.entrySet()) {
				if(!(entry.getValue().equals(search_fac.get(entry.getKey())))){
					flag_a=false;
				}
			}
			if(flag_a) {
				ans += Double.valueOf(fac.getFactor_values()[i][fac.getFactor_values()[0].length-1]);	
				sum++;
				flag_a=false;
			}
		}
		sum--;
		return Double.valueOf(ans);
	}
	/**
	@input: Factors.
	@description: A function that do the last join (without send the Factor to elimination).
	@output: Factor.
	**/
	public Factors.Factor last_join(Factors factor) {
		factor.printFactors();

		String name = factor.getFactors().get(0).getName();
		int rows = factor.getFactors().get(0).getFactor_values().length;
		int cols = factor.getFactors().get(0).getFactor_values()[0].length;
		Factors.Factor new_factor = new Factors.Factor(name, rows, cols);

		for (int i = 1; i < new_factor.getFactor_values().length; i++) {
			new_factor.getFactor_values()[i][0] = factor.getFactors().get(0).getFactor_values()[i][0];
			new_factor.getFactor_values()[i][1] = "1.0";

		}
		for (int i = 0; i < factor.getFactors().size(); i++) {
			for (int j = 1; j < new_factor.getFactor_values().length; j++) {
				mul++;
				new_factor.getFactor_values()[j][1]
						= String.valueOf(Double.valueOf(new_factor.getFactor_values()[j][1]) * Double.valueOf(factor.getFactors().get(i).getFactor_values()[j][1]));
			}
			mul--;
		}
		return new_factor;	
	}
	/**
	@input: Factor.
	@description: A function that do the normalization and calculate the answer.
	@output: double.
	**/
	public double normalization(Factors.Factor factor) {
		double answer1 = 0, answer2 = 0, ans = 0;

		String tag = query_node.getTag();

		for (int i = 1; i < factor.getFactor_values().length; i++) {
			answer2 += Double.valueOf(factor.getFactor_values()[i][1]);

			sum++;
			if(factor.getFactor_values()[i][0].equals(tag)) {
				answer1 = Double.valueOf(factor.getFactor_values()[i][1]);
			}
		}
		sum--;
		System.out.println("moane: "+answer1);

		System.out.println("mehane: "+answer2);
		ans = (answer1/answer2);
		DecimalFormat df = new DecimalFormat("#0.#####");
		System.out.println("anserr: "+ans);
		return Double.valueOf(df.format(ans));

	}
}
