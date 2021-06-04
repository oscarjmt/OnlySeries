package dataAccessLayer;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.TransactionWork;

import static org.neo4j.driver.Values.parameters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Jeyner Arango, Moises Alonso
 * @version 1.0
 */
public class EmbeddedNeo4j implements AutoCloseable{
    private final Driver driver;
    
    /**
     * 
     * @param uri
     * @param user
     * @param password
     */
    public EmbeddedNeo4j( String uri, String user, String password )
    {
        driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ) );
    }
    /**
     * Closes Driver
     */
    @Override
    public void close() throws Exception
    {
    	driver.close();
    }
    /**
     * Welcome Message
     * @param message
     */
    public void printGreeting( final String message )
    {
    	try ( Session session = driver.session() )
    	{
    		String greeting = session.writeTransaction( new TransactionWork<String>()
    		{
    			@Override
    			public String execute( Transaction tx )
    			{
    				Result result = tx.run( "CREATE (a:Greeting) " +
    						"SET a.message = $message " +
    						"RETURN a.message + ', from node ' + id(a)",
    						parameters( "message", message ) );
    				return result.single().get( 0 ).asString();
    			}
    		} );
    		System.out.println( greeting );
    	}
    }
    /**
     * Get all Users
     * @return all Users name
     */
    public LinkedList<String> getAllUsersNames()
    {
    	try ( Session session = driver.session() )
    	{
    		LinkedList<String> users = session.readTransaction( new TransactionWork<LinkedList<String>>()
    		{
    			@Override
    			public LinkedList<String> execute( Transaction tx )
    			{
    				Result result = tx.run( "MATCH (user:User) RETURN user.name");
    				LinkedList<String> allUsers = new LinkedList<String>();
    				List<Record> registros = result.list();
    				for (int i = 0; i < registros.size(); i++) {
    					allUsers.add(registros.get(i).get("user.name").asString());
    				}

    				return allUsers;
    			}
    		} );

    		return users;
    	}
    }
    /**
     * Get User Node
     * @return all Users name
     */
    public LinkedList<String> getUserPassword(String email)
    {
    	try ( Session session = driver.session() )
    	{
    		LinkedList<String> users = session.readTransaction( new TransactionWork<LinkedList<String>>()
    		{
    			@Override
    			public LinkedList<String> execute( Transaction tx )
    			{
    				Result result = tx.run("MATCH (user:User {email:'"+email+"'}) RETURN user.password");
    				LinkedList<String> allUsers = new LinkedList<String>();
    				List<Record> registros = result.list();
    				for (int i = 0; i < registros.size(); i++) {
    					allUsers.add(registros.get(i).get("user.password").asString());
    				}

    				return allUsers;
    			}
    		} );

    		return users;
    	}
    }
    /**
     * Get all Series
     * @return all Series Name
     */
    public LinkedList<String> getSeriesName()
    {
    	try ( Session session = driver.session() )
    	{
    		LinkedList<String> series = session.readTransaction( new TransactionWork<LinkedList<String>>()
    		{
    			@Override
    			public LinkedList<String> execute( Transaction tx )
    			{
    				Result result = tx.run( "MATCH (series:Series) return series.name" );
    				LinkedList<String> myseries = new LinkedList<String>();
    				List<Record> registros = result.list();
    				for (int i = 0; i < registros.size(); i++) {
    					String individualSeries = registros.get(i).get("series.name").asString();
    					myseries.add(individualSeries);
    				}                    
    				return myseries;
    			}
    		} );
    		return series;
    	}
    }
    /**
     * Get User Series
     * @return all Series Name
     */
    public LinkedList<String> getUserSeries(String userEmail)
    {
    	try ( Session session = driver.session() )
    	{
    		LinkedList<String> series = session.readTransaction( new TransactionWork<LinkedList<String>>()
    		{
    			@Override
    			public LinkedList<String> execute( Transaction tx )
    			{
    				Result result = tx.run("MATCH(n:User {email:'"+userEmail+"'})-[:LIKE]->(s:Series) RETURN s.name" );
    				LinkedList<String> myseries = new LinkedList<String>();
    				List<Record> registros = result.list();
    				for (int i = 0; i < registros.size(); i++) {
    					myseries.add(registros.get(i).get("s.name").asString());
    				}
    				return myseries;
    			}
    		} );
    		return series;
    	}
    }
    /**
     * Get Similar recommendations
     * @param userName
     * @return LinkedList<String> of Similar Series
     */
    public LinkedList<String> getSimilarSeries(String userName)
    {
    	try ( Session session = driver.session() )
    	{
    		LinkedList<String> series = session.readTransaction( new TransactionWork<LinkedList<String>>()
    		{
    			@Override
    			public LinkedList<String> execute( Transaction tx )
    			{
    				Result result = tx.run( "MATCH (u:User{name: '"+ userName +"'})-[l:LIKE]->(s:Series)-[t:SIMILAR] ->(serie) RETURN serie.name" );
    				LinkedList<String> myseries = new LinkedList<String>();
    				List<Record> registros = result.list();
    				for (int i = 0; i < registros.size(); i++) {
    					myseries.add(registros.get(i).get("serie.name").asString());
    				}                    
    				return myseries;
    			}
    		} );
    		return series;
    	}
    }
    /**
     * Get Friends enable recommendations
     * @return String of Series
     */
    public LinkedList<String> getFriendsSeries(String email)
    {
    	try ( Session session = driver.session() )
    	{
    		LinkedList<String> series = session.readTransaction( new TransactionWork<LinkedList<String>>()
    		{
    			@Override
    			public LinkedList<String> execute( Transaction tx )
    			{
    				Result result = tx.run("MATCH (u:User{email: '"+email+"'})-[f:FRIEND]-(v:User {friends: True})-[l:LIKE]->(s:Series) RETURN s.name" );
    				LinkedList<String> myseries = new LinkedList<String>();
    				List<Record> registros = result.list();
    				for (int i = 0; i < registros.size(); i++) {
    					myseries.add(registros.get(i).get("s.name").asString());
    				}                    
    				return myseries;
    			}
    		} );
    		return series;
    	}
    }
    /**
     * Get Genre recommendations
     * @param userName
     * @return
     */
    public LinkedList<String> getGenreSeries(String userName)
    {
    	try ( Session session = driver.session() )
    	{
    		LinkedList<String> series = session.readTransaction( new TransactionWork<LinkedList<String>>()
    		{
    			@Override
    			public LinkedList<String> execute( Transaction tx )
    			{
    				Result result = tx.run("MATCH (u:User{name: '"+userName+"'})-[l:LIKE]->(g:Genre)<-[m:GENRE]-(like) RETURN like.name");
    				LinkedList<String> myseries = new LinkedList<String>();
    				List<Record> registros = result.list();
    				for (int i = 0; i < registros.size(); i++) {
    					myseries.add(registros.get(i).get("like.name").asString());
    				}                    
    				return myseries;
    			}
    		} );
    		return series;
    	}
    }
    /**
     * Returns Series Ratings 
     * @return
     */
    public HashMap<String, Float> getSeriesRatingsMap()
    {
    	try ( Session session = driver.session() )
    	{
    		HashMap<String, Float> series = session.readTransaction( new TransactionWork<HashMap<String,Float>>()
    		{
    			@Override
    			public HashMap<String, Float> execute( Transaction tx )
    			{
    				Result result = tx.run("match (series:Series) return series.name, series.rating");
    				HashMap<String, Float> seriesRatings = new HashMap<String, Float>();
    				List<Record> registros = result.list();
    				for (int i = 0; i < registros.size(); i++) {
    					seriesRatings.put(registros.get(i).get("series.name").asString(), (float) registros.get(i).get("series.rating").asDouble());
    				}                    
    				return seriesRatings;
    			}
    		} );
    		return series;
    	}
    }
    /**
     * Returns Running Time
     * @return Map<String, Integer> with Series Running Time
     */
    public HashMap<String, Integer> getSeriesRunning_timeMap()
    {
    	try ( Session session = driver.session() )
    	{
    		HashMap<String, Integer> series = session.readTransaction( new TransactionWork<HashMap<String,Integer>>()
    		{
    			@Override
    			public HashMap<String, Integer> execute( Transaction tx )
    			{
    				Result result = tx.run("match (series:Series) return series.name, series.running_time");
    				HashMap<String, Integer> seriesRatings = new HashMap<String, Integer>();
    				List<Record> registros = result.list();
    				for (int i = 0; i < registros.size(); i++) {
    					seriesRatings.put(registros.get(i).get("series.name").asString(), registros.get(i).get("series.running_time").asInt());
    				}                    
    				return seriesRatings;
    			}
    		} );
    		return series;
    	}
    }
    /**
     * Returns Running Time
     * @return Map<String, Integer> with Series Running Time
     */
    public HashMap<String, Integer> getSeriesSeasonsMap()
    {
    	try ( Session session = driver.session() )
    	{
    		HashMap<String, Integer> series = session.readTransaction( new TransactionWork<HashMap<String,Integer>>()
    		{
    			@Override
    			public HashMap<String, Integer> execute( Transaction tx )
    			{
    				Result result = tx.run("match (series:Series) return series.name, series.seasons");
    				HashMap<String, Integer> seriesRatings = new HashMap<String, Integer>();
    				List<Record> registros = result.list();
    				for (int i = 0; i < registros.size(); i++) {
    					seriesRatings.put(registros.get(i).get("series.name").asString(), registros.get(i).get("series.seasons").asInt());
    				}                    
    				return seriesRatings;
    			}
    		} );
    		return series;
    	}
    }
    /**
     * Get User Rating preference
     * @return
     */
    public LinkedList<Float> getUserRatingPreference(String email)
    {
    	try ( Session session = driver.session() )
    	{
    		LinkedList<Float> users = session.readTransaction( new TransactionWork<LinkedList<Float>>()
    		{
    			@Override
    			public LinkedList<Float> execute( Transaction tx )
    			{
    				Result result = tx.run("MATCH (user:User {email:'"+email+"'}) RETURN user.rating");
    				LinkedList<Float> allUsers = new LinkedList<Float>();
    				List<Record> registros = result.list();
    				for (int i = 0; i < registros.size(); i++) {
    					if (registros.get(i).get("user.rating") == null) {
    						allUsers.add(0f);
    					} else {
    					allUsers.add((float) registros.get(i).get("user.rating").asDouble());
    					}
    				}
    				return allUsers;
    			}
    		} );
    		return users;
    	}
    }
    /**
     * Get User Seasons preference
     * @return
     */
    public LinkedList<Float> getUserSeasonsPreference(String email)
    {
    	try ( Session session = driver.session() )
    	{
    		LinkedList<Float> users = session.readTransaction( new TransactionWork<LinkedList<Float>>()
    		{
    			@Override
    			public LinkedList<Float> execute( Transaction tx )
    			{
    				Result result = tx.run("MATCH (user:User {email:'"+email+"'}) RETURN user.seasons");
    				LinkedList<Float> allUsers = new LinkedList<Float>();
    				List<Record> registros = result.list();
    				for (int i = 0; i < registros.size(); i++) {
    					if (registros.get(i).get("user.seasons") == null) {
    						allUsers.add(0f);
    					} else {
    					allUsers.add((float) registros.get(i).get("user.seasons").asDouble());
    					}
    				}
    				return allUsers;
    			}
    		} );
    		return users;
    	}
    }
    /**
     * Get User Duration preference
     * @return
     */
    public LinkedList<Float> getUserDurationPreference(String email)
    {
    	try ( Session session = driver.session() )
    	{
    		LinkedList<Float> users = session.readTransaction( new TransactionWork<LinkedList<Float>>()
    		{
    			@Override
    			public LinkedList<Float> execute( Transaction tx )
    			{
    				Result result = tx.run("MATCH (user:User {email:'"+email+"'}) RETURN user.duration");
    				LinkedList<Float> allUsers = new LinkedList<Float>();
    				List<Record> registros = result.list();
    				for (int i = 0; i < registros.size(); i++) {
    					if (registros.get(i).get("user.duration") == null) {
    						allUsers.add(0f);
    					} else {
    					allUsers.add((float) registros.get(i).get("user.duration").asDouble());
    					}
    				}
    				return allUsers;
    			}
    		} );
    		return users;
    	}
    }
    /**
     * Get User Seasons Taste
     * @return
     */
    public LinkedList<Integer> getUserSeasonsTaste(String email)
    {
    	try ( Session session = driver.session() )
    	{
    		LinkedList<Integer> users = session.readTransaction( new TransactionWork<LinkedList<Integer>>()
    		{
    			@Override
    			public LinkedList<Integer> execute( Transaction tx )
    			{
    				Result result = tx.run("MATCH (user:User {email:'"+email+"'}) RETURN user.tasteSeasons");
    				LinkedList<Integer> allUsers = new LinkedList<Integer>();
    				List<Record> registros = result.list();
    				for (int i = 0; i < registros.size(); i++) {
    					if (registros.get(i).get("user.seasons") == null) {
    						allUsers.add(2);
    					} else {
    					allUsers.add(registros.get(i).get("user.tasteSeasons").asInt());
    					}
    				}
    				return allUsers;
    			}
    		} );
    		return users;
    	}
    }
    /**
     * Get User Duration Taste
     * @return
     */
    public LinkedList<Boolean> getUserDurationTaste(String email)
    {
    	try ( Session session = driver.session() )
    	{
    		LinkedList<Boolean> users = session.readTransaction( new TransactionWork<LinkedList<Boolean>>()
    		{
    			@Override
    			public LinkedList<Boolean> execute( Transaction tx )
    			{
    				Result result = tx.run("MATCH (user:User {email:'"+email+"'}) RETURN user.tasteDuration");
    				LinkedList<Boolean> allUsers = new LinkedList<Boolean>();
    				List<Record> registros = result.list();
    				for (int i = 0; i < registros.size(); i++) {
    					if (registros.get(i).get("user.seasons") == null) {
    						allUsers.add(true);
    					} else {
    					allUsers.add(registros.get(i).get("user.tasteDuration").asBoolean());
    					}
    				}
    				return allUsers;
    			}
    		} );
    		return users;
    	}
    }
    /**
     * Return Personal recommendations
     * @return HashMap<String, Float>
     */
    @SuppressWarnings("unchecked")
	public ArrayList<String> algorithm(String userEmail)
    {
    	try( Session session = driver.session())
    	{
    		LinkedList<String> allSeriesNames = getSeriesName();
    		HashMap<String, Float> finalScore = new HashMap<String, Float>();
    		for(String series:allSeriesNames){
    			if (series == null || series.isEmpty()){
    			} else {
    			finalScore.put(series, 0f);
    			}
    		}
    		
    		// Obtener las relaciones extendidas
    		LinkedList<String> listOfSimilar = getSimilarSeries(userEmail);
    		LinkedList<String> listOfFriends = getFriendsSeries(userEmail);
    		LinkedList<String> listOfGenre = getGenreSeries(userEmail);
    		
    		for(int i=0; i<allSeriesNames.size(); i++) {
    			//Agregar al rating a la calificacion
    			Float scoreRating = getSeriesRatingsMap().get(allSeriesNames.get(i));
    			scoreRating = (scoreRating-6.0f)*2.0f;
    			finalScore.put(allSeriesNames.get(i),finalScore.get(allSeriesNames.get(i))+scoreRating);
    			
    			//Agregar el gusto de las temporadas
        		int currentSeasons = getSeriesSeasonsMap().get(allSeriesNames.get(i));
        		int seasonlenght;
        		if (currentSeasons <= 3) {
        			seasonlenght = 1;
        		} else if (currentSeasons >3 && currentSeasons <=6){
        			seasonlenght = 2;
        		} else {
        			seasonlenght = 3;
        		}
        		int valueofSeasonsTaste = getUserSeasonsTaste(userEmail).get(0);
        		if(valueofSeasonsTaste == seasonlenght) {
        			finalScore.put(allSeriesNames.get(i),finalScore.get(allSeriesNames.get(i))+ 2);
        		} else if(Math.abs(valueofSeasonsTaste - seasonlenght)== 1) {
        			finalScore.put(allSeriesNames.get(i),finalScore.get(allSeriesNames.get(i))- 1);
        		} else {
        			finalScore.put(allSeriesNames.get(i),finalScore.get(allSeriesNames.get(i))- 2);
        		}
        		
        		//Agregar el gusto la duracion
        		boolean valueofDurationTaste = getUserDurationTaste(userEmail).get(0);
        		float currentRunning_time = getSeriesRunning_timeMap().get(allSeriesNames.get(i));
        		boolean scoreDuration;
        		if (currentRunning_time < 30) {
        			scoreDuration = false;
        		} else {
        			scoreDuration = true;
        		}
        		if (valueofDurationTaste == scoreDuration) {
        			finalScore.put(allSeriesNames.get(i),finalScore.get(allSeriesNames.get(i))+ 2);
        		} else {
        			finalScore.put(allSeriesNames.get(i),finalScore.get(allSeriesNames.get(i))- 2);
        		}
        		
        		//Agregar Relaciones de usuario
        		
        		//Agregar Puntuacion de Series Similares
        		if (listOfSimilar.contains(allSeriesNames.get(i))){
        			finalScore.put(allSeriesNames.get(i),finalScore.get(allSeriesNames.get(i))+ 2);
        		}
        		
        		//Agregar Puntuacion de Series Amigo
        		if (listOfFriends.contains(allSeriesNames.get(i))) {
        			finalScore.put(allSeriesNames.get(i),finalScore.get(allSeriesNames.get(i))+ 2);
        		}
        		
        		//Agregar Puntuacion de Series Genero
        		if (listOfGenre.contains(allSeriesNames.get(i))) {
        			finalScore.put(allSeriesNames.get(i),finalScore.get(allSeriesNames.get(i))+ 2);
        		}
    		}
    		HashMap<String, Float> allRankingsMap = finalScore;
    		LinkedList<String> userSeries = getUserSeries(userEmail);
    		for (String watchedSeries: userSeries) {
    			allRankingsMap.remove(watchedSeries);
    		}
    		
    		/* Impresion de Mapa
    		HashMap <String, Float> finalMap = sortByValues(allRankingsMap);
    		for (Map.Entry<String, Float> entry : finalMap.entrySet()) {
    		    System.out.println(entry.getKey() + ":" + entry.getValue().toString());
    		}
    		*/
    		
    		//Sort Descending ranking
    		ArrayList<String> finalSelection = new ArrayList<String>();
    		@SuppressWarnings("rawtypes")
			Set set2 = sortByValues(allRankingsMap).entrySet();
    	      @SuppressWarnings("rawtypes")
			Iterator iterator2 = set2.iterator();
    	      while(iterator2.hasNext() && finalSelection.size()<13) {
    	           @SuppressWarnings("rawtypes")
				Map.Entry me2 = (Map.Entry)iterator2.next();
    	           finalSelection.add(me2.getKey().toString());
    	           //System.out.println(me2.getKey().toString());
    	      }
    		return finalSelection;	
    	}
    }
    /**
     * Sort HashMap in descending order
     * @param map
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	private static HashMap sortByValues(HashMap map) { 
        List list = new LinkedList(map.entrySet());
        // Defined Custom Comparator here
        Collections.sort(list, new Comparator() {
             public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o2)).getValue()).compareTo(((Map.Entry) (o1)).getValue());
             }
        });

        // Here I am copying the sorted list in HashMap
        // using LinkedHashMap to preserve the insertion order
        HashMap sortedHashMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
               Map.Entry entry = (Map.Entry) it.next();
               sortedHashMap.put(entry.getKey(), entry.getValue());
        } 
        return sortedHashMap;
   }
    	
}