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

	public PhotoVoltaicPanel(String name, double time) {
		super(name);
		addInport("SolIn");
		addOutport("PowOut");

		this.time = time;

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
		Continue(e);
		for (int i = 0; i < x.getLength(); i++) {
			if (messageOnPort(x, "SolIn", i)) {
				ent = x.getValOnPort("SolIn", i);
				holdIn("active", time);
			}
		}
	}

	public void deltint() {
		if (phaseIs("active")) {
			generated = generated + 1;
			holdIn("active", time);
		}
	}

	public message out() {
		message m = new message();

		m.add(makeContent("PowOut", new entity("Generated" + generated * genVal)));
		return m;
	}
}
