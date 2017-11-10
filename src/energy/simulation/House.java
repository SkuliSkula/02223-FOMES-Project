package energy.simulation;

import GenCol.entity;
import model.modeling.message;
import view.modeling.ViewableAtomic;

public class House extends ViewableAtomic {

	protected double time;
	protected double consumed;
	protected entity ent;
	protected int genVal;
	protected Energy en;
	protected double incrementTime;

	public House(String name, double time, double incrementTime) {
		super(name);
		addInport("inFromLU");
		addOutport("outToLU");

		this.time = time;
		this.incrementTime = incrementTime;
		
		initialize();
	}

	public void initialize() {
		phase = "active";
		sigma = INFINITY;
		super.initialize();
	}

	public void deltint() {
		time += incrementTime;
		holdIn("active", time);
	}

	// I receive 0 or smth
	public void deltext(double e, message x) {
		time += e;
		Continue(e);

		if (phaseIs("active"))
			for (int i = 0; i < x.getLength(); i++) {
				if (messageOnPort(x, "in", i)) {
					Energy energy = (Energy) x.getValOnPort("in", i);
					System.out.println("Energy received in the house: " + energy.getEnergy());
					// Maybe add the energy in later iterations, not just instantiate it
					this.en = energy;
					this.consumed += energy.getEnergy();
					holdIn("active", time);
				}
			}
		System.out.println("So far, the house has consumed " + consumed + " energies");
		System.out.println("external-Phase after: " + phase);
	}

	public message out() {
		message m = new message();

		if (phaseIs("active")) {
			m.add(makeContent("out", new entity("Hello, I am Adrian and I have just moved out and consumed " + consumed)));
		}
		return m;
	}

	public void deltcon(double e, message x) {
		deltint();
		deltext(0, x);
	}

	public void showState() {
		super.showState();
		// System.out.println("job: " + job.getName());
	}

	public String getTooltipText() {
		return super.getTooltipText() + "\n" + "job: ";
	}
}
