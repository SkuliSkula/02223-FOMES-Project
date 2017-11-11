package energy.simulation;

import java.util.ArrayList;

import GenCol.entity;
import model.modeling.message;
import view.modeling.ViewableAtomic;

public class Trancducer extends ViewableAtomic {
	private ArrayList<Double> houseUsage;
	private ArrayList<Double> extraEnergy;
	protected double clock, total_ta, observation_time;
	public Double count = 0.00;

	public Trancducer(String name, double Observation_time) {
		super(name);
		addInport("inFromHouse");
		addInport("inFromGrid");
		addOutport("out");
		// addOutport("out");
		observation_time = Observation_time;
		initialize();
	}

	public Trancducer() {
		this("tranqooder", 200);
	}

	public void initialize() {
		phase = "active";
		sigma = observation_time;
		clock = 0;
		total_ta = 0;
		super.initialize();
		holdIn("active", INFINITY);
	}

	public void showState() {
		super.showState();
	}

	public void deltext(double e, message x) {
		System.out.println("--------Transduceer elapsed time =" + e);
		System.out.println("-------------------------------------");
		clock = clock + e;
		Continue(e);
		for (int i = 0; i < x.size(); i++) {
			if (messageOnPort(x, "inFromHouse", i)) {
				houseUsage.add(((Energy) x.getValOnPort("inFromHouse", i)).getEnergy());
				System.out.println();
			}
			if (messageOnPort(x, "inFromGrid", i)) {
				extraEnergy.add(((Energy) x.getValOnPort("inFromGrid", i)).getEnergy());
				//count++;
				/*
				 * if(arrived.contains(val)){
				 * System.out.println("Debug: val="+val); entity ent =
				 * (entity)arrived.assoc(val.getName());
				 * 
				 * doubleEnt num = (doubleEnt)ent; double arrival_time =
				 * num.getv();
				 * 
				 * double turn_around_time = clock - arrival_time; total_ta =
				 * total_ta + turn_around_time; solved.put(val, new
				 * doubleEnt(clock)); }
				 */
				/*if (arrived.containsKey(val.getName())) {
					// entity ent = (entity)arrived.assoc(val.getName());
					entity ent = (entity) arrived.get(val.getName());

					doubleEnt num = (doubleEnt) ent;
					double arrival_time = num.getv();

					double turn_around_time = clock - arrival_time;
					total_ta = total_ta + turn_around_time;
					solved.put(val.getName(), new doubleEnt(clock));
				}*/
			}
		}
	}

	public void deltint() {
		clock = clock + sigma;
		holdIn("active", clock);
		//passivate();
	}

	public message out() {
		message m = new message();
		
		/*content con1 = makeContent("TA", new entity(" " + compute_TA()));
		content con2 = makeContent("out", new entity(count.toString()));
		content con3 = makeContent("Thru", new entity(" " + compute_Thru()));
		m.add(con1);
		m.add(con2);
		m.add(con3);*/
		return m;
	}


}
