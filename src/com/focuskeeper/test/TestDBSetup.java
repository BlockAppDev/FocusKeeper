package com.focuskeeper.test;

import static org.junit.Assert.*;
import org.junit.Test;
import com.focuskeeper.DatabaseController;
import static com.focuskeeper.DatabaseController.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TestDBSetup {
	
	@Test
	public void testGetConnection() {
		
		DatabaseController.connect();
		boolean result = getCon() != null;
		DatabaseController.restartDB();
		assertEquals(true, result);			
	}
	
	@Test
	public void testCreateTable() throws SQLException {
		
		String check = "SELECT CASE WHEN"
				+ " EXISTS (SELECT name FROM sqlite_master WHERE type='table' "
				+ " AND (name='Items' OR name='BlockLists' OR name='WebsiteUsage' OR name='ItemSettings'))"
				+ " THEN 'true' ELSE 'false' END AS test_result;";
		
		DatabaseController.connect();
		DatabaseController.createTable();
		
		Statement state = DatabaseController.getCon().createStatement();
        ResultSet rs = state.executeQuery(check);
        String result = rs.getString("test_result");
        
        DatabaseController.restartDB();
        
		assertEquals("true", result);
	}
	
}






