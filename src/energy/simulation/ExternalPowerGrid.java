package energy.simulation;

import GenCol.entity;
import model.modeling.message;
import view.modeling.ViewableAtomic;

public class ExternalPowerGrid extends ViewableAtomic{

	public ExternalPowerGrid() {
		super("External Grid");
	}
	
	public ExternalPowerGrid(String name) {
		super(name);
		
		addInport("inFromLU");
		addOutport("outToLU");
		
		initialize();
	}
	
	public void initialize() {
		phase = "active";
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
		deltint();
		deltext(0, x);
	}

	public void showState() {
		super.showState();
	}

	public String getTooltipText() {
		return super.getTooltipText();
	}
}
