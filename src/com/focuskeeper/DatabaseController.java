package com.focuskeeper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseController {
	private static Connection con;
	private static boolean hasTables;
	private static String DATEFORMAT = "yyy/MM/dd";
    static final Logger logger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

	//Functions:
		//getConnection()  		 :  connects to database*
		//getDatabaseMetaData()  :  prints all database columns and values
		//createTable()			 :  creates all database tables with correct columns (only needs to be called if tables don't exist)
		//restartDB()			 :  wipe and delete all database tables (cannot be undone)
		//addList()				 :  adds new URLS and list when new blocklist is created
		//deleteList()			 :	deletes blocklist and it's URLs (if not used by another list)
		//addURLUsage()			 :	updates URL Usage time each time user goes on new website
		//getRecentlyUsed()		 :  gets top 5 most visited sites that day (to display on hope page of application)
		//getTotalTimeToday()	 :  gets total screen time user has spent today 
	
	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		if(con==null) getConnection();
		
		createTable();
		//always need to call getDatabaseMetaData(); it saves data to our global variables
		getDatabaseMetaData();
		//testing addList feature
		
//		restartDB();
		

		getDatabaseMetaData();
		getMostUsed("2019/04/28", "2019/04/29");
		
	}
	
	private static void getConnection() throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		con = DriverManager.getConnection("jdbc:sqlite:src/com/focuskeeper/DB/FocusKeeper.db");
	}
	
	 public static void getDatabaseMetaData()
	    {
	        try {

	            DatabaseMetaData dbmd = con.getMetaData();
	            Statement stmt = con.createStatement();
	            String[] types = {"TABLE"};
	            ResultSet rs = dbmd.getTables(null, null, "%", types);
	            
			 	ArrayList<String> tableNames = new ArrayList<>();
	            while(rs.next())
	            	tableNames.add(rs.getString("TABLE_NAME"));
	            
	            //print BlockLists
	            System.out.println(tableNames.get(0));
	            ResultSet res = stmt.executeQuery("SELECT * FROM BlockLists");
	            //print column values
	            System.out.println("BlockID     BlockName");
	            while (res.next()) {
	            	int BlockID = res.getInt("BlockID");
	            	String BlockName = res.getString("BlockName");
	            	System.out.println(BlockID + "          " + BlockName);
		            }
	            
	            System.out.println("\n");

	            //print URLs Table
	            System.out.println(tableNames.get(1));
	            rs = stmt.executeQuery("SELECT * FROM URLSettings");
	            
	            //permanent column names
	            System.out.println("ID    BlockId");
	            
	            //print column values
	            while (rs.next()) {
	               int id = rs.getInt("ID");
	               int blockID = rs.getInt("BlockID");
	               System.out.println(id+"        "+ blockID);
	            }
	            System.out.println("\n");
	            
	            
	            //print WebsiteUsage Table
	            System.out.println(tableNames.get(2));
	            rs = stmt.executeQuery("SELECT * FROM URLs");
	            
	            //permanent column names - no need to grab from table
	            System.out.println("ID      URL");
	            
	            while(rs.next()) {
	            	int id = rs.getInt("ID");
	            	String url = rs.getString("URL");
	            	System.out.println(id + "    " + url);
	            }
	            System.out.println("\n");
	            

	            System.out.println(tableNames.get(3));
	            rs = stmt.executeQuery("SELECT * FROM WebsiteUsage");
	            
	            //permanenet column names
	            System.out.println("ID     Elapsed     Date");
	            //print column values
	            while(rs.next()) {
	            	int id = rs.getInt("id");
	            	int time = rs.getInt("elapsedTime");
	            	String date = rs.getString("Date");
	            	System.out.println(id + "      " + time + "        " + date + "      ");
	            	
	            }
	            System.out.println();
	            
	         } catch(SQLException e) {
	        	 FocusKeeper.logger.error("", e);
	         }

	    }
	//creates our three tables: URLs, URLSettings, WebsiteUsage
	//can be called if they are already created
	public static void createTable() throws SQLException, ClassNotFoundException {
		//creating the tables in the database:
			//URLS
			//WebsiteUsage
			//URLSettings
		if(!hasTables) {
			Statement state = con.createStatement();
			String createURLS = "CREATE TABLE IF NOT EXISTS URLs (\n"
					+ " ID INTEGER PRIMARY KEY AUTOINCREMENT, \n"
					+ " URL text NOT NULL);";
			state.executeUpdate(createURLS);	
			
			String createBlockList = "CREATE TABLE IF NOT EXISTS BlockLists (\n"
					+ " BlockID INTEGER PRIMARY KEY AUTOINCREMENT, \n"
					+ " BlockName text NOT NULL UNIQUE);";
			state.executeUpdate(createBlockList);
					
			
			String createUsage = "CREATE TABLE IF NOT EXISTS WebsiteUsage (\n"
					+ " ID INTEGER NOT NULL, \n"
					+ " elapsedTime int DEFAULT 0, \n"
					+ " Date text NOT NULL, \n"
					+ " CONSTRAINT UQ_WebsiteUsage UNIQUE(ID, DATE));";   
												 //store time as integer of minutes on the site. 
												 //if user goes on site multiple times in one day,
												 //add elapsed time to current stored value
			//when adding data: Insert into Event(time,Date) values(time, GETDATE());
			state.executeUpdate(createUsage);	
			
			String addNew = "INSERT OR IGNORE INTO BlockLists (BlockID, BlockName)\n"
					+ " VALUES(null,'Distractions');";
			state.executeUpdate(addNew);
			
			String createSettings = "CREATE TABLE IF NOT EXISTS URLSettings (\n"
					+ " ID INTEGER NOT NULL, \n"
					+ " BlockID INTEGER NOT NULL, \n"
					+ " CONSTRAINT UQ_URLSettings UNIQUE(ID, BLOCKID));";
			
			state.executeUpdate(createSettings);
			hasTables = true;
		}
	}
	
	//deletes all tables and wipes all database 
	public static void restartDB() throws ClassNotFoundException, SQLException {
		if(con == null) getConnection();
		
		Statement state = con.createStatement();
		String sql = "DROP TABLE URLs;";
		state.executeUpdate(sql);
		
		sql = "DROP TABLE URLSettings;";
		state.executeUpdate(sql);

		sql = "DROP TABLE WebsiteUsage;";
		state.executeUpdate(sql);
		
		sql = "DROP TABLE BlockLists;";
		state.executeUpdate(sql);
	}
	
	//When user adds a new block list with URLS
	//Parameters: Name of new List, and list of URLS in list
	public static void addList(String list, String[] URLS) throws SQLException{
		//adds new list as field in URLSettings database
		Statement state = con.createStatement();
		String addNew = "INSERT INTO BlockLists (BlockID, BlockName)\n"
				+ " VALUES(null,'" + list + "');";
		state.executeUpdate(addNew);
		

		//adds new URLS to URLs database
		Statement state2 = con.createStatement();
		for(String url : URLS) {
			addNew = "INSERT INTO URLs (id, URL)\n" + 
					"SELECT * FROM (SELECT null, '" + url + "') AS tmp\n" + 
					"WHERE NOT EXISTS (\n" + 
					"    SELECT URL FROM URLs WHERE URL = '" + url + "'\n" + 
					") LIMIT 1;";
			state2.executeUpdate(addNew);
	}
		
		//insert into URLSettings id of URLS where URL is in block list passed to function
			Statement state3 = con.createStatement();
			//inserting all default values as false
            	
            //inserting the URL ID and BlockListName ID into URLSettings
            //ignores this command if URL already exists in database with blockListID
        for(String url : URLS) {
        	String insert = "INSERT or IGNORE INTO URLSettings (ID, BlockID)\n"
        			+ " VALUES((SELECT ID from URLs where URL='" + url + "'), (SELECT BlockID from BlockLists"
        					+ " where BlockName='" + list + "'));";
        	state3.executeUpdate(insert);
        }
	}
	
	
	//When user wants to delete block list
	//Parameters: Name of the list to be deleted
	public static void deleteList(String list) throws SQLException {
		//deletes entry in URLSettings where URL is attached to list being dropped
		//deletes URLS in URLs database where they only existed in deleted list
		Statement state2 = con.createStatement();
		String delete = "DELETE FROM URLSettings WHERE BlockID= (SELECT BlockID FROM BlockLists WHERE BlockName='"
				+ list + "');";
		state2.executeUpdate(delete);
		
		String delete2 = "DELETE FROM URLs WHERE ID NOT IN (SELECT US.ID FROM URLSettings US);";
		state2.executeUpdate(delete2);
		
		String delete3 = "DELETE FROM BlockLists WHERE BlockName='" + list + "';";
		state2.executeUpdate(delete3);

		
	}
	
	//When user goes on website
	//Parameters: Time spent on website in current period, name of website
	public static void addURLUsage(Integer elapsedTime, String URL) throws SQLException {
		//adds current elapsed time to current value in URLUsage database
		//adds new entry if first visit in day
		int currentTime;
		int id;
		LocalDate localDate = LocalDate.now();
        String date = DateTimeFormatter.ofPattern(DATEFORMAT).format(localDate);
        
        Statement state = con.createStatement();
			
		String getID = "SELECT * FROM URLs WHERE URL = '" + URL + "';";
		ResultSet gotID = state.executeQuery(getID);
		id = gotID.getInt("ID");
		
		
		String getUsage = "SELECT * FROM WebsiteUsage WHERE ID = " + id + " AND"
					+ " Date = '" + date + "';";
		ResultSet usage = state.executeQuery(getUsage);
		if(usage.next()) {
			currentTime = usage.getInt("elapsedTime");
		} else currentTime = 0;

		currentTime += elapsedTime;
		
		//inserts if can (unique constraint won't let it if already there)
		String add = "INSERT OR IGNORE INTO WebsiteUsage (ID, elapsedTime, Date)\n"
					+ " VALUES(" + id + ", " + currentTime + ", '" + date + "');";
		state.executeUpdate(add);
		
		//updates is done if the insert failed (done anyways but doesn't matter)
		String insert = "UPDATE WebsiteUsage SET ID= " + id + ", elapsedTime = "
					 + currentTime + ", Date = '" + date + "' WHERE"
					 		+ " ID = " + id + " AND Date = '" + date + "';";
		state.executeUpdate(insert);
							
	}
	
	//To Display top visited sites in given start and end dates
	public static Map<String, Integer> getMostUsed(String start, String end) throws SQLException {
		//queries to get sites with highest elapsedTime in WebsiteUsage

		LinkedHashMap <String, Integer> mostUsed = new LinkedHashMap<>();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATEFORMAT);
		StringBuilder datesForQuery = new StringBuilder("(");
				
		//convert String to LocalDate
		LocalDate newStart = LocalDate.parse(start, formatter);
		LocalDate newEnd = LocalDate.parse(end, formatter);
		
        String formattedDate = DateTimeFormatter.ofPattern(DATEFORMAT).format(newStart);
        datesForQuery.append("'" + formattedDate + "'");
		if(newStart.isBefore(newEnd)) {
			newStart = newStart.plusDays(1);
			for (LocalDate date = newStart; ((date.isBefore(newEnd)) || (date.isEqual(newEnd))); date = date.plusDays(1)) {
		        formattedDate = DateTimeFormatter.ofPattern(DATEFORMAT).format(date);
		        datesForQuery.append(", '" + formattedDate + "'");
			}
		}
		datesForQuery.append(")");
		
		//query for most used
		Statement state = con.createStatement();
		String get = "SELECT u.URL AS url, sum(w.elapsedTime) AS elapsed"
				+ " FROM WebsiteUsage AS w"
				+ " LEFT JOIN URLs AS u on w.ID = u.ID"
				+ " WHERE w.Date IN " + datesForQuery 
				+ " GROUP BY u.URL"
				+ " ORDER BY elapsed DESC LIMIT 5;";
		ResultSet rs = state.executeQuery(get);
		while(rs.next()) {
			mostUsed.put(rs.getString("url"), rs.getInt("elapsed"));
		}
		return mostUsed;
	}
	

	//To Display recently used sites in day
	public static Map<String, Integer> getRecentlyUsed() throws SQLException {
		//queries to get sites recently used in WebsiteUsage
		//same as print but add to a dictionary-type thing
		
		//("www.instagram.com", 45)
		LinkedHashMap <String, Integer> recents = new LinkedHashMap<>();
		Statement state = con.createStatement();
		String getRecent = "SELECT * FROM WebsiteUsage ORDER BY elapsedTime DESC LIMIT 5;";
		ResultSet usage = state.executeQuery(getRecent);
		//add to lists to return
		
        //save column values
        while(usage.next()) {
        	int id = usage.getInt("ID");
        	int time = usage.getInt("elapsedTime");

        	Statement state2 = con.createStatement();
        	String getURL = "SELECT * FROM URLs WHERE ID = '" + id + "';";
        	ResultSet url = state2.executeQuery(getURL);
        	String foundURL = url.getString("URL");
        	
        	recents.put(foundURL, time);
        }
        		
		return recents;
		
	}
	
	//to Display total screen time today
	//can be used to display screen time of past week (save values)
	public static int getTotalTimeToday() throws SQLException {
		int totalTimeToday = 0;
		Statement state = con.createStatement();
		LocalDate localDate = LocalDate.now();
        String date = DateTimeFormatter.ofPattern(DATEFORMAT).format(localDate);
                
        String get = "SELECT * FROM WebsiteUsage WHERE Date='" + date + "';";
        ResultSet rs = state.executeQuery(get);
        while(rs.next()) {
        	totalTimeToday += rs.getInt("elapsedTime");  	
        }
        return totalTimeToday;
	}
	
	//to Display total time spend in focus today
	//used to display as a % -- use function with getTotalTimeToday()
	public static int getTotalFocusTimeToday() throws SQLException {
		int totalTimeToday = 0;
		Statement state = con.createStatement();
		LocalDate localDate = LocalDate.now();
        String date = DateTimeFormatter.ofPattern(DATEFORMAT).format(localDate);
        
        String get = "SELECT * FROM WebsiteUsage WHERE Date = '" + date + "'"
        		+ " AND ID IN (SELECT ID FROM URLSettings WHERE BlockID = 1);";
        ResultSet rs = state.executeQuery(get);
        while(rs.next()) {
        	totalTimeToday += rs.getInt("elapsedTime");
        }
        return totalTimeToday;
	}	
}
