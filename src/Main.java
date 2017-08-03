import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import com.mongodb.BasicDBObject;

public class Main {
	/*
	 * To create CSV output, make "printCSV" true To save the experiments to DB,
	 * make "saveMongo" true
	 */
	public static boolean printCSV = false;
	public static boolean saveMongo = true;

	public static NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
	public static DecimalFormat df = (DecimalFormat) nf;
	public static NumberFormat formatter = df;
	public static DatabaseController dbController;

	public static double[] thresholds = { 0.9, 0.8, 0.7, 0.5, 0.3 };
	public static ArrayList<Vehicle> vehicles = new ArrayList<>();
	public static ArrayList<String> v1;
	public static ArrayList<String> v2;
	public static ArrayList<String> header = new ArrayList<>();

	public static void main(String[] args) throws IOException {

		PrintWriter writer = new PrintWriter("experiments.csv", "UTF-8");

		initHeader();
		String formattedString = header.toString().replace("[", "").replace("]", "").trim();

		if (printCSV) {
			writer.println(formattedString);
		}
		dbController = new DatabaseController();

		ArrayList<String> accidentsIndexes =
				// (ArrayList<String>) dbController.getAccidentIndexes();
				getGoodIDS();

		// Print accident number
		//System.out.println(accidentsIndexes.size());

		for (int i = 0; i < accidentsIndexes.size(); i++) {
			vehicles = dbController.getVehiclesByAccidentIndex(accidentsIndexes.get(i));
			vehicles.get(0).setPrivacyRandom();
			vehicles.get(1).setPrivacyRandom();

			// Print vehicle info
			//System.out.println(vehicles.get(0));
			//System.out.println(vehicles.get(1));

			BasicDBObject thres = new BasicDBObject();

			v1 = new ArrayList<>();
			v2 = new ArrayList<>();
			v1.add(accidentsIndexes.get(i));
			v2.add(accidentsIndexes.get(i));
			v1.add("v1");
			v2.add("v2");
			for (int k = 0; k < thresholds.length; k++) {
				BasicDBObject type = new BasicDBObject();
				vehicles.get(0).setThreshold(thresholds[k]);
				vehicles.get(0).setShareablePrivacy();
				vehicles.get(1).setThreshold(thresholds[k]);
				vehicles.get(1).setShareablePrivacy();
				// System.out.println("Threshold: " + thresholds[k] + "
				// __________________________________________");

				// System.out.println("Basic
				// ---------------------------------------------------------------------");
				type.put("Basic", startAndResult(0, 1, 1));
				vehicles.get(0).clear();
				vehicles.get(1).clear();

				vehicles.get(0).isTurn = true;
				vehicles.get(1).isTurn = true;
				// System.out.println("Turn Based
				// ----------------------------------------------------------------");
				type.put("Turn Based", startAndResult(0, 1, 2));
				vehicles.get(0).clear();
				vehicles.get(1).clear();
				vehicles.get(0).isTurn = false;
				vehicles.get(1).isTurn = false;

				// System.out.println("Auction Based
				// -------------------------------------------------------------");
				type.put("Auction Based", startAndResult(0, 1, 3));
				vehicles.get(0).clear();
				vehicles.get(1).clear();

				// System.out.println("Modified Auction Based
				// ----------------------------------------------------");
				type.put("Modified Auction", startAndResult(0, 1, 4));
				vehicles.get(0).clear();
				vehicles.get(1).clear();

				thres.put(("" + thresholds[k]).replace('.', ','), type);
			}

			formattedString = v1.toString().replace("[", "") // remove the right
																// bracket
					.replace("]", "") // remove the left bracket
					.trim();
			if (printCSV) {
				writer.println(formattedString);
			}
			formattedString = v2.toString().replace("[", "") // remove the right
																// bracket
					.replace("]", "") // remove the left bracket
					.trim();

			if (printCSV) {
				writer.println(formattedString);
			}
			//System.out.println();

			BasicDBObject accident = new BasicDBObject();
			

			BasicDBObject vehicle1Info = vehicleObject(vehicles.get(0));
			BasicDBObject vehicle2Info = vehicleObject(vehicles.get(1));

			accident.put("Accident_Index", accidentsIndexes.get(i));
			accident.put("Random Seed", Vehicle.randomSeed);
			accident.put("Vehicle 1", vehicle1Info);
			accident.put("Vehicle 2", vehicle2Info);
			accident.put("Auctions", thres);

			//System.out.println(accident.toString());

			if (saveMongo) {
				dbController.saveExperiment(accident);
			}
			vehicles.get(0).setThreshold(thresholds[0]);
			vehicles.get(1).setThreshold(thresholds[0]);
		}
		// System.out.println();
		writer.close();
	}

