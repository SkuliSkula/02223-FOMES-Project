package energy.simulation;

import GenCol.entity;
import model.modeling.*;
import view.modeling.ViewableAtomic;

public class Battery extends ViewableAtomic { // ViewableAtomic is used instead
												// of atomic due to its
												// graphics capability
	private final static double BATTERY_CAPACITY = 13500;
	private final static double MIN_PERCENT = 25, MAX_PERCENT = 85;
	protected static final double INC_TIME = 1;
	private double availableStorage;
	private double stateOfCharge;
	private double totalCapacity;
	private int noOfBatteries;
	protected double time;
	protected Energy excessEnergy;
	protected Energy outputEnergy;

	public Battery() {
		this("Battery", 3);
	}

	public Battery(String name) {
		this(name, 3);
	}

	public Battery(String name, int noOfBatteries) {
		super(name);
		this.noOfBatteries = noOfBatteries;
		this.totalCapacity = this.noOfBatteries * (BATTERY_CAPACITY / 100 * (MAX_PERCENT - MIN_PERCENT));
		this.availableStorage = totalCapacity;
		this.stateOfCharge = 0;
		this.excessEnergy = null;
		addInport("inFromLU"); // Charge the battery
		addInport("inFromLURequest"); // Requested energy from House or
		// Electric
		// car
		addOutport("outExtraToLU");
		addOutport("outToLU"); // Send the requested energy
		initialize();
	}

	public Battery(String name, int noOfBatteries, double stateOfCharge) {
		super(name);
		this.noOfBatteries = noOfBatteries;
		this.totalCapacity = this.noOfBatteries * (BATTERY_CAPACITY / 100 * (MAX_PERCENT - MIN_PERCENT));
		this.availableStorage = totalCapacity-stateOfCharge;
		this.stateOfCharge = stateOfCharge;
		this.excessEnergy = null;
		addInport("inFromLU"); // Charge the battery
		addInport("inFromLURequest"); // Requested energy from House or
		// Electric
		// car
		addOutport("outExtraToLU");
		addOutport("outToLU"); // Send the requested energy
		initialize();
	}

	public void initialize() {
		holdIn("idle", INFINITY);
		System.out.println("4. Battery, initialized with sigma = " + sigma);
		this.time = 0;
		super.initialize();
	}

	public void deltext(double e, message x) {
		Continue(e);
		System.out.println("4. ################# Battery, external sigma = " + sigma + ", and elapsed time = " + e);
		System.out.println("4. Battery, Total capacity: " + totalCapacity + ", available storage: " + availableStorage);

		boolean charging = false;
		for (int i = 0; i < x.getLength(); i++) {
			if (messageOnPort(x, "inFromLU", i)) {
				Energy pvEnergy = (Energy) x.getValOnPort("inFromLU", i);
				System.out.println("4. Received energy from LU: " + pvEnergy.getEnergy());
				if (pvEnergy.getEnergy() > (noOfBatteries - 1) * 5000) {
					availableStorage -= (noOfBatteries - 1) * 5000;
					stateOfCharge += (noOfBatteries - 1) * 5000;
					excessEnergy = new Energy(pvEnergy.getEnergy() - ((noOfBatteries - 1) * 5000), pvEnergy.getTime());
				} else if (pvEnergy.getEnergy() <= availableStorage) {
					charging = true;
					availableStorage -= pvEnergy.getEnergy();
					stateOfCharge += pvEnergy.getEnergy();

				} else if (pvEnergy.getEnergy() > availableStorage) {
					charging = true;
					excessEnergy = new Energy(pvEnergy.getEnergy() - availableStorage, pvEnergy.getTime());
					availableStorage = 0;
					stateOfCharge = totalCapacity;
				} else {
					if (availableStorage != 0)
						charging = true;
					excessEnergy = new Energy(pvEnergy.getEnergy() - availableStorage, pvEnergy.getTime());// Time
																											// added
					availableStorage = 0;
					stateOfCharge = totalCapacity;
				}
				System.out.println("4. Current time in battery: " + time);
				System.out.println("4. Battery, Available storage: " + availableStorage);
				time = pvEnergy.getTime(); // Time changed
			} else if (messageOnPort(x, "inFromLURequest", i)) {
				Energy reqEnergy = (Energy) x.getValOnPort("inFromLURequest", i);
				if (reqEnergy.getEnergy() <= stateOfCharge) {
					stateOfCharge -= reqEnergy.getEnergy();
					availableStorage += reqEnergy.getEnergy();
					outputEnergy = reqEnergy;
				} else {
					if (stateOfCharge != 0)
						outputEnergy = new Energy(stateOfCharge, reqEnergy.getTime());
					else {
						outputEnergy = new Energy(0, reqEnergy.getTime());
					}
					stateOfCharge = 0;
					availableStorage = totalCapacity;
				}
			}
		}

		if (charging) {
			if (outputEnergy != null) {
				holdIn("djarging", 0);
			} else {
				holdIn("charging", 0);
			}
		} else {
			if (outputEnergy != null)
				if (outputEnergy.getEnergy() != 0) {
					holdIn("discharging", 0);
				} else {
					holdIn(phase, 0);
				}
		}
	}

	public void deltint(double e, message x) {
		System.out.println("1. BATTERY!!!!!!!!!!!!!!");
		if (phaseIs("djarging")) {
			System.out.println("BATTERY djarging, time: " + time);
			time++;
			holdIn("djarging", 1);
		} else if (phaseIs("charging")) {
			System.out.println("BATTERY charging, time: " + time);
			time++;
			holdIn("charging", 1);
		} else if (phaseIs("discharging")) {
			System.out.println("BATTERY discharging, time: " + time);
			time++;
			holdIn("discharging", 1);
		} else if (phaseIs("idle")) {
			System.out.println("BATTERY idle, time: " + time);
			time++;
			holdIn("idle", 1);
		}
	}

	public message out() {
		message m = new message();
		System.out.println("Battery message out");
		if (phaseIs("charging")) {
			if (excessEnergy != null) {
				m.add(makeContent("outExtraToLU", excessEnergy));
				System.out.println("________________________ Energy to be sent to the overflow: "
						+ excessEnergy.getEnergy() + ", at time: " + excessEnergy.getTime());
				excessEnergy = null;
			}
		} else if (phaseIs("discharging")) {
			System.out.println("$$$$$$$$$$$$$$ Discharging " + outputEnergy.getEnergy() + "W");
			m.add(makeContent("outToLU", outputEnergy));
			outputEnergy = null;
		} else if (phaseIs("djarging")) {
			if (excessEnergy != null) {
				System.out.println("________________________ Energy to be sent to the overflow: "
						+ excessEnergy.getEnergy() + ", at time: " + excessEnergy.getTime());
				m.add(makeContent("outExtraToLU", excessEnergy));
				excessEnergy = null;
			}
			System.out.println("$$$$$$$$$$$$$$ Discharging " + outputEnergy.getEnergy() + "W");
			m.add(makeContent("outToLU", outputEnergy));
			outputEnergy = null;
		}
		return m;
	}

	public String getTooltipText() {
		return super.getTooltipText() + "\n" + "Available storage: " + availableStorage + "\nState of charge: "
				+ stateOfCharge;
	}
}
