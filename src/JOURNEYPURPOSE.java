public enum JOURNEYPURPOSE {
	PARTOFWORK(1), COMMUTINGTOWORK(0.8), SCHOOL(0.8), OTHER(0.8);

	double utilityValue;

	private JOURNEYPURPOSE(double val) {
		this.utilityValue = val;
	}

	public double getValue() {
		return this.utilityValue;
	}

	public static String abbreviation(JOURNEYPURPOSE e) {
		switch (e) {
		case PARTOFWORK:
			return "POW";
		case COMMUTINGTOWORK:
			return "CTW";
		case SCHOOL:
			return "SCH";
		case OTHER:
			return "OTH";
		default:
			return null;
		}
	}
	
	
	public static JOURNEYPURPOSE getJourneyPurposeById(int id){
		switch (id) {
		case 1:
			return JOURNEYPURPOSE.PARTOFWORK;
		case 2:
			return JOURNEYPURPOSE.COMMUTINGTOWORK;
		case 3:
		case 4:
			return JOURNEYPURPOSE.SCHOOL;
		case 5:
		case 6:
		case 15:
		case -1:
			return JOURNEYPURPOSE.OTHER;

		default:
			break;
		}
		
		return null;
	}
}
//
//{ "_id" : ObjectId("58de7005b1365278d9165819"), "code" : 1, "label" : "Journey as part of work" }
//{ "_id" : ObjectId("58de7005b1365278d916581a"), "code" : 2, "label" : "Commuting to/from work" }
//{ "_id" : ObjectId("58de7005b1365278d916581b"), "code" : 3, "label" : "Taking pupil to/from school" }
//{ "_id" : ObjectId("58de7005b1365278d916581c"), "code" : 4, "label" : "Pupil riding to/from school" }
//{ "_id" : ObjectId("58de7005b1365278d916581d"), "code" : 5, "label" : "Other" }
//{ "_id" : ObjectId("58de7005b1365278d916581e"), "code" : 6, "label" : "Not known" }
//{ "_id" : ObjectId("58de7005b1365278d916581f"), "code" : 15, "label" : "Other/Not known (2005-10)" }
//{ "_id" : ObjectId("58de7005b1365278d9165820"), "code" : -1, "label" : "Data missing or out of range" }