	private static void initHeader() {
		header.add("Accident Index");
		header.add("Vehicles");
		for (int i = 0; i < thresholds.length; i++) {
			header.add("Basic Method Privacy Loss - " + thresholds[i]);
			header.add("Basic Method Winner - " + thresholds[i]);
			header.add("Turn Based Privacy Loss - " + thresholds[i]);
			header.add("Turn Based Winner - " + thresholds[i]);
			header.add("Auction Method Privacy Loss - " + thresholds[i]);
			header.add("Auction Method Winner - " + thresholds[i]);
			header.add("Modified Auction Method Privacy Loss - " + thresholds[i]);
			header.add("Modified Auction Method Winner - " + thresholds[i]);
		}

	}

	public static BasicDBObject vehicleObject(Vehicle v) {
		BasicDBObject info = new BasicDBObject();

		BasicDBObject vehicleType = new BasicDBObject();
		vehicleType.put("Value", "" + v.vehicleType);
		vehicleType.put("Privacy Value", v.privacy[0]);
		info.put("Vehicle Type", vehicleType);

		BasicDBObject journeyPurpose = new BasicDBObject();
		journeyPurpose.put("Value", "" + v.journeyType);
		journeyPurpose.put("Privacy Value", v.privacy[1]);
		info.put("Purpose of Journey", journeyPurpose);

		BasicDBObject ageBand = new BasicDBObject();
		ageBand.put("Value", "" + v.ageBandOfDriver);
		ageBand.put("Privacy Value", v.privacy[2]);
		info.put("Age Band of Driver", ageBand);

		BasicDBObject ageVehicle = new BasicDBObject();
		ageVehicle.put("Value", "" + v.ageOfCar);
		ageVehicle.put("Privacy Value", v.privacy[3]);
		info.put("Age of Vehicle", ageVehicle);

		return info;
	}

