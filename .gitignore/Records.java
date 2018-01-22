import java.util.ArrayList;

public class Records {
	public long nComp;
	private double[][] data;

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

	public Records(int m, int n) {
		data = new double[m][n]; 
	}

	// Επιστρέφει την i εγγραφή (γραμμή) του πίνακα: data 
	public double[] getRecord(int i) {
		if (i > -1 && i < data.length)
			return data[i];
		else 
			return null;
	}

	// Θέτει την i εγγραφή (γραμμή) του πίνακα: data ίση με τα περιεχόμενα του πίνακα: rec
	public void setRecord(int i, double[] rec) {
		for (int j = 0; j < rec.length; j++) {
			data[i][j] = rec[j];
		}
	}

	// Επιστρέφει τα αναγνωριστικά (αριθμούς γραμμής στον πίνακα: data) των εγγραφών που βρίσκονται εντός κουτιού
	// οριζόμενου από τα σημεία pmin (κάτω αριστερή γωνία) και pmax (πάνω δεξιά γωνία)
	
	public Integer[] containsScan(double[] pmin, double[] pmax) {
		if (pmin.length != pmax.length || pmin.length > data[0].length) {		// or pmax.length since they have same length.
			return null;
		}
		else {
			ArrayList<Integer> id_list = new ArrayList<Integer>();
			
			for (int i = 0; i < data.length; i++) {				// scan every record.
				boolean not_inside = false;						// boolean variable that determines if the specific record is in the rectangle.
				for (int j = 0; j < pmin.length; j++) {			// scan every field of each record.
					nComp++;									// increment the comparison counter.
					if (data[i][j] < pmin[j] || data[i][j] > pmax[j]) {
						not_inside = true;
						break;
					}
					// if a single dimension doesn't satisfy the condition, it doesn't belong to the rectangle so break the inner loop.
					// It doesn't have to check for the rest dimensions.
				}
				
				if (!not_inside)		// if the record is inside the rectangle, add its id to the list.
					id_list.add(i);
			}
			
			if (id_list.isEmpty()) {
				return null;		// if no IDs found, return null.
			}
			else {
				Integer[] recorded_rows = new Integer[id_list.size()];
				recorded_rows = id_list.toArray(recorded_rows);		// else convert the ArrayList to an Integer[] and return it.
				return recorded_rows;
			}
		}
	}
	
	// Επιστρέφει τα αναγνωριστικά (αριθμούς γραμμής στον πίνακα: data) των εγγραφών 
	// που περιέχονται σε κύκλο (υπερσφαίρα) με κέντρο το p και ακτίνα r
	
	public Integer[] rangeScan(double[] p, double r) {
		if ( data[0].length < p.length ) {			// if number of fields doesn't equal to the dimension of the center, return null.
			return null;
		}
		else {
			ArrayList<Integer> id_list = new ArrayList<Integer>();
			
			for (int i = 0; i < data.length; i++) {				// scan every record.
				double[] adjust = new double[p.length];
				
				for (int j = 0; j < p.length; j++)				// iteration to assign to "adjust" the input fields.
					adjust[j] = data[i][j];
				
				double distance = euclidean(adjust, p);		// calculate distance between current record and p.
				nComp++;										// increment comparison counter.
				if (distance <= r)								// if "distance" is less than the radius, add its id to the list.
					id_list.add(i);
			}
			
			if (id_list.isEmpty()) {
				return null;			// if no IDs found, return null.
			}
			else {
				Integer[] recorded_rows = new Integer[id_list.size()];
				recorded_rows = id_list.toArray(recorded_rows);			// else convert the ArrayList to an Integer[] and return it.
				return recorded_rows;
			}
		}
	}
}
