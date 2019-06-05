package com.focuskeeper.test;

import static org.junit.Assert.*;
import java.util.ArrayList;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.focuskeeper.DatabaseController;
import com.focuskeeper.FocusKeeper;

import static com.focuskeeper.DatabaseController.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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






