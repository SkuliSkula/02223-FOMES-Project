package energy.simulation;

import model.modeling.message;
import view.modeling.ViewableAtomic;

public class House extends ViewableAtomic {
	private double energyRequested;
	private Energy deltaEnergy;
	private final static double[] CONSUMPTION_WH = { 110, 110, 110, 110, 110, 110, 110, 400, 400, 110, 110, 110, 110, 110, 110,
			110, 110, 450, 450, 450, 450, 400, 110, 110 };

	private final int INC_TIME = 1;
	private double consumed;

	public House(String name) {
		super(name);
		addInport("inFromLU");
		addOutport("outToEXPF");

		initialize();
	}

	public void initialize() {
		holdIn("idle", INFINITY);
		System.out.println("7. House, initialized with sigma = " + sigma);
		consumed = 0;
		super.initialize();

		energyRequested = 0.0;
		deltaEnergy = new Energy();
	}

	public void deltint() {
		System.out.println("7. internal house");
		if (phaseIs("receiving")) {
			holdIn("idle", 1);
		}
		if (phaseIs("idle")) {
			holdIn("receiving", 0);
		}
	}

	public void deltext(double e, message x) {
		Continue(e);
		System.out.println("7. external house");
		if (phaseIs("idle") || phaseIs("receiving")) {
			for (int i = 0; i < x.getLength(); i++) {
				if (messageOnPort(x, "inFromLU", i)) {
					Energy en = (Energy) x.getValOnPort("inFromLU", i);
					energyRequested = CONSUMPTION_WH[(int) en.getTime() % 24];
					deltaEnergy = new Energy(en.getEnergy() - energyRequested, en.getTime());
					consumed += en.getEnergy();
					System.out.println("House has consumed: " + consumed + "W from the battery since the start of the program!");
					holdIn("receiving", 0);
				}
			}
		}
	}

	public message out() {
		message m = new message();
		if (phaseIs("receiving") && deltaEnergy != null) {
			m.add(makeContent("outToEXPF", deltaEnergy));
			deltaEnergy = null;
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
		return super.getTooltipText() + "\n" + "job: ";
	}
	
	public static double getConsumptionPerHour(int hour){
		return CONSUMPTION_WH[hour];
	}
}
