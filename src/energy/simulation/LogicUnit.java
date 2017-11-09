package energy.simulation;

import GenCol.doubleEnt;
import GenCol.entity;
import model.modeling.message;
import view.modeling.ViewableAtomic;

public class LogicUnit extends ViewableAtomic {
	private double generatedEnergy;

	protected customer customer;
	protected Queue cq;
	protected int noCustomerInQueue;
	protected double time;

	public LogicUnit() {
		this("LogicUnit");
	}

	public LogicUnit(String name) {
		super(name);
		addInport("inPVPanels");
		addOutport("outHouse");

		addTestInput("in", new doubleEnt(1000));
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

		if (phaseIs("passive"))
			for (int i = 0; i < x.getLength(); i++) {
				if (messageOnPort(x, "in", i)) {
					customer c = (customer) x.getValOnPort("in", i);
					c.service_time = time;
					this.customer = c;
					holdIn("busy", c.processing_time);
					return;
				}

				if (messageOnPort(x, "q_status", i)) {
					signal q_status = (signal) x.getValOnPort("q_status", i);

					if (q_status.name.equals("1")) {
						holdIn("calling", 20);
					}
				}
			}

		System.out.println("external-Phase after: " + phase);
	}

	public void deltint() {
		time += sigma;

		if (phaseIs("calling")) {
			passivate();
		}

		if (phaseIs("busy")) {
			holdIn("calling", 20);
		}
	}

	public void deltcon(double e, message x) {
		deltint();
		deltext(0, x);
	}

	public message out() {
		message m = new message();

		if (phaseIs("calling")) {
			m.add(makeContent("next", (signal) new signal("1")));
		}
		if (phaseIs("busy")) {
			this.customer.depart_time = time;
			m.add(makeContent("out", (customer) this.customer));
		}
		return m;
	}

	public void showState() {
		super.showState();
		// System.out.println("job: " + job.getName());
	}

	public String getTooltipText() {
		return super.getTooltipText() + "\n" + "job: ";
	}

	/*
	 * public void initialize() { phase = "passive"; sigma = INFINITY;
	 * super.initialize(); clockTime = 0; }
	 * 
	 * public void deltext(double e, message x) { clockTime += e; Continue(e);
	 * 
	 * if (phaseIs("passive")) for (int i = 0; i < x.getLength(); i++) { if
	 * (messageOnPort(x, "in", i)) { customer c = (customer) x.getValOnPort("in",
	 * i); c.service_time = clockTime; this.customer = c; holdIn("busy",
	 * c.processing_time); return; }
	 * 
	 * if (messageOnPort(x, "q_status", i)) { signal q_status = (signal)
	 * x.getValOnPort("q_status", i);
	 * 
	 * if (q_status.name.equals("1")) { holdIn("calling", 20); } } }
	 * 
	 * System.out.println("external-Phase after: " + phase); }
	 * 
	 * public void deltint() { clockTime += sigma;
	 * 
	 * if (phaseIs("calling")) { passivate(); }
	 * 
	 * if (phaseIs("busy")) { holdIn("calling", 20); } }
	 * 
	 * public void deltcon(double e, message x) { deltint(); deltext(0, x); }
	 * 
	 * public message out() { message m = new message();
	 * 
	 * if (phaseIs("calling")) { m.add(makeContent("next", (signal) new
	 * signal("1"))); } if (phaseIs("busy")) { this.customer.depart_time =
	 * clockTime; m.add(makeContent("out", (customer) this.customer)); } return m; }
	 * 
	 * public void showState() { super.showState(); // System.out.println("job: " +
	 * job.getName()); }
	 * 
	 * public String getTooltipText() { return super.getTooltipText() + "\n" +
	 * "job: "; }
	 */
}
