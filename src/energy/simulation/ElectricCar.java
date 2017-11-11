package energy.simulation;

import model.modeling.message;
import view.modeling.ViewableAtomic;

/*This class will simulate Tesla Model S P100D*/
public class ElectricCar extends ViewableAtomic {
	private final double BATTERY_CAPACITY = 100000; // Watts
	private double currentCapacity;
	private final double MAX_INPUT = 17300; // Watts
	private Energy energyReceived;
	private Energy energyExtra;

	private final int INC_TIME = 1;
	private double time;

	public ElectricCar() {
		super("Tesla Model s");
	}

	public ElectricCar(String name) {
		super(name);

		addInport("inFromLU");
		addOutport("outExtraToLU");

		initialize();
	}

	public void initialize() {
		phase = "charging";
		sigma = INFINITY;
		super.initialize();
		time = 0;
		currentCapacity = 0;
		energyReceived = new Energy();
		energyExtra = new Energy();
	}

	public void deltint() {
		time += sigma;
		energyExtra.setEnergy(0);
	}

	public void deltext(double e, message x) {
		Continue(e);
		this.time += e;

		energyExtra.setEnergy(0);

		if (phaseIs("charging")) {
			for (int i = 0; i < x.getLength(); i++) {
				if (messageOnPort(x, "inFromLU", i)) {
					energyReceived = (Energy) x.getValOnPort("inFromLU", i);
					charge(energyReceived.getEnergy());
					energyReceived.setEnergy(0);

					if (charged()) {
						holdIn("idle", INC_TIME);
					} else {
						holdIn("charging", INC_TIME);
					}
				}
			}
		}
		if (phaseIs("idle")) {
			if (charged()) {
				for (int i = 0; i < x.getLength(); i++) {
					if (messageOnPort(x, "inFromLU", i)) {
						energyExtra = (Energy) x.getValOnPort("inFromLU", i);
					}
				}
				holdIn("idle", INC_TIME);
			} else {
				holdIn("charging", INC_TIME);
			}
		}
	}

	public message out() {
		message m = new message();

		if (phaseIs("idle")) {
			if (energyExtra.getEnergy() != 0) {
				m.add(makeContent("outExtraToLU", energyExtra));
			}
		}
		return m;
	}

	public void deltcon(double e, message x) {
		deltint();
		deltext(0, x);
	}

	public void showState() {
		super.showState();
	}

	public String getTooltipText() {
		return super.getTooltipText();
	}

	private boolean charged() {
		return this.BATTERY_CAPACITY == this.currentCapacity;
	}

	private void charge(double energyReceived) {
		if (!charged()) {
			if ((currentCapacity + energyReceived) <= BATTERY_CAPACITY) {
				currentCapacity += energyReceived;
			} else {
				double energyRequired = BATTERY_CAPACITY - currentCapacity;
				currentCapacity += energyRequired;
				energyExtra.setEnergy(energyReceived - energyRequired);
			}
		}
	}
}
