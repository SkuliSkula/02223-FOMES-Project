package energy.simulation;

import GenCol.*;
import model.modeling.*;
import view.modeling.ViewableAtomic;

public class PhotoVoltaicPanel extends ViewableAtomic { // ViewableAtomic is
														// used instead
														// of atomic due to its
														// graphics capability
	protected double time;
	protected int generated;
	protected entity ent;
	protected int genVal;
	protected double incrementTime;

	public PhotoVoltaicPanel(String name, double time, double incrementTime) {
		super(name);
		addInport("inFromEXPF");
		addOutport("outToLU");

		this.time = 0;
		this.incrementTime = incrementTime;

		initialize();
	}

	public void initialize() {
		holdIn("active", time);
		generated = 0;
		genVal = 345;
		super.initialize();
	}

	public void deltext(double e, message x) {
		time += e;
		System.out.println("PV panels, value of e: " + e);
		Continue(e);
		for (int i = 0; i < x.getLength(); i++) {
			if (messageOnPort(x, "SolIn", i)) {
				ent = x.getValOnPort("SolIn", i);
				holdIn("active", time);
			}
		}
	}

	public void deltint() {
		time += incrementTime;
		if (phaseIs("active")) {
			//generated = generated + 1;
			generated = 1;
			holdIn("active", time);
		}
	}

	public message out() {
		message m = new message();

		m.add(makeContent("PowOut", new Energy(generated * genVal)));
		return m;
	}
}
