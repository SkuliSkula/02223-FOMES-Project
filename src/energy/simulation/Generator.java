package energy.simulation;

import GenCol.doubleEnt;
import GenCol.entity;
import model.modeling.message;
import view.modeling.ViewableAtomic;

public class Generator extends ViewableAtomic{
	private Year year;
	private int timeCycle;
	private int monthCounter;
	private int dayCounter;
	private int hour;
	double genValMultiplier;
	public Generator() {
		super("Generator");
		
		addInport("start");
		addOutport("outFromEXPF");
		addInport("stop");
		
		initialize();
	}
	
	public void initialize() {
		holdIn("active", INFINITY);
		sigma = 1;
		timeCycle = 0;
		year = new Year();
		monthCounter = 0;
		dayCounter = 1;
		hour = 0;
		super.initialize();
	}
	
	public void deltint(double e, message x) {
		
	}

	public void deltext(double e, message x) {
		
		for(int i = 0; i < x.getLength(); i++) {
			if(messageOnPort(x, "start", i)) {
				
				//hour = (int) e;
				timeCycle = (int)hour % 24;
				
				if(timeCycle == 23)
					dayCounter++;
				
				int monthLength = year.getMonths().get(monthCounter).getLength();
				if(dayCounter == monthLength)
					monthCounter++;

				genValMultiplier = year.getMonths().get(monthCounter).getDayArray()[timeCycle];
				
				hour++;
				System.out.println("Day: " + dayCounter);
				System.out.println("Month: " + monthCounter);
				
				holdIn("active", hour);
			}
			else if(messageOnPort(x, "stop", i)) {
				holdIn("idle", INFINITY);
			}
		}
	}

	public message out() {
		message m = new message();
		m.add(makeContent("outToPV", new doubleEnt(genValMultiplier)));
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
	

}
