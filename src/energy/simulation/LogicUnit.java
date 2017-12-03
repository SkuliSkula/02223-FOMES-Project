package energy.simulation;

import model.modeling.message;
import view.modeling.ViewableAtomic;

public class LogicUnit extends ViewableAtomic {
	protected Energy energyToHouse, energyToBattery, energyToGrid, energyToRequest/* , energyToEV */;
	protected final static double EV_OUTPUT = 17300;
	protected double time;
	private int count;

	public LogicUnit(String name) {
		super(name);
		addInport("inFromPVPanel");
		// addInport("inFromHouse"); // Request energy
		addInport("inExtraFromBattery"); // Excesive flow of energy from the
											// battery
		addInport("inFromBattery"); // Energy back after request
		// addInport("inExtraFromEV"); // Excesive energy from the Electric
		// vehicle

		addOutport("outToHouse");
		addOutport("outToBattery");
		addOutport("outToEG");
		addOutport("outRequestEnergy"); // House or electric car can request
		count = 0;
		// energy
		// addOutport("outToEV"); // EV = Electric vehicle
		initialize();
	}

	public void initialize() {
		holdIn("idle", INFINITY);
		System.out.println("3. LU initialized with sigma = " + sigma);
		this.time = 0;
		super.initialize();
	}

	public void deltext(double e, message x) {
		Continue(e);
		System.out.println("3. ################# Logic Unit external sigma = " + sigma + ", and elapsed time = " + e);
		for (int i = 0; i < x.getLength(); i++) {
			if (count >= 744) {
				passivate();
			}
			System.out.println("in loop");
			if (messageOnPort(x, "inFromPVPanel", i)) {
				System.out.println("3. Logic Unit found message from pv panel on port");
				Energy receivedEnergy = (Energy) x.getValOnPort("inFromPVPanel", i);
				System.out.println("3. Logic unit - receiving energy: " + receivedEnergy.getEnergy());
				this.energyToBattery = receivedEnergy;
				holdIn("active", 0);
				if (receivedEnergy.getTime() != 0) {
					this.energyToRequest = new Energy(
							House.getConsumptionPerHour((int) ((receivedEnergy.getTime() - 1) % 24)),
							receivedEnergy.getTime() - 1);
				}
				System.out.println("Old time in LU:" + time);
				time = receivedEnergy.getTime();
				System.out.println("New time in LU from PV:" + time);
			} else if (messageOnPort(x, "inExtraFromBattery", i)) {
				Energy receivedEnergy = (Energy) x.getValOnPort("inExtraFromBattery", i);
				System.out
						.println("Logic unit - receiving ovelflow energy from battery: " + receivedEnergy.getEnergy());
				this.energyToGrid = receivedEnergy;
				holdIn("active", 0);
			} else if (messageOnPort(x, "inFromBattery", i)) {
				Energy receivedEnergy = (Energy) x.getValOnPort("inFromBattery", i);
				this.energyToHouse = receivedEnergy;
				holdIn("active", 0);
				count++;
			}
		}

		System.out.println("3. external-Phase after: " + phase);
	}

	public void deltint(double e, message x) {
		System.out.println("3. Logic Unit internal");
		if (phaseIs("active")) {
			holdIn("idle", 1);
		}
		if (phaseIs("idle")) {
			holdIn("active", 0);
		}
	}

	public void deltcon(double e, message x) {
		deltint();
		deltext(0, x);
	}

	public message out() {
		message m = new message();
		System.out.println("3. Logic unit message out() with sigma = " + sigma);

		if (phaseIs("active")) {
			System.out.println("3. lu is in active phase");
			if (energyToBattery != null) {
				m.add(makeContent("outToBattery", energyToBattery));
				energyToBattery = null;
			}
			if (energyToHouse != null) {
				m.add(makeContent("outToHouse", energyToHouse));
				energyToHouse = null;
			}
			if (energyToRequest != null) {
				m.add(makeContent("outRequestEnergy", energyToRequest));
				energyToRequest = null;
			}
			if (energyToGrid != null) {
				m.add(makeContent("outToEG", energyToGrid));
				energyToGrid = null;
			}
			System.out.println("Logic unit message out(): inside active ");
			System.out.println("Logic unit current time active: " + time);
		} else {
			System.out.println("Logic unit message out(): not active ");
			System.out.println("Logic unit current time not active: " + time);
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
