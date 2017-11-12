package energy.simulation;

import model.modeling.message;
import view.modeling.ViewableAtomic;

public class House extends ViewableAtomic {
	private Energy energyRequested;
	private Energy energyReceived;
	private final double[] CONSUMPTION_WH = { 110, 110, 110, 110, 110, 110, 110, 400, 400, 110, 110, 110,
			110, 110, 110, 110, 110, 450, 450, 450, 450, 400, 110, 110 };

	private final int INC_TIME = 1;
	private int dayHour;
	private int time;
	private double consumed;

	public House(String name, double time, double incrementTime) {
		super(name);
		addInport("inFromLU");
		addOutport("outToLU");
		addOutport("outToEXPF");

		initialize();
	}

	public void initialize() {
		phase = "requesting";
		sigma = 20;
		super.initialize();

		energyRequested = new Energy();
		energyReceived = new Energy();
		time = 0;
		dayHour = 0;
		consumed = 0;
	}

	public void deltint() {
		System.out.println("house int");
		time += sigma;
	}

	public void deltext(double e, message x) {
		Continue(e);

		if (phaseIs("requesting")) {
			dayHour = time % 24;
			energyRequested = new Energy(CONSUMPTION_WH[dayHour]);
			holdIn("receiving", INC_TIME);
		} else if (phaseIs("receiving")) {
			for (int i = 0; i < x.getLength(); i++) {
				if (messageOnPort(x, "inFromLU", i)) {
					energyReceived = (Energy) x.getValOnPort("in", i);
					consumed += energyReceived.getEnergy();
					System.out.println("House has consumed: " + consumed + "W");
					energyReceived.setEnergy(0);
					holdIn("requesting", INC_TIME);
				}
			}
		}
	}

	public message out() {
		message m = new message();
		if (phaseIs("requesting")) {
			m.add(makeContent("outToLU", energyRequested));
		}
		if (phaseIs("receiving")) {
			m.add(makeContent("outToEXPF", getDeltEnergy()));
		}
		return m;
	}

	private Energy getDeltEnergy() {
		double balance = this.energyReceived.getEnergy() - this.energyRequested.getEnergy();
		return new Energy(balance);
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
}
