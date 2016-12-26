import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class Vehicle {

	public VEHICLETYPE vehicleType;
	public EMERGENCYTYPE emergencyType;
	public MALFUNCTIONTYPE malfunctionType;
	public int numOfPeople;
	public double lostPrivacy;
	public double totalPrivacy;

	// public double vehiclePrivacy;
	// public double emergencyPrivacy;
	// public double malfunctionPrivacy;
	// public double peoplePrivacy;
	public double[] privacy = new double[4];

	public boolean vehicleTypeEnabled = false;
	public boolean emergencyTypeEnabled = false;
	public boolean malfunctionTypeEnabled = false;
	public boolean numOfPeopleEnabled = false;
	public boolean[] enabled = new boolean[4];

	public double utility;

	public Vehicle(VEHICLETYPE vehicle, EMERGENCYTYPE emergency, MALFUNCTIONTYPE malfunction, int num) {
		this.vehicleType = vehicle;
		this.emergencyType = emergency;
		this.malfunctionType = malfunction;
		this.numOfPeople = num;
		this.utility = 0;
		this.lostPrivacy = 0;
		/*
		 * setPrivacyRandom(); for (int i = 0; i < 4; i++) {
		 * System.out.print(privacy[i] + " "); } System.out.println();
		 * System.out.println();
		 */
	}

	public void clear() {
		for (int i = 0; i < 4; i++) {
			enabled[i] = false;
		}
		this.lostPrivacy = 0;
		this.utility = 0;
	}

	public void setPrivacy(double vehicle, double emergency, double malfunction, double people) {
		this.privacy[0] = vehicle;
		this.privacy[1] = emergency;
		this.privacy[2] = malfunction;
		this.privacy[3] = people;
		totalPrivacy = vehicle + emergency + malfunction + people;
		for (int i = 0; i < 4; i++) {
			// this.privacy[i] /= 4;
		}
		/*
		 * for (int i = 0; i < 4; i++) { System.out.print(privacy[i] + " "); }
		 * System.out.println(); System.out.println();
		 */
	}

	public void setPrivacyRandom() {
		Random rand = new Random();
		totalPrivacy = 0;

		for (int i = 0; i < 4; i++) {
			this.privacy[i] = rand.nextDouble();
			privacy[i] = (int) (privacy[i] * 1000) / 1000.0;
			totalPrivacy += this.privacy[i];
		}
		for (int i = 0; i < 4; i++) {
			// this.privacy[i] /= totalPrivacy;
			this.privacy[i] /= 4;
		}
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

	public double makeOffer() {
		int min = getMinPrivacy();
		if (min == 0 && privacy[0] < 0.2) {
			System.out.println("\tVehicle Type Offer\n\tprivacy = " + privacy[0] + " utility = "
					+ Main.formatter.format(this.vehicleType.getValue() * Main.proportionVehicleType));
			this.lostPrivacy += privacy[0];
			utility += this.vehicleType.getValue() * Main.proportionVehicleType;
			return this.vehicleType.getValue() * Main.proportionVehicleType;
		}
		if (min == 1 && privacy[1] < 0.2) {
			System.out.println("\tEmergency Type Offer\n\tprivacy = " + privacy[1] + " utility = "
					+ Main.formatter.format(this.emergencyType.getValue() * Main.proportionEmergencyType));
			this.lostPrivacy += privacy[1];
			utility += this.emergencyType.getValue() * Main.proportionEmergencyType;
			return this.emergencyType.getValue() * Main.proportionEmergencyType;
		}
		if (min == 2 && privacy[2] < 0.2) {
			System.out.println("\tMalfunction Type Offer\n\tprivacy = " + privacy[2] + " utility = "
					+ Main.formatter.format(this.malfunctionType.getValue() * Main.proportionMalfunctionType));
			this.lostPrivacy += privacy[2];
			utility += this.malfunctionType.getValue() * Main.proportionMalfunctionType;
			return this.malfunctionType.getValue() * Main.proportionMalfunctionType;
		}
		if (min == 3 && privacy[3] < 0.2) {
			System.out.println("\tNumber of People Offer\n\tprivacy = " + privacy[3] + " utility = "
					+ Main.formatter.format(this.numOfPeople / 50.0 * Main.proportionNumberPeople));
			this.lostPrivacy += privacy[3];
			utility += this.numOfPeople / 50.0 * Main.proportionNumberPeople;
			return this.numOfPeople / 50.0 * Main.proportionNumberPeople;
		}
		return 0;
	}

	public double makeOffer(double opponentOffer) {
		double newOffer = 0;
		double offerLostPrivacy = 0;
		int min;
		do {
			min = getMinPrivacy();
			if (min == 0 && privacy[0] < 0.2) {
				System.out.println("\tVehicle Type Offer\n\tprivacy = " + privacy[0] + " utility = "
						+ Main.formatter.format(this.vehicleType.getValue() * Main.proportionVehicleType));
				offerLostPrivacy += privacy[0];
				utility += this.vehicleType.getValue() * Main.proportionVehicleType;
				newOffer += this.vehicleType.getValue() * Main.proportionVehicleType;
			}
			if (min == 1 && privacy[1] < 0.2) {
				System.out.println("\tEmergency Type Offer\n\tprivacy = " + privacy[1] + " utility = "
						+ Main.formatter.format(this.emergencyType.getValue() * Main.proportionEmergencyType));
				offerLostPrivacy += privacy[1];
				utility += this.emergencyType.getValue() * Main.proportionEmergencyType;
				newOffer += this.emergencyType.getValue() * Main.proportionEmergencyType;
			}
			if (min == 2 && privacy[2] < 0.2) {
				System.out.println("\tMalfunction Type Offer\n\tprivacy = " + privacy[2] + " utility = "
						+ Main.formatter.format(this.malfunctionType.getValue() * Main.proportionMalfunctionType));
				offerLostPrivacy += privacy[2];
				utility += this.malfunctionType.getValue() * Main.proportionMalfunctionType;
				newOffer += this.malfunctionType.getValue() * Main.proportionMalfunctionType;
			}
			if (min == 3 && privacy[3] < 0.2) {
				System.out.println("\tNumber of People Offer\n\tprivacy = " + privacy[3] + " utility = "
						+ Main.formatter.format(this.numOfPeople / 50.0 * Main.proportionNumberPeople));
				offerLostPrivacy += privacy[3];
				utility += this.numOfPeople / 50.0 * Main.proportionNumberPeople;
				newOffer += this.numOfPeople / 50.0 * Main.proportionNumberPeople;
			}
			if (min == -1) {
				break;
			}
		} while (newOffer <= opponentOffer);

		if (newOffer <= opponentOffer) {

			return 0;
		}
		this.lostPrivacy += offerLostPrivacy;
		return newOffer;

	}

}
