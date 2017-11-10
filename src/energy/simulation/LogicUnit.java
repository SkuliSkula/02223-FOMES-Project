package energy.simulation;

import model.modeling.message;
import view.modeling.ViewableAtomic;

public class LogicUnit extends ViewableAtomic {
	protected Energy energy;
	protected int noCustomerInQueue;
	protected double time;
	protected double incrementTime;

	public LogicUnit(String name, double time, double incrementTime) {
		super(name);
		addInport("inFromPVPanels");
		addOutport("outToHouse");
		
		this.time = time;
		this.incrementTime = incrementTime;

		initialize();
	}

	public void initialize() {
		phase = "active";
		sigma = INFINITY;
		super.initialize();
	}

	public void deltext(double e, message x) {
		time += e;
		Continue(e);

		if (phaseIs("active")) {
			for (int i = 0; i < x.getLength(); i++) {
				if (messageOnPort(x, "inFromPVPanels", i)) {
					Energy receivedEnergy = (Energy) x.getValOnPort("inFromPVPanels", i);
					System.out.println("Logic unit - receiving energy: " + receivedEnergy.getEnergy());
					this.energy = receivedEnergy;
					holdIn("active", time);
				}
			}
		}

		System.out.println("external-Phase after: " + phase);
	}

	public void deltint() {
		//time += sigma;
		time += incrementTime;
		holdIn("active", time);
	}

	public void deltcon(double e, message x) {
		deltint();
		deltext(0, x);
	}

	public message out() {
		message m = new message();
		System.out.println("Logic unit message out(): before if " + energy.getEnergy());
		
		if (phaseIs("active")) {
			System.out.println("Logic unit message out(): inside active " + energy.getEnergy());
			if (energy.getEnergy() != 0) {
				System.out.println("Logic unit message out(): energy is not 0 " + energy.getEnergy());
				m.add(makeContent("outToHouse", energy));
			} else {
				Energy e = new Energy();
				m.add(makeContent("outToHouse", e));
				System.out.println("Logic unit message out(): energy is " + e.getEnergy());
			}

		}
		return m;
	}

	public void showState() {
		super.showState();
	}

	public String getTooltipText() {
		return super.getTooltipText() + "\n" + "logic unit job: ";
	}
}
