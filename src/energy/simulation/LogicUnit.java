package energy.simulation;

import GenCol.doubleEnt;
import GenCol.entity;
import model.modeling.message;
import view.modeling.ViewableAtomic;

public class LogicUnit extends ViewableAtomic {
	protected Energy energy;
	// protected Queue cq;
	protected int noCustomerInQueue;
	protected double time;

	public LogicUnit() {
		this("LogicUnit");
	}

	public LogicUnit(String name) {
		super(name);
		addInport("inFromPVPanels");
		addOutport("outToHouse");

		//addTestInput("in", new Energy(1000));
	}

	public void initialize() {
		phase = "passive";
		sigma = INFINITY;
		super.initialize();
		time = 0;
	}

	public void deltext(double e, message x) {
		time += e;
		Continue(e);

		if (phaseIs("active")) {
			for (int i = 0; i < x.getLength(); i++) {
				if (messageOnPort(x, "inFromPVPanels", i)) {
					Energy receivedEnergy = (Energy) x.getValOnPort("inFromPVPanels", i);
					this.energy = receivedEnergy;
					return;
				}
			}
		}

		System.out.println("external-Phase after: " + phase);
	}

	public void deltint() {
		time += sigma;
	}

	public void deltcon(double e, message x) {
		deltint();
		deltext(0, x);
	}

	public message out() {
		message m = new message();

		if (phaseIs("active")) {
			if (energy.getEnergy() != 0) {
				m.add(makeContent("outToHouse", energy));
			} else {
				m.add(makeContent("outToHouse", new Energy()));
			}

		}
		return m;
	}

	public void showState() {
		super.showState();
	}

	public String getTooltipText() {
		return super.getTooltipText() + "\n" + "job: ";
	}
}
