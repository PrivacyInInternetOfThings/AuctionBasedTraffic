public enum VEHICLETYPE {
	AMBULANCE(1), FIRETRUCK(0.9), POLICE(0.9), CAR(0.9), TAXI(0.9), VAN(0.9), BUS(0.9), MINIBUS(0.9), MOTORCYCLE(0.9), AGRICULTURALVEHICLE(0.9), OTHER(0.9);

	double utilityValue;

	private VEHICLETYPE(double val) {
		this.utilityValue = val;
	}

	public double getValue() {
		return this.utilityValue;
	}

	public static String abbreviation(VEHICLETYPE v) {
		if (v == VEHICLETYPE.AMBULANCE) {
			return "AMB";
		} else if (v == VEHICLETYPE.FIRETRUCK) {
			return "FIR";
		} else if (v == VEHICLETYPE.POLICE) {
			return "POL";
		} else if (v == VEHICLETYPE.CAR) {
			return "CAR";
		} else if (v == VEHICLETYPE.TAXI) {
			return "TAX";
		} else if (v == VEHICLETYPE.VAN) {
			return "VAN";
		} else if (v == VEHICLETYPE.BUS) {
			return "BUS";
		} else if (v == VEHICLETYPE.MINIBUS) {
			return "MBS";
		} else if (v == VEHICLETYPE.MOTORCYCLE) {
			return "MTR";
		} else if (v == VEHICLETYPE.AGRICULTURALVEHICLE) {
			return "AGR";
		} else if(v == VEHICLETYPE.OTHER){
			return "OTH";
		}
		return null;
	}
	
	
	public static VEHICLETYPE getById(int id){
		switch (id) {
		case 2:
		case 3:
		case 4:
		case 5:
		case 97:
			return VEHICLETYPE.MOTORCYCLE;
		case 8:
			return VEHICLETYPE.TAXI;
		case 9:
			return VEHICLETYPE.CAR;
		case 10:
			return VEHICLETYPE.MINIBUS;
		case 11:
			return VEHICLETYPE.BUS;
		case 17:
			return VEHICLETYPE.AGRICULTURALVEHICLE;
		case 19:
			return VEHICLETYPE.VAN;
		
		default:
			return VEHICLETYPE.OTHER;
		}
//		{ "_id" : ObjectId("58de700ab1365278d9165bf5"), "code" : 1, "label" : "Pedal cycle" }
//		{ "_id" : ObjectId("58de700ab1365278d9165bf6"), "code" : 2, "label" : "Motorcycle 50cc and under" }
//		{ "_id" : ObjectId("58de700ab1365278d9165bf7"), "code" : 3, "label" : "Motorcycle 125cc and under" }
//		{ "_id" : ObjectId("58de700ab1365278d9165bf8"), "code" : 4, "label" : "Motorcycle over 125cc and up to 500cc" }
//		{ "_id" : ObjectId("58de700ab1365278d9165bf9"), "code" : 5, "label" : "Motorcycle over 500cc" }
//		{ "_id" : ObjectId("58de700ab1365278d9165bfa"), "code" : 8, "label" : "Taxi/Private hire car" }
//		{ "_id" : ObjectId("58de700ab1365278d9165bfb"), "code" : 9, "label" : "Car" }
//		{ "_id" : ObjectId("58de700ab1365278d9165bfc"), "code" : 10, "label" : "Minibus (8 - 16 passenger seats)" }
//		{ "_id" : ObjectId("58de700ab1365278d9165bfd"), "code" : 11, "label" : "Bus or coach (17 or more pass seats)" }
//		{ "_id" : ObjectId("58de700ab1365278d9165bfe"), "code" : 16, "label" : "Ridden horse" }
//		{ "_id" : ObjectId("58de700ab1365278d9165bff"), "code" : 17, "label" : "Agricultural vehicle" }
//		{ "_id" : ObjectId("58de700ab1365278d9165c00"), "code" : 18, "label" : "Tram" }
//		{ "_id" : ObjectId("58de700ab1365278d9165c01"), "code" : 19, "label" : "Van / Goods 3.5 tonnes mgw or under" }
//		{ "_id" : ObjectId("58de700ab1365278d9165c02"), "code" : 20, "label" : "Goods over 3.5t. and under 7.5t" }
//		{ "_id" : ObjectId("58de700ab1365278d9165c03"), "code" : 21, "label" : "Goods 7.5 tonnes mgw and over" }
//		{ "_id" : ObjectId("58de700ab1365278d9165c04"), "code" : 22, "label" : "Mobility scooter" }
//		{ "_id" : ObjectId("58de700ab1365278d9165c05"), "code" : 23, "label" : "Electric motorcycle" }
//		{ "_id" : ObjectId("58de700ab1365278d9165c06"), "code" : 90, "label" : "Other vehicle" }
//		{ "_id" : ObjectId("58de700ab1365278d9165c07"), "code" : 97, "label" : "Motorcycle - unknown cc" }
//		{ "_id" : ObjectId("58de700ab1365278d9165c08"), "code" : 98, "label" : "Goods vehicle - unknown weight" }
		
		
	}
}