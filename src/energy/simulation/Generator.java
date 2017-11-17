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

	public Generator() {
		super("Generator");

		addInport("start");
		addOutport("outFromEXPF");
		addInport("stop");

		addTestInput("start", new entity());
		addTestInput("stop", new entity());

		initialize();
	}

	public void initialize() {
		holdIn("passive", INFINITY);
		timeCycle = 0;
		year = new Year();

		monthCounter = 0;
		dayCounter = 1;
		super.initialize();
		time = 0;
		System.out.println("1. init generator");
	}

	public void deltint() {
		System.out.println("1. internal generator");
		if (phaseIs("active")) {
			time++;
			holdIn("idle", 1);
		}
		if (phaseIs("idle")) {
			holdIn("active", 0);
		}
	}

	public void deltext(double e, message x) {
		Continue(e);
		for (int i = 0; i < x.getLength(); i++) {
			if (messageOnPort(x, "start", i)) {
				holdIn("active", 0);
			} else if (messageOnPort(x, "stop", i)) {
				holdIn("passive", INFINITY);
			}
		}
		System.out.println("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
	}

	/*
	 * public void deltext(double e, message x) { Continue(e);
	 * System.out.println("################# 1. external sigma = " + sigma +
	 * ", and elapsed time = " + e); for (int i = 0; i < x.getLength(); i++) {
	 * if (messageOnPort(x, "start", i)) { holdIn("active", 1); } /* else
	 * if(messageOnPort(x, "stop", i)) { holdIn("idle", INFINITY); }
	 */
	/*
	 * } }
	 */

	public message out() {
		System.out.println("1. out generator: " + genValMultiplier);

		// calculateValues();

		message m = new message();
		m.add(makeContent("outFromEXPF", new doubleEnt(genValMultiplier)));
		System.out.println("Generator Time gen: " + time);
		//time++;
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
		return super.getTooltipText() + "\n" + "Day: " + dayCounter + "\nMonth: " + monthCounter;
	}

	private void calculateValues() {
		timeCycle = (int) time % 24;

		if (timeCycle == 23)
			dayCounter++;

		int monthLength = year.getMonths().get(monthCounter).getLength();
		if (dayCounter == monthLength)
			monthCounter++;

		genValMultiplier = year.getMonths().get(monthCounter).getDayArray()[timeCycle];
		System.out.println("Time: " + time + ", Index month: " + monthCounter + ", Index time: " + timeCycle
				+ ", The value for month and hour is: " + genValMultiplier);

		System.out.println("Day: " + dayCounter);
		System.out.println("Month: " + monthCounter);
	}
}
