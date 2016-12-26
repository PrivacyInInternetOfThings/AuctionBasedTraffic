import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class Main {

	public static double proportionVehicleType = 0.3;
	public static double proportionEmergencyType = 0.45;
	public static double proportionMalfunctionType = 0.1;
	public static double proportionNumberPeople = 0.15;
	public static NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
	public static DecimalFormat df = (DecimalFormat) nf;
	public static NumberFormat formatter = df;

	public static void main(String[] args) {
		Vehicle v1 = new Vehicle(VEHICLETYPE.ORDINARY, EMERGENCYTYPE.NOEMERGENCY, MALFUNCTIONTYPE.NOMALFUNCTION, 1);
		Vehicle v2 = new Vehicle(VEHICLETYPE.EMERGENCY, EMERGENCYTYPE.PATIENT, MALFUNCTIONTYPE.NOMALFUNCTION, 4);
		Vehicle v3 = new Vehicle(VEHICLETYPE.ORDINARY, EMERGENCYTYPE.NOEMERGENCY, MALFUNCTIONTYPE.WHEEL, 3);
		Vehicle v4 = new Vehicle(VEHICLETYPE.ORDINARY, EMERGENCYTYPE.LATEFORWORK, MALFUNCTIONTYPE.NOMALFUNCTION, 1);
		Vehicle v5 = new Vehicle(VEHICLETYPE.ORDINARY, EMERGENCYTYPE.LATEFORSCHOOL, MALFUNCTIONTYPE.NOMALFUNCTION, 14);

		v1.setPrivacy(0.0445, 0.115, 0.01575, 0.1755);
		v2.setPrivacy(0.1875, 0.243, 0.029, 0.174);
		v3.setPrivacy(0.25, 0.25, 0.25, 0.25);
		v4.setPrivacy(0.094, 0.19, 0.18, 0.17);
		v5.setPrivacy(0.171, 0.066, 0.22, 0.174);

		ArrayList<Vehicle> vehicles = new ArrayList<>();
		vehicles.add(v1);
		vehicles.add(v2);
		vehicles.add(v3);
		vehicles.add(v4);
		vehicles.add(v5);
		for (int i = 0; i < vehicles.size(); i++) {
			for (int j = i + 1; j < vehicles.size(); j++) {
				System.out.println("v1 = Vehicle " + (i + 1) + " v2 = Vehicle " + (j + 1));
				int result = makeNegotiation(vehicles.get(i), vehicles.get(j));
				System.out.println("---------------------------------");
				if (result == 1) {
					System.out.println("Vehicle " + (i + 1) + " gets priority");
				} else {
					System.out.println("Vehicle " + (j + 1) + " gets priority");
				}
				System.out.println("v1 lostPrivacy: " + formatter.format(vehicles.get(i).lostPrivacy) + "/"
						+ formatter.format(vehicles.get(i).totalPrivacy) + " "
						+ formatter.format(100*vehicles.get(i).lostPrivacy / vehicles.get(i).totalPrivacy) + "%"
						+ " v2 lostPrivacy: " + formatter.format(vehicles.get(j).lostPrivacy) + "/"
						+ formatter.format(vehicles.get(j).totalPrivacy) + " "
						+ formatter.format(100*vehicles.get(j).lostPrivacy / vehicles.get(j).totalPrivacy) + "%");
				vehicles.get(i).clear();
				vehicles.get(j).clear();
				System.out.println();
				System.out.println();
			}
		}
		System.out.println();
		System.out.println();
		System.out.println();
		for (int i = 0; i < vehicles.size(); i++) {
			for (int j = i + 1; j < vehicles.size(); j++) {
				System.out.println("v1 = Vehicle " + (i + 1) + " v2 = Vehicle " + (j + 1));
				int result = makeNegotiationOld(vehicles.get(i), vehicles.get(j));
				System.out.println("---------------------------------");
				if (result == 1) {
					System.out.println("Vehicle " + (i + 1) + " gets priority");
				} else {
					System.out.println("Vehicle " + (j + 1) + " gets priority");
				}
				System.out.println("v1 lostPrivacy: " + formatter.format(vehicles.get(i).lostPrivacy) + "/"
						+ formatter.format(vehicles.get(i).totalPrivacy) + " "
						+ formatter.format(100*vehicles.get(i).lostPrivacy / vehicles.get(i).totalPrivacy) + "%"
						+ " v2 lostPrivacy: " + formatter.format(vehicles.get(j).lostPrivacy) + "/"
						+ formatter.format(vehicles.get(j).totalPrivacy) + " "
						+ formatter.format(100*vehicles.get(j).lostPrivacy / vehicles.get(j).totalPrivacy) + "%");
				vehicles.get(i).clear();
				vehicles.get(j).clear();
				System.out.println();
				System.out.println();
			}
		}
	}

	public static int makeNegotiation(Vehicle v1, Vehicle v2) {
		// Auction
		double oldUtility1 = 0, oldUtility2 = 0;
		double utility1 = 0, utility2 = 0;
		System.out.println("\n-----------" + "Turn for v" + 1 + "-----------\n");
		utility1 += v1.makeOffer();
		System.out.println("\nv1 utility: " + utility1 + " v2 utility: " + utility2);
		int turn = 2, count = 0;
		while (++count < 15) {
			System.out.println("\n-----------" + "Turn for v" + turn + "-----------\n");
			if (turn == 1) {
				oldUtility1 = utility1;
				utility1 += v1.makeOffer(utility2);
				System.out.println("\nv1 utility: " + utility1 + " v2 utility: " + utility2);

				if (utility1 - oldUtility1 == 0) {
					break;
				}
				turn = 2;
			} else {
				oldUtility2 = utility2;
				utility2 += v2.makeOffer(utility1);
				System.out.println("\nv1 utility: " + utility1 + " v2 utility: " + utility2);
				if (utility2 - oldUtility2 == 0) {
					break;
				}

				turn = 1;
			}
		}
		if (utility1 >= utility2) {
			return 1;
		} else {
			return 2;
		}
	}

	public static int makeNegotiationOld(Vehicle v1, Vehicle v2) {
		// Auction
		double oldUtility1 = 0, oldUtility2 = 0;
		double utility1 = 0, utility2 = 0;
		System.out.println("\n-----------" + "Turn for v" + 1 + "-----------\n");
		utility1 += v1.makeOffer();
		System.out.println("\nv1 utility: " + utility1 + " v2 utility: " + utility2);
		int turn = 2, count = 0;
		while (++count < 15) {
			System.out.println("\n-----------" + "Turn for v" + turn + "-----------\n");
			if (turn == 1) {
				oldUtility1 = utility1;
				utility1 += v1.makeOffer();
				System.out.println("\nv1 utility: " + utility1 + " v2 utility: " + utility2);
				if (utility1 > utility2)
					turn = 2;
				if (utility1 - oldUtility1 < 0.00001)
					break;
			} else {
				oldUtility2 = utility2;
				utility2 += v2.makeOffer();
				System.out.println("\nv1 utility: " + utility1 + " v2 utility: " + utility2);
				if (utility1 < utility2)
					turn = 1;
				if (utility2 - oldUtility2 < 0.00001)
					break;
			}
		}
		if (utility1 >= utility2) {
			return 1;
		} else {
			return 2;
		}
	}
}
