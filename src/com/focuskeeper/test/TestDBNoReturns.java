package com.focuskeeper.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
		boolean result = path.toFile().exists();
		assertEquals(false, result);
	}
	
	@Test
	public void testAddList() {
		DatabaseController.connect();
		DatabaseController.createTable();
		String check = "SELECT CASE WHEN"
				+ " EXISTS (SELECT BlockName FROM BlockLists WHERE BlockName='Testy')"
				+ " THEN 'true' ELSE 'false' END AS test_result;";
		String[] sites = {"A", "B", "C"};
		DatabaseController.addList("Testy", sites);
		
		String result = new String();
		try {
		Statement state = DatabaseController.getCon().createStatement();
        ResultSet rs = state.executeQuery(check);
        result = rs.getString("test_result");
		} catch (Exception e) {
			System.out.println("the sql messed up, yikes");
		}
        DatabaseController.restartDB();
		assertTrue(Boolean.parseBoolean(result));
	}
}
