package energy.simulation;

import model.modeling.message;
import view.modeling.ViewableAtomic;

public class ExternalPowerGrid extends ViewableAtomic{
	
	protected Energy excess;
	protected double totalExcess;
	protected double time;
	protected static final double INC_TIME = 1;

	public ExternalPowerGrid() {
		super("External Grid");
	}
	
	public ExternalPowerGrid(String name) {
		super(name);
		addInport("inFromLU");
		addOutport("outToTrancducer");

		this.time = 0;
		initialize();
	}
	
	public void initialize() {
		phase = "IDLE";
		sigma = INFINITY;
		super.initialize();
	}

	public void deltint() {
		time += INC_TIME;
		/*if(phaseIs("TAKING")){
			holdIn("IDLE", INC_TIME);
		}*/
	}

	public void deltext(double e, message x) {
		time += e;
		Continue(e);
		
		if(phaseIs("IDLE") || phaseIs("TAKING")){
			for (int i = 0; i < x.getLength(); i++) {
				if (messageOnPort(x, "inFromLU", i)) {
					Energy energy = (Energy) x.getValOnPort("inFromLU", i);
					System.out.println("####### Energy received in the Power Grid: " + energy.getEnergy());
					this.excess = energy;
					this.totalExcess += energy.getEnergy();
					holdIn("TAKING", INC_TIME);
				}
			}
		}
	}

	public message out() {
		message m = new message();
		
		m.add(makeContent("outToTrancducer", excess));
		/*
		if(phaseIs("TAKING")){
			m.add(makeContent("outToTrancducer", excess));
		}
		*/
		return m;
	}

	public void deltcon(double e, message x) {
		deltint();
		deltext(0, x);
	}

	public void showState() {
		super.showState();
	}

	public String getTooltipText() {
		return super.getTooltipText();
	}
}
