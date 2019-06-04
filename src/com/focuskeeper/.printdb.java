	//getDatabaseMetaData()  :  prints all database columns and values
		 public static void getDatabaseMetaData() throws SQLException {
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
				int blockid = res.getInt("BlockID");
				String blockname = res.getString("BlockName");
				System.out.println(blockid + "          " + blockname);
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
			   int blockid = rs.getInt("BlockID");
			   System.out.println(id+"        "+ blockid);
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
				int time = rs.getInt(elapsed);
				String date = rs.getString("Date");
				System.out.println(id + "      " + time + "        " + date + "      ");
	
			}
			System.out.println();	
		}
