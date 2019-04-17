package com.focuskeeper;
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
	private static int numColumnsURLSettings;
	private static ArrayList<String> colNamesURLSettings;
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
		System.out.println("Running.");	
		if(con==null) getConnection();
		
		createTable();
		getDatabaseMetaData();
		
		//testing addList feature
		deleteList("School");
//		String[] URLs = {"www.facebook.com", "www.instagram.com"};
//		addList("School", URLs);
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
	            
	            //print URLSettings 
	            System.out.println(tableNames.get(0));
	            ResultSet res = stmt.executeQuery("SELECT * FROM URLSettings");
	            ResultSetMetaData rsmd = res.getMetaData();
	            int columnCount = rsmd.getColumnCount();
	            numColumnsURLSettings = columnCount;

	            //print column names
	            ArrayList<String> columns = new ArrayList<>();
	            for (int i = 1; i <= columnCount; i++ ) {
	              String name = rsmd.getColumnName(i);
	              System.out.printf(name + "    ");
	              columns.add(name);
	            }
	            System.out.println();
	            colNamesURLSettings = columns;
	            //print column values
	            while (res.next()) {
		               for(String col : columns) {
		            	   String colVal = res.getString(col);
			               System.out.print(colVal + "           ");
		               }
		               System.out.println();
		            }
	            System.out.println("\n");

	            //print URLs Table
	            System.out.println(tableNames.get(1));
	            rs = stmt.executeQuery("SELECT * FROM URLs");
	            
	            //permanent column names - no need to grab from table
	            System.out.println("ID    URL");
	            
	            //print column values
	            while (rs.next()) {
	               int id = rs.getInt("id");
	               String URL = rs.getString("URL");
	               System.out.println(id+"   "+URL);
	            }
	            System.out.println();
	            
	            
	            //print WebsiteUsage Table
	            System.out.println(tableNames.get(2));
	            rs = stmt.executeQuery("SELECT * FROM WebsiteUsage");
	            
	            //permanent column names - no need to grab from table
	            System.out.println("ID    ElapsedTime    Date");
	            
	            //print column values
	            while(rs.next()) {
	            	int id = rs.getInt("id");
	            	int time = rs.getInt("elapsedTime");
	            	int date = rs.getInt("date");
	            	System.out.println(id + "   " + time + "    " + date + "    ");
	            	
	            }
	            System.out.println();
	            
	         } catch(SQLException e) {
	            System.out.println(e);
	         }

	    }
	
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
			
			String createUsage = "CREATE TABLE IF NOT EXISTS WebsiteUsage (\n"
					+ " ID INTEGER NOT NULL, \n"
					+ " elapsedTime int DEFAULT 0, \n"
					+ " Date date NOT NULL, \n"
					+ " CONSTRAINT UQ_WebsiteUsage UNIQUE(ID, DATE));";   
												 //store time as integer of minutes on the site. 
												 //if user goes on site multiple times in one day,
												 //add elapsed time to current stored value
			//when adding data: Insert into Event(time,Date) values(time, GETDATE());
			state.executeUpdate(createUsage);
			
			
			String createSettings = "CREATE TABLE IF NOT EXISTS URLSettings (\n"
					+ " ID INTEGER NOT NULL UNIQUE, \n"
					+ " Distracting boolean DEFAULT FALSE);";
			
			state.executeUpdate(createSettings);
			hasTables = true;
		}
	}
	
	public static void restartDB() throws ClassNotFoundException, SQLException {
		if(con == null) getConnection();
		
		Statement state = con.createStatement();
		String sql = "DROP TABLE URLs;";
		state.executeUpdate(sql);
		
		sql = "DROP TABLE URLSettings;";
		state.executeUpdate(sql);

		sql = "DROP TABLE WebsiteUsage;";
		state.executeUpdate(sql);
	}
	
	//When user adds a new block list with URLS
	//Parameters: Name of new List, and list of URLS in list
	public static void addList(String list, String[] URLS){
		//adds new list as field in URLSettings database
		try {
			Statement state = con.createStatement();
			String addNew = "ALTER TABLE URLSettings ADD " + list + " boolean;";
			state.executeUpdate(addNew);

		} catch (SQLException e) {
			//this exception is caught if the list already exists.
			System.out.println("List " + list + " already exists. Carry on.\n");
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
			String colNames = "id";
			String insertFalse = "false";
            for(int i = 1; i < numColumnsURLSettings; i++) {
            	colNames += ", " + colNamesURLSettings.get(i);
            	if(i+1 != numColumnsURLSettings) {
            		insertFalse += ", false";
            	}
            }
            	
            //inserting the URL ID into URLSettings with all false values
            //ignores this command if URL already exists in database
            for(String URL : URLS) {
            	String insert = "INSERT or IGNORE INTO URLSettings (" + colNames + ")\n"
            			+ " VALUES((SELECT ID from URLs where URL='" + URL + "'), " + insertFalse + ");";
            	state3.executeUpdate(insert);
                //then adding the true booleans 
            	String update = "UPDATE URLSettings SET "
            			+ list + " =true WHERE ID=(SELECT ID from URLs where URL='" + URL + "');";
            	state3.executeUpdate(update);
            }
		} catch (SQLException e){
			System.out.println(e);
		}
		//sets boolean values to true if URL in that block list
	}
	
	
	//When user wants to delete block list
	//Parameters: Name of the list to be deleted
	public static void deleteList(String list) {
		//deletes list as column from URLSettings database
		//SQLite doesn't support DROP COLUMN so we set all URLs to false under that column
		//another option is we could delete all tables and start over by saving list of urls and their lists
		try {
			Statement state = con.createStatement();
			String dropList = "UPDATE URLSettings SET " + list + "=false;";
			state.executeUpdate(dropList);

		} catch (SQLException e) {
			//this exception is caught if the list already exists.
			System.out.println("\nError droping list '" + list + "'\n");
			System.out.println(e);
		}
		
		//deletes URLS in URLs database where they only existed in deleted list
		//deletes Entry in URLsettings where only list belonged to was deleted list
		try {
			Statement state2 = con.createStatement();
			String delete = "DELETE FROM URLSettings WHERE " + colNamesURLSettings.get(1) + "=false";
			for(int i = 2; i < numColumnsURLSettings; i++) {
				
				delete += " AND " + colNamesURLSettings.get(i) + "=false";
			}
			delete+=";";
			state2.executeUpdate(delete);
			
			String delete2 = "DELETE FROM URLs WHERE ID NOT IN (SELECT US.ID FROM URLSettings US);";
			state2.executeUpdate(delete2);
		} catch (SQLException e) {
			System.out.println(e);
		}
		
		
	}
	
	//When user goes on website
	//Parameters: Time spent on website in current period, name of website
	public static void addURLUsage(Integer elapsedTime, String URL) {
		//adds current elapsed time to current value in URLUsage database
		//adds new entry if first visit in day
		
	}
	
	//To Display top visited sites 
	public static void getTopVisited() {
		//queries to get sites with highest elapsedTime in WebsiteUsage
	}
	
}
