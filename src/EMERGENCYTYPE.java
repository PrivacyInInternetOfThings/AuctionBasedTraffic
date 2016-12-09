public enum EMERGENCYTYPE {
	PATIENT(1), 
	LATEFORWORK(0.5), 
	LATEFORSCHOOL(0.5), 
	NOEMERGENCY(0.1);

	double utilityValue;

	private EMERGENCYTYPE(double val) {
		this.utilityValue = val;
	}

	public double getValue() {
		return this.utilityValue;
	}

}