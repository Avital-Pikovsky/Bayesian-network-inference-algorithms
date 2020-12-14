import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

public class Nodes {
	
	private static Vector<Node> nodes = new Vector<Node>();
	
	//Getters and setters

	public Vector<Node> getNodes() {
		return nodes;
	}
	
	//Constructor
	public Nodes(String input) {
		String [] node = input.toString().split("Var "); //split by new lines
		for(int i = 1; i < node.length; i++)
		{
			Node newNode = new Node(node[i]);
			nodes.add(newNode);
		}
		
	}
	/**
	@input: The Node's parent.
	@description: A function that checks if the parent exists at the Node's vector.
	@output: the Node if exist, null if not.
	**/
	
	public static Node convert(char par) {
		for(int i = 0; i < nodes.size(); i++) {
			if(nodes.get(i) != null && nodes.get(i).getName() == par)
				return nodes.get(i);
		}
		return null;
	}
	
	public class Node{
		
		private char name;
		private String[] values;
		private String tag = "null";
		private ArrayList<Node> parents;
		private CPT cpt;
		
		//Getters and setters
		
		public char getName() {
			return name;
		}
		public void setName(char name) {
			this.name = name;
		}
		public String[] getValues() {
			return values;
		}
		public void setValues(String[] values) {
			this.values = values;
		}
		public String getTag() {
			return tag;
		}
		public void setTag(String tag) {
			this.tag = tag;
		}
		public ArrayList<Node> getParents() {
			return parents;
		}
		public void setParents(ArrayList<Node> parents) {
			this.parents = parents;
		}
		public CPT getCpt() {
			return cpt;
		}
		public void setCpt(CPT cpt) {
			this.cpt = cpt;
		}
		
		
		
		/**
		@Constructor
		@input: String of Node's details.
		@description: A constructor that divides and initializes every detail of the Node.
		**/
		
		public Node(String vars) {
			this.parents= new ArrayList<>();
			String[] cpt = vars.split("CPT:\n");
			String [] var = cpt[0].split("\n");
			this.name = var[0].charAt(0); //Var X
			this.values = var[1].substring(8).split(","); //Values: true, false

			if(!var[2].substring(9).equals("none")){
				String [] par = var[2].substring(9).split(",");//Parents: Y,Z,Q
				for(int i = 0; i < par.length; i++) {
					Node n = convert(par[i].charAt(0));
					parents.add(n);
				}
			}
			else {
				this.parents = null;
			}
			
			this.cpt = new CPT(this.name, this.values, this.parents, cpt[1]);
			this.cpt.printCPT();
		}
		
	}
}

