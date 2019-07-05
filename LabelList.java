import java.util.ArrayList;
import java.util.NoSuchElementException;

public class LabelList {
	
	public class Node{
		private int labelValue;
		private int count;
		private Node next;

		
		public Node(int l){
			labelValue = l;
			count = 1;
		}
		
		public Node(){
			
		}
		
		
		public int getCount(){
			return count;
		}
		
		public int getLabelValue(){
			return labelValue;
		}
		
		public void increment(){
			count++;
		}
		
		public Node getNext(){
			return next;
		}
		
		public void setNext(Node x){
			next = x;
		}
		
	}
	
	public Node head = new Node();
	public ArrayList<Integer> list = new ArrayList<Integer>();
	
	public LabelList(ArrayList<Integer> fullList, ArrayList<Integer> data_indices){
		
		
		for(int i = 0; i < data_indices.size(); i++)
			this.add(fullList.get(data_indices.get(i)));
	}
	
	public void add(int labelVal){
		if(list.contains(labelVal)) increment(find(labelVal));
		else{
			//insert at head, add to local list
			Node n = new Node(labelVal);
			n.setNext(head.getNext());
			head.setNext(n);
			list.add(labelVal);
		}
	}
	
	
	public void increment(Node n){
		n.increment();
	}
	
	public Node find(int labelVal){
		if(!list.contains(labelVal)) throw new NoSuchElementException("This label does not exist in the list of labels.");
		
		Node p = this.head.getNext();
		while(p.getLabelValue() != labelVal){
			//--System.out.println("LL: line 75 hit");
			p = p.getNext();
		}
		
		return p;
	}	
	
	public ArrayList<Integer> getListOfLabels(){
		return list;
	}
	
	public int[] majLabel(){
		Node p = head;
		int maj = -1;
		int maxCount = -1;
		do{
			p = p.getNext();
			//System.out.println("LL: line 89 hit");
			
			if(p.getCount() > maxCount){
				maxCount = p.getCount();
				maj = p.getLabelValue();
			}
			
		}while(p.getNext() != null);
		int[] r = new int[2];
		r[0] = maj;
		r[1] = maxCount;
		return r;
		
	}
	

}
