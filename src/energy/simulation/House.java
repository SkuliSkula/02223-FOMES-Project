package energy.simulation;

import GenCol.entity;
import model.modeling.message;
import view.modeling.ViewableAtomic;

public class House extends ViewableAtomic{
	
	protected double time;
	protected double consumed;
	protected entity ent;
	protected int genVal;
	protected Energy en;
	
	public House(){
		this("house");
	}
	
	public House(String name){
		super(name);
		
		addInport("in");
		
		addTestInput("in", new Energy(500));
	}
	
	public void initialize() {
		phase = "active";
		sigma = INFINITY;
		super.initialize();
		time = 0;
	}
	
	public void deltint() {
		time += sigma;
	}
	
	//I receive 0 or smth
	public void deltext(double e, message x) {
		time += e;
		Continue(e);

		if (phaseIs("active"))
			for (int i = 0; i < x.getLength(); i++) {
				if (messageOnPort(x, "in", i)) {
					Energy energ = (Energy) x.getValOnPort("in", i);
					System.out.println("Energy received in the house: " + energ.getEnergy());
					//Maybe add the energy in later iterations, not just instantiate it
					this.en = energ;
					this.consumed += energ.getEnergy();
					return;
				}
			}
		System.out.println("So far, the house has consumed " + consumed + " energies");
		System.out.println("external-Phase after: " + phase);
	}
	
	public void deltcon(double e, message x) {
		deltint();
		deltext(0, x);
	}

	public void showState() {
		super.showState();
		//System.out.println("job: " + job.getName());
	}

	public String getTooltipText() {
		return super.getTooltipText() + "\n" + "job: ";
	}
}
