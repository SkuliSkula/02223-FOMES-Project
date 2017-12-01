package energy.simulation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

import GenCol.doubleEnt;
import energy.simulation.Year.Day;
import energy.simulation.Year.Month;
import model.modeling.message;
import view.modeling.ViewableAtomic;

public class Transducer extends ViewableAtomic {
	private Year houseUsage;
	private Year extraEnergyGrid;

	private int currentMonth;
	private int dayCounter;

	private int HOURS_IN_MONTH;
	private boolean monthDone;

	private static final String ELEMENT_SEPARATOR = ",";
	private static final String LINE_SEPARATOR = "\n";

	private int currentMonthIndex;

	private final String[] fileName = { "jan.csv", "feb.csv", "march.csv", "april.csv", "may.csv", "jun.csv", "jul.csv",
			"aug.csv", "sep.csv", "oct.csv", "nov.csv", "dec.csv" };

	private final String[] monthName = { "Jan", "Feb", "March", "April", "May", "Jun", "Jul", "Aug", "Sep", "Oct",
			"Nov", "Dec" };

	protected double clock;

	public Transducer(String name) {
		super(name);
		addInport("inFromHouse");
		addInport("inFromGrid");
		addInport("stop");
		addOutport("out");

		initialize();
	}

	public Transducer() {
		this("Transducer");
	}

	public void initialize() {
		phase = "active";
		clock = 0;
		dayCounter = 1;

		monthDone = false;

		houseUsage = new Year();
		extraEnergyGrid = new Year();

		currentMonth = 5;
		currentMonthIndex = currentMonth - 1;
		houseUsage.getMonths().get(currentMonthIndex).setHoursInDaysToZero();
		extraEnergyGrid.getMonths().get(currentMonthIndex).setHoursInDaysToZero();

		HOURS_IN_MONTH = 744;
		System.out.println("HOURS IN MONTH ARE: " + HOURS_IN_MONTH);

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

		if (monthDone) {
			writeData();
			System.out.println("************************************************************");
			System.out.println("****************************DONE****************************");
			System.out.println("************************************************************");
			holdIn("stop", INFINITY);
		} else {
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
				if (messageOnPort(x, "stop", i)) {
					monthDone = true;
					System.out.println("Stopped from Generator, month done!!!!!!!!");
				}
			}
		}
	}

	public void deltint() {
		/*
		 * System.out.println("trans"); clock = clock + 1; holdIn("active", clock);
		 */
		// passivate();
	}

	public message out() {
		message m = new message();
		if (phaseIs("stop")) {
			System.out.println("Trancducer stopping the system, yearDone: " + monthDone);
			m.add(makeContent("out", new doubleEnt(-1))); // Stopping the generator production
		} else {
			System.out.println("Trancducer not sending any command to Generator, yearDone: " + monthDone);
		}

		return m;
	}

	private void storeDataStatistics(Energy energy, Year year) {
		int time = (int) energy.getTime();
		ArrayList<Day> daysInMonth = year.getMonths().get(currentMonthIndex).getAllDaysInMonth();
		System.out.println("DAYS IN MONTH SIZE: " + daysInMonth.size());
		System.out.println("CURRENT HOURS ARE: " + time);

		if (time <= HOURS_IN_MONTH) {
			int timeCycle = time % 24;

			if (timeCycle == 23) {
				dayCounter++;
			}

			System.out.println("TRANCUDER STORING: " + energy.getEnergy() + " at time: " + energy.getTime());
			daysInMonth.get(dayCounter - 1).getDayArray()[timeCycle] = energy.getEnergy();
			System.out.println("TRANCUDER STORED: " + daysInMonth.get(dayCounter - 1).getDayArray()[timeCycle]);
			System.out.println("TRANCUDER STORED IN DAY: " + daysInMonth.get(dayCounter - 1).getDay());
			System.out.println("TRANCUDER STORED IN HOUR: " + timeCycle);

		} else {
			System.out.println("MONTH HAS PASSED");
			monthDone = true;
		}
	}

	private void writeData() {

		try {
			String fileToWrite = fileName[currentMonthIndex];
			PrintWriter pw = new PrintWriter(new File(fileToWrite));
			pw.append(appendHeader());

			// Loop through days in month
			Month monthNowHouse = houseUsage.getMonths().get(currentMonthIndex);
			Month monthNowGrid = extraEnergyGrid.getMonths().get(currentMonthIndex);

			for (int i = 0; i < houseUsage.getMonths().get(currentMonthIndex).getLength(); i++) {
				String day = houseUsage.getMonths().get(currentMonthIndex).getAllDaysInMonth().get(i).getDay();

				// Loop through hours
				for (int j = 0; j < 24; j++) {
					String data = monthNowHouse.getAllDaysInMonth().get(i).getDayArray()[j] + ELEMENT_SEPARATOR
							+ monthNowGrid.getAllDaysInMonth().get(i).getDayArray()[j];
					pw.append(appendMonthTimeData(monthName[currentMonthIndex], day, j, data));
				}
			}
			pw.close();
			System.out.println("Done writing!");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private String appendHeader() {
		return "Period" + ELEMENT_SEPARATOR + "House" + ELEMENT_SEPARATOR + "Grid" + LINE_SEPARATOR;
	}

	private String appendMonthTimeData(String month, String day, int hour, String data) {
		return monthName[currentMonthIndex] + day + "_Hour" + hour + ELEMENT_SEPARATOR + data + LINE_SEPARATOR;
	}
}
