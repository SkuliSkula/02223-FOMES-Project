package energy.simulation;

import java.awt.*;

import GenCol.entity;
import view.modeling.ViewableAtomic;
import view.modeling.ViewableComponent;
import view.modeling.ViewableDigraph;

public class HomeAutomationSystem extends ViewableDigraph {

	private double time;
	private double incrementTime;
	
	public HomeAutomationSystem(String name) {
		super(name);
		homeAutomationConstruct();
	}

	public HomeAutomationSystem() {
		super("HomeAutomationSystem");
		homeAutomationConstruct();
	}

	private void homeAutomationConstruct() {

		addInport("in");
		addOutport("outOfThisFuckingSimulation");

		this.time = 1;
		this.incrementTime = 1;
		
		ViewableAtomic generator = new Generator();
		ViewableAtomic pv = new PhotoVoltaicPanel("Penis", 1);
		ViewableAtomic lu = new LogicUnit("Other penis");
		ViewableAtomic bat = new Battery();
		ViewableAtomic car = new ElectricCar();
		ViewableAtomic grid = new ExternalPowerGrid();

		add(generator);
		add(pv);
		add(lu);
		add(bat);
		add(car);
		add(grid);

		initialize();

		addCoupling(this, "in", generator, "start");
		addCoupling(generator, "outFromEXPF", pv, "inFromEXPF");
		addCoupling(pv, "outToLU", lu, "inFromPVPanel");
		addCoupling(lu, "outToBattery", bat, "inFromLU");
		addCoupling(bat, "outExtraToLU", lu, "inExtraFromBattery");
		addCoupling(lu, "outToEV", car, "inFromLU1");
		addCoupling(car, "carOverflow", lu, "inExtraFromEV");
		addCoupling(lu, "outToEG", grid, "inFromLU");
		addCoupling(grid, "outToTrancducer", this, "outOfThisFuckingSimulation");
		
		addTestInput("in", new Energy());
		addTestInput("in", new Energy());
		addTestInput("in", new Energy());
	}

    /**
     * Automatically generated by the SimView program.
     * Do not edit this manually, as such changes will get overwritten.
     */
    public void layoutForSimView()
    {
        preferredSize = new Dimension(805, 332);
        ((ViewableComponent)withName("Penis")).setPreferredLocation(new Point(-46, 42));
        ((ViewableComponent)withName("Battery")).setPreferredLocation(new Point(340, 37));
        ((ViewableComponent)withName("Other penis")).setPreferredLocation(new Point(75, 125));
        ((ViewableComponent)withName("Generator")).setPreferredLocation(new Point(0, 202));
        ((ViewableComponent)withName("Tesla Model s")).setPreferredLocation(new Point(365, 235));
        ((ViewableComponent)withName("External Grid")).setPreferredLocation(new Point(137, 35));
    }
}
