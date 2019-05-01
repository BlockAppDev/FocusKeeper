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
	private static boolean hashTables;
	private static String dateFormat = "yyy/MM/dd";
    static final Logger logger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    private static String elapsed = "elapsedTime";

	
	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		if(con==null) getConnection();
		String[] urls = {"www.facebook.com", "www.instagram.com"};
		addList("School", urls);
	}
	
	//getconnection()  		 :  connects to database*
	private static void getConnection() throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		con = DriverManager.getConnection("jdbc:sqlite:FocusKeeper.db");
	}
	
	
	//createTable()			 :  creates all database tables with correct columns (only needs to be called if tables don't exist)
	public static void createTable(){
		//creates our three tables: URLs, URLSettings, WebsiteUsage
		if(!hashTables) {
			try (Statement state = con.createStatement()) {
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
			} catch (SQLException e) {
				FocusKeeper.logger.error("" + e);
			}
		}
		hashTables = true;
	}
	
	//restartDB()			 :  wipe and delete all database tables (cannot be undone)
	public static void restartDB() {
		if(con == null) {
			try {
				getConnection();
			} catch (ClassNotFoundException e1) {
				FocusKeeper.logger.error("" + e1);
			} catch (SQLException e1) {
				FocusKeeper.logger.error("" + e1);
			}
		}
		try (Statement state = con.createStatement()){
			String sql = "DROP TABLE Items;";
			state.executeUpdate(sql);
			
			sql = "DROP TABLE ItemSettings;";
			state.executeUpdate(sql);
	
			sql = "DROP TABLE WebsiteUsage;";
			state.executeUpdate(sql);
			
			sql = "DROP TABLE BlockLists;";
			state.executeUpdate(sql);
		} catch (SQLException e) {
			FocusKeeper.logger.error("" + e);
		}
	}
	
	//addList()				 :  adds new URLS and list when new blocklist is created
	//Parameters: Name of new List, and list of URLS in list
	public static void addList(String list, String[] urls) throws SQLException{
		//adds new list as field in URLSettings database
		Statement state = con.createStatement();
		String addNew = "INSERT OR IGNORE INTO BlockLists (BlockID, BlockName)\n"
				+ " VALUES(null,'" + list + "');";
		state.executeUpdate(addNew);
		StringBuilder insertQuery = new StringBuilder("BEGIN TRANSACTION;\n");

		//adds new URLS to URLs database
		Statement state2 = con.createStatement();
		
		for(String url : urls) {
				insertQuery.append((" INSERT OR IGNORE INTO Items (id, Item)\n" + 
						" VALUES(null, '" + url + "');\n"));
		}
		insertQuery.append("COMMIT;");
		state2.executeUpdate(insertQuery.toString());

		//insert into URLSettings id of URLS where URL is in block list passed to function
		Statement state3 = con.createStatement();
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
		Statement state2 = con.createStatement();
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
        String date = DateTimeFormatter.ofPattern(dateFormat).format(localDate);
        
        Statement state = con.createStatement();
			
		String getID = "SELECT * FROM Items WHERE Item = '" + url + "';";
		ResultSet gotID = state.executeQuery(getID);
		id = gotID.getInt("ID");
		
		
		String getUsage = "SELECT * FROM WebsiteUsage WHERE ID = " + id + " AND"
					+ " Date = '" + date + "';";
		ResultSet usage = state.executeQuery(getUsage);
		if(usage.next()) {
			currentTime = usage.getInt(elapsed);
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
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
		StringBuilder datesForQuery = new StringBuilder("(");
				
		//convert String to LocalDate
		LocalDate newStart = LocalDate.parse(start, formatter);
		LocalDate newEnd = LocalDate.parse(end, formatter);
		
        String formattedDate = DateTimeFormatter.ofPattern(dateFormat).format(newStart);
        datesForQuery.append("'" + formattedDate + "'");
		if(newStart.isBefore(newEnd)) {
			newStart = newStart.plusDays(1);
			for (LocalDate date = newStart; ((date.isBefore(newEnd)) || (date.isEqual(newEnd))); date = date.plusDays(1)) {
		        formattedDate = DateTimeFormatter.ofPattern(dateFormat).format(date);
		        datesForQuery.append(", '" + formattedDate + "'");
			}
		}
		datesForQuery.append(")");
		
		//query for most used
		Statement state = con.createStatement();
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
		Statement state = con.createStatement();
		String getRecent = "SELECT * FROM WebsiteUsage ORDER BY elapsedTime DESC LIMIT 5;";
		ResultSet usage = state.executeQuery(getRecent);
		//add to lists to return
		
        //save column values
        while(usage.next()) {
        	int id = usage.getInt("ID");
        	int time = usage.getInt(elapsed);

        	Statement state2 = con.createStatement();
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
		Statement state = con.createStatement();
		LocalDate localDate = LocalDate.now();
        String date = DateTimeFormatter.ofPattern(dateFormat).format(localDate);
                
        String get = "SELECT * FROM WebsiteUsage WHERE Date='" + date + "';";
        ResultSet rs = state.executeQuery(get);
        while(rs.next()) {
        	totalTimeToday += rs.getInt(elapsed);  	
        }
        return totalTimeToday;
	}
	
	//getTotalFocusTimeToday()  :  gets total screen time user has spenr in focus today
	public static int getTotalFocusTimeToday() throws SQLException {
		int totalTimeToday = 0;
		Statement state = con.createStatement();
		LocalDate localDate = LocalDate.now();
        String date = DateTimeFormatter.ofPattern(dateFormat).format(localDate);
        
        String get = "SELECT * FROM WebsiteUsage WHERE Date = '" + date + "'"
        		+ " AND ID IN (SELECT ID FROM ItemSettings WHERE BlockID = 1);";
        ResultSet rs = state.executeQuery(get);
        while(rs.next()) {
        	totalTimeToday += rs.getInt(elapsed);
        }
        return totalTimeToday;
	}	
}
