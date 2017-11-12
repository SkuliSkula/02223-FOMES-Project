package energy.simulation;

import GenCol.entity;

public class Energy extends entity {
	private double energy;
	private double time;

	public Energy() {
		this.energy = 0;
		this.time = 0;
	}

	public Energy(double energy, double time) {
		this.energy = energy;
		this.time = time;
	}

	public double getEnergy() {
		return this.energy;
	}

	public void setEnergy(double energy) {
		this.energy = energy;
	}

	public void print() {
		System.out.println("Energy: " + energy);
	}

	public double getTime() {
		return this.time;
	}

	public void setTime(double time) {
		this.time = time;
	}
	
	public String getName(){
	     return Double.toString(energy);
	}
}
