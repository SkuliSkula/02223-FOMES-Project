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
		this.totalCapacity = this.noOfBatteries * (BATTERY_CAPACITY * (MAX_PERCENT - MIN_PERCENT));
		this.availableStorage = totalCapacity;
		this.stateOfCharge = 0;
		addInport("inFromLU"); // Charge the battery
		addInport("inFromLURequest"); // Requested energy from House or Electric
										// car
		addOutport("outExtraToLU");
		addOutport("outToLU"); // Send the requested energy
		
		addTestInput("inFromLU", new Energy(5000));
		addTestInput("inFromLU", new Energy(7000));
		addTestInput("inFromLU", new Energy(8000));
		addTestInput("inFromLU", new Energy(14000));
		addTestInput("inFromLU", new Energy(3000));
		addTestInput("inFromLU", new Energy(1000));
		addTestInput("inFromLURequest", new Energy(1000));
		addTestInput("inFromLURequest", new Energy(5000));
		addTestInput("inFromLURequest", new Energy(6000));
		addTestInput("inFromLURequest", new Energy(8000));
		addTestInput("inFromLURequest", new Energy(12000));
		addTestInput("inFromLURequest", new Energy(1000));
		addTestInput("inFromLURequest", new Energy(5000));
	}

	public void initialize() {
		phase = "IDLE";
		sigma = INFINITY;
		System.out.println("Initialize in battery!");
		super.initialize();
		time = 0;
	}

	public void deltext(double e, message x) {
		Continue(e);
		time += e;

		boolean charging = false;
		System.out.println("External event received in battery!");
		for (int i = 0; i < x.getLength(); i++) {
			if (messageOnPort(x, "inFromLU", i)) {
				Energy pvEnergy = (Energy) x.getValOnPort("inFromLU", i);
				if (pvEnergy.getEnergy() <= availableStorage && pvEnergy.getEnergy() <= (noOfBatteries - 1) * 5000) {
					charging = true;
					availableStorage -= pvEnergy.getEnergy();
					stateOfCharge += pvEnergy.getEnergy();
				} else if (pvEnergy.getEnergy() <= availableStorage) {
					charging = true;
					availableStorage -= (noOfBatteries - 1) * 5000;
					stateOfCharge += (noOfBatteries - 1) * 5000;
					excessEnergy = new Energy(pvEnergy.getEnergy() - (noOfBatteries - 1) * 5000);
				} else {
					if (availableStorage != 0)
						charging = true;
					excessEnergy = new Energy(pvEnergy.getEnergy() - availableStorage);
					availableStorage = 0;
					stateOfCharge = totalCapacity;
				}
			} else if (messageOnPort(x, "inFromLURequest", i)) {
				Energy reqEnergy = (Energy) x.getValOnPort("inFromLURequest", i);
				if (reqEnergy.getEnergy() <= stateOfCharge) {
					stateOfCharge -= reqEnergy.getEnergy();
					availableStorage += reqEnergy.getEnergy();
					outputEnergy = reqEnergy;
				} else {
					if (stateOfCharge != 0)
						outputEnergy = new Energy(stateOfCharge);
					stateOfCharge = 0;
					availableStorage = totalCapacity;
				}
			}
		}
		if (charging) {
			if (outputEnergy != null) {
				holdIn("DJARGING", INC_TIME);
			} else {
				holdIn("CHARGING", INC_TIME);
			}
		} else {
			if (outputEnergy != null) {
				holdIn("DISCHARGING", INC_TIME);
			} else {
				holdIn("IDLE", INC_TIME);
			}
		}
		//// Everything from above should probably be the case for all phases
	}

	public void deltint() {
		System.out.println("Time incremented");
		time += sigma;
		// passivate();
	}

	public message out() {
		message m = new message();

		if (!phaseIs("CHARGING")) {
			if (excessEnergy != null) {
				m.add(makeContent("outExtraToLU", excessEnergy));
				excessEnergy = null;
			} else {
				holdIn("IDLE", 0);
			}
		} else if (phaseIs("DISCHARGING")) {
			if (outputEnergy != null) {
				m.add(makeContent("outToLU", outputEnergy));
				outputEnergy = null;
			}else{
				m.add(makeContent("outToLU", new Energy()));
			}
		} else if (phaseIs("DJARGING")) {
			if(excessEnergy != null){
				m.add(makeContent("outExtraToLU", excessEnergy));
				excessEnergy = null;
			}
			m.add(makeContent("outToLU", outputEnergy));
			outputEnergy = null;
		}
		return m;
	}
}
