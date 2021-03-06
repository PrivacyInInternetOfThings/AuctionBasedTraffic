import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class Vehicle {

	public VEHICLETYPE vehicleType;
	public JOURNEYPURPOSE journeyType;
	public MALFUNCTIONTYPE malfunctionType;
	public int ageOfCar;
	public int ageBandOfDriver;
	public double lostPrivacy;
	public double totalShareablePrivacy = 0;
	public double totalPrivacy;
	public boolean isTurn;
	public int id;
	public static int lastId = 0;
	// public double vehiclePrivacy;
	// public double journeyPrivacy;
	// public double malfunctionPrivacy;
	// public double peoplePrivacy;
	public double[] privacy = new double[4];
	public boolean[] enabled = new boolean[4];
	
	public static double proportionVehicleType = 0.25;
	public static double proportionJourneyType = 0.25;
	public static double proportionAgeBand = 0.25;
	public static double proportionVehicleAge = 0.25;

	public double utility;
	public int reference = 0;
	public static int randomSeed =100;
	public static Random rand = new Random(randomSeed);
	
	public double threshold;
	
	public static NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
	public static DecimalFormat df = (DecimalFormat) nf;
	public static NumberFormat formatter = df;
    public String info = "";

	
	
	public Vehicle(
			VEHICLETYPE vehicle, 
			JOURNEYPURPOSE journey, 
//			MALFUNCTIONTYPE malfunction,
			int ageBandOfDriver,
			int ageOfCar
			) {
		this.vehicleType = vehicle;
		this.journeyType = journey;
//		this.malfunctionType = malfunction;
		this.ageBandOfDriver = ageBandOfDriver;
		this.ageOfCar = ageOfCar;
		this.utility = 0;
		this.lostPrivacy = 0;
		this.threshold = 1;
		this.id = Vehicle.lastId;
		Vehicle.lastId++;
	}

	public void clear() {
		for (int i = 0; i < 4; i++) {
			enabled[i] = false;
		}
		this.lostPrivacy = 0;
		this.utility = 0;
	}

	public void setPrivacy(
			double vehicle, 
			double journey, 
			double malfunction, 
			double people) {
		this.privacy[0] = vehicle;
		this.privacy[1] = journey;
		this.privacy[2] = malfunction;
		this.privacy[3] = people;
		totalPrivacy = vehicle + journey + malfunction + people;
		
		/*
		 * for (int i = 0; i < 4; i++) { System.out.print(privacy[i] + " "); }
		 * System.out.println(); System.out.println();
		 */
	}

	public void setPrivacyRandom() {
		totalPrivacy = 0;
		for (int i = 0; i < 4; i++) {
			this.privacy[i] = rand.nextDouble();
			privacy[i] = (int) (privacy[i] * 1000) / 1000.0;
			totalPrivacy += privacy[i];
			
		}
	}
	
	public void setShareablePrivacy(){

		totalShareablePrivacy = 0;
		
		if (privacy[0] < this.threshold) {

			totalShareablePrivacy += privacy[0];
		}
		if (privacy[1] < this.threshold) {

			totalShareablePrivacy += privacy[1];
		}
		if (privacy[2] < this.threshold) {

			totalShareablePrivacy += privacy[2];
		}
		if (privacy[3] < this.threshold) {

			totalShareablePrivacy += privacy[3];
		}
	}
	
	public void setReference(int ref){
		this.reference = ref;
	}
	public void setThreshold(double t) {
		this.threshold = t;
	}
	/**
	 * 
	 * @return indexes of properties that are not enabled
	 */
	public ArrayList<Integer> getEnabledIndex() {

		ArrayList<Integer> list = new ArrayList<>();
		for (int i = 0; i < enabled.length; i++) {
			if (!enabled[i]) {
				list.add(i);
			}
		}
		return list;
	}

//	public int getMinPrivacy() {
//		int minIndex = -1;
//		double minVal = 1;
//		ArrayList<Integer> list = getEnabledIndex();
//		for (int i = 0; i < list.size(); i++) {
//			if (privacy[i] < minVal) {
//				minIndex = list.get(i);
//				minVal = privacy[i];
//			}
//		}
//		if (minIndex >= 0)
//			enabled[minIndex] = true;
//
//		return minIndex;
//	}

	public int getMinPrivacy() {
		int minIndex = -1;
		double minVal = 1;
		int in=0;
		//ArrayList<Integer> list = getEnabledIndex();
		//System.out.println(enabled[0]+" "+enabled[1]+" "+enabled[2]+" "+enabled[3]);
		
		for (int i = 0; i < enabled.length; i++) {
			if(!enabled[i]){
				if (privacy[i] < minVal) {
					minIndex = in;
					minVal = privacy[i];
				}
			}
			in++;
		}
		if (minIndex >= 0)
			enabled[minIndex] = true;

		//System.out.println("min index: "+minIndex);
		return minIndex;
	}

	
	public int getMinPrivacy(boolean isTurnBased) {
		int minIndex = -1;
		ArrayList<Integer> list = getEnabledIndex();
		if (!list.isEmpty()) {
			minIndex = list.get(0);
			enabled[minIndex] = true;
		}
		return minIndex;
	}
	
	public double makeBidAllOffer() {
		//int min;
		
		info = "";

		info += "\tVehicle Type Offer\tprivacy = "
				+ privacy[0]
				+ " utility = "
				+ formatter.format(this.vehicleType.getValue()
						* proportionVehicleType);
		this.lostPrivacy += privacy[0];
		utility += this.vehicleType.getValue() * proportionVehicleType;

		info += "\n\tJourney Type Offer\tprivacy = "
				+ privacy[1]
				+ " utility = "
				+ formatter.format(this.journeyType.getValue()
						* proportionJourneyType);
		this.lostPrivacy += privacy[1];
		utility += this.journeyType.getValue() * proportionJourneyType;

		info += "\n\tAge Band of Driver Offer\tprivacy = "
				+ privacy[2]
				+ " utility = "
				+ formatter.format(getBandValue(this.ageBandOfDriver)
						* proportionAgeBand);
		this.lostPrivacy += privacy[2];
		utility += getBandValue(this.ageBandOfDriver) * proportionAgeBand;

		info += "\n\tAge of Vehicle Offer\tprivacy = " + privacy[3]
				+ " utility = " + formatter.format(proportionVehicleAge);
		this.lostPrivacy += privacy[3];
		utility += proportionVehicleAge;
		
		return utility;

	}
	
	
	public double makeOffer() {
		int min;
		info = "";
		
		if(isTurn) {
			min = getMinPrivacy(isTurn);
		} else {
			min = getMinPrivacy();
		}
		if (min == 0 && privacy[0] < this.threshold) {
			info += "\tVehicle Type Offer\tprivacy = " + privacy[0] + " utility = " + formatter.format(this.vehicleType.getValue() * proportionVehicleType)+"\n";
			this.lostPrivacy += privacy[0];
			utility += this.vehicleType.getValue() * proportionVehicleType;
			
			return this.vehicleType.getValue() * proportionVehicleType;
		}
		if (min == 1 && privacy[1] < this.threshold) {
			info += "\tJourney Type Offer\tprivacy = " + privacy[1] + " utility = " + formatter.format(this.journeyType.getValue() * proportionJourneyType)+"\n";
			this.lostPrivacy += privacy[1];
			utility += this.journeyType.getValue() * proportionJourneyType;
			
			return this.journeyType.getValue() * proportionJourneyType;
		}
		if (min == 2 && privacy[2] < this.threshold) {
			info += "\tAge Band of Driver Offer\tprivacy = " + privacy[2] + " utility = " + formatter.format(getBandValue(this.ageBandOfDriver) * proportionAgeBand)+"\n";
			this.lostPrivacy += privacy[2];
			utility += getBandValue(this.ageBandOfDriver) * proportionAgeBand;
			
			return getBandValue(this.ageBandOfDriver) * proportionAgeBand;
		}
		if (min == 3 && privacy[3] < this.threshold) {
			info += "\tAge of Vehicle Offer\tprivacy = " + privacy[3] + " utility = " + formatter.format(proportionVehicleAge)+"\n";
			this.lostPrivacy += privacy[3];
			utility += proportionVehicleAge;
			
			return proportionVehicleAge;
		}
		info += "\tNo Offer";
		
		return 0;
	}

//	public double makeOffer(double opponentOffer) {
//		double newOffer = 0;
//		double offerLostPrivacy = 0;
//		int min;
//		do {
//			min = getMinPrivacy();
//			if (min == 0 && privacy[0] < this.threshold) {
//				System.out.println("\tVehicle Type Offer\n\tprivacy = " + privacy[0] + " utility = "
//						+ formatter.format(this.vehicleType.getValue() * proportionVehicleType));
//				offerLostPrivacy += privacy[0];
//				utility += this.vehicleType.getValue() * proportionVehicleType;
//				newOffer += this.vehicleType.getValue() * proportionVehicleType;
//			}
//			if (min == 1 && privacy[1] < this.threshold) {
//				System.out.println("\tJourney Type Offer\n\tprivacy = " + privacy[1] + " utility = "
//						+ formatter.format(this.journeyType.getValue() * proportionJourneyType));
//				offerLostPrivacy += privacy[1];
//				utility += this.journeyType.getValue() * proportionJourneyType;
//				newOffer += this.journeyType.getValue() * proportionJourneyType;
//			}
//			if (min == 2 && privacy[2] < this.threshold) {
//				System.out.println("\tAge Band of Driver Offer\n\tprivacy = " + privacy[2] + " utility = "
//						+ formatter.format(this.ageBandOfDriver / 20.0 * proportionAgeBand));
//				offerLostPrivacy += privacy[2];
//				utility += this.ageBandOfDriver / 20.0 * proportionAgeBand;
//				newOffer += this.ageBandOfDriver / 20.0 * proportionAgeBand;
//			}
//			if (min == 3 && privacy[3] < this.threshold) {
//				System.out.println("\tAge of Vehicle Offer\n\tprivacy = " + privacy[3] + " utility = "
//						+ formatter.format(this.ageOfCar / 50.0 * proportionVehicleAge));
//				offerLostPrivacy += privacy[3];
//				utility += this.ageOfCar / 50.0 * proportionVehicleAge;
//				newOffer += this.ageOfCar / 50.0 * proportionVehicleAge;
//			}
//			if (min == -1) {
//				break;
//			}
//		} while (newOffer <= opponentOffer);
//
//		if (newOffer <= opponentOffer) {
//			System.out.println();
//			System.out.println("\t--No Offer--");
//			return 0;
//		}
//		this.lostPrivacy += offerLostPrivacy;
//		return newOffer;
//
//	}
	
	public double makeOffer(double opponentOffer) {
		double newOffer = 0;
		double offerLostPrivacy = 0;
		boolean stopFlag = false;
		int min = 10;
		info = "";
		
		do {
			min = getMinPrivacy();
			if (min == 0 && privacy[0] < this.threshold) {
				info += "\tVehicle Type Offer\tprivacy = " + privacy[0] + " utility = " + formatter.format(this.vehicleType.getValue() * proportionVehicleType);
				offerLostPrivacy += privacy[0];
				newOffer += this.vehicleType.getValue() * proportionVehicleType;
			}
			if (min == 1 && privacy[1] < this.threshold) {
				info += "\tJourney Type Offer\tprivacy = " + privacy[1] + " utility = " + formatter.format(this.journeyType.getValue() * proportionJourneyType);
				offerLostPrivacy += privacy[1];
				newOffer += this.journeyType.getValue() * proportionJourneyType;
			}
			if (min == 2 && privacy[2] < this.threshold) {
				info += "\tAge Band of Driver Offer\tprivacy = " + privacy[2] + " utility = " + formatter.format(getBandValue(this.ageBandOfDriver) * proportionAgeBand);
				offerLostPrivacy += privacy[2];
				newOffer += getBandValue(this.ageBandOfDriver) * proportionAgeBand;
			}
			if (min == 3 && privacy[3] < this.threshold) {
				info += "\tAge of Vehicle Offer\tprivacy = " + privacy[3] + " utility = " + formatter.format(proportionVehicleAge);
				offerLostPrivacy += privacy[3];
				newOffer += proportionVehicleAge;
			}
			if (min == -1) {
				break;
			}

			info += "\n candidate:"+ (this.utility + newOffer)+" opp:"+opponentOffer+ "\n";
			if((this.utility + newOffer) >= opponentOffer)
				stopFlag=true;
		} while (!stopFlag);
		
		if (min == -1 && this.utility + newOffer < opponentOffer) {
			info += "\n\t--No Offer--";
			
			return 0;
		}
		this.lostPrivacy += offerLostPrivacy;
		this.utility += newOffer;
		
		return newOffer;

	}
	public double calculatePossibleUtilityPoints() {
		double res = 0;

		if (privacy[0] < this.threshold) {

			res += this.vehicleType.getValue() * proportionVehicleType;
		}
		if (privacy[1] < this.threshold) {

			res += this.journeyType.getValue() * proportionJourneyType;
		}
		if (privacy[2] < this.threshold) {

			res += getBandValue(this.ageBandOfDriver) * proportionAgeBand;
		}
		if (privacy[3] < this.threshold) {

			res += proportionVehicleAge;
		}
		return res;
	}
	
	public int numberOfShareable() {
		int res = 0;

		if (privacy[0] < this.threshold) {

			res += 1;
		}
		if (privacy[1] < this.threshold) {

			res += 1;
		}
		if (privacy[2] < this.threshold) {

			res += 1;
		}
		if (privacy[3] < this.threshold) {

			res += 1;
		}
		return res;
	}
	
	public String toString() {
		return this.id + "\t" + VEHICLETYPE.abbreviation(this.vehicleType) + "\t"
				+ JOURNEYPURPOSE.abbreviation(this.journeyType) + "\t"
				+ this.ageBandOfDriver + "\t" + this.ageOfCar;
	}
	
	public double getBandValue(int value){
		double res=0.8;
		
		if(value == 1 || value == 2 || value==10 || value==11)
			res = 1.0;
		return res;
	}

}