	public static BasicDBObject startAndResult(int index1, int index2, int commType) {
//		System.out.println("v1 = Vehicle " + (index1 + 1) + " v2 = Vehicle " + (index2 + 1));
		int result;
		if (commType == 1) {
			result = basicNegotiation(vehicles.get(index1), vehicles.get(index2));
		} else if (commType == 2) {
			result = turnBaseNegotiation(vehicles.get(index1), vehicles.get(index2));
		} else if (commType == 3) {
			result = auctionNegotiation(vehicles.get(index1), vehicles.get(index2));
		} else {
			result = modifiedNegotiation(vehicles.get(index1), vehicles.get(index2));
		}
/*		System.out.println("---------------------------------");
		if (result == 1) {
			System.out.println("Vehicle " + (index1 + 1) + " gets priority");
		} else {
			System.out.println("Vehicle " + (index2 + 1) + " gets priority");
		}
		System.out.println("v1 lostPrivacy: " + formatter.format(vehicles.get(index1).lostPrivacy) + "/"
				+ formatter.format(vehicles.get(index1).totalPrivacy) + " "
				+ formatter.format(100 * vehicles.get(index1).lostPrivacy / vehicles.get(index1).totalPrivacy) + "%"
				+ " v2 lostPrivacy: " + formatter.format(vehicles.get(index2).lostPrivacy) + "/"
				+ formatter.format(vehicles.get(index2).totalPrivacy) + " "
				+ formatter.format(100 * vehicles.get(index2).lostPrivacy / vehicles.get(index2).totalPrivacy) + "%");
		System.out.println();
		System.out.println();
*/
		BasicDBObject item = new BasicDBObject();

		double shlossv1 =0;
		double shlossv2 =0;
		
		if(vehicles.get(index1).totalShareablePrivacy != 0 && vehicles.get(index1).lostPrivacy!=0)
			shlossv1 = 100 * vehicles.get(index1).lostPrivacy / vehicles.get(index1).totalShareablePrivacy;
		if(vehicles.get(index2).totalShareablePrivacy != 0 && vehicles.get(index2).lostPrivacy!=0)
			shlossv2 = 100 * vehicles.get(index2).lostPrivacy / vehicles.get(index2).totalShareablePrivacy;
		
		/*if(shlossv1 >100 || shlossv2 >100){
			System.out.println(commType);
			
			System.out.println("threshold: "+vehicles.get(index1).threshold);
			System.out.println(vehicles.get(index1).privacy[0]+" "+vehicles.get(index1).privacy[1]+" "+vehicles.get(index1).privacy[2]+" "+vehicles.get(index1).privacy[3]);
					
			System.out.println("lost:"+ vehicles.get(index1).lostPrivacy);
			System.out.println("total shareable:"+ vehicles.get(index1).totalShareablePrivacy);
			
			System.out.println("shareable loss:"+shlossv1);
			System.out.println();
		}*/
		
		
		item.put("Privacy Loss of Vehicle 1", 100 * vehicles.get(index1).lostPrivacy / vehicles.get(index1).totalPrivacy);
		item.put("Privacy Loss of Vehicle 2", 100 * vehicles.get(index2).lostPrivacy / vehicles.get(index2).totalPrivacy);
		item.put("Privacy Shareable Loss of Vehicle 1", shlossv1);
		item.put("Privacy Shareable Loss of Vehicle 2", shlossv2);
		item.put("LossAVG", (100 * vehicles.get(index1).lostPrivacy / vehicles.get(index1).totalPrivacy)/2+(100 * vehicles.get(index2).lostPrivacy / vehicles.get(index2).totalPrivacy)/2);
		item.put("ShareableLossAVG", (shlossv1+shlossv2)/2);
		
		item.put("Winner", "Vehicle "+result);
		v1.add("" + formatter.format(100 * vehicles.get(index1).lostPrivacy / vehicles.get(index1).totalPrivacy));
		v2.add("" + formatter.format(100 * vehicles.get(index2).lostPrivacy / vehicles.get(index2).totalPrivacy));
		
		if (result == 1) {
			v1.add("W");
			v2.add("L");
		} else {
			v1.add("L");
			v2.add("W");
		}
		
		double gain1=0;
		double gain2=0;
		if(vehicles.get(index1).calculatePossibleUtilityPoints()!=0 && vehicles.get(index1).calculatePossibleUtilityPoints() != vehicles.get(index1).utility)
			gain1= (vehicles.get(index1).calculatePossibleUtilityPoints()-vehicles.get(index1).utility) / vehicles.get(index1).calculatePossibleUtilityPoints();
		if(vehicles.get(index2).calculatePossibleUtilityPoints()!=0 && vehicles.get(index2).calculatePossibleUtilityPoints() != vehicles.get(index2).utility)
			gain2= (vehicles.get(index2).calculatePossibleUtilityPoints()-vehicles.get(index2).utility) / vehicles.get(index2).calculatePossibleUtilityPoints();
		item.put("UtilV1",vehicles.get(index1).utility);
		item.put("UtilV2",vehicles.get(index2).utility);
		item.put("MaxUtilV1",vehicles.get(index1).calculatePossibleUtilityPoints());
		item.put("MaxUtilV2",vehicles.get(index2).calculatePossibleUtilityPoints());
		item.put("PrivacyGainV1",gain1);
		item.put("PrivacyGainV2",gain2);
		item.put("PrivacyGainAVG", (gain1+gain2)/2);
		item.put("UP", vehicles.get(index1).utility*vehicles.get(index2).utility);
		item.put("PP", vehicles.get(index1).utility*vehicles.get(index2).utility*(1-Math.abs(vehicles.get(index1).utility-vehicles.get(index2).utility)));
		
		item.put("ShareableNoV1",vehicles.get(index1).numberOfShareable());
		item.put("ShareableNoV2",vehicles.get(index2).numberOfShareable());
		
		return item;
	}

