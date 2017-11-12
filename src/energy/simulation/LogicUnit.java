package energy.simulation;

import model.modeling.message;
import view.modeling.ViewableAtomic;

public class LogicUnit extends ViewableAtomic {
	protected Energy energyToHouse, energyToBattery, energyToGrid, energyToRequest, energyToEV;
	protected final static double EV_OUTPUT = 17300;
	private static final double INC_TIME = 1;
	protected double time;

	public LogicUnit(String name) {
		super(name);
		addInport("inFromPVPanel");
		//addInport("inFromHouse"); // Request energy
		addInport("inExtraFromBattery"); // Excesive flow of energy from the
											// battery
		//addInport("inFromBattery"); // Energy back after request
		addInport("inExtraFromEV"); // Excesive energy from the Electric vehicle

		//addOutport("outToHouse");
		addOutport("outToBattery");
		addOutport("outToEG");
		//addOutport("outRequestEnergy"); // House or electric car can request
										// energy
		addOutport("outToEV"); // EV = Electric vehicle

		

		initialize();
	}

	public void initialize() {
		holdIn("idle", 1);
		System.out.println("3. initialized with sigma = " + sigma);
		this.time = 0;
		super.initialize();
	}

	public void deltext(double e, message x) {
		Continue(e);
		System.out.println("3. ################# external sigma = " + sigma + ", and elapsed time = " + e);
		for (int i = 0; i < x.getLength(); i++) {
			System.out.println("in loop");
			if (messageOnPort(x, "inFromPVPanel", i)) {
				System.out.println("3. found message from pv panel on port");
				Energy receivedEnergy = (Energy) x.getValOnPort("inFromPVPanel", i);
				System.out.println("3. Logic unit - receiving energy: " + receivedEnergy.getEnergy());
				this.energyToBattery = receivedEnergy;
				holdIn("active", 1);
			} else if (messageOnPort(x, "inExtraFromBattery", i)) {
				Energy receivedEnergy = (Energy) x.getValOnPort("inExtraFromBattery", i);
				System.out
						.println("Logic unit - receiving ovelflow energy from battery: " + receivedEnergy.getEnergy());
				if ((int) time % 24 > 7 && (int) time % 24 < 16) {
					this.energyToGrid = receivedEnergy;
				} else {
					if (receivedEnergy.getEnergy() > EV_OUTPUT) {
						this.energyToEV = new Energy(EV_OUTPUT);
						this.energyToGrid = new Energy(receivedEnergy.getEnergy() - EV_OUTPUT);
					} else {
						this.energyToEV = receivedEnergy;
					}
				}
				holdIn("active", 1);
			}  else if (messageOnPort(x, "inExtraFromEV", i)) {
				Energy receivedEnergy = (Energy) x.getValOnPort("inExtraFromEV", i);
				System.out.println("Logic unit - receiving overflow energy from EV: " + receivedEnergy.getEnergy());
				this.energyToGrid = receivedEnergy;
				holdIn("active", INC_TIME);
			} /*else if (messageOnPort(x, "inFromHouse", i)) {
				Energy receivedEnergy = (Energy) x.getValOnPort("inFromHouse", i);
				System.out.println("Logic unit - receiving energy request from house: " + receivedEnergy.getEnergy());
				this.energyToRequest = receivedEnergy;
				holdIn("active", time);
			}else if (messageOnPort(x, "inFromBattery", i)) {
				Energy receivedEnergy = (Energy) x.getValOnPort("inFromBattery", i);
				System.out.println("Logic unit - receiving overflow energy from EV: " + receivedEnergy.getEnergy());
				this.energyToHouse = receivedEnergy;
				holdIn("active", time);
			}*/
		}

		System.out.println("3. external-Phase after: " + phase);
	}

		public void deltint(double e, message x) {
			System.out.println("3. internal");
			if (phaseIs("active")){
				time++;
				holdIn("active", 1);
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
			if (energyToEV != null) {
				m.add(makeContent("outToEV", energyToEV));
				energyToEV = null;
			}
			if (energyToGrid != null) {
				m.add(makeContent("outToEG", energyToGrid));
				energyToGrid = null;
			}
			System.out.println("Logic unit message out(): inside active ");

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
