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
	public double lostPrivacy;
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
	
	public static double proportionVehicleType = 0.3;
	public static double proportionJourneyType = 0.45;
	public static double proportionMalfunctionType = 0.1;
	public static double proportionVehicleAge = 0.15;

	public double utility;
	
	public static int randomSeed =100;
	public static Random rand = new Random(randomSeed);
	
	public double threshold;
	
	public static NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
	public static DecimalFormat df = (DecimalFormat) nf;
	public static NumberFormat formatter = df;

	public Vehicle(VEHICLETYPE vehicle, JOURNEYPURPOSE journey, MALFUNCTIONTYPE malfunction, int age) {
		this.vehicleType = vehicle;
		this.journeyType = journey;
		this.malfunctionType = malfunction;
		this.ageOfCar = age;
		this.utility = 0;
		this.lostPrivacy = 0;
		this.threshold = 0.8;
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

	public void setPrivacy(double vehicle, double journey, double malfunction, double people) {
		this.privacy[0] = vehicle;
		this.privacy[1] = journey;
		this.privacy[2] = malfunction;
		this.privacy[3] = people;
		totalPrivacy = vehicle + journey + malfunction + people;
		for (int i = 0; i < 4; i++) {
			// this.privacy[i] /= 4;
		}
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

	public int getMinPrivacy() {
		int minIndex = -1;
		double minVal = 1;
		ArrayList<Integer> list = getEnabledIndex();
		for (int i = 0; i < list.size(); i++) {
			if (privacy[i] < minVal) {
				minIndex = list.get(i);
				minVal = privacy[i];
			}
		}
		if (minIndex >= 0)
			enabled[minIndex] = true;

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
	
	public double makeOffer() {
		int min = getMinPrivacy();
		if (min == 0 && privacy[0] < this.threshold) {
			System.out.println("\tVehicle Type Offer\n\tprivacy = " + privacy[0] + " utility = "
					+ formatter.format(this.vehicleType.getValue() * proportionVehicleType));
			this.lostPrivacy += privacy[0];
			utility += this.vehicleType.getValue() * proportionVehicleType;
			return this.vehicleType.getValue() * proportionVehicleType;
		}
		if (min == 1 && privacy[1] < this.threshold) {
			System.out.println("\tJourney Type Offer\n\tprivacy = " + privacy[1] + " utility = "
					+ formatter.format(this.journeyType.getValue() * proportionJourneyType));
			this.lostPrivacy += privacy[1];
			utility += this.journeyType.getValue() * proportionJourneyType;
			return this.journeyType.getValue() * proportionJourneyType;
		}
		if (min == 2 && privacy[2] < this.threshold) {
			System.out.println("\tMalfunction Type Offer\n\tprivacy = " + privacy[2] + " utility = "
					+ formatter.format(this.malfunctionType.getValue() * proportionMalfunctionType));
			this.lostPrivacy += privacy[2];
			utility += this.malfunctionType.getValue() * proportionMalfunctionType;
			return this.malfunctionType.getValue() * proportionMalfunctionType;
		}
		if (min == 3 && privacy[3] < this.threshold) {
			System.out.println("\tAge of Vehicle Offer\n\tprivacy = " + privacy[3] + " utility = "
					+ formatter.format(this.ageOfCar / 50.0 * proportionVehicleAge));
			this.lostPrivacy += privacy[3];
			utility += this.ageOfCar / 50.0 * proportionVehicleAge;
			return this.ageOfCar / 50.0 * proportionVehicleAge;
		}
		System.out.println("\tNo Offer");
		return 0;
	}

	public double makeOffer(double opponentOffer,boolean isTurnBased) {
		double newOffer = 0;
		double offerLostPrivacy = 0;
		int min;
		do {
			if(isTurnBased) {
				min = getMinPrivacy(isTurnBased);
			} else {
				min = getMinPrivacy();
			}
			if (min == 0 && privacy[0] < this.threshold) {
				System.out.println("\tVehicle Type Offer\n\tprivacy = " + privacy[0] + " utility = "
						+ formatter.format(this.vehicleType.getValue() * proportionVehicleType));
				offerLostPrivacy += privacy[0];
				utility += this.vehicleType.getValue() * proportionVehicleType;
				newOffer += this.vehicleType.getValue() * proportionVehicleType;
			}
			if (min == 1 && privacy[1] < this.threshold) {
				System.out.println("\tJourney Type Offer\n\tprivacy = " + privacy[1] + " utility = "
						+ formatter.format(this.journeyType.getValue() * proportionJourneyType));
				offerLostPrivacy += privacy[1];
				utility += this.journeyType.getValue() * proportionJourneyType;
				newOffer += this.journeyType.getValue() * proportionJourneyType;
			}
			if (min == 2 && privacy[2] < this.threshold) {
				System.out.println("\tMalfunction Type Offer\n\tprivacy = " + privacy[2] + " utility = "
						+ formatter.format(this.malfunctionType.getValue() * proportionMalfunctionType));
				offerLostPrivacy += privacy[2];
				utility += this.malfunctionType.getValue() * proportionMalfunctionType;
				newOffer += this.malfunctionType.getValue() * proportionMalfunctionType;
			}
			if (min == 3 && privacy[3] < this.threshold) {
				System.out.println("\tAge of Vehicle Offer\n\tprivacy = " + privacy[3] + " utility = "
						+ formatter.format(this.ageOfCar / 50.0 * proportionVehicleAge));
				offerLostPrivacy += privacy[3];
				utility += this.ageOfCar / 50.0 * proportionVehicleAge;
				newOffer += this.ageOfCar / 50.0 * proportionVehicleAge;
			}
			if (min == -1) {
				break;
			}
		} while (newOffer <= opponentOffer);

		if (newOffer <= opponentOffer) {
			System.out.println();
			System.out.println("\t--No Offer--");
			return 0;
		}
		this.lostPrivacy += offerLostPrivacy;
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

			res += this.malfunctionType.getValue() * proportionMalfunctionType;
		}
		if (privacy[3] < this.threshold) {

			res += this.ageOfCar / 50.0 * proportionVehicleAge;
		}
		return res;
	}
	
	public String toString() {
		return this.id + "\t" + VEHICLETYPE.abbreviation(this.vehicleType) + "\t"
				+ JOURNEYPURPOSE.abbreviation(this.journeyType) + "\t"
				+ MALFUNCTIONTYPE.abbreviation(this.malfunctionType) + "\t" + this.ageOfCar;
	}

}
