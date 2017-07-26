public enum MALFUNCTIONTYPE {
	WHEEL(0.2), MOTOR(0.3), LIGHTSANDSENSORS(0.5), NOMALFUNCTION(1);

	double utilityValue;

	private MALFUNCTIONTYPE(double val) {
		this.utilityValue = val;
	}

	public double getValue() {
		return this.utilityValue;
	}

	public static String abbreviation(MALFUNCTIONTYPE m) {
		if (m == MALFUNCTIONTYPE.WHEEL) {
			return "WHL";
		} else if (m == MALFUNCTIONTYPE.MOTOR) {
			return "MTR";
		} else if (m == MALFUNCTIONTYPE.LIGHTSANDSENSORS) {
			return "LGT";
		} else if (m == MALFUNCTIONTYPE.NOMALFUNCTION) {
			return "NON";
		}
		return null;
	}
	
	public static MALFUNCTIONTYPE getMalfunctionById(int id){
		
		switch (id) {
		case 0:
			return MALFUNCTIONTYPE.NOMALFUNCTION;
		case 1:
			return MALFUNCTIONTYPE.WHEEL;
		case 2:
			return MALFUNCTIONTYPE.MOTOR;
		case 3:
			return MALFUNCTIONTYPE.LIGHTSANDSENSORS;
		default:
			break;
		}
		
		return null;
	}
	
}