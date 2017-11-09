package energy.simulation;

/*This class will simulate Tesla Model S P100D*/
public class ElectricCar {
	private static final double BATTERY_CAPACITY = 100; // This is 100kW
	
	public ElectricCar() {
		this.carBattery = new Battery(BATTERY_CAPACITY);
	}
	
	
}
