package energy.simulation;

import java.util.ArrayList;

public class Year {
	private ArrayList<Month> months;
	
	public Year() {
		months = new ArrayList<>();
		initializeMonths();
	}
	
	public ArrayList<Month> getMonths() {
		return months;
	}
	private void initializeMonths() {
		// Create the days arrays
		
		months.add(new Month("January", 31, hours1()));
		months.add(new Month("February", 28, hours1()));
		months.add(new Month("March", 31, hours2()));
		months.add(new Month("April", 30, hours3()));
		months.add(new Month("Mai", 31, hours4()));
		months.add(new Month("June", 30, hours5()));
		months.add(new Month("July", 31, hours5()));
		months.add(new Month("August", 31, hours5()));
		months.add(new Month("September", 30, hours4()));
		months.add(new Month("October", 31, hours3()));
		months.add(new Month("November", 30, hours2()));
		months.add(new Month("December", 31, hours1()));
	}
	
	private double[] hours1() {
		double[] hours = new double[24];
		hours[0] = 0.0;
		hours[1] = 0.0;
		hours[2] = 0.0;
		hours[3] = 0.0;
		hours[4] = 0.0;
		hours[5] = 0.0;
		hours[6] = 0.0;
		hours[7] = 0.0;
		hours[8] = 0.0;
		hours[9] = 0.0;
		hours[10] = 0.0;
		hours[11] = 0.0;
		hours[12] = 0.0;
		hours[13] = 0.0;
		hours[14] = 0.0;
		hours[15] = 0.0;
		hours[16] = 0.0;
		hours[17] = 0.0;
		hours[18] = 0.0;
		hours[19] = 0.0;
		hours[20] = 0.0;
		hours[21] = 0.0;
		hours[22] = 0.0;
		hours[23] = 0.0;
		return hours;
	}
	
	private double[] hours2() {
		double[] hours = new double[24];
		hours[0] = 0.0;
		hours[1] = 0.0;
		hours[2] = 0.0;
		hours[3] = 0.0;
		hours[4] = 0.0;
		hours[5] = 0.0;
		hours[6] = 0.0;
		hours[7] = 0.0;
		hours[8] = 0.0;
		hours[9] = 0.0;
		hours[10] = 0.0;
		hours[11] = 0.0;
		hours[12] = 0.0;
		hours[13] = 0.0;
		hours[14] = 0.0;
		hours[15] = 0.0;
		hours[16] = 0.0;
		hours[17] = 0.0;
		hours[18] = 0.0;
		hours[19] = 0.0;
		hours[20] = 0.0;
		hours[21] = 0.0;
		hours[22] = 0.0;
		hours[23] = 0.0;
		return hours;
	}
	
	private double[] hours3() {
		double[] hours = new double[24];
		hours[0] = 0.0;
		hours[1] = 0.0;
		hours[2] = 0.0;
		hours[3] = 0.0;
		hours[4] = 0.0;
		hours[5] = 0.0;
		hours[6] = 0.0;
		hours[7] = 0.0;
		hours[8] = 0.0;
		hours[9] = 0.0;
		hours[10] = 0.0;
		hours[11] = 0.0;
		hours[12] = 0.0;
		hours[13] = 0.0;
		hours[14] = 0.0;
		hours[15] = 0.0;
		hours[16] = 0.0;
		hours[17] = 0.0;
		hours[18] = 0.0;
		hours[19] = 0.0;
		hours[20] = 0.0;
		hours[21] = 0.0;
		hours[22] = 0.0;
		hours[23] = 0.0;
		return hours;
	}
	
	private double[] hours4() {
		double[] hours = new double[24];
		hours[0] = 0.0;
		hours[1] = 0.0;
		hours[2] = 0.0;
		hours[3] = 0.0;
		hours[4] = 0.0;
		hours[5] = 0.0;
		hours[6] = 0.0;
		hours[7] = 0.0;
		hours[8] = 0.0;
		hours[9] = 0.0;
		hours[10] = 0.0;
		hours[11] = 0.0;
		hours[12] = 0.0;
		hours[13] = 0.0;
		hours[14] = 0.0;
		hours[15] = 0.0;
		hours[16] = 0.0;
		hours[17] = 0.0;
		hours[18] = 0.0;
		hours[19] = 0.0;
		hours[20] = 0.0;
		hours[21] = 0.0;
		hours[22] = 0.0;
		hours[23] = 0.0;
		return hours;
	}
	
	private double[] hours5() {
		double[] hours = new double[24];
		hours[0] = 0.0;
		hours[1] = 0.0;
		hours[2] = 0.0;
		hours[3] = 0.0;
		hours[4] = 0.0;
		hours[5] = 0.0;
		hours[6] = 0.0;
		hours[7] = 0.0;
		hours[8] = 0.0;
		hours[9] = 0.0;
		hours[10] = 0.0;
		hours[11] = 0.0;
		hours[12] = 0.0;
		hours[13] = 0.0;
		hours[14] = 0.0;
		hours[15] = 0.0;
		hours[16] = 0.0;
		hours[17] = 0.0;
		hours[18] = 0.0;
		hours[19] = 0.0;
		hours[20] = 0.0;
		hours[21] = 0.0;
		hours[22] = 0.0;
		hours[23] = 0.0;
		return hours;
	}
	
	private class Month {
		private String month;
		private int length;
		private double[] dayArray;
		private Month(String month, int length, double[] dayArray) {
			this.month = month;
			this.length = length;
			this.dayArray = dayArray;
		}
		
		public double[] getDayArray() {
			return this.dayArray;
		}
		
		public int getLength() {
			return this.length;
		}
		
		public String getMonthName() {
			return this.month;
		}
	}
	
	public static void main(String[] args) {
		
		Year year = new Year();
		
		for(int i = 0; i < year.getMonths().size(); i++) {
			System.out.println(year.getMonths().get(i).getMonthName());
			for(int j = 0; j < year.getMonths().get(i).getDayArray().length; j++)
				System.out.println(year.getMonths().get(i).getDayArray()[j]);
			System.out.println();
		}
	}
}
