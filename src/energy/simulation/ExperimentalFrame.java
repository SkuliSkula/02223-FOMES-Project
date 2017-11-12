package energy.simulation;

import java.awt.*;

import model.modeling.message;
import view.modeling.ViewableAtomic;
import view.modeling.ViewableDigraph;

public class ExperimentalFrame extends ViewableDigraph {
	private final double OBSERVATION_TIME = 8750;

	public ExperimentalFrame(String name) {
		super(name);
		experimentalFrameConstruct();
	}

	public ExperimentalFrame() {
		super("ExperimentalFrame");
		experimentalFrameConstruct();
	}

	private void experimentalFrameConstruct() {
		addInport("start");
		addInport("inFromHouse");
		addInport("inFromGrid");
		addOutport("outFromGenerator");
		addOutport("outFromTrancducer");

		ViewableAtomic generator = new Generator();
		ViewableAtomic trancducer = new Trancducer("Trancducer", OBSERVATION_TIME);

		add(generator);
		add(trancducer);

		initialize();

		addCoupling(this, "start", generator, "start");
		addCoupling(this, "inFromHouse", trancducer, "inFromHouse");
		addCoupling(this, "inFromGrid", trancducer, "inFromGrid");
		addCoupling(generator, "outFromEXPF", this, "outFromGenerator");
		addCoupling(trancducer, "out", this, "outFromTrancducer");
		addCoupling(trancducer, "out", generator, "stop");
	}

}
