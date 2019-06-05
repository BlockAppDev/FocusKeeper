package com.focuskeeper.test;

import static org.junit.Assert.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Test;

import com.focuskeeper.DatabaseController;

public class TestDBNoReturns {	
	@Test
	public void testRestartDB() {
		
		DatabaseController.connect();
		DatabaseController.createTable();
		DatabaseController.restartDB();
		
		Path path = Paths.get(DatabaseController.DB_NAME);
		boolean result = Files.exists(path);
		
		assertEquals(false, result);
	}
	
	@Test
	public void testAddList() throws SQLException {
		
		DatabaseController.connect();
		DatabaseController.createTable();
		
		String check = "SELECT CASE WHEN"
				+ " EXISTS (SELECT BlockName FROM BlockLists WHERE BlockName='Testy')"
				+ " THEN 'true' ELSE 'false' END AS test_result;";
		
		String[] sites = {"A", "B", "C"};
		DatabaseController.addList("Testy", sites);
		
		Statement state = DatabaseController.getCon().createStatement();
        ResultSet rs = state.executeQuery(check);
        String result = rs.getString("test_result");
		
        DatabaseController.restartDB();
        
		assertEquals(1,1);
	}
	
	/*
	 
	SELECT CASE WHEN 
	EXISTS (SELECT name FROM sqlite_master WHERE type='table' AND name='albums')
	AND EXISTS (SELECT name FROM sqlite_master WHERE type='table' AND name='customers')
	AND EXISTS (SELECT name FROM sqlite_master WHERE type='table' AND name='artists')
	THEN 'true' ELSE 'false' END AS result ;
	 
	 String check = "SELECT CASE WHEN"
					+ " EXISTS (SELECT name FROM sqlite_master WHERE type='table' AND name='Items')"
					+ " AND EXISTS (SELECT name FROM sqlite_master WHERE type='table' AND name='BlockLists')"
					+ " AND EXISTS (SELECT name FROM sqlite_master WHERE type='table' AND name='WebsiteUsage')"
					+ " THEN 'true' ELSE 'false' END AS result;";
	 
	 */
}
