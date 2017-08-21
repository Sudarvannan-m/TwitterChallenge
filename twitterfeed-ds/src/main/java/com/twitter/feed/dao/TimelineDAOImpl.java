package com.twitter.feed.dao;
/**
 * TimeLine DAO class to select, insert and delete tweets in H2 - in-memory Java SQL Database
 */

import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimelineDAOImpl implements TimelineDAO {

	Logger logger = LoggerFactory.getLogger(TimelineDAOImpl.class);
	static String selectQuery = "SELECT * FROM TWEETS WHERE LOWER(TWEETTEXT) LIKE ?";
	static String deleteQuery = "DELETE FROM TWEETS";
	static String insertQuery = "INSERT INTO TWEETS VALUES (?,?,?)";
	
	public TimelineDAOImpl(){
		try {
			Class.forName("org.h2.Driver").newInstance();
		} catch(IllegalAccessException | ClassNotFoundException | InstantiationException e) {
			logger.error("Failed to locate driver",e);
		}catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Exception in TimelineDAO!!", ex);
		}
	}

	/**
	 * Search tweets for the provided input string from H2 DB 
	 * @param searchStr
	 * @return
	 * @throws Exception
	 */
	public Response searchTweets(String searchStr) throws Exception {
		Connection conn = null;
		JSONArray array = new JSONArray();
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			conn =  DriverManager.getConnection("jdbc:h2:~/test", "sa",  "");
			statement = conn.prepareStatement(selectQuery);
			statement.setString(1, "%" +searchStr.toLowerCase() +"%");
			logger.info("Select Query : " + statement.toString());
			rs = statement.executeQuery();
			while(rs.next()) {
				Clob clob = rs.getClob(3);
				JSONObject obj = new JSONObject(clob.getSubString(1, (int) clob.length()));
				array.put(obj);
			}
		} catch(SQLException e) {
			logger.error("Failed to retrieve tweets",e);
		}  
		finally {
			if (rs != null) rs.close();
			if(statement != null) statement.close();
			if(conn != null) conn.close();
		}
		return Response.ok() //200
				.entity(array.toString())
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
				.allow("OPTIONS").build();
	}

	/**
	 * Save the retrieved tweets in memory database using the tweet Id, text and the tweet.
	 * @param jsonarray
	 * @throws Exception
	 */
	public void save(JSONArray jsonarray) throws Exception {
		Connection conn = null;
		PreparedStatement statement = null;
		try {
			conn=DriverManager.getConnection("jdbc:h2:~/test;INIT=RUNSCRIPT FROM 'classpath:scripts/create.sql'", "sa",  "");
			//Delete existing data before inserting new data
			statement = conn.prepareStatement(deleteQuery);
			statement.execute();
			statement = conn.prepareStatement(insertQuery);
			for (int i = 0 ; i < jsonarray.length(); i++) {
				JSONObject obj = jsonarray.getJSONObject(i);
				String tweetId = obj.getString("id_str");
				String tweetText = obj.getString("text");
				statement.setString(1, tweetId);
				statement.setString(2, tweetText);
				Clob clob = conn.createClob();
				clob.setString(1, obj.toString());
				statement.setClob(3, clob);
				statement.addBatch();
			}
			statement.executeBatch();
			conn.commit();
		}
		catch(SQLException e) {
			logger.error("Error inserting tweet Data",e);
		}
		finally {
			if(statement != null) statement.close();
			if(conn != null) conn.close();
		}
	}
}
