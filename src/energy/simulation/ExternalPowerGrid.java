package energy.simulation;

import model.modeling.message;
import view.modeling.ViewableAtomic;

public class ExternalPowerGrid extends ViewableAtomic {

	protected Energy excess;
	protected double totalExcess;
	protected double time;
	protected static final double INC_TIME = 1;

	public ExternalPowerGrid() {
		this("External Grid");
	}

	public ExternalPowerGrid(String name) {
		super(name);
		addInport("inFromLU");
		addOutport("outToTrancducer");

		this.time = 0;
		initialize();
	}

	public void initialize() {
		holdIn("idle", INFINITY);
		System.out.println("6. initialized with sigma = " + sigma);
		this.time = 0;
		super.initialize();
	}

	public void deltint() {
		System.out.println("6. grid internal");
		if (phaseIs("taking")) {
			holdIn("idle", 1);
		} else if (phaseIs("idle")) {
			holdIn("taking", 0);
		}
	}

	public void deltext(double e, message x) {
		Continue(e);

		if (phaseIs("idle") || phaseIs("taking")) {
			for (int i = 0; i < x.getLength(); i++) {
				if (messageOnPort(x, "inFromLU", i)) {
					Energy energy = (Energy) x.getValOnPort("inFromLU", i);
					System.out.println("6. Energy received in the Power Grid: " + energy.getEnergy());
					this.excess = energy;
					this.totalExcess += energy.getEnergy();
					holdIn("taking", 0);
				}
			}
		}
	}

	public message out() {
		message m = new message();

		if (phaseIs("taking")) {
			m.add(makeContent("outToTrancducer", excess));
			excess = null;
		}
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
