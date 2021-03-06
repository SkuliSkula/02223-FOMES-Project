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
	private int finalMonth;

	public Generator() {
		super("Generator");

		addInport("start");
		addOutport("outFromEXPF");
		addOutport("stop");

		addTestInput("start", new entity());
		addTestInput("stop", new entity());

		monthCounter = 0;
		finalMonth = monthCounter;

		initialize();
	}

	public Generator(int monthNumber) {
		super("Generator");

		addInport("start");
		addOutport("outFromEXPF");
		addOutport("stop");

		addTestInput("start", new entity());
		addTestInput("stop", new entity());

		initialize();
		this.monthCounter = monthNumber - 1;
		this.finalMonth = monthCounter;
		System.out.println("Final Month " + monthCounter);
	}

	public void initialize() {
		holdIn("passive", INFINITY);
		timeCycle = 0;
		year = new Year();

		dayCounter = 0;
		super.initialize();
		time = 0;
		generationDone = false;
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
	}

	public message out() {
		System.out.println("1. out generator: " + genValMultiplier);

		message m = new message();
		calculateValues();
		if (phaseIs("active")) {
			m.add(makeContent("outFromEXPF", new doubleEnt(genValMultiplier)));
			System.out.println("Generator Time gen: " + time);
		}
		// time++;

		if (generationDone) {
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
		return super.getTooltipText() + "\n" + "Day: " + dayCounter + "\nMonth: " + monthCounter;
	}

	private void calculateValues() {
		System.out.println("calc values");
		if (this.monthCounter == finalMonth) {
			timeCycle = (int) time % 24;

			if (timeCycle == 23)
				dayCounter++;

			int monthLength = year.getMonths().get(monthCounter).getLength();
			if (dayCounter == monthLength)
				monthCounter++;

			if (dayCounter < year.getMonths().get(monthCounter).getAllDaysInMonth().size()) {
				genValMultiplier = year.getMonths().get(monthCounter).getAllDaysInMonth().get(dayCounter)
						.getDayArray()[timeCycle];
			}
			System.out.println("Time: " + time + ", Index month: " + monthCounter + ", Index time: " + timeCycle
					+ ", The value for month and hour is: " + genValMultiplier);

			System.out.println("Day: " + dayCounter);
			System.out.println("Month: " + monthCounter);
		} else {
			holdIn("passive", INFINITY);
			generationDone = true;
			System.out.println("Generator done!!!!!!!!!!!!!!!!!!!!!!!!");
		}
	}
}
