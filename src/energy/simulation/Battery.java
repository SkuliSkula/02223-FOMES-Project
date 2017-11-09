package energy.simulation;

import GenCol.*;
import model.modeling.*;
import view.modeling.ViewableAtomic;

public class Battery extends ViewableAtomic { // ViewableAtomic is used instead
												// of atomic due to its
												// graphics capability
	protected double storage;
	protected double storage_max;
	protected double storage_min;
	protected double time;
	double diff;

	public Battery() {
		this("Battery");
	}

	public Battery(String name) {
		super(name);
		addInport("in");
		addInport("statusIn");
		addOutport("out");
		addOutport("statusOut");
	}

	public void initialize() {
		phase = "passive";
		sigma = INFINITY;
		super.initialize();
		time = 0;
		storage_min = 135000 * 0.25;
		storage_max = 135000 * 0.85;
		diff = 0;
	}

	public void deltext(double e, message x) {
		Continue(e);
		time += e;

		/*
		 * signal 0 is passive signal 1 is active signal 2 is charging
		 */
		if (phaseIs("charging")) {
			for (int i = 0; i < x.getLength(); i++) {
				if (messageOnPort(x, "in", i)) {
					entity val = x.getValOnPort("in", i);
					doubleEnt f = (doubleEnt) val;
					if ((storage += f.getv()) <= storage_max) {
						storage += f.getv();
						holdIn("charging", 0);
					} else if ((storage += f.getv()) > storage_max) {
						storage += f.getv();
						holdIn("passive", 0);
					}
				}
			}
		} else if (phaseIs("passive")) {
			for (int i = 0; i < x.getLength(); i++) {
				if (messageOnPort(x, "statusIn", i)) {
					signal statusIn = (signal) x.getValOnPort("statusIn", i);
					if (statusIn.name.equals("1")) {
						holdIn("active", 0);
					} else if (statusIn.name.equals("2")) {
						holdIn("charging", 0);
					}
				}
			}
		} else if (phaseIs("active")) {
			for (int i = 0; i < x.getLength(); i++) {
				if (messageOnPort(x, "in", i)) {
					entity val = x.getValOnPort("in", i);
					doubleEnt f = (doubleEnt) val;
					if ((storage -= f.getv()) > storage_min) {
						storage -= f.getv();
						diff = f.getv();
						holdIn("active", 0);
					} else if ((storage += f.getv()) <= storage_min) {
						storage -= f.getv();
						diff = f.getv();
						holdIn("passive", 0);
					}
				}
				if (messageOnPort(x, "statusIn", i)) {
					signal statusIn = (signal) x.getValOnPort("statusIn", i);
					if (statusIn.name.equals("0")) {
						holdIn("passive", 0);
					} else if (statusIn.name.equals("2")) {
						holdIn("charging", 0);
					}
				}
			}
		}
	}

	public void deltint() {
		// passivate();
	}

	public message out() {
		message m = new message();

		if (!phaseIs("charging") && storage < storage_min) {
			m.add(makeContent("statusOut", new doubleEnt(storage)));
			holdIn("passive", 0);
		} else if (!phaseIs("active") && storage >= storage_max) {
			m.add(makeContent("statusOut", new doubleEnt(storage)));
			holdIn("passive", 0);
		} else if (phaseIs("active")) {
			m.add(makeContent("out", new doubleEnt(diff)));
		}
		return m;
	}
}
