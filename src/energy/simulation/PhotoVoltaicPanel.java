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
		super.initialize();
	}
	// external function like input
	// e = elapsed time
	public void deltext(double e, message x) {
		time += e;
		System.out.println("PV panels, value of e: " + e);
		Continue(e);
		for (int i = 0; i < x.getLength(); i++) {
			if (messageOnPort(x, "inFromEXPF", i)) {
				ent = (doubleEnt) x.getValOnPort("SolIn", i);
				generated = (int)(genVal * ent.getv());

			}
			else if(messageOnPort(x, "outFromEXPF", i)) {
				holdIn("idle", 0);
			}
		}
	}

	// internal function like time
	public void deltint() {
		
	}
	
	// If deltint and deltext happen at the same time this function decides the priority
	public void deltcon(double e, message x) {
		
	}
	
	public message out() {
		message m = new message();

		m.add(makeContent("outToLU", new Energy(generated)));
		return m;
	}
}
