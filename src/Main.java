import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class Main {

	public static NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
	public static DecimalFormat df = (DecimalFormat) nf;
	public static NumberFormat formatter = df;
	public static DatabaseController dbController;

	public static double[] thresholds = {0.8,0.5,0.3};
	public static ArrayList<Vehicle> vehicles = new ArrayList<>();
	
	public static void main(String[] args) throws UnknownHostException {
		
		//dbController = new DatabaseController();
		//ArrayList<String> accidentsIndexes = (ArrayList<String>) dbController.getAccidentIndexes();
		//ArrayList<Vehicle> v = dbController.getVehiclesByAccidentIndex(accidentsIndexes.get(0));
		//System.out.println(v.get(0));

		
		
		Vehicle v1 = new Vehicle(VEHICLETYPE.CAR, JOURNEYPURPOSE.OTHER, MALFUNCTIONTYPE.NOMALFUNCTION, 1);
		Vehicle v2 = new Vehicle(VEHICLETYPE.AMBULANCE, JOURNEYPURPOSE.PARTOFWORK, MALFUNCTIONTYPE.NOMALFUNCTION, 4);
		Vehicle v3 = new Vehicle(VEHICLETYPE.CAR, JOURNEYPURPOSE.OTHER, MALFUNCTIONTYPE.WHEEL, 3);
		Vehicle v4 = new Vehicle(VEHICLETYPE.CAR, JOURNEYPURPOSE.COMMUTINGTOWORK, MALFUNCTIONTYPE.NOMALFUNCTION, 1);
		Vehicle v5 = new Vehicle(VEHICLETYPE.CAR, JOURNEYPURPOSE.SCHOOL, MALFUNCTIONTYPE.NOMALFUNCTION, 14);

		v1.setPrivacy(0.0445, 0.115, 0.01575, 0.1755);
		v2.setPrivacy(0.1875, 0.243, 0.029, 0.174);
		v3.setPrivacy(0.25, 0.25, 0.25, 0.25);
		v4.setPrivacy(0.094, 0.19, 0.18, 0.17);
		v5.setPrivacy(0.171, 0.066, 0.22, 0.174);
		
		
		vehicles.add(v1);
		vehicles.add(v2);
		vehicles.add(v3);
		vehicles.add(v4);
		vehicles.add(v5);
		for (int i = 0; i < vehicles.size(); i++) {
			for (int j = i + 1; j < vehicles.size(); j++) {
				for(int k=0; k<3;k++) {
					vehicles.get(i).setThreshold(thresholds[k]);
					vehicles.get(j).setThreshold(thresholds[k]);
					System.out.println("Threshold: "+ thresholds[k] + " ____________________________________________________");
			
					System.out.println("Basic ---------------------------------------------------------------------");
					startAndResult(i, j, 1);
					vehicles.get(i).clear();
					vehicles.get(j).clear();
				
					vehicles.get(i).isTurn = true;
					vehicles.get(j).isTurn = true;
					System.out.println("Turn Based ----------------------------------------------------------------");
					startAndResult(i, j, 2);
					vehicles.get(i).clear();
					vehicles.get(j).clear();
					vehicles.get(i).isTurn = false;
					vehicles.get(j).isTurn = false;
				
					System.out.println("Auction Based -------------------------------------------------------------");
					startAndResult(i, j, 3);
					vehicles.get(i).clear();
					vehicles.get(j).clear();
				
					System.out.println("Modified Auction Based -------------------------------------------------------------");
					startAndResult(i, j, 4);
					vehicles.get(i).clear();
					vehicles.get(j).clear();
					
				}
				vehicles.get(i).setThreshold(thresholds[0]);
				vehicles.get(j).setThreshold(thresholds[0]);
			}
		}
		System.out.println();
	}
	
	public static void startAndResult(int index1, int index2, int commType) {
		System.out.println("v1 = Vehicle " + (index1 + 1) + " v2 = Vehicle " + (index2 + 1));
		int result;
		if(commType == 1) {
			result = basicNegotiation(vehicles.get(index1), vehicles.get(index2));
		} else if(commType == 2) {
			result = turnBaseNegotiation(vehicles.get(index1), vehicles.get(index2));
		} else if(commType == 3) {
			result = auctionNegotiation(vehicles.get(index1), vehicles.get(index2));
		} else {
			result = modifiedNegotiation(vehicles.get(index1), vehicles.get(index2));
		}
		System.out.println("---------------------------------");
		if (result == 1) {
			System.out.println("Vehicle " + (index1 + 1) + " gets priority");
		} else {
			System.out.println("Vehicle " + (index2 + 1) + " gets priority");
		}
		System.out.println("v1 lostPrivacy: " + formatter.format(vehicles.get(index1).lostPrivacy) + "/"
				+ formatter.format(vehicles.get(index1).totalPrivacy) + " "
				+ formatter.format(100*vehicles.get(index1).lostPrivacy / vehicles.get(index1).totalPrivacy) + "%"
				+ " v2 lostPrivacy: " + formatter.format(vehicles.get(index2).lostPrivacy) + "/"
				+ formatter.format(vehicles.get(index2).totalPrivacy) + " "
				+ formatter.format(100*vehicles.get(index2).lostPrivacy / vehicles.get(index2).totalPrivacy) + "%");
		System.out.println();
		System.out.println();
		
	}

	public static int modifiedNegotiation(Vehicle v1, Vehicle v2) {
		// Auction
		double oldUtility1 = 0, oldUtility2 = 0;
		double utility1 = 0, utility2 = 0;
		System.out.println("\n-----------" + "Turn for v" + 1 + "-----------\n");
		utility1 += v1.makeOffer();
		System.out.println("\nv1 utility: " + utility1 + " v2 utility: " + utility2);
		int turn = 2, count = 0;
		while (++count < 25) {
			System.out.println("\n-----------" + "Turn for v" + turn + "-----------\n");
			if (turn == 1) {
				oldUtility1 = utility1;
				utility1 += v1.makeOffer(utility2);
				System.out.println("\nv1 utility: " + utility1 + " v2 utility: " + utility2);

				if (utility1 - oldUtility1 < 0.00001) {
					break;
				}
				turn = 2;
			} else {
				oldUtility2 = utility2;
				utility2 += v2.makeOffer(utility1);
				System.out.println("\nv1 utility: " + utility1 + " v2 utility: " + utility2);
				if (utility2 - oldUtility2 < 0.00001) {
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
	
	public static int auctionNegotiation(Vehicle v1, Vehicle v2) {
		// Auction
		double oldUtility1 = 0, oldUtility2 = 0;
		double utility1 = 0, utility2 = 0;
		System.out.println("\n-----------" + "Turn for v" + 1 + "-----------\n");
		utility1 += v1.makeOffer();
		System.out.println("\nv1 utility: " + utility1 + " v2 utility: " + utility2);
		int turn = 2, count = 0;
		while (++count < 25) {
			System.out.println("\n-----------" + "Turn for v" + turn + "-----------\n");
			if (turn == 1) {
				oldUtility1 = utility1;
				utility1 += v1.makeOffer();
				System.out.println("\nv1 utility: " + utility1 + " v2 utility: " + utility2);

				if (utility1 - oldUtility1 < 0.00001) {
					break;
				}
				turn = 2;
			} else {
				oldUtility2 = utility2;
				utility2 += v2.makeOffer();
				System.out.println("\nv1 utility: " + utility1 + " v2 utility: " + utility2);
				if (utility2 - oldUtility2 < 0.00001) {
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
	
	public static int turnBaseNegotiation(Vehicle v1, Vehicle v2) {
		// turn based negotiation
		double utility1 = 0, utility2 = 0;
		String[] turnType = {"\n--------Vehicle Type Turn--------\n",
				"\n-------Journey Type Turn-------\n",
				"\n------Malfunction Type Turn------\n",
				"\n-------Number of People Turn-------\n"};
		for (int i = 0; i < 4; i++) {
			System.out.println(turnType[i]);
			System.out.println("\tOffer of v" + 1);
			System.out.println("\t-----------");
			utility1 += v1.makeOffer();
			System.out.println("\n\tOffer of v" + 2);
			System.out.println("\t-----------");
			utility2 += v2.makeOffer();
			System.out.println("\nv1 utility: " + utility1 + " v2 utility: " + utility2);
			if (utility1 > utility2) {
				return 1;
			} else if (utility1 < utility2) {
				return 2;
			}
		}
		return 1;
	}
	
	public static int basicNegotiation(Vehicle v1, Vehicle v2) {
		// Basic Communication
		double utility1 = 0, utility2 = 0;
		System.out.println("\n\tOffers of v" + 1);
		System.out.println("\t------------");
		for (int i = 0; i < 4; i++) {
			utility1 += v1.makeOffer();	
		}
		if(utility1 == 0) System.out.println("\tNo Offer");
		System.out.println("\n\tOffers of v" + 2);
		System.out.println("\t------------");
		for (int i = 0; i < 4; i++) {
			utility2 += v2.makeOffer();	
		}
		if(utility2 == 0) System.out.println("\tNo Offer");
		System.out.println("\nv1 utility: " + formatter.format(utility1) + " v2 utility: " + formatter.format(utility2));
		if (utility1 >= utility2) {
			return 1;
		} else {
			return 2;
		}
	}
}
