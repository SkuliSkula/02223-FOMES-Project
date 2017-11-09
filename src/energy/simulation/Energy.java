package energy.simulation;

import GenCol.entity;

public class Energy extends entity {
	public String name;
	public double energy;

	public Energy(String name) {
		super(name);
		this.name = name;
		this.energy = 10;
	}

	public Energy(String name, double energy) {
		super(name);
		this.name = name;
		this.energy = energy;
	}

	public void print() {
		System.out.println("Energy unit: " + name + " energy: " + energy);
	}
}
