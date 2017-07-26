import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.Document;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.model.Aggregates;

public class DatabaseController {

	private MongoClient mongoClient;
	private DB trafficDatabase;
	private String dbName = "traffic";
	private Set<String> collectionSet;
	private String vehiclesCollectionName = "Vehicles";
	private String accidentsCollectionName = "Accidents";

	public DatabaseController() throws UnknownHostException {
		mongoClient = new MongoClient();
		trafficDatabase = mongoClient.getDB(dbName);
		collectionSet = trafficDatabase.getCollectionNames();
		System.out.println(collectionSet);
	}

	public List<String> getAccidentIndexes() {

		List<String> result = new ArrayList<String>();

		DBCollection accidentCollection = trafficDatabase.getCollection(vehiclesCollectionName);

		DBObject group = new BasicDBObject("$group",
				new BasicDBObject("_id", "$Accident_Index").append("count", new BasicDBObject("$sum", 1)));
		DBObject match = new BasicDBObject("$match", new BasicDBObject("count", new BasicDBObject("$gt", 1)));
		DBObject sort = new BasicDBObject("$sort", new BasicDBObject("count", -1));
		Iterable<DBObject> o = accidentCollection.aggregate(Arrays.asList(group, match, sort)).results();

		for (DBObject dbo : o) {

//			System.out.println("Accident Index: " + dbo.get("_id").toString() + ", Number Of Vehicles: "
//					+ dbo.get("count").toString());
			result.add("Accident Index: " + dbo.get("_id").toString() + ", Number Of Vehicles: "
					+ dbo.get("count").toString());

		}

		return result;
	}

	public ArrayList<Vehicle> getVehiclesByAccidentIndex(String accidentIndex){
		ArrayList<Vehicle> result = new ArrayList<>();
//		Map<String,Integer> m = getThresholdVariables(accidentIndex);
		
		DBCollection vehiclesCollection = trafficDatabase.getCollection(vehiclesCollectionName);
		DBCursor c = vehiclesCollection.find(new BasicDBObject("Accident_Index",accidentIndex),new BasicDBObject("Accident_Index",1).append("Vehicle_Reference",1).append("Age_of_Driver",1).append("Engine_Capacity_(CC)",1).append("Vehicle_Reference", 1).append("_id", 0).append("Vehicle_Type", 1).append("Journey_Purpose_of_Driver", 1).append("Age_of_Vehicle", 1));
		while(c.hasNext()){
			BasicDBObject o = (BasicDBObject) c.next();
			Vehicle v = new Vehicle(
					VEHICLETYPE.getById(o.getInt("Vehicle_Type")), 
					JOURNEYPURPOSE.getJourneyPurposeById(o.getInt("Journey_Purpose_of_Driver")), 
					MALFUNCTIONTYPE.getMalfunctionById((int)Math.random()*3), 
					(o.getInt("Age_of_Vehicle") == -1 ? 50 :  o.getInt("Age_of_Vehicle")));
			v.setReference(o.getInt("Vehicle_Reference"));
			//v.setThreshold(m.get("Day_of_Week"), m.get("Time"), m.get("Urban_or_Rural_Area"), m.get("Weather_Conditions"), m.get("Road_Surface_Conditions"), m.get("Special_Conditions_at_Site"), m.get("Light_Conditions"), o.getInt("Age_of_Driver"), o.getInt("Engine_Capacity_(CC)"));
			
			result.add(v);
		}
		
		return result;
	}
	
/*	public Map<String,Integer> getThresholdVariables(String accidentIndex){
		Map<String,Integer> variables = new HashMap<String,Integer>();
		DBCollection accidentCollection = trafficDatabase.getCollection(accidentsCollectionName);
		DBCursor c = accidentCollection.find(new BasicDBObject("Accident_Index",accidentIndex),new BasicDBObject("Day_of_Week",1).append("Time", 1).append("Light_Conditions", 1)
				.append("Weather_Conditions", 1).append("Road_Surface_Conditions", 1).append("Special_Conditions_at_Site", 1).append("Urban_or_Rural_Area", 1).append("_id", 0));
		if(c.hasNext()){
			BasicDBObject o = (BasicDBObject) c.next();
			variables.put("Day_of_Week", o.getInt("Day_of_Week"));
			String t = o.getString("Time");
			if(t.compareTo("08:00") > 0 && t.compareTo("20:00") < 0){
				variables.put("Time", 1);
			}else{
				variables.put("Time", 0);
			}
			variables.put("Light_Conditions", o.getInt("Light_Conditions"));
			variables.put("Weather_Conditions", o.getInt("Weather_Conditions"));
			variables.put("Road_Surface_Conditions", o.getInt("Road_Surface_Conditions"));
			variables.put("Special_Conditions_at_Site", o.getInt("Special_Conditions_at_Site"));
			variables.put("Urban_or_Rural_Area", o.getInt("Urban_or_Rural_Area"));
			
		}
		
		return variables;
	}
*/
	public void getVehicleByAccidentIdAndReference(String accidentIndex, int reference) {
		DBCollection vehiclesCollection = trafficDatabase.getCollection("Vehicles");
		DBCursor c = vehiclesCollection
				.find(new BasicDBObject("Accident_Index", accidentIndex).append("Vehicle_Reference", reference));
	}
}
