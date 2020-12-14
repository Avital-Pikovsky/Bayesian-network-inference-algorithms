import java.io.*;
import java.util.Vector;


public class Parser {
	
	/**
	@input: The name of the input file.
	@description: A function that read lines from the input file.
	@output: String Builder with input file lines.
	**/
	
	public static StringBuilder readLines(String input) throws Exception {
        
		FileReader inputFile = new FileReader(input);
        BufferedReader reader = new BufferedReader(inputFile);
        String line;
        StringBuilder content = new StringBuilder();
        
        //ready - Tells whether this stream is ready to be read.
        //A buffered character stream is ready if the buffer is not empty,
        //or if the underlying character stream is ready.
        
        while(reader.ready()){
        	line = reader.readLine();
        	if(line.isEmpty())
        		continue;
        	content.append(line+"\n");
        }
        reader.close();
        return content;
	}
	
	/**
	@input: Vector with queries answer.
	@description: A function that write to output file the answer of the queries.
	**/
    public static void output(Vector<String> ans)throws IOException {
        File file = new File("output.txt");
        file.createNewFile();
        if (!file.canWrite()) throw new IOException("Erorr- can't write to file");
        if (file.exists()) file.delete();
        FileWriter fileWriter = new FileWriter(file, true);
        for (int i = 0; i < ans.size(); i++)
            fileWriter.write(ans.get(i) + "\n");
        fileWriter.close();
    }	

}
