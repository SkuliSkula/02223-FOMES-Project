package energy.simulation;

import GenCol.entity;
import view.modeling.ViewableDigraph;

public class Simulator extends ViewableDigraph {

	public Simulator(String name) {
		super(name);
		simulatorConstruct();
	}

	public Simulator() {
		super("Simulator");
		simulatorConstruct();
	}

	private void simulatorConstruct() {

		addInport("start");
		addOutport("out");
		
		ViewableDigraph homeAutomationSystem = new HomeAutomationSystem("HomeAutomationSystem");
		ViewableDigraph experimentalFrame  = new ExperimentalFrame("ExperimentalFrame");

		add(homeAutomationSystem);
		add(experimentalFrame);
		
		addTestInput("start", new entity("start"));

		initialize();

		addCoupling(this, "start", experimentalFrame, "start");
		addCoupling(experimentalFrame, "outFromTrancducer", this, "out");
		addCoupling(homeAutomationSystem, "outFromGrid", experimentalFrame, "inFromGrid");
		addCoupling(homeAutomationSystem, "outFromHouse", experimentalFrame, "inFromHouse");
		addCoupling(experimentalFrame, "outFromGenerator", homeAutomationSystem, "in");
	}
}
