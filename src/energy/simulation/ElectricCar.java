package energy.simulation;

import model.modeling.message;
import view.modeling.ViewableAtomic;

/*This class will simulate Tesla Model S P100D*/
public class ElectricCar extends ViewableAtomic {
	private final double BATTERY_CAPACITY = 2000; // Wh
	private double currentCapacity;
	protected Energy energyReceived;
	protected Energy energyExtra;
	private boolean takenOut;
	private int time;
	private double totalReceived;
	private double totalExtraReceived;

	private final double INC_TIME = 1;

	public ElectricCar() {
		this("Tesla Model s");
	}

	public ElectricCar(String name) {
		super(name);

		addInport("inFromLU1");
		addOutport("carOverflow");

		initialize();
	}

	public void initialize() {
		holdIn("idle", INC_TIME);
		System.out.println("5. initialized with sigma = " + sigma);
		currentCapacity = 0;
		energyReceived = new Energy();
		energyExtra = new Energy();
		takenOut = false;
		time = 7;
		totalReceived = 0;
		super.initialize();
	}

	public void deltint() {
		System.out.println("5. internal");

		if (phaseIs("charging")) {
			holdIn("charging", INC_TIME);
		} else if (phaseIs("idle")) {
			holdIn("idle", INC_TIME);
		}

		if (!carIsHome()) {
			takenOut = true;
		}
	}

	public void deltext(double e, message x) {
		Continue(e);

		energyExtra.setEnergy(0);
		System.out.println(
				"5. ################# external sigma = " + sigma + ", and elapsed time = " + e + " in phase: " + phase);

		if (phaseIs("idle")) {
			for (int i = 0; i < x.getLength(); i++) {
				if (messageOnPort(x, "inFromLU1", i)) {
					energyReceived = (Energy) x.getValOnPort("inFromLU1", i);
					totalReceived += energyReceived.getEnergy();
					time = (int) energyReceived.getTime();
					System.out.println("The time in the EV is: " + time % 24);
				}
			}
		}

		if (carIsHome())
			System.out.println("******* THE CAR IS HOME ********");
		else
			System.out.println("******* THE CAR IS NOT HOME ********");

		if (carIsHome()) {
			if (phaseIs("charging")) {
				for (int i = 0; i < x.getLength(); i++) {
					if (messageOnPort(x, "inFromLU1", i)) {
						energyReceived = (Energy) x.getValOnPort("inFromLU1", i);
						time = (int) energyReceived.getTime();
						totalReceived += energyReceived.getEnergy();
						charge(energyReceived.getEnergy());
						energyReceived.setEnergy(0);
						if (charged()) {
							holdIn("idle", INC_TIME);
						} else {
							holdIn("charging", INC_TIME);
						}
					}
				}
			} else if (phaseIs("idle")) {
				if (charged()) {
					for (int i = 0; i < x.getLength(); i++) {
						if (messageOnPort(x, "inFromLU1", i)) {
							energyExtra = (Energy) x.getValOnPort("inFromLU1", i);
							totalExtraReceived += energyExtra.getEnergy();
							time = (int) energyReceived.getTime();
						}
					}
					holdIn("idle", INC_TIME);
				} else {
					for (int i = 0; i < x.getLength(); i++) {
						if (messageOnPort(x, "inFromLU1", i)) {
							energyReceived = (Energy) x.getValOnPort("inFromLU1", i);
							time = (int) energyReceived.getTime();
							totalReceived += energyReceived.getEnergy();
							charge(energyReceived.getEnergy());
							energyReceived.setEnergy(0);
						}
					}
					holdIn("charging", INC_TIME);
				}
			}
		}
	}

	public message out() {
		message m = new message();
		if (phaseIs("idle")) {
			if (energyExtra.getEnergy() != 0) {
				m.add(makeContent("carOverflow", energyExtra));
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
		return super.getTooltipText() + "\n EV time: " + (time % 24) + "\n Car home: " + carIsHome()
				+ "\n Total energy received: " + totalReceived + "\n Total Extra energy received: " + totalExtraReceived
				+ "\n Received time: " + time;
	}

	private boolean charged() {
		return this.BATTERY_CAPACITY == this.currentCapacity;
	}

	private void carReturns() {
		this.currentCapacity = currentCapacity / 2;
	}

	private boolean carIsHome() {
		return (time % 24 <= 7) || (time % 24 >= 16);
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
