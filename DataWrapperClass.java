import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;

public class DataWrapperClass {
	public int num_data;		// number of data (N) - num of rows
	public int num_features;	// number of features (D) - num of columns
	public int num_classes;		// number of different classes (K) - number of different labels
	
	public int num_cont_fea; 	// number of continuous features
	public int num_cat_fea;		// number of categorical features
	public ArrayList<ArrayList<Double> > continuous_features = new ArrayList<ArrayList<Double>>();// only continuous features
	public ArrayList<ArrayList<Integer> > categorical_features = new ArrayList<ArrayList<Integer>>();	// only categorical features
	public ArrayList<Integer> labels = new ArrayList<Integer>();	// labels of all data
	
	// read features and labels from input files
	public DataWrapperClass(String feature_fname, String label_fname) throws FileNotFoundException{
		// FILL IN
		// read feature and label file
		// store feature in continuous_/categorical_features, 
		// store labels 
		// if file name starts with 'CAT_', all features are categorical
		// otherwise, all features are continuous
		
		Scanner scanf = new Scanner(new File(feature_fname));
		Scanner scanl = new Scanner(new File(label_fname));
		
		num_features = 0;
		num_data = 0;

		
		//if we are using CAT files
		if(feature_fname.contains("CAT")){
			//create a string from each line of text in feature_fname,
			//then break it up into tokens, and store each token in an ArrayList called row
			//when the line is done being parsed, add row to categorical_features
			
			String line = scanf.nextLine();
			StringTokenizer t = new StringTokenizer(line);
			ArrayList<Integer> row = new ArrayList<Integer>();
			
			//do it all once outside the general loop, to count number of features
			while(t.hasMoreTokens()){
				row.add(Integer.parseInt(t.nextToken()));
				num_features++;
			}
			categorical_features.add(row);
			num_data++;
			
			
			while(scanf.hasNextLine()){
				line = scanf.nextLine();
				t = new StringTokenizer(line);
				row = new ArrayList<Integer>();
				while(t.hasMoreTokens())
					row.add(Integer.parseInt(t.nextToken()));
				categorical_features.add(row);
				num_data++;
			}
			num_cat_fea = num_features;
			num_cont_fea = 0;
		}
		
		else{
			//create a string from each line of text in feature_fname,
			//then break it up into tokens, and store each token in an ArrayList called row
			//when the line is done being parsed, add row to categorical_features
			String line = scanf.nextLine();
			StringTokenizer t = new StringTokenizer(line);
			ArrayList<Double> row = new ArrayList<Double>();
			
			//do it all once outside the general loop, to count number of features
			while(t.hasMoreTokens()){
				row.add(Double.parseDouble(t.nextToken()));
				num_features++;										//count number of features
			}
			continuous_features.add(row);
			num_data++;
			
			
			while(scanf.hasNextLine()){
				line = scanf.nextLine();
				t = new StringTokenizer(line);
				row = new ArrayList<Double>();
				while(t.hasMoreTokens())
					row.add(Double.parseDouble(t.nextToken()));
				continuous_features.add(row);
				num_data++;
			}
			num_cont_fea = num_features;
			num_cat_fea = 0;
		}
		
		//label_fname only has one int per line, making it easier to parse
		//go through the file, and if there is another int, then add it to ArrayList labels
		//if this label is new, then increment num_classes
		num_classes = 0;
		while(scanl.hasNextInt()){
			int n = scanl.nextInt();
			if(!labels.contains(n)) num_classes++;
			labels.add(n);
		}
		
		//close files
		scanf.close();
		scanl.close();
	}
	
	// static function, compare two label lists, report how many are correct
	public static int evaluate(ArrayList<Integer> l1, ArrayList<Integer> l2){
		int len = l1.size();
		assert len == l2.size();	// length should be equal
		assert len > 0;				// length should be bigger than zero
		int ct = 0;
		for(int i = 0; i < len; ++i){
			if(l1.get(i).equals(l2.get(i))) ++ct;
		}
		return ct;
	}
	
	// static function, compare two label lists, report score (between 0 and 1)
	public static double accuracy(ArrayList<Integer> l1, ArrayList<Integer> l2){
		int len = l1.size();
		assert len == l2.size();	// label lists should have equal length
		assert len > 0;				// lists should be non-empty
		double score = evaluate(l1,l2);
		score = score / len;		// normalize by divided by the length
		return score;
	}
}