	public static int modifiedNegotiation(Vehicle v1, Vehicle v2) {
		// Auction
		double oldUtility1 = 0, oldUtility2 = 0;
		double utility1 = 0, utility2 = 0;
		// System.out.println("\n-----------" + "Turn for v" + 1 +
		// "-----------\n");
		utility1 += v1.makeOffer(0);
		// System.out.println("\nv1 utility: " + utility1 + " v2 utility: " +
		// utility2);
		int turn = 2, count = 0;
		while (++count < 25) {
			// System.out.println("\n-----------" + "Turn for v" + turn +
			// "-----------\n");
			if (turn == 1) {
				oldUtility1 = utility1;
				utility1 += v1.makeOffer(utility2);
				// System.out.println("\nv1 utility: " + utility1 + " v2
				// utility: " + utility2);

				if (utility1 - oldUtility1 < 0.00001) {
					break;
				}
				turn = 2;
			} else {
				oldUtility2 = utility2;
				utility2 += v2.makeOffer(utility1);
				// System.out.println("\nv1 utility: " + utility1 + " v2
				// utility: " + utility2);
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
		// System.out.println("\n-----------" + "Turn for v" + 1 +
		// "-----------\n");
		utility1 += v1.makeOffer();
		// System.out.println("\nv1 utility: " + utility1 + " v2 utility: " +
		// utility2);
		int turn = 2, count = 0;
		while (++count < 25) {
			// System.out.println("\n-----------" + "Turn for v" + turn +
			// "-----------\n");
			if (turn == 1) {
				oldUtility1 = utility1;
				utility1 += v1.makeOffer();
				// System.out.println("\nv1 utility: " + utility1 + " v2
				// utility: " + utility2);
				if (utility1 - utility2 > 0.0001) {
					turn = 2;
				}
				if (utility1 - oldUtility1 < 0.00001) {
					break;
				}
			} else {
				oldUtility2 = utility2;
				utility2 += v2.makeOffer();
				// System.out.println("\nv1 utility: " + utility1 + " v2
				// utility: " + utility2);
				if (utility2 - utility1 > 0.0001) {
					turn = 1;
				}
				if (utility2 - oldUtility2 < 0.00001) {
					break;
				}
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
		String[] turnType = { "\n--------Vehicle Type Turn--------\n", "\n-------Journey Type Turn-------\n",
				"\n------Age Band of Driver Turn------\n", "\n-------Age of Vehicle Turn-------\n" };
		for (int i = 0; i < 4; i++) {
			// System.out.println(turnType[i]);
			// System.out.println("\tOffer of v" + 1);
			// System.out.println("\t-----------");
			utility1 += v1.makeOffer();
			// System.out.println("\n\tOffer of v" + 2);
			// System.out.println("\t-----------");
			utility2 += v2.makeOffer();
			// System.out.println("\nv1 utility: " + utility1 + " v2 utility: "
			// + utility2);
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
		// System.out.println("\n\tOffers of v" + 1);
		// System.out.println("\t------------");
		for (int i = 0; i < 4; i++) {
			utility1 += v1.makeOffer();
		}
		/*
		 * if (utility1 == 0) System.out.println("\tNo Offer");
		 * System.out.println("\n\tOffers of v" + 2);
		 * System.out.println("\t------------");
		 */ for (int i = 0; i < 4; i++) {
			utility2 += v2.makeOffer();
		}
		/*
		 * if (utility2 == 0) System.out.println("\tNo Offer");
		 * System.out.println("\nv1 utility: " + formatter.format(utility1) +
		 * " v2 utility: " + formatter.format(utility2));
		 */ if (utility1 >= utility2) {
			return 1;
		} else {
			return 2;
		}
	}

	public static ArrayList<String> getGoodIDS() throws IOException {
		ArrayList<String> res = new ArrayList<String>();

		String fileName = "ids.txt";

		FileInputStream fis = new FileInputStream(fileName);

		// Construct BufferedReader from InputStreamReader
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));

		String line = null;
		while ((line = br.readLine()) != null) {
			// System.out.println(line);
			res.add(line);
		}

		br.close();

		return res;
	}
}