package energy.simulation;

import java.awt.*;

import GenCol.entity;
import view.modeling.ViewableAtomic;
import view.modeling.ViewableComponent;
import view.modeling.ViewableDigraph;

public class HomeAutomationSystem extends ViewableDigraph {

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
		addOutport("out");

		ViewableAtomic pvPanel = new PhotoVoltaicPanel("PVpanel", 3);
		ViewableAtomic battery = new Battery("SuperBattery");
		ViewableAtomic logicUnit = new LogicUnit("LogicUnit");
		ViewableAtomic house = new House("AdrianHouse");

		add(pvPanel);
		add(battery);
		add(logicUnit);
		add(house);

		addTestInput("in", new entity("1000"));
		addTestInput("in", new entity("2000"));

		initialize();

		addCoupling(this, "in", pvPanel, "SolIn");
		addCoupling(pvPanel, "PowOut", logicUnit, "inFromPVPanels");
		addCoupling(logicUnit, "outToHouse", house, "in");
		addCoupling(house, "out", this, "out");
	}

	public void layoutForSimView() {
		preferredSize = new Dimension(549, 253);
		((ViewableComponent) withName("PVpanel")).setPreferredLocation(new Point(243, 93));
		((ViewableComponent) withName("SuperBattery")).setPreferredLocation(new Point(243, 93));
		((ViewableComponent) withName("LogicUnit")).setPreferredLocation(new Point(243, 93));
		((ViewableComponent) withName("AdrianHouse")).setPreferredLocation(new Point(243, 93));
	}
}
