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
	private int time;
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
		sigma = INFINITY;
		timeCycle = 0;
		year = new Year();
		monthCounter = 0;
		dayCounter = 1;
		time = 0;
		super.initialize();
		System.out.println("########## init generator");
	}
	
	public void deltint(double e, message x) {
		System.out.println("############### internal generator");
		time += 1;
	}

	public void deltext(double e, message x) {
		System.out.println("############## external generator");
		for(int i = 0; i < x.getLength(); i++) {
			if(messageOnPort(x, "start", i)) {
				
				//hour = (int) e;
				timeCycle = (int)time % 24;
				
				if(timeCycle == 23)
					dayCounter++;
				
				int monthLength = year.getMonths().get(monthCounter).getLength();
				if(dayCounter == monthLength)
					monthCounter++;

				genValMultiplier = year.getMonths().get(monthCounter).getDayArray()[timeCycle];
				
				System.out.println("Day: " + dayCounter);
				System.out.println("Month: " + monthCounter);
				
				holdIn("active", time);
			}
			else if(messageOnPort(x, "stop", i)) {
				holdIn("idle", INFINITY);
			}
		}
	}

	public message out() {
		System.out.println("############### out generator");
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
