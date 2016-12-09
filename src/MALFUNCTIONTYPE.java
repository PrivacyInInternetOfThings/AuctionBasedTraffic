public enum MALFUNCTIONTYPE {
	WHEEL(0.2), MOTOR(0.3), LIGHTSANDSENSORS(0.5), NOMALFUNCTION(1);

	double utilityValue;

	private MALFUNCTIONTYPE(double val) {
		this.utilityValue = val;
	}

	public double getValue() {
		return this.utilityValue;
	}
}