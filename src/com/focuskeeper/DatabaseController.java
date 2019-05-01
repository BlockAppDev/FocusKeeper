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
	private static Connection CON;
	private static boolean HASTABLES;
	private static String DATEFORMAT = "yyy/MM/dd";
    static final Logger LOGGER = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    private static String ELAPSED = "elapsedTime";

	
	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		if(CON==null) getConnection();
		String[] urls = {"www.facebook.com", "www.instagram.com"};
		addList("School", urls);
		getDatabaseMetaData();
	}
	
	//getConnection()  		 :  connects to database*
	private static void getConnection() throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		CON = DriverManager.getConnection("jdbc:sqlite:FocusKeeper.db");
	}
	
	//getDatabaseMetaData()  :  prints all database columns and values
	 public static void getDatabaseMetaData() throws SQLException {
		 
			DatabaseMetaData dbmd = CON.getMetaData();
			Statement stmt = CON.createStatement();
			String[] types = {"TABLE"};
			ResultSet rs = dbmd.getTables(null, null, "%", types);
			ArrayList<String> tableNames = new ArrayList<>();
			while(rs.next())
				tableNames.add(rs.getString("TABLE_NAME"));
		try {	
			//print BlockLists
			System.out.println(tableNames.get(0));
			ResultSet res = stmt.executeQuery("SELECT * FROM BlockLists");
			//print column values
			System.out.println("BlockID     BlockName");
			while (res.next()) {
				int BlockId = res.getInt("BlockID");
				String blockName = res.getString("BlockName");
				System.out.println(BlockId + "          " + blockName);
			    }
			
			System.out.println("\n");
			
			//print URLs Table
			System.out.println(tableNames.get(1));
			rs = stmt.executeQuery("SELECT * FROM ItemSettings");
			
			//permanent column names
			System.out.println("ID    BlockId");
			
			//print column values
			while (rs.next()) {
			   int id = rs.getInt("ID");
			   int blockId = rs.getInt("BlockID");
			   System.out.println(id+"        "+ blockId);
			}
			System.out.println("\n");
			
			
			//print WebsiteUsage Table
			System.out.println(tableNames.get(2));
			rs = stmt.executeQuery("SELECT * FROM Items");
			
			//permanent column names - no need to grab from table
			System.out.println("ID      Item");
			
			while(rs.next()) {
				int id = rs.getInt("ID");
				String url = rs.getString("Item");
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
				int time = rs.getInt(ELAPSED);
				String date = rs.getString("Date");
				System.out.println(id + "      " + time + "        " + date + "      ");
				
			}
			System.out.println();	
		 } catch (SQLException e) {
			 
		 } finally {
			 stmt.close();
			 rs.close();
		 }
	}
	 
	//createTable()			 :  creates all database tables with correct columns (only needs to be called if tables don't exist)
	public static void createTable() throws SQLException{
		//creates our three tables: URLs, URLSettings, WebsiteUsage
		if(!HASTABLES) {
			Statement state = CON.createStatement();
			String createURLS = "CREATE TABLE IF NOT EXISTS Items (\n"
					+ " ID INTEGER PRIMARY KEY AUTOINCREMENT, \n"
					+ " Item text NOT NULL UNIQUE);";
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
												
			state.executeUpdate(createUsage);	
			
			String addNew = "INSERT OR IGNORE INTO BlockLists (BlockID, BlockName)\n"
					+ " VALUES(null,'Distractions');";
			state.executeUpdate(addNew);
			
			String createSettings = "CREATE TABLE IF NOT EXISTS ItemSettings (\n"
					+ " ID INTEGER NOT NULL, \n"
					+ " BlockID INTEGER NOT NULL, \n"
					+ " CONSTRAINT UQ_URLSettings UNIQUE(ID, BLOCKID));";
			
			state.executeUpdate(createSettings);
			HASTABLES = true;
		}
	}
	
	//restartDB()			 :  wipe and delete all database tables (cannot be undone)
	public static void restartDB() throws ClassNotFoundException, SQLException {
		if(CON == null) getConnection();
		
		Statement state = CON.createStatement();
		String sql = "DROP TABLE Items;";
		state.executeUpdate(sql);
		
		sql = "DROP TABLE ItemSettings;";
		state.executeUpdate(sql);

		sql = "DROP TABLE WebsiteUsage;";
		state.executeUpdate(sql);
		
		sql = "DROP TABLE BlockLists;";
		state.executeUpdate(sql);
	}
	
	//addList()				 :  adds new URLS and list when new blocklist is created
	//Parameters: Name of new List, and list of URLS in list
	public static void addList(String list, String[] urls) throws SQLException{
		//adds new list as field in URLSettings database
		Statement state = CON.createStatement();
		String addNew = "INSERT OR IGNORE INTO BlockLists (BlockID, BlockName)\n"
				+ " VALUES(null,'" + list + "');";
		state.executeUpdate(addNew);
		StringBuilder insertQuery = new StringBuilder("BEGIN TRANSACTION;\n");

		//adds new URLS to URLs database
		Statement state2 = CON.createStatement();
		
		for(String url : urls) {
				insertQuery.append((" INSERT OR IGNORE INTO Items (id, Item)\n" + 
						" VALUES(null, '" + url + "');\n"));
		}
		insertQuery.append("COMMIT;");
		state2.executeUpdate(insertQuery.toString());

		//insert into URLSettings id of URLS where URL is in block list passed to function
			Statement state3 = CON.createStatement();
			//inserting all default values as false
            	
            //inserting the URL ID and BlockListName ID into URLSettings
            //ignores this command if URL already exists in database with blockListID
        for(String url : urls) {
        	String insert = "INSERT or IGNORE INTO ItemSettings (ID, BlockID)\n"
        			+ " VALUES((SELECT ID from Items where Item='" + url + "'), (SELECT BlockID from BlockLists"
        					+ " where BlockName='" + list + "'));";
        	state3.executeUpdate(insert);
        }
	}
	
	
	//deleteList()			 :	deletes blocklist and it's URLs (if not used by another list)
	//Parameters: Name of the list to be deleted
	public static void deleteList(String list) throws SQLException {
		//deletes entry in URLSettings where URL is attached to list being dropped
		//deletes URLS in URLs database where they only existed in deleted list
		Statement state2 = CON.createStatement();
		String delete = "DELETE FROM ItemSettings WHERE BlockID= (SELECT BlockID FROM BlockLists WHERE BlockName='"
				+ list + "');";
		state2.executeUpdate(delete);
		
		String delete2 = "DELETE FROM Items WHERE ID NOT IN (SELECT US.ID FROM ItemSettings US);";
		state2.executeUpdate(delete2);
		
		String delete3 = "DELETE FROM BlockLists WHERE BlockName='" + list + "';";
		state2.executeUpdate(delete3);

		
	}
	
	//addURLUsage()			 :	updates URL Usage time each time user goes on new website
	//Parameters: Time spent on website in current period, name of website
	public static void addURLUsage(Integer elapsedTime, String url) throws SQLException {
		//adds current elapsed time to current value in URLUsage database
		//adds new entry if first visit in day
		int currentTime;
		int id;
		LocalDate localDate = LocalDate.now();
        String date = DateTimeFormatter.ofPattern(DATEFORMAT).format(localDate);
        
        Statement state = CON.createStatement();
			
		String getID = "SELECT * FROM Items WHERE Item = '" + url + "';";
		ResultSet gotID = state.executeQuery(getID);
		id = gotID.getInt("ID");
		
		
		String getUsage = "SELECT * FROM WebsiteUsage WHERE ID = " + id + " AND"
					+ " Date = '" + date + "';";
		ResultSet usage = state.executeQuery(getUsage);
		if(usage.next()) {
			currentTime = usage.getInt(ELAPSED);
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
	
	//getMostUsed()		:		returns a map with url and total minutes spent on that site in the given date range
	//Parameters: Two Strings in form "yyy/MM/dd" representing start date and end date, including
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
		Statement state = CON.createStatement();
		String get = "SELECT u.Item AS item, sum(w.elapsedTime) AS elapsed"
				+ " FROM WebsiteUsage AS w"
				+ " LEFT JOIN Items AS u on w.ID = u.ID"
				+ " WHERE w.Date IN " + datesForQuery 
				+ " GROUP BY u.Item"
				+ " ORDER BY elapsed DESC LIMIT 5;";
		ResultSet rs = state.executeQuery(get);
		while(rs.next()) {
			mostUsed.put(rs.getString("item"), rs.getInt("elapsed"));
		}
		return mostUsed;
	}
	

	//getRecentlyUsed()		 :  gets top 5 most visited sites that day (to display on hope page of application)
	public static Map<String, Integer> getRecentlyUsed() throws SQLException {
		//queries to get sites recently used in WebsiteUsage
		//same as print but add to a dictionary-type thing
		
		//("www.instagram.com", 45)
		LinkedHashMap <String, Integer> recents = new LinkedHashMap<>();
		Statement state = CON.createStatement();
		String getRecent = "SELECT * FROM WebsiteUsage ORDER BY elapsedTime DESC LIMIT 5;";
		ResultSet usage = state.executeQuery(getRecent);
		//add to lists to return
		
        //save column values
        while(usage.next()) {
        	int id = usage.getInt("ID");
        	int time = usage.getInt(ELAPSED);

        	Statement state2 = CON.createStatement();
        	String getURL = "SELECT * FROM Items WHERE ID = '" + id + "';";
        	ResultSet url = state2.executeQuery(getURL);
        	String foundURL = url.getString("Item");
        	
        	recents.put(foundURL, time);
        }
        		
		return recents;
		
	}
	
	//getTotalTimeToday()	 :  gets total screen time user has spent today 
	public static int getTotalTimeToday() throws SQLException {
		int totalTimeToday = 0;
		Statement state = CON.createStatement();
		LocalDate localDate = LocalDate.now();
        String date = DateTimeFormatter.ofPattern(DATEFORMAT).format(localDate);
                
        String get = "SELECT * FROM WebsiteUsage WHERE Date='" + date + "';";
        ResultSet rs = state.executeQuery(get);
        while(rs.next()) {
        	totalTimeToday += rs.getInt(ELAPSED);  	
        }
        return totalTimeToday;
	}
	
	//getTotalFocusTimeToday()  :  gets total screen time user has spenr in focus today
	public static int getTotalFocusTimeToday() throws SQLException {
		int totalTimeToday = 0;
		Statement state = CON.createStatement();
		LocalDate localDate = LocalDate.now();
        String date = DateTimeFormatter.ofPattern(DATEFORMAT).format(localDate);
        
        String get = "SELECT * FROM WebsiteUsage WHERE Date = '" + date + "'"
        		+ " AND ID IN (SELECT ID FROM ItemSettings WHERE BlockID = 1);";
        ResultSet rs = state.executeQuery(get);
        while(rs.next()) {
        	totalTimeToday += rs.getInt(ELAPSED);
        }
        return totalTimeToday;
	}	
}
