package energy.simulation;

import GenCol.entity;
import model.modeling.message;
import view.modeling.ViewableAtomic;

/*This class will simulate Tesla Model S P100D*/
public class ElectricCar extends ViewableAtomic {
	private static final double BATTERY_CAPACITY = 100; // This is 100kW
	// Phases are idle or charging
	public ElectricCar() {
		super("Tesla Model s");
		//this.carBattery = new Battery(BATTERY_CAPACITY);
	}
	
	public ElectricCar(String name) {
		super(name);
		
		addInport("inFromLU");
		addOutport("outExtraToLU");
		
		initialize();
	}
	
	public void initialize() {
		phase = "idle";
		sigma = INFINITY;
		super.initialize();
	}

	public void deltint() {

	}

	// I receive 0 or smth
	public void deltext(double e, message x) {

	}

	public message out() {
		message m = new message();

		return m;
	}

	public void deltcon(double e, message x) {

	}

	public void showState() {
		super.showState();
	}

	public String getTooltipText() {
		return super.getTooltipText();
	}
	
}
