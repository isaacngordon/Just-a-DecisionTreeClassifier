import java.io.FileNotFoundException;
import java.util.*;

public class DTMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// parameters: train_feature_fname, train_label_fname, 
		// 			   test_feature_fname, test_label_fname,
		//			   max_height(int), max_num_leaves(int)
		
		long startTime, endTime;
		startTime = System.currentTimeMillis();
		
		if(args.length < 6){
			System.out.println("java DTMain train_feature_fname train_label_fname test_feature_fname test_label_fname max_height max_num_leaves");
			return;
		}
		try{
			System.out.println("check 0");
			String train_feature_fname = args[0];
			String train_label_fname = args[1];
			String test_feature_fname = args[2];
			String test_label_fname = args[3];
			int max_height = Integer.parseInt(args[4]);
			int max_num_leaves = Integer.parseInt(args[5]);
			
			//--System.out.println("check 1");
			
			DataWrapperClass train_data = new DataWrapperClass(train_feature_fname, train_label_fname);
			DataWrapperClass test_data = new DataWrapperClass(test_feature_fname, test_label_fname);
			
			//--System.out.println("check 2");
			
			DecisionTreeClass my_dt = new DecisionTreeClass(train_data, max_height, max_num_leaves);
			
			//--System.out.println("check 4");
			
			ArrayList<Integer> prediction = my_dt.predict(test_data);
			
			//--System.out.println("check 4");
			
			double final_accuracy = DataWrapperClass.accuracy(prediction, test_data.labels);
			endTime = System.currentTimeMillis();
			System.out.println("Test Accuracy = " + final_accuracy);
			System.out.println("Total Run Time: " + (endTime - startTime));
			
			
	    } 
//		catch (FileNotFoundException e){
//	    	System.out.println("NULL: File not found");
//	    	e.printStackTrace();
//	        return;
//	    } catch (IllegalArgumentException e){
//	    	System.out.println("NULL: Illegal Argument");
//	    	e.printStackTrace();
//	        return;
//	    	
//	    } 
		catch (Exception e) {
	    	System.out.println("NULL: Something is wrong");
	    	e.printStackTrace();
	        return;
	    }
		
	}

}
