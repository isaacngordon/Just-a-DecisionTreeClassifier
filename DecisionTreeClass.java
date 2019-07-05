import java.util.*;

public class DecisionTreeClass {
	private class DecisionTreeNode{
		public ArrayList<Integer> data_list; // list of data IDs
		public int opt_fea_type = -1;	// 0 if continuous, 1 if categorical
		public int opt_fea_id = -1;		// the index of the optimal feature
		public double opt_fea_thd = Double.NEGATIVE_INFINITY;	// the optimal splitting threshold 
																// for continuous feature
		public int opt_accuracy = Integer.MIN_VALUE; // the improvement (gain) if split based on the optimal feature
		public boolean is_leaf = true;		// is it a leaf
		//public LabelList myLabelList;
		public int majority_class = -1;		// class prediction based on majority vote
		public int num_accurate = -1;		// number of accurate data using majority_class
		public DecisionTreeNode parent = null;		// parent node
		public ArrayList<DecisionTreeNode> children = new ArrayList<DecisionTreeNode>(); 	// list of children when split
		public int depth = 0;
		
		//public double ent; 
		
		public DecisionTreeNode(ArrayList<Integer> d_list, int m_class, int n_acc){
			data_list = new ArrayList<Integer>(d_list);
			majority_class = m_class;
			num_accurate = n_acc;
			//ent = entropy(this);
		}
		
		public DecisionTreeNode(ArrayList<Integer> d_list, int m_class, int n_acc, DecisionTreeNode p){
			data_list = new ArrayList<Integer>(d_list);
			majority_class = m_class;
			num_accurate = n_acc;
			//ent = entropy(this);
			parent = p;
		}
		
//		public double calcGain(int idx_of_fea){
//			double g = 0;
//			double hs = entropy(parent);
//			return g;
//		}
		
//		public double entropy(DecisionTreeNode n){
//			double e = 1;
//			
//			return e;
//		}
	}
	
	private class DTHeap{
		private ArrayList<DecisionTreeNode> heap;
		
		public DTHeap(){
			heap = new ArrayList<DecisionTreeNode>();
		}
		
		protected void insert(DecisionTreeNode n){
			heap.add(n);
			siftUp();
		}
		
	    public DecisionTreeNode delete() throws NoSuchElementException {
	    	        if (heap.size() == 0) {
	    	            throw new NoSuchElementException();
	    	        }
	    	        if (heap.size() == 1) {
	    	            return heap.remove(0);
	    	        }
	    	        DecisionTreeNode hold = heap.get(0);
	    	        heap.set(0, heap.remove(heap.size()-1));
	    	        siftDown();
	    	        return hold;
	    	    }
		
	    private void siftUp() {
	        int k = heap.size() - 1;
	        while (k > 0) {
	            int p = (k-1)/2;
	            DecisionTreeNode item = heap.get(k);
	            DecisionTreeNode parent = heap.get(p);
	            if (item.opt_accuracy > parent.opt_accuracy) {
	                // swap
	                heap.set(k, parent);
	                heap.set(p, item);
	                 
	                // move up one level
	                k = p;
	            } else {
	                break;
	            }
	        }
	    }
		
	    private void siftDown() {
	        int k = 0;
	        int l = 2*k+1;
	        while (l < heap.size()) {
	            int max=l, r=l+1;
	            if (r < heap.size()) { // there is a right child
	                if (heap.get(r).opt_accuracy > heap.get(l).opt_accuracy) {
	                    max++;
	                }
	            }
	            if (heap.get(k).opt_accuracy < heap.get(max).opt_accuracy) {
	                    // switch
	                    DecisionTreeNode temp = heap.get(k);
	                    heap.set(k, heap.get(max));
	                    heap.set(max, temp);
	                    k = max;
	                    l = 2*k+1;
	            } else {
	                break;
	            }
	        }
	    }
	    
	    public int size() {
	        return heap.size();
	    }
	     
	    public boolean isEmpty() {
	        return heap.isEmpty();
	         
	    }
	     
	    public String toString() {
	        return heap.toString();
	    }
	    
	}
	
	public DataWrapperClass train_data;
	public int max_height;
	public int max_num_leaves;
	public int height;
	public int num_leaves = 0;
	public DecisionTreeNode root;
	public DTHeap myHeap = new DTHeap();
	
