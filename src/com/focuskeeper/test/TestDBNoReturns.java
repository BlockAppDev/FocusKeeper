package com.focuskeeper.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
		assertTrue(Boolean.parseBoolean(result));
	}
}
