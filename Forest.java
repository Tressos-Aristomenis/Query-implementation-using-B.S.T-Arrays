import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


class Node {
	public double dData;           // data item (key)
	public int id;           	   // data item
	public Node leftChild;         // this node's left child
	public Node rightChild;        // this node's right child
}

public class Forest {
	public long nComp;
	private Node[] roots;
	private Records records;
	
	// Κατασκευάζει τον πίνακα roots που περιέχει n ρίζες ΔΔΑ, και το αντικείμενο records
	public Forest(int m, int n) {
		roots = new Node[n];
		records = new Records(m, n);
	}
	
	
	public void load(String filename, int lines, int subDim) {
		BufferedReader br = null;
		String sLine = "";
		int nCntLines = 0;
		double dd = 0;
		try {
			br = new BufferedReader(new FileReader(filename));
			br.readLine(); // Skip first line of file (header line)
			while ((sLine=br.readLine()) != null && nCntLines < lines) {	
				sLine = sLine.trim();	
				String[] arLine = sLine.split("\t");
				int id = nCntLines; 
				double[] rec = new double[roots.length];
				for (int i=0 ; i < arLine.length ; i++) {					
					if ((i > 4) && (i < 4 + subDim + 1)) {
						String s = arLine[i];
						if (s.trim().isEmpty()) 
							dd = 0;
						else
							dd = Double.parseDouble(s);
						insert(i-5, id, dd);
						rec[i-5] = dd;
					}
				}
				records.setRecord(id, rec);
				nCntLines++;
			}
		} catch (FileNotFoundException fnf) {
			fnf.printStackTrace();
		} catch (IOException io) {
			io.printStackTrace();
		}
		finally {
			try {
				if (br != null)
					br.close();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	// Υπολογίζει και επιστρέφει την ευκλείδια απόσταση μεταξύ των σημείων p1 και p2
	private double euclidean(double[] p1, double[] p2) {
		if (p1.length != p2.length)
			return -1;
		
		double sum = 0;
		for (int i=0 ; i < p1.length ; i++) {
			sum += Math.pow(p1[i] - p2[i], 2);
		}
		return Math.sqrt(sum);
	}
	
	
	public void insert(int j, int id, double dd) {
		Node newNode = new Node();    
		newNode.id = id;          
		newNode.dData = dd;
		
		if(roots[j] == null)
			roots[j] = newNode;
		else {
			Node current = roots[j];
			Node parent;
			while(true) {
				parent = current;
				if(dd < current.dData)  {
					current = current.leftChild;
					if(current == null) {             
						parent.leftChild = newNode;
						return;
					}
				}  
				else  {
					current = current.rightChild;
					if(current == null)  {
						parent.rightChild = newNode;
						return;
					}
				}
			}
		}
	}
	
	
	private void postOrderTraversal(ArrayList<Integer> records, Node localroot, final double min, final double max) {
		if (localroot == null) {	// if current node is null, return.
			return;
		}
		else {
			nComp++;		// increment the comparison counter.
			if (localroot.dData < min) {
				postOrderTraversal(records, localroot.rightChild, min, max);
				// if the current data is under the bound [min, max], go only to the rightChild. On the left there will be no data that satisfy
				// the condition. All of them will be less than min, because it's a BST.
			}
			if (localroot.dData >= min && localroot.dData <= max) {
				postOrderTraversal(records, localroot.leftChild, min, max);
				postOrderTraversal(records, localroot.rightChild, min, max);
				// if data is between min and max, first traverse the left sub-tree and then the right sub-tree.
				// add the IDs of these data in the list.
				records.add(localroot.id);
			}
			if (localroot.dData > max) {
				// if the current data is beyond the bound [min, max], go only to the leftChild. The right child will be beyond as well so
				// there is no point searching at them.
				postOrderTraversal(records, localroot.leftChild, min, max);
			}
		}
		// these if conditions optimize the traversal very much.
	}
	
	// Λειτουργεί στο j-οστό ΔΔΑ και επιστρέφει τα αναγνωριστικά που αυτό περιέχει στο εύρος τιμών [k1,k2]
	
	public Integer[] findInterval(int j, double k1, double k2) {
		if (k1 > k2 || k1 < 0 || k2 < 0) {		// k1 should always be <= k2. If not OR if either k1 or k2 is < 0, return null.
			return null;
		}
		else {
			ArrayList<Integer> id_list = new ArrayList<Integer>();
			
			// traverse the specified BST to find all IDs with data between [k1, k2] and save them in the list.
			postOrderTraversal(id_list, roots[j], k1, k2);
			
			if (id_list.isEmpty()) {
				return null;		// if no id found, return null.
			}
			else {
				Integer[] recorded_rows = new Integer[id_list.size()];
				recorded_rows = id_list.toArray(recorded_rows);				// else convert the ArrayList to an Integer[] and return it.
				return recorded_rows;
			}
		}
	}
	
	// Επιστρέφει τα αναγνωριστικά των εγγραφών που βρίσκονται εντός κουτιού 
	// οριζόμενου από τα σημεία pmin (κάτω αριστερή γωνία) και pmax (πάνω δεξιά γωνία).
	// Χρησιμοποιεί τα υπάρχοντα ΔΔΑ (roots) για αυτό το σκοπό.
	
	public Integer[] containsIndex(double[] pmin, double[] pmax) {
		// if pmin, pmax have different lengths or pmin/pmax has different length than the number of roots, return null.
		if (pmin.length != pmax.length || pmin.length > roots.length) {
			return null;
		}
		else {
			Integer[][] results = new Integer[pmin.length][];
			
			// traverse every BST (every dimension) and save all the arrays of Integer that are returned to a 2D array.
			// every row of this array contains an array that every time saves the IDs that satisfy the condition at the specific dimension.
			for (int j = 0; j < pmin.length; j++)
				results[j] = findInterval(j, pmin[j], pmax[j]);
			
			return (results == null) ? null : intersect(results);
			// if no IDs found, return null. Else return the intersection of these arrays. The common IDs satisfy the condition at
			// every dimension !
		}
	}
	
	// Επιστρέφει την τομή πολλών πινάκων ακεραίων αριθμών ή null, αν η τομή είναι κενή.
	private Integer[] intersect(Integer[][] results) {
		ArrayList<Integer> union = new ArrayList<Integer>();
		
		for (int i = 0; i < results.length; i++) {
			// if an array contains only null values, return null. We know this array will not have common IDs with any other.
			if (results[i] == null)
				return null;
			union.addAll(Arrays.asList(results[i]));
			
			/* 
			 * append to the end of the ArrayList every array inside "results".
			 * It's actually the union of all the arrays.
			 */
		}
		
		ArrayList<Integer> common = new ArrayList<Integer>(); // create ArrayList that saves the common IDs temporarily !
		
		for (int id : union) {
			if (Collections.frequency(union, id) == results.length)
				common.add(id);
			/* 
			 * for each id in the union ArrayList, if the list contains the specific id as many times as the number of the arrays in "results",
			 * add it to "common". Eg. if there are 3 arrays, the specific id must have a frequency of 3 in order to be common to all arrays.
			 */
		}
		
		// "common" will have duplicates though. Eg. if 2 is common in each of the 3 arrays, it will be added at its first occurence,
		// because it has a frequency of 3 and its second and its third occurence.
		
		Set<Integer> union_set = new HashSet<>();		// create a set to store common IDs without duplicates !
		union_set.addAll(common);
		/* 
		 * add every common id in the set. The method addAll(), adds somethinig to the set ONLY IF it's not already present.
		 * Eg. if the common list looks sth like: [2, 2, 5, 5] set will add only the first occurence of 2 not the second, because
		 * it's already present. Same with 5. So the set will be : [2, 5].
		 */
		
		if (union_set.isEmpty()) {		// if there are no common IDs, return null.
			return null;
		}
		else {
			Integer[] same_elements = new Integer[union_set.size()];
			same_elements = union_set.toArray(same_elements);			// else convert the set to an Integer[] and return it.
			return same_elements;
		}
	}

	// Επιστρέφει τα αναγνωριστικά (αριθμούς γραμμής στον πίνακα: data) των εγγραφών 
	// που περιέχονται σε κύκλο (υπερσφαίρα) με κέντρο το p και ακτίνα r.
	// Χρησιμοποιεί τα υπάρχοντα ΔΔΑ (roots) για αυτό το σκοπό.
	
	public Integer[] rangeIndex(double[] p, double r) {
		if (p.length > roots.length) {			// if number of fields doesn't equal to the dimension of the center, return null.
			return null;
		}
		else {
			double[] pmin = new double[p.length];		// create the bottom left point of a rectangle.
			double[] pmax = new double[p.length];		// create the upper right point of a rectangle.
			
			for (int i = 0; i < p.length; i++) {
				// if (the current coordinate of p minus the radius is less than 0), set the current coordinate of pmin with 0.
				// else set it with: the current coordinate of p minus the radius.
				pmin[i] = (p[i] - r < 0) ? 0 : p[i] - r;
				pmax[i] = p[i] + r;
				// set the current coordinate of pmax with the current coordinate of p plus the radius.
			}
			
			ArrayList<Integer> id_list = new ArrayList<Integer>();
			
			// find the IDs in the rectangle that pmin, pmax form.
			Integer[] inside_box = containsIndex(pmin, pmax);
			
			if (inside_box == null) {
				return null;			// if no IDs found, return null.
			}
			else {
				/* 
				 * run a loop for each id in the array returned from containsIndex().
				 */
				for (int c : inside_box) {
					double[] adjust = new double[p.length];			// saves specific id's record.
					
					for (int j = 0; j < p.length; j++)
						adjust[j] = records.getRecord(c)[j];		// iteration to assign to "adjust" the input fields.
					
					double distance = euclidean(p, adjust); // calculate the distance between id's record and p.
					nComp++;				// increment the comparison counter.
					if (distance <= r)		// if "distance" is less than the radius, add the id to the list.
						id_list.add(c);
				}
				
				if (id_list.isEmpty()) {
					return null;		// if no IDs found, return null.
				}
				else {
					Integer[] recorded_rows = new Integer[id_list.size()];
					recorded_rows = id_list.toArray(recorded_rows);			// else convert the ArrayList to an Integer[] and return it.
					return recorded_rows;
				}
			}
		}
	}
	
	@SuppressWarnings("unused")		// removes yellow signs that something is not used.
	public static void main(String[] args) {
		try {
			Forest forest = new Forest(9000, 5);
			forest.load("NBA-5d-17265n.txt", 9000, 5);  // txt's path is: workspace/YourProject.
			
			
			double[] pmin = {25, 40, 60};
			double[] pmax = {200, 400, 600};
			
			String string = Arrays.toString(forest.records.containsScan(pmin, pmax));
			String string1 = Arrays.toString(forest.containsIndex(pmin, pmax));
			System.out.println("nComp for containsScan(): "+forest.records.nComp+ " against containsIndex(): "+forest.nComp);
			
			
			double[] p = {50, 80, 130};
			double r = 90;
			
			forest.nComp = 0;
			forest.records.nComp = 0;
			
			System.out.println();
			
			String string2 = Arrays.toString(forest.records.rangeScan(p, r));
			String string3 = Arrays.toString(forest.rangeIndex(p, r));
			
			System.out.println("nComp for rangeScan(): "+forest.records.nComp+ " against rangeIndex(): "+forest.nComp);
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
