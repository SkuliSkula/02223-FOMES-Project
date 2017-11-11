package energy.simulation;

import view.modeling.ViewableAtomic;

public class Generator extends ViewableAtomic{
	
	public Generator() {
		super("Generator");
		
		addInport("inFromEXPF");
		addOutport("outFromEXPF");
		
		initialize();
	}
	
	public void initialize() {
		phase = "idle";
		sigma = INFINITY;
		super.initialize();
	}
	
	

}
