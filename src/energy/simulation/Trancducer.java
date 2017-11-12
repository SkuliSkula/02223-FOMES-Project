package energy.simulation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import GenCol.doubleEnt;
import model.modeling.message;
import view.modeling.ViewableAtomic;

public class Trancducer extends ViewableAtomic {
	private Year houseUsage;
	private Year extraEnergyGrid;

	private int localTime;
	private int monthCounter;
	private int dayCounter;
	private final int HOURS_IN_YEAR = 100; // One day per month (12*24)
	private boolean yearDone;

	private static final String ELEMENT_SEPARATOR = ",";
	private static final String LINE_SEPARATOR = "\n";

	/*
	 * private final String[] fileName =
	 * {"jan.csv, feb.csv, march.csv, april.csv, may.csv, jun.csv, " +
	 * "jul.csv, aug.csv, sep.csv, oct.csv, nov.csv, dec.csv"};
	 */

	protected double clock;

	public Trancducer(String name) {
		super(name);
		addInport("inFromHouse");
		addInport("inFromGrid");
		addOutport("out");

		initialize();
	}

	public Trancducer() {
		this("Trancducer");
	}

	public void initialize() {
		phase = "active";
		clock = 0;
		monthCounter = 0;
		dayCounter = 1;
		yearDone = false;
		localTime = 0;

		houseUsage = new Year();
		extraEnergyGrid = new Year();
		houseUsage.setToZero();
		extraEnergyGrid.setToZero();

		super.initialize();
		holdIn("active", INFINITY);
	}

	public void showState() {
		super.showState();
	}

	public void deltext(double e, message x) {
		System.out.println("--------Transducer elapsed time =" + e);
		System.out.println("-------------------------------------");
		Continue(e);

		for (int i = 0; i < x.size(); i++) {
			if (messageOnPort(x, "inFromHouse", i)) {
				Energy houseEnergy = ((Energy) x.getValOnPort("inFromHouse", i));
				System.out.println("Trancducer - got energy from HOUSE: " + houseEnergy.getEnergy() + ", time: "
						+ houseEnergy.getTime());
				storeDataStatistics(houseEnergy, houseUsage);
			}
			if (messageOnPort(x, "inFromGrid", i)) {
				Energy gridEnergy = ((Energy) x.getValOnPort("inFromGrid", i));
				System.out.println("Trancducer - got energy from GRID: " + gridEnergy.getEnergy() + ", time: "
						+ gridEnergy.getTime());
				storeDataStatistics(gridEnergy, extraEnergyGrid);
			}
		}
		if (yearDone) {
			writeData();
			holdIn("stop", INFINITY);
		}
	}

	public void deltint() {
		System.out.println("trans");
		clock = clock + 1;
		holdIn("active", clock);
		// passivate();
	}

	public message out() {
		message m = new message();
		if (phaseIs("stop")) {
			System.out.println("Trancducer stopping the system, yearDone: " + yearDone);
			m.add(makeContent("out", new doubleEnt(-1))); // Stopping the generator production
		} else {
			System.out.println("Trancducer not sending any command to Generator, yearDone: " + yearDone);
		}

		return m;
	}

	private void storeDataStatistics(Energy gridEnergy, Year year) {
		int time = (int) gridEnergy.getTime();

		if (time < HOURS_IN_YEAR) {
			int timeCycle = time % 24;

			if (timeCycle == 23) {
				//dayCounter++;
				monthCounter++;
			}

			/*int monthLength = year.getMonths().get(monthCounter).getLength();
			if (dayCounter == monthLength) {
				// writeData(year.getMonths().get(monthCounter).getDayArray()); // Write data
				// per month, send days in month
				monthCounter++;
			}*/

			year.getMonths().get(monthCounter).getDayArray()[timeCycle] = gridEnergy.getEnergy();
			System.out.println("*****************Trancducer, received energy: " + gridEnergy.getEnergy());

			System.out.println("Day: " + dayCounter + " saved");
			System.out.println("Month: " + monthCounter + " saved");
		} else {
			System.out.println("Year has passed");
			yearDone = true;
		}
	}

	private void writeData() {
		try {
			String fileToWrite = "statistics.csv";
			PrintWriter pw = new PrintWriter(new File(fileToWrite));
			StringBuilder sb = new StringBuilder();

			pw.append(appendHeader());

			// Loop through months
			for (int i = 0; i < houseUsage.getMonths().size(); i++) {
				String currentMonth = houseUsage.getMonths().get(i).getMonthName();
				// Loop through hours
				for (int j = 0; j < 24; j++) {
					String data = houseUsage.getMonths().get(i).getDayArray()[j] + ELEMENT_SEPARATOR
							+ extraEnergyGrid.getMonths().get(i).getDayArray()[j];
					pw.append(appendMonthTimeData(currentMonth, j, data));
				}
			}
			pw.write(sb.toString());
			pw.close();
			System.out.println("Done writing!");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private String appendHeader() {
		return "Period" + ELEMENT_SEPARATOR + "House" + ELEMENT_SEPARATOR + "Grid" + LINE_SEPARATOR;
	}

	private String appendMonthTimeData(String month, int hour, String data) {
		return month + "_Hour" + hour + ELEMENT_SEPARATOR + data + LINE_SEPARATOR;
	}
}
