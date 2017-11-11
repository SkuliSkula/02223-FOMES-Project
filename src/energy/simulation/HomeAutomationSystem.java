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
		addOutport("outFromHouse");
		addOutport("outFromGrid");

		this.time = 1;
		this.incrementTime = 1;
		
		ViewableAtomic pvPanel = new PhotoVoltaicPanel("PVpanel", this.time, incrementTime);
		ViewableAtomic battery = new Battery("SuperBattery");
		ViewableAtomic logicUnit = new LogicUnit("LogicUnit", this.time, incrementTime);
		ViewableAtomic house = new House("AdrianHouse", this.time, incrementTime);
		ViewableAtomic electricCar = new ElectricCar();
		ViewableAtomic externalGrid = new ExternalPowerGrid();
		ViewableAtomic generator = new Generator();

		add(pvPanel);
		add(battery);
		add(logicUnit);
		add(house);
		add(electricCar);
		add(externalGrid);
		add(generator);

		//addTestInput("in", new entity("1000"));
		//addTestInput("in", new entity("2000"));

		initialize();

		addCoupling(this, "in", pvPanel, "SolIn");
		addCoupling(pvPanel, "outToLU", logicUnit, "inFromPVPanel");
		addCoupling(logicUnit, "outToHouse", house, "inFromLU");
		addCoupling(house, "outToLU", logicUnit, "inFromHouse");
		addCoupling(logicUnit, "outToEV", electricCar, "inFromLU");
		addCoupling(electricCar, "outExtraToLU", logicUnit, "inExtraFromEV");
		addCoupling(logicUnit, "outToEG", externalGrid, "inFromLU");
		addCoupling(logicUnit, "outRequestEnergy", battery, "inFromLURequest");
		addCoupling(battery, "outToLU", logicUnit, "inFromBattery");
		addCoupling(logicUnit, "outToBattery", battery, "inFromLU");
		addCoupling(battery, "outExtraToLU", logicUnit, "inExtraFromBattery");
		addCoupling(generator, "outFromEXPF", pvPanel, "inFromEXPF");
		addCoupling(house, "out", this, "outFromHouse");
		addCoupling(externalGrid, "outToTrancducer", this, "outFromGrid");
	}
    /**
     * Automatically generated by the SimView program.
     * Do not edit this manually, as such changes will get overwritten.
     */
    public void layoutForSimView()
    {
        preferredSize = new Dimension(838, 560);
        ((ViewableComponent)withName("SuperBattery")).setPreferredLocation(new Point(64, 443));
        ((ViewableComponent)withName("AdrianHouse")).setPreferredLocation(new Point(502, 98));
        ((ViewableComponent)withName("PVpanel")).setPreferredLocation(new Point(-17, 231));
        ((ViewableComponent)withName("LogicUnit")).setPreferredLocation(new Point(204, 184));
    }
}
