package dataAccessLayer;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.TransactionWork;
import org.neo4j.driver.Value;

import static org.neo4j.driver.Values.parameters;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

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
    public LinkedList<String> getSeries()
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
    public LinkedList<String> getUserSeries(String userName)
    {
    	try ( Session session = driver.session() )
    	{
    		LinkedList<String> series = session.readTransaction( new TransactionWork<LinkedList<String>>()
    		{
    			@Override
    			public LinkedList<String> execute( Transaction tx )
    			{
    				Result result = tx.run("MATCH(n:User {name:'"+userName+"'})-[:LIKE]->(s:Series) RETURN s.name" );
    				LinkedList<String> myseries = new LinkedList<String>();
    				List<Record> registros = result.list();
    				for (int i = 0; i < registros.size(); i++) {
    					myseries.add(registros.get(i).toString());
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
    					myseries.add(registros.get(i).toString());
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
    public LinkedList<String> getFriendsSeries(String userName)
    {
    	try ( Session session = driver.session() )
    	{
    		LinkedList<String> series = session.readTransaction( new TransactionWork<LinkedList<String>>()
    		{
    			@Override
    			public LinkedList<String> execute( Transaction tx )
    			{
    				Result result = tx.run("MATCH (u:User{name: ’"+userName+"’})-[f:FRIEND]-(v:User {friends: True})-[l:LIKE]->(s:Series) RETURN s.name" );
    				LinkedList<String> myseries = new LinkedList<String>();
    				List<Record> registros = result.list();
    				for (int i = 0; i < registros.size(); i++) {
    					myseries.add(registros.get(i).toString());
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
    				Result result = tx.run("MATCH (u:User{name: ’"+userName+"’})-[l:LIKE]->(g:Genre)<-[m:GENRE]-(like) RETURN like.name");
    				LinkedList<String> myseries = new LinkedList<String>();
    				List<Record> registros = result.list();
    				for (int i = 0; i < registros.size(); i++) {
    					myseries.add(registros.get(i).toString());
    				}                    
    				return myseries;
    			}
    		} );
    		return series;
    	}
    }
    /**
     * Returns User 
     * @return
     */
    public HashMap<String, Integer> getUserPreferences(String userName)
    {
    	HashMap<String, Integer> mySeries = new HashMap<String, Integer>();
    	LinkedList<String> allSeries = getSeries();
    	LinkedList<String> userSeries = getUserSeries(userName);
    	return mySeries;
    }
}