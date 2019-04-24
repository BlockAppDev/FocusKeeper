package com.focuskeeper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DatabaseController {
	private static Connection con;
//	private static int numColumnsURLSettings;
//	private static ArrayList<String> colNamesURLSettings;
	private static boolean hasTables;
	
	//Functions:
		//getConnection()  		 :  connects to database*
		//getDatabaseMetaData()  :  prints all database columns and values
		//createTable()			 :  creates all database tables with correct columns (only needs to be called if tables don't exist)
		//restartDB()			 :  wipe and delete all database tables (cannot be undone)
		//addList()				 :  adds new URLS and list when new blocklist is created
		//deleteList()			 :	deletes blocklist and it's URLs (if not used by another list)
		//addURLUsage()			 :	updates URL Usage time each time user goes on new website
		//getTopVisited()		 :  gets top 5 most visited sites that day (to display on hope page of application)
	
	
	
	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		System.out.println("Running...\n");	
		if(con==null) getConnection();
		
		createTable();
		//always need to call getDatabaseMetaData(); it saves data to our global variables
		getDatabaseMetaData();
		//testing addList feature
//		restartDB();
		
		String[] URLS = {"www.facebook.com", "www.myportal.com"};
		addList("Work", URLS);
		
		addURLUsage(12, "www.facebook.com");
		getDatabaseMetaData();

		
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
	            	String URL = rs.getString("URL");
	            	System.out.println(id + "    " + URL);
	            }
	            System.out.println("\n");
	            

	            System.out.println(tableNames.get(3));
	            rs = stmt.executeQuery("SELECT * FROM WebsiteUsage");
	            
	            //permanenet column names
	            System.out.println("ID     Date     Time");
	            //print column values
	            while(rs.next()) {
	            	int id = rs.getInt("id");
	            	int time = rs.getInt("elapsedTime");
	            	String date = rs.getString("Date");
	            	System.out.println(id + "      " + time + "        " + date + "      ");
	            	
	            }
	            System.out.println();
	            
	         } catch(SQLException e) {
	            System.out.println(e);
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
	public static void addList(String list, String[] URLS){
		//adds new list as field in URLSettings database
		try {
			Statement state = con.createStatement();
			String addNew = "INSERT INTO BlockLists (BlockID, BlockName)\n"
					+ " VALUES(null,'" + list + "');";
			state.executeUpdate(addNew);

		} catch (SQLException e) {
			//this exception is caught if the list already exists.
			System.out.println(list + " already exists in BlockLists. Carry on.\n");
		}

		//adds new URLS to URLs database
		try {
			Statement state2 = con.createStatement();
			for(String URL : URLS) {
				String addNew = "INSERT INTO URLs (id, URL)\n" + 
						"SELECT * FROM (SELECT null, '" + URL + "') AS tmp\n" + 
						"WHERE NOT EXISTS (\n" + 
						"    SELECT URL FROM URLs WHERE URL = '" + URL + "'\n" + 
						") LIMIT 1;";
				state2.executeUpdate(addNew);
			}
		} catch (SQLException e) {
			System.out.println("Error: cannot add new URLs to URLs Database.");
			e.printStackTrace();
		}
		
		//insert into URLSettings id of URLS where URL is in block list passed to function
		try {
			Statement state3 = con.createStatement();
			//inserting all default values as false
            	
            //inserting the URL ID and BlockListName ID into URLSettings
            //ignores this command if URL already exists in database with blockListID
            for(String URL : URLS) {
            	String insert = "INSERT or IGNORE INTO URLSettings (ID, BlockID)\n"
            			+ " VALUES((SELECT ID from URLs where URL='" + URL + "'), (SELECT BlockID from BlockLists"
            					+ " where BlockName='" + list + "'));";
            	state3.executeUpdate(insert);
            }
		} catch (SQLException e){
			System.out.println(e);
		}
	}
	
	
	//When user wants to delete block list
	//Parameters: Name of the list to be deleted
	public static void deleteList(String list) {
		//deletes entry in URLSettings where URL is attached to list being dropped
		//deletes URLS in URLs database where they only existed in deleted list
		try {
			Statement state2 = con.createStatement();
			String delete = "DELETE FROM URLSettings WHERE BlockID= (SELECT BlockID FROM BlockLists WHERE BlockName='"
					+ list + "');";
			state2.executeUpdate(delete);
			
			String delete2 = "DELETE FROM URLs WHERE ID NOT IN (SELECT US.ID FROM URLSettings US);";
			state2.executeUpdate(delete2);
			
			String delete3 = "DELETE FROM BlockLists WHERE BlockName='" + list + "';";
			state2.executeUpdate(delete3);
			
		} catch (SQLException e) {
			System.out.println(e);
		}
		
		
	}
	
	//When user goes on website
	//Parameters: Time spent on website in current period, name of website
	public static void addURLUsage(Integer elapsedTime, String URL) {
		//adds current elapsed time to current value in URLUsage database
		//adds new entry if first visit in day
		int currentTime;
		int ID;
		boolean New = false;
		LocalDate localDate = LocalDate.now();
        String date = DateTimeFormatter.ofPattern("yyy/MM/dd").format(localDate);
        
		try {
			Statement state = con.createStatement();
			String getUsage = "SELECT * FROM WebsiteUsage WHERE"
				+ " EXISTS (SELECT 1 FROM WebsiteUsage WHERE ID = (SELECT ID FROM URLs WHERE URL = '" + URL + "')) AND"
						+ " Date = '" + date + "';";
			ResultSet usage = state.executeQuery(getUsage);
			try {
				ID = usage.getInt("ID");
				currentTime = usage.getInt("elapsedTime");
			} catch (SQLException e) {   //means the entry is not there. Add it.
				String getID = "SELECT * FROM URLs WHERE URL = '" + URL + "';";
				usage = state.executeQuery(getID);
				ID = usage.getInt("ID");
				currentTime = 0;
				New = true;
			}
			currentTime += elapsedTime;
			if(New) {  //to add new entry
				String add = "INSERT INTO WebsiteUsage (ID, elapsedTime, Date)\n"
						+ " VALUES(" + ID + ", " + currentTime + ", '" + date + "');";
				state.executeUpdate(add);
			} else {
				String insert = "UPDATE WebsiteUsage SET ID= " + ID + ", elapsedTime = "
						 + currentTime + ", Date = '" + date + "' WHERE"
						 		+ " ID = " + ID + " AND Date = '" + date + "';";
				state.executeUpdate(insert);
			}
						
		} catch (SQLException e) {
			System.out.println(e);
		}	
	}
	
	//To Display top visited sites in week
	public static void getMostUsed() {
		//queries to get sites with highest elapsedTime in WebsiteUsage
		try {
			Statement state = con.createStatement();

			
		} catch (SQLException e) {
			
		}

		//same as print but add to a dictionary-type thing
		
	}
	

	//To Display recently used sites in day
	public static void getRecentlyUsed() {
		//queries to get sites recently used in WebsiteUsage
		//same as print but add to a dictionary-type thing
		try {
			Statement state = con.createStatement();
			String getRecent = "SELECT * FROM WebsiteUsage ORDER BY elapsedTime DESC LIMIT 5;";
			ResultSet usage = state.executeQuery(getRecent);
			//add to lists to return
            System.out.println("ID     Date     Time");
            //print column values
            while(usage.next()) {
            	int id = usage.getInt("id");
            	int time = usage.getInt("elapsedTime");
            	String date = usage.getString("Date");
            	System.out.println(id + "      " + time + "        " + date + "      ");
            }
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
}
