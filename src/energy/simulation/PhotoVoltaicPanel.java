package energy.simulation;

import GenCol.*;
import model.modeling.*;
import view.modeling.ViewableAtomic;

public class PhotoVoltaicPanel extends ViewableAtomic {
	protected double time;
	protected int generated;
	protected doubleEnt ent;
	protected double genVal;
	protected int noOfPanels;
	private static final double PANEL_OUTPUT = 345;

	public PhotoVoltaicPanel(String name, int numberOfPanels) {
		super(name);
		addInport("inFromEXPF");
		addOutport("outToLU");
		this.time = 0;
		this.noOfPanels = numberOfPanels;
		initialize();
	}

	public void initialize() {
		holdIn("idle", INFINITY);
		System.out.println("2. PV panel, initialized with sigma = " + sigma);
		generated = 0;
		genVal = PANEL_OUTPUT * noOfPanels;
		super.initialize();
	}

	// external function like input
	// e = elapsed time
	public void deltext(double e, message x) {
		Continue(e);

		System.out.println("2. PV panel ################# external sigma = " + sigma + ", and elapsed time = " + e);
		for (int i = 0; i < x.getLength(); i++) {
			if (messageOnPort(x, "inFromEXPF", i)) {
				ent = (doubleEnt) x.getValOnPort("inFromEXPF", i);
				System.out.println("Value obtained from Generator in PV panel: " + ent.getv());
				System.out.println("Time in PV panel: " + time);
				generated = (int) (genVal * ent.getv());
				time++;
				System.out.println("PV panel Generated energy: " + generated);
				holdIn("active", 0);
			}
		}
	}

	// internal function like time
	public void deltint(double e, message x) {
		System.out.println("2. PV panel internal ");
		System.out.println("Incrementing time in deltInt PVPanel!");
		if (phaseIs("active")) {
			
			holdIn("idle", 1);
		}
		if (phaseIs("idle")) {
			holdIn("active", 0);
		}
	}

	// If deltint and deltext happen at the same time this function decides the
	// priority
	public void deltcon(double e, message x) {
		deltint();
		deltext(0, x);
	}

	public message out() {
		message m = new message();
		if (phaseIs("active")) {
			System.out.println("2. Pv panel out() message with sigma = " + sigma + ", energy generated " + generated);
			m.add(makeContent("outToLU", new Energy(generated, time)));
			System.out.println("2. Pv panel out() sending: " + generated + " at time " + time);
		}
		return m;
	}
}