	// constructor, build the decision tree using train_data, max_height and max_num_leaves
	public DecisionTreeClass(DataWrapperClass t_d, int m_h, int m_n_l){
		train_data = t_d;
		max_height = m_h;
		max_num_leaves = m_n_l;
		
		// FILL IN
		//
		// create the root node, use all data_ids [0..N-1]
		ArrayList<Integer> all_data_ids = new ArrayList<Integer>();
		for(int i = 0; i < train_data.num_data; i++){
			all_data_ids.add(i);
		}
		
		//--System.out.println("all_data_ids have been set.");
		
		// find the majority class, also how many accurate using the majority class
		
		LabelList l = new LabelList(train_data.labels, all_data_ids);
		int[] m_a = l.majLabel();
		
		root = new DecisionTreeNode(all_data_ids, m_a[0], m_a[1]);
		
		//--System.out.println("root created");
		
		// if (the majority class is correct for all data,) {
		//		no improvement possible 
		// 		the optimal accuracy improvement = 0
		// }
		if(m_a[1] == train_data.num_data){
			root.opt_accuracy = 0;
		}
		
        // 		for (each feature ){
		//			if (categorical){
		//			}
		
		//			if (continuous){
		//			}
		//				sum up number of accurate predictions for all sub-lists as the score
		//			}
		//		}
		
		else{ 
			//find the optimal feature to split
			double[] fscores = new double[train_data.num_features];
			double[] ths = new double[train_data.num_features];
			if(train_data.num_cat_fea > 0){
				root.opt_fea_type = 1;
				for(int i = 0; i < fscores.length; i++){
					fscores[i] = calculateFScore_cat(all_data_ids, i);
				}
			}
			else{
				root.opt_fea_type = 0;
				for(int i = 0; i < fscores.length; i++){
					 double[] score_and_th = calculateFScore_cont(all_data_ids, i);
					 fscores[i] = score_and_th[0];
					 ths[i] = score_and_th[1];
				}
			}
			// 		find the feature with the largest score (best total num of accurate prediction after splitting)
			root.opt_fea_id = idx_max(fscores);
			
			if(train_data.num_cont_fea > 0){
				root.opt_fea_thd = ths[root.opt_fea_id];
			}
			
		//
		// 		optimal accuracy improvement = the difference between the best total num of accurate prediction after splitting
		//								and the number of accurate prediction using the majority class of the current node (IG)
			root.opt_accuracy = (int) (fscores[root.opt_fea_id] - root.num_accurate);	
		}
		
		
		// put the root node and the optimal accuracy improvement into a max-heap
		root.depth = 1;
		num_leaves++;
		myHeap.insert(root);
		
		
		//--System.out.println("Root has been prepared. It's heap time.");
		
		// while (the heap is not empty)
		// 		extract the maximum entry (the leaf node with the maximal optimal accuracy improvement) from the heap
		//		if (the optimal accuracy improvement is zero (no improvement possible))
		//			break;
		
		while(!myHeap.isEmpty()){
			 //--System.out.println("line 224 hit");
			
			
			DecisionTreeNode work = myHeap.delete();
			ArrayList<ArrayList<Integer>> children_data_lists = new ArrayList<ArrayList<Integer>>();
			
			if(work.opt_accuracy == 0) break;
			else{
				//Split by opt_fea into children nodes, store in work.childern
				try{
					if(work.opt_fea_type == 1){ //if categorical
						children_data_lists = splitData("CAT", work.data_list, work.opt_fea_id);
					}
					else if(work.opt_fea_type == 0){ //if continuous
						children_data_lists = splitData("CONT", work.data_list, work.opt_fea_id);		
					}
				} catch (Exception e) {
					System.out.println("There is something with the current work node's opt_fea_type.");
					e.printStackTrace();
				  }
				
				
				for(int i = 0; i < children_data_lists.size(); i++){
					int[] n_m_a = majClass(children_data_lists.get(i));
					DecisionTreeNode child = new DecisionTreeNode(children_data_lists.get(i), n_m_a[0], n_m_a[1]);
					child.parent = work;
					if(child.num_accurate == work.data_list.size()){
						child.opt_accuracy = 0;
					}
					else{

						double[] cscores = new double[train_data.num_features];
						double[] cths = new double[train_data.num_features];
						if(train_data.num_cat_fea > 0){
							child.opt_fea_type = 1;
							for(int q = 0; q < cscores.length; q++){
								cscores[q] = calculateFScore_cat(all_data_ids, q);
							}
						}
						else{
							child.opt_fea_type = 0;
							for(int q = 0; q < cscores.length; q++){
								 double[] score_and_th = calculateFScore_cont(all_data_ids, q);
								 cscores[q] = score_and_th[0];
								 cths[q] = score_and_th[1];
							}
						}
						child.opt_fea_id = idx_max(cscores);
						
						if(train_data.num_cont_fea > 0){
							child.opt_fea_thd = cths[child.opt_fea_id];
						}
						child.opt_accuracy = (int) (cscores[child.opt_fea_id] - child.num_accurate);	
					}
					child.depth = child.parent.depth + 1;
					
					
					work.children.add(child);
					num_leaves++;
					myHeap.insert(child);
					
				}//end of individual child
				
			}//all children have been made

			work.is_leaf = false;
			num_leaves--;
			
			if(num_leaves > max_num_leaves){
				System.out.println("Exceeded max_num_leaves");
				break;
			}
			if(work.children.get(0).depth > max_height){
				System.out.println("Exceeded max_height.");
				break;
			}
		}//heap is empty

		//TODO:
		//		else{ 
		//			split the node
		//			create children based on the optimal feature and split (each sub-list creates one child)
		//			for each child node
		//				find its optimal accuracy improvement (and the optimal feature) (as you do for the root)
		//				put the node into the max-heap [this part is recursive?]
		//		}
		//		if (the number of leaves > max_num_leaves)
		//			break;
		//		if (the height > max_height)
		//			break;
		
		
		
		
		
	}
	
	
	public ArrayList<Integer> predict(DataWrapperClass test_data) throws Exception{
		// for (each data in the test_data){
		//	   starting from the root,
		//	   at each node, go to the right child based on the splitting feature
		//	   continue until a leaf is reached
		//	   assign the label to the data based on the majority-class of the leaf node
		// }
		// return the list of predicted labels
		
		ArrayList<Integer> predictions = new ArrayList<Integer>();
		
		if(test_data.num_cont_fea > 0){
			ArrayList<ArrayList<Double>> data = test_data.continuous_features;
			ArrayList<Double> obs = new ArrayList<Double>();
			for(int n = 0; n < test_data.num_data; n++){
				obs = data.get(n);
				DecisionTreeNode curr = root;
				while(!curr.is_leaf){
					//TODO: change curr to correct child based on CONTINOUS data
					//Take curr's opt_fea opt_fea_thd
					//see if obs's value at opt_fea is greater than or equal to opt_fea_thd
					//if true, goto child #1 [index2], if false goto child #2 [index 1]
					 
					//--System.out.println("line 343 hit");
					
					 if(obs.get(curr.opt_fea_id) >= curr.opt_fea_thd){
						 curr = curr.children.get(0);
					 }
					 else{
						 curr = curr.children.get(1);
					 }
					
				}
				predictions.add(curr.majority_class);
				
			}
		} 
		else{
			ArrayList<ArrayList<Integer>> data = test_data.categorical_features;
			ArrayList<Integer> obs = new ArrayList<Integer>();
			for(int n = 0; n < test_data.num_data; n++){
				obs = data.get(n);
				DecisionTreeNode curr = root;
				while(!curr.is_leaf){
					
					//--System.out.println("line 365 hit");
					
					//TODO: change curr to correct child based on CATEGORICAL data
					//take curr's opt_fea
					//get obs's value for feature opt_fea
					//find child with data matching the above value (only need to check first data point)
					int obs_v = obs.get(curr.opt_fea_id);
					for(int idx = 0; idx < curr.children.size(); idx++){
						DecisionTreeNode c = curr.children.get(idx);
						ArrayList<Integer> firstData = train_data.categorical_features.get(c.data_list.get(0));
						int c_v = firstData.get(curr.opt_fea_id);
						if(obs_v == c_v){
							curr = c;
						}
					}
					if(curr == root) throw new Exception("YO, THE NECESSARY CHILD DOES NOT EXIST??!! Good thing I'm stopping an infinite loop.");

				}
				predictions.add(curr.majority_class);
				
			}
		}
		
		return predictions;
	}
	
