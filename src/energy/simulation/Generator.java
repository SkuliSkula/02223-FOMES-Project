package energy.simulation;

import GenCol.doubleEnt;
import GenCol.entity;
import model.modeling.message;
import view.modeling.ViewableAtomic;

public class Generator extends ViewableAtomic {
	private Year year;
	private int timeCycle;
	private int monthCounter;
	private int dayCounter;
	private int time;
	double genValMultiplier;
	private boolean generationDone;

	public Generator() {
		super("Generator");

		addInport("start");
		addOutport("outFromEXPF");
		addOutport("stop");

		initialize();
	}

	public void initialize() {
		holdIn("active", 1);
		timeCycle = 0;
		year = new Year();

		monthCounter = 0;
		dayCounter = 1;
		super.initialize();
		time = 0;
		generationDone = false;
		System.out.println("1. init generator");
	}

	public void deltint(double d) {
		System.out.println("1. internal generator");
		if (phaseIs("active")) {
			time++;
			holdIn("active", 1);
		}
	}

	/*
	 * public void deltext(double e, message x) { Continue(e);
	 * System.out.println("################# 1. external sigma = " + sigma +
	 * ", and elapsed time = " + e); for (int i = 0; i < x.getLength(); i++) { if
	 * (messageOnPort(x, "start", i)) { holdIn("active", 1); } /* else
	 * if(messageOnPort(x, "stop", i)) { holdIn("idle", INFINITY); }
	 */
	/*
	 * } }
	 */

	public message out() {
		System.out.println("1. out generator: " + genValMultiplier);
		message m = new message();

		calculateValues();

		m.add(makeContent("outFromEXPF", new doubleEnt(genValMultiplier)));
		System.out.println("Generator Time gen: " + time);
		time++;
		
		if(generationDone) {
			m.add(makeContent("stop", new doubleEnt(-1)));
		}

		return m;
	}

	public void deltcon(double e, message x) {
		deltint();
		deltext(0, x);
	}

	public void showState() {
		super.showState();
		// System.out.println("job: " + job.getName());
	}

	public String getTooltipText() {
		return super.getTooltipText() + "\n" + "job: ";
	}

	private void calculateValues() {
		if (monthCounter == 0) {

			timeCycle = (int) time % 24;

			if (timeCycle == 23)
				dayCounter++;

			int monthLength = year.getMonths().get(monthCounter).getLength();
			if (dayCounter == monthLength)
				monthCounter++;

			// genValMultiplier =
			// year.getMonths().get(monthCounter).getDayArray()[timeCycle];
			genValMultiplier = year.getMonths().get(monthCounter).getAllDaysInMonth().get(dayCounter)
					.getDayArray()[timeCycle];
			System.out.println("Time: " + time + ", Index month: " + monthCounter + ", Index time: " + timeCycle
					+ ", The value for month and hour is: " + genValMultiplier);

			System.out.println("Day: " + dayCounter);
			System.out.println("Month: " + monthCounter);
		} else {
			generationDone = true;
			System.out.println("Geerator done!!!!!!!!!!!!!!!!!!!!!!!!");
		}
	}
}
