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

	private int monthCounter;
	private int dayCounter;
	private final int HOURS_IN_YEAR = 8760;
	private boolean yearDone;

	private static final char ELEMENT_SEPARATOR = ',';
	private static final char LINE_SEPARATOR = '\n';

	/*
	 * private final String[] fileName =
	 * {"jan.csv, feb.csv, march.csv, april.csv, may.csv, jun.csv, " +
	 * "jul.csv, aug.csv, sep.csv, oct.csv, nov.csv, dec.csv"};
	 */

	protected double clock;

	public Trancducer(String name, double Observation_time) {
		super(name);
		addInport("inFromHouse");
		addInport("inFromGrid");
		addOutport("out");

		initialize();
	}

	public Trancducer() {
		this("Trancducer", 2000);
	}

	public void initialize() {
		phase = "active";
		clock = 0;
		monthCounter = 0;
		dayCounter = 1;
		yearDone = false;

		houseUsage = new Year();
		extraEnergyGrid = new Year();

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
				storeDataStatistics(houseEnergy, houseUsage);
			}
			if (messageOnPort(x, "inFromGrid", i)) {
				Energy gridEnergy = ((Energy) x.getValOnPort("inFromGrid", i));
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
				dayCounter++;
			}

			int monthLength = year.getMonths().get(monthCounter).getLength();
			if (dayCounter == monthLength) {
				// writeData(year.getMonths().get(monthCounter).getDayArray()); // Write data
				// per month, send days in month
				monthCounter++;
			}

			// Replacing sunlight hours with energy statistics data
			year.getMonths().get(monthCounter).getDayArray()[timeCycle] = gridEnergy.getEnergy();

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

			sb.append(appendHeader());

			// Loop through months
			for (int i = 0; i < houseUsage.getMonths().size(); i++) {
				String currentMonth = houseUsage.getMonths().get(i).getMonthName();
				// Loop through hours
				for (int j = 0; j < 24; j++) {
					sb.append(appendMonthAndTime(currentMonth, j));
					sb.append(houseUsage.getMonths().get(i).getDayArray()[j] + ELEMENT_SEPARATOR
							+ extraEnergyGrid.getMonths().get(i).getDayArray()[j] + LINE_SEPARATOR);
				}
			}
			pw.write(sb.toString());
			pw.flush();
			pw.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private String appendHeader() {
		return "Period" + ELEMENT_SEPARATOR + "House" + ELEMENT_SEPARATOR + "Grid" + LINE_SEPARATOR;
	}

	private String appendMonthAndTime(String month, int hour) {
		return month + "_Hour" + hour + ELEMENT_SEPARATOR;
	}
}