	public double calculateFScore_cat(ArrayList<Integer> data_indices, int featureIndex){
		ArrayList<ArrayList<Integer>> subGroups = splitData("CAT", data_indices, featureIndex);
		double score = 0;
		
		for(int i = 0; i < subGroups.size(); i++){
			int[] m_a = new int[2];
			m_a = majClass(subGroups.get(i));
			score = score + m_a[1];
		}
		
		return score;
	}
	
	public double[] calculateFScore_cont(ArrayList<Integer> data_indices, int featureIndex){
		//TODO:
		//				sort the data based on the continuous feature
		//				find the optimal threshold to split the data_list into two sub-lists
		//				for (each of sub-list){
		//					find the majority class
		//					compute the number of accurate prediction using this majority class

		double score = 0;
		
		ArrayList<ArrayList<Integer>> subGroups = splitData("CONT", data_indices, featureIndex);
		
		//for each subgroup, calculate its score
		for(int i = 0; i < subGroups.size(); i++){			
			int[] m_a = new int[2];							
			m_a = majClass(subGroups.get(i));
			score = score + m_a[1];
		}
		double[] score_and_th = new double[2];
		
		score_and_th[0] = score;
		score_and_th[1] = bestThresh(data_indices, featureIndex);
		return score_and_th;
	}
	
