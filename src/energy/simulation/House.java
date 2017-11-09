package energy.simulation;

import GenCol.Queue;
import GenCol.entity;
import model.modeling.message;
import view.modeling.ViewableAtomic;

public class House extends ViewableAtomic{
	
	protected double time;
	protected int consumed;
	protected entity ent;
	protected int genVal;
	
	public House(){
		this("house");
	}
	
	public House(String name){
		super(name);
		
		addInport("in");
		
		addTestInput("in", new Energy("banana", 500));
	}
	
	public void initialize() {
		phase = "active";
		sigma = INFINITY;
		super.initialize();
		time = 0;
	}
	
	public void deltint() {
		clockTime += sigma;
	}
	
	//I receive 0 or smth
	public void deltext(double e, message x) {
		time += e;
		Continue(e);

		if (phaseIs("passive"))
			for (int i = 0; i < x.getLength(); i++) {
				if (messageOnPort(x, "in", i)) {
					customer c = (customer) x.getValOnPort("in", i);
					c.service_time = clockTime;
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
	
	
	
	
	////////////////////////////////////
	protected customer customer;
	protected Queue cq;
	protected int noCustomerInQueue;
	protected double clockTime;

	public teller() {
		this("teller");
	}

	public teller(String name) {
		super(name);
		addInport("in");
		addOutport("next");
		addOutport("out");
		addInport("q_status");

		addTestInput("in", new customer("C1", 1000));
		addTestInput("in", new customer("C2", 2000));
		addTestInput("in", new customer("C3", 1500));
		addTestInput("in", new customer("C4", 4000));
		addTestInput("q_status", new signal("1"));
		addTestInput("q_status", new signal("0"));
	}

	public void deltext1(double e, message x) {
		clockTime += e;
		Continue(e);

		if (phaseIs("passive"))
			for (int i = 0; i < x.getLength(); i++) {
				if (messageOnPort(x, "in", i)) {
					customer c = (customer) x.getValOnPort("in", i);
					c.service_time = clockTime;
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
			this.customer.depart_time = clockTime;
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
}
