import java.text.DecimalFormat;
import java.util.ArrayList;

public class CPT {

	private String [][] CPT_values;
	private char CPTnode;

	//Getters 

	public char getCPTnode() {
		return CPTnode;
	}

	public String[][] getCPT_values() {
		return CPT_values;
	}


	/**
	@Constructor
	@input: All the details that need to build the CPT.
	@description: A constructor that build the CPT.
	**/
	
	public CPT(char name, String[] values, ArrayList<Nodes.Node> parents, String cpt) {
		this.CPTnode = name;
		if(parents == null) {
			this.CPT_values = new String[2][values.length];
			for(int j = 0; j < values.length; j++) {
				CPT_values[0][j] = values[j];
			}
		}
		else {
			this.CPT_values = new String[rows(values, parents)][cols(values, parents)];

			int i;
			//variables names.
			for(i = 0; i < parents.size(); i++)
				CPT_values[0][i] = String.valueOf(parents.get(i).getName());

			for(int j = 0; j < values.length; j++) {
				CPT_values[0][i+j] = values[j];
			}
		}	


		String[] block_rows = cpt.split("\n");
		//for the rest of matrix
		initMat(values, block_rows, 0, 1, 0);
	}	

	/**
	@input: Node's values and Node's parents ArrayList.
	@description: A function that calculates how many rows the CPT needs.
	**/
	
	private int rows(String[] values, ArrayList<Nodes.Node> parents) {
		int size = 1;
		for(int i=0; i<parents.size(); i++) {
			size *= parents.get(i).getValues().length;
		}
		return size + 1; //1 for names.
	}

	/**
	@input: Node's values and Node's parents ArrayList.
	@description: A function that calculates how many cols the CPT needs.
	**/
	
	private int cols(String[] values, ArrayList<Nodes.Node> parents) {
		return parents.size() + values.length;
	}
	
	/**
	@input: All the details that need to init the CPT.
	@description: A function that init the CPT (except for the rows of names).
	**/
	
	private void initMat(String[] values, String[] block_rows, int block, int rows, int cols) {
		while (block < block_rows.length && rows < CPT_values.length) {
			String[] s = block_rows[block].split(",");
			cols = 0;
			double sum_val = 0.0;
			for (int i = 0; i < s.length; i++) {
				if(s[i].charAt(0) == '=') {
					for (int j = 0; j < CPT_values[0].length; j++) {
						if(s[i].substring(1).equals(CPT_values[0][j])) {
							i++;
							this.CPT_values[rows][j] = s[i];
							sum_val += Double.parseDouble(s[i]);
							cols++;
							j = CPT_values[0].length;
						}

					}
				}
				else {					
					this.CPT_values[rows][cols] = s[i];
					cols++;
				}

			}
			DecimalFormat df = new DecimalFormat("#0.###");
			this.CPT_values[rows][cols] = String.valueOf(df.format(1.0-(sum_val)));
			rows++;
			block++;
		}
	}
	//TODO - delete this before sent
		public void printCPT() {
		System.out.println();
		System.out.println(CPTnode);
		for (int i = 0; i < CPT_values.length; i++) {
			for (int j = 0; j < CPT_values[0].length; j++) {
				System.out.print(CPT_values[i][j]+ " , ");
			}
			System.out.println();
		}

	}

}