	public ArrayList<ArrayList<Integer>> splitData(String fea_type, ArrayList<Integer> data_indices, int featureIndex){
		
		
		ArrayList<ArrayList<Integer>> subGroups = new ArrayList<ArrayList<Integer>>();						//create a list of subgroups, made up of label-index lists
		
		if(fea_type.equalsIgnoreCase("CAT")){
			ArrayList<Integer> possible_values = new ArrayList<Integer>();										//make a list for possible feature values
			for(int la = 0; la < data_indices.size(); la++){													//for every observation index (la) in this dataset
				ArrayList<Integer> dataRow = train_data.categorical_features.get(data_indices.get(la));			//pull the data row at index la
				int v = dataRow.get(featureIndex);																//get the value at the feature we are observing
				if(!possible_values.contains(v)) possible_values.add(v);										//if that value is new, add it to possible_values
			}
			
			
			for(int i = 0; i < possible_values.size(); i++){													//for every possible_value, lets make a group for subgroups
				ArrayList<Integer> group = new ArrayList<Integer>();											//new index group list
				for(int la = 0; la < data_indices.size(); la++){												//for every datarow index la
					ArrayList<Integer> dataRow = train_data.categorical_features.get(data_indices.get(la));		//pull up that datarow
					int v = dataRow.get(featureIndex);															// look at the feature value
					if(v == possible_values.get(i)){															//if it is an instance of the value we are looking for
						group.add(data_indices.get(la));													//add it to our new group
					}
				}
				subGroups.add(group);																			//add that new group to the subGroup list
				
			}
		}
		
		else if(fea_type.equalsIgnoreCase("CONT")){
			double best_thresh = bestThresh(data_indices, featureIndex);
			
			ArrayList<Integer> groupUnder = new ArrayList<Integer>();
			ArrayList<Integer> groupOver = new ArrayList<Integer>();
		
			for(int la = 0; la < data_indices.size(); la++){													//for every observation index (la) in this dataset
				ArrayList<Double> dataRow = train_data.continuous_features.get(data_indices.get(la));			//pull the data row at index la
				double v = dataRow.get(featureIndex);																//get the value at the feature we are observing
				if(v >= best_thresh) groupOver.add(data_indices.get(la));																		//if that value is greater than current maxVal, add to over group
				if(v < best_thresh) groupUnder.add(data_indices.get(la));																		//if that value is less than current minVal, add to under group
			}
			subGroups.add(groupOver);
			subGroups.add(groupUnder);
			
		}
		else throw new IllegalArgumentException(fea_type + " is not a feature type");
		
		return subGroups;
	}
	
	public double bestThresh(ArrayList<Integer> data_indices, int featureIndex){
		double minVal = Double.MAX_VALUE;
		double maxVal = Double.MIN_VALUE;
		
		for(int la = 0; la < data_indices.size(); la++){													//for every observation index (la) in this dataset
			ArrayList<Double> dataRow = train_data.continuous_features.get(data_indices.get(la));			//pull the data row at index la
			double v = dataRow.get(featureIndex);																//get the value at the feature we are observing
			if(v > maxVal) maxVal = v;																		//if that value is greater than current maxVal, make it maxVal
			if(v < minVal) minVal = v;																		//if that value is less than current minVal, make it minVal
		}
		
		double bt = minVal + .00001;
		double bestT = bt;
		int bscore = 0;
		
		while(bt < maxVal){
			
			//--System.out.println("line 495 hit");
			
			ArrayList<Integer> groupUnder = new ArrayList<Integer>();
			ArrayList<Integer> groupOver = new ArrayList<Integer>();
		
			for(int la = 0; la < data_indices.size(); la++){													//for every observation index (la) in this dataset
				ArrayList<Double> dataRow = train_data.continuous_features.get(data_indices.get(la));			//pull the data row at index la
				double v = dataRow.get(featureIndex);																//get the value at the feature we are observing
				if(v > bt) groupOver.add(data_indices.get(la));																		//if that value is greater than current maxVal, add to over group
				if(v < bt) groupUnder.add(data_indices.get(la));																		//if that value is less than current minVal, add to under group
			}
			
			int[] over = majClass(groupOver);
			int[] under = majClass(groupUnder);
			
			int score = over[1] + under[1];
			
			if(score > bscore){
				bscore = score;
				bestT = bt;
			}
			
			bt+= .00001;
		}
		
		
		
		return bestT;
	}
	
	public int[] majClass(ArrayList<Integer> data_indices){
		LabelList ll = new LabelList(train_data.labels, data_indices);
		return ll.majLabel();
	}
	
	public int idx_max(double[] a){
		double max = 0;
		int idx = -1;
		
		for(int i = 0; i < a.length; i++){
			if(a[i] > max){
				idx = i;
				max = a[i];
			}
		}
		
		return idx;
	}
	
}


