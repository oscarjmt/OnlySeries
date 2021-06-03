import java.util.LinkedList;

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
		 	
		 	LinkedList<String> allSeries = greeter.getSeries();
		 	for (int i = 0; i < allSeries.size(); i++) {
		 		 //out.println( "<p>" + myactors.get(i) + "</p>" );
		 		System.out.println(allSeries.get(i));
		 	}
		 	
		 	LinkedList<String> allpassword = greeter.getUserPassword("Jeyner Arango");
		 	for (int i = 0; i < allpassword.size(); i++) {
		 		 //out.println( "<p>" + myactors.get(i) + "</p>" );
		 		System.out.println(allpassword.get(i));
		 	}
		 	
        	
        } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
