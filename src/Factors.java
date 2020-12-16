
import java.util.ArrayList;
import java.util.Vector;
public class Factors {

	private Vector<Factor> factors = new Vector<Factor>();

	public Vector<Factor> getFactors() {
		return factors;
	}
	public void setFactors(Vector<Factor> factors) {
		this.factors = factors;
	}
	/**
	@Constructor
	@input: All the nodes and query node name.
	@description: A Constructor that builds factors by nodes.
	 **/
	public Factors(Vector<Nodes.Node> nodes, Nodes.Node query_node) {
		for (int i = 0; i < nodes.size(); i++) {
			Factor f = new Factor(nodes.get(i), query_node);
			if(f.getFactor_values().length > 2)
				factors.add(f);
		}
	}
	public static class Factor {

		private String [][] factor_values;
		private String name = "";

		public String[][] getFactor_values() {
			return factor_values;
		}

		public void setFactor_values(String[][] factor_values) {
			this.factor_values = factor_values;
		}
		public String getName() {
			return name;
		}


		public void setName(String name) {
			this.name = name;
		}
		/**
	 	@Constructor
		@input: The new Factor name, number of rows and number of columns.
		@description: A Constructor that build new factor.
		 **/
		public Factor(String name, int rows, int cols) {
			this.name = name;
			this.factor_values = new String[rows][cols];

		}
		/**
		@Constructor
		@input: Node and query node.
		@description: A Constructor that build new factor by Node's CPT.
		 **/
		public Factor(Nodes.Node node, Nodes.Node query_node) {
			this.name += node.getName();

			if (node.getParents() == null) {
				if(!node.getTag().equals("not_null") && !node.getName().equals(query_node.getName())) {

					int rows = 2; //for Node name
					int cols = 2;
					this.factor_values = new String[rows][cols];

					for (int j = 0; j < node.getCpt().getCPT_values()[0].length; j++) {
						factor_values[0][0] = String.valueOf(node.getName()); //Node name
						factor_values[0][1] = null;
						if(node.getCpt().getCPT_values()[0][j].equals(node.getTag())) {
							factor_values[1][0] = node.getCpt().getCPT_values()[0][j];
							factor_values[1][1] = node.getCpt().getCPT_values()[1][j];
						}
					}
				}
				else { //if(node.getTag().equals("not_null")


					int rows = node.getValues().length + 1; //for Node name
					int cols = 2;
					this.factor_values = new String[rows][cols];

					for (int j = 0; j < factor_values[0].length; j++) {
						for (int i = 0; i < factor_values.length-1; i++) {

							factor_values[0][0] = String.valueOf(node.getName()); //Node name
							factor_values[i+1][j] = node.getCpt().getCPT_values()[j][i];
						}
					}
				}
			}
			else {//(node.getParents() != null)

				if(!node.getTag().equals("not_null") && !node.getName().equals(query_node.getName())) {
					ArrayList<Nodes.Node> var_list = new ArrayList<>();

					int rows = node.getCpt().getCPT_values().length;
					int cols = node.getCpt().getCPT_values()[0].length - (node.getValues().length-1);
					this.factor_values = new String[rows][cols];

					int j;
					for (int i = 0; i < rows; i++) {
						for (j = 0; j < node.getParents().size(); j++) {
							factor_values[i][j] = node.getCpt().getCPT_values()[i][j];

						}
						factor_values[0][j] = String.valueOf(node.getName());

						for (int k = node.getParents().size(); k < node.getCpt().getCPT_values()[0].length; k++) {
							if(node.getTag().equals(node.getCpt().getCPT_values()[0][k])){
								factor_values[i][j] = node.getCpt().getCPT_values()[i][k];
							}
						}
					}
					for (int i = 0; i < factor_values[0].length-1; i++) {
						var_list.add(Nodes.convert(factor_values[0][i]));
					}
				this.factor_values = check_parents(factor_values, query_node,var_list); 
				}
				else {//node.getTag().equals("not_null") 
					ArrayList<Nodes.Node> var_list = new ArrayList<>();


					int rows = node.getCpt().getCPT_values().length*node.getValues().length - node.getValues().length + 1;
					int cols = (node.getCpt().getCPT_values()[0].length - node.getValues().length) + 2;
					this.factor_values = new String[rows][cols];


					// Title
					for(int i = 0; i < node.getParents().size(); i++) {
						factor_values[0][i] = String.valueOf(node.getParents().get(i).getName());
						var_list.add(node.getParents().get(i));
					}
					for(int i = node.getParents().size(); i < cols-1; i++) {
						factor_values[0][i] = String.valueOf(node.getName());
						var_list.add(node);
					}


					// Body
					int values = node.getValues().length;
					for(int times = 0; times < values; times ++) { // How many times its will duplicate

						//copy all the values
						int possibilties = node.getCpt().getCPT_values().length - 1; //cpt without title
						for(int parent = 0; parent < node.getParents().size(); parent++) { // parents values
							for(int j = 0; j < possibilties; j++) {
								factor_values[1+j + times*possibilties][parent] = node.getCpt().getCPT_values()[1+j][parent]; 
							}
						}

						for(int i = 1; i <= possibilties; i++) {
							// copy values names
							factor_values[times*possibilties + i][node.getParents().size()] = 
									node.getCpt().getCPT_values()[0][node.getParents().size() + times].toString();
							// copy values
							factor_values[times*possibilties + i][node.getParents().size() + 1] = 
									node.getCpt().getCPT_values()[i][node.getParents().size() + times].toString();	

						}
					}

				this.factor_values = check_parents(this.factor_values, query_node,var_list);
				}
			}
		}
		/**
		@param var_list 
		 * @input: The Factor's values and query node.
		@description: A function that delete evidence columns and rows.
		 **/
		private String[][] check_parents(String[][] factor_values, Nodes.Node query_node, ArrayList<Nodes.Node> var_list) {
			for (Nodes.Node  n : var_list) {
				
//				Nodes.Node n = Nodes.convert(factor_valuess[0][i]);


				if(!n.getTag().equals("not_null") && !n.getName().equals(query_node.getName())) {

					int row = ((factor_values.length - 1)/n.getValues().length) + 1;
					int col = factor_values[0].length - 1;
					String [][] f_values = new String[row][col];

					int bad_var = 0;
					for (int j = 0; j < factor_values[0].length - 1; j++) {
						if(factor_values[0][j].equals(n.getName()))
							bad_var = j;
					}
					//Title
					for (int j = 0; j < f_values[0].length-1; j++) {
						if(j < bad_var)
							f_values[0][j] = factor_values[0][j];
						else {
							f_values[0][j] = factor_values[0][j+1];
						}
					}
					//Body
					int r = 0;
					for (int j = 1; j < factor_values.length; j++) {
						if(factor_values[j][bad_var].equals(n.getTag())) {
							r++;
							for (int j2 = 0; j2 < factor_values[0].length-1; j2++) {
								if(j2 < bad_var)
									f_values[r][j2] = factor_values[j][j2];
								else f_values[r][j2] = factor_values[j][j2+1];
							}
						}
					}
					factor_values = f_values;
				}
			}
			return factor_values;

			
		}
	}

	public void printFactors() {
		for (Factor factor : factors) {


			System.out.println();
			System.out.println(factor.name);
			for (int i = 0; i < factor.factor_values.length; i++) {
				for (int j = 0; j < factor.factor_values[0].length; j++) {
					System.out.print(factor.factor_values[i][j]+ " , ");
				}
				System.out.println();
			}
		}
	}
}
