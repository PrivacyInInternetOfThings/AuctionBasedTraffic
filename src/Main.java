import java.util.ArrayList;

public class Main {

	public static double proportionVehicleType = 0.3;
	public static double proportionEmergencyType = 0.45;
	public static double proportionMalfunctionType = 0.1;
	public static double proportionNumberPeople = 0.15;

	public static void main(String[] args) {
		Vehicle v1 = new Vehicle(VEHICLETYPE.ORDINARY, EMERGENCYTYPE.NOEMERGENCY, MALFUNCTIONTYPE.NOMALFUNCTION, 1);
		Vehicle v2 = new Vehicle(VEHICLETYPE.EMERGENCY, EMERGENCYTYPE.PATIENT, MALFUNCTIONTYPE.NOMALFUNCTION, 4);
		Vehicle v3 = new Vehicle(VEHICLETYPE.ORDINARY, EMERGENCYTYPE.NOEMERGENCY, MALFUNCTIONTYPE.WHEEL, 3);
		Vehicle v4 = new Vehicle(VEHICLETYPE.ORDINARY, EMERGENCYTYPE.LATEFORWORK, MALFUNCTIONTYPE.NOMALFUNCTION, 1);
		Vehicle v5 = new Vehicle(VEHICLETYPE.ORDINARY, EMERGENCYTYPE.LATEFORSCHOOL, MALFUNCTIONTYPE.NOMALFUNCTION, 14);

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
				if (result == 1) {
					System.out.println("Vehicle " + (i + 1) + " gets priority");
				} else {
					System.out.println("Vehicle " + (j + 1) + " gets priority");
				}
				vehicles.get(i).clear();
				vehicles.get(j).clear();
				System.out.println();
			}
		}
	}

	public static int makeNegotiation(Vehicle v1, Vehicle v2) {
		// Auction
		double oldUtility1 = 0, oldUtility2 = 0;
		double utility1 = 0, utility2 = 0;
		System.out.println("Turn " + 1);
		utility1 += v1.makeOffer();
		System.out.println(" V1: " + utility1 + " V2: " + utility2);
		int turn = 2, count = 0;
		while (++count < 15) {
			System.out.println("Turn " + turn);
			if (turn == 1) {
				oldUtility1 = utility1;
				utility1 += v1.makeOffer();
				System.out.println(" V1: " + utility1 + " V2: " + utility2);
				if (utility1 > utility2)
					turn = 2;
				if (utility1 - oldUtility1 < 0.00001)
					break;
			} else {
				oldUtility2 = utility2;
				utility2 += v2.makeOffer();
				System.out.println(" V1: " + utility1 + " V2: " + utility2);
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
