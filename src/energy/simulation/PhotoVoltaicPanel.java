package energy.simulation;

import GenCol.*;
import model.modeling.*;
import view.modeling.ViewableAtomic;

public class PhotoVoltaicPanel extends ViewableAtomic {
	protected double time;
	protected int generated;
	protected doubleEnt ent;
	protected int genVal;
	protected double incrementTime;
	private final int INC_TIME = 1;

	public PhotoVoltaicPanel(String name, double time, double incrementTime) {
		super(name);
		addInport("inFromEXPF");
		addOutport("outToLU");

		this.time = 0;
		this.incrementTime = incrementTime;

		initialize();
	}

	public void initialize() {
		holdIn("idle", 0);
		generated = 0;
		genVal = 345;
		sigma = INFINITY;
		super.initialize();
	}

	// external function like input
	// e = elapsed time
	public void deltext(double e, message x) {
		Continue(e);
		time += e;
		
		System.out.println("PV panels, value of e: " + e);
		Continue(e);
		for (int i = 0; i < x.getLength(); i++) {
			if (messageOnPort(x, "inFromEXPF", i)) {
				ent = (doubleEnt) x.getValOnPort("SolIn", i);
				generated = (int) (genVal * ent.getv());
				holdIn("active", INC_TIME);

			} else if (messageOnPort(x, "outFromEXPF", i)) {
				holdIn("idle", INFINITY);
			}
		}
	}

	// internal function like time
	public void deltint() {
		time += sigma;
	}

	// If deltint and deltext happen at the same time this function decides the
	// priority
	public void deltcon(double e, message x) {
		deltint();
		deltext(0, x);
	}

	public message out() {
		message m = new message();

		m.add(makeContent("outToLU", new Energy(generated)));
		return m;
	}
}
