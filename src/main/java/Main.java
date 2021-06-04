import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import dataAccessLayer.EmbeddedNeo4j;

/**
 * 
 */

/**
 * @author Administrator
 *
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try ( EmbeddedNeo4j greeter = new EmbeddedNeo4j( "bolt://localhost:7687", "neo4j", "AED2021grupo10" ) )
        {
		 	LinkedList<String> myUsers = greeter.getAllUsersNames();
		 	
		 	for (int i = 0; i < myUsers.size(); i++) {
		 		 //out.println( "<p>" + myactors.get(i) + "</p>" );
		 		System.out.println(myUsers.get(i));
		 	}
		 	
		 	LinkedList<String> allSeries = greeter.getSeriesName();
		 	for (int i = 0; i < allSeries.size(); i++) {
		 		 //out.println( "<p>" + myactors.get(i) + "</p>" );
		 		System.out.println(allSeries.get(i));
		 	}
		 	
		 	LinkedList<String> allpassword = greeter.getUserPassword("ara201106@uvg.edu.gt");
		 	for (int i = 0; i < allpassword.size(); i++) {
		 		 //out.println( "<p>" + myactors.get(i) + "</p>" );
		 		System.out.println(allpassword.get(i));
		 	}
		 	
		 	System.out.println("Ratings-------");
		 	HashMap<String, Float> ratings = greeter.getSeriesRatingsMap();
		 	for( Entry<String, Float> entry : ratings.entrySet() ){
		 	    System.out.println( entry.getKey() + ":" + entry.getValue() );
		 	}
		 	
		 	System.out.println("Seasons-------");
		 	HashMap<String, Integer> seasons = greeter.getSeriesSeasonsMap();
		 	for( Entry<String, Integer> entry : seasons.entrySet() ){
		 	    System.out.println( entry.getKey() + ":" + entry.getValue() );
		 	}
		 	
		 	System.out.println("Running Time-------");
		 	HashMap<String, Integer> running_time = greeter.getSeriesRunning_timeMap();
		 	for( Entry<String, Integer> entry : running_time.entrySet() ){
		 	    System.out.println( entry.getKey() + ":" + entry.getValue() );
		 	}
		 	
		 	System.out.println("------Final Score ----JEyner---");
		 	ArrayList<String> score = greeter.algorithm("ara201106@uvg.edu.gt");
		 	for( String movie: score){
		 	    System.out.println(movie);
		 	}
		 	
		 	/*
		 	System.out.println("------Final Score -------");
		 	ArrayList<String> score2 = greeter.algorithm("cas20082@uvg.edu.gt");
		 	for( String movie: score2){
		 	    System.out.println(movie);
		 	}*/
		 	
        } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
