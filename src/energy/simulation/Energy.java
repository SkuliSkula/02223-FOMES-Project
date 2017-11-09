package energy.simulation;

import GenCol.entity;

public class Energy extends entity {
	private double energy;

	public Energy() {
		this.energy = 0;
	}

	public Energy(double energy) {
		this.energy = energy;
	}

	public double getEnergy() {
		return this.energy;
	}

	public void print() {
		System.out.println("Energy: " + energy);
	}
}
