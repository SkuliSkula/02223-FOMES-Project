package energy.simulation;

/*This class will simulate all batteries in this project*/
public class Battery {
	private double capacity;
	private String batteryBrand;
	private boolean isCharging;
	private double status; // How much kW does the battery currently have
	
	public Battery(double capacity) {
		this.capacity = capacity;
		this.batteryBrand = "";
	}
	
	public Battery(double capacity, String brand) {
		this.capacity = capacity;
		this.batteryBrand = brand;
	}
	
	public boolean isCharging() {
		return this.isCharging;
	}
	
	public boolean isFull() {
		return this.capacity == this.status; // For simplicity just check the capacity equals the status
	}
	
	public void setBatteryBrand(String brand) {
		this.batteryBrand = brand;
	}
	
	public String getBatteryBrand() {
		return this.batteryBrand;
	}
}
