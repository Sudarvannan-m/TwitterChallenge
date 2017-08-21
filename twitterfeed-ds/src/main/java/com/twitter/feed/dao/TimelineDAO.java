package com.twitter.feed.dao;

import javax.ws.rs.core.Response;

import org.json.JSONArray;

/**
 * TimeLine interface to select, insert and delete tweets
 */

public interface TimelineDAO {
	
	/**
	 * Search tweets for the provided input string from H2 DB 
	 * @param searchStr
	 * @return
	 * @throws Exception
	 */
	public Response searchTweets(String searchStr) throws Exception;
	
	/**
	 * Save the retrieved tweets in memory database using the tweet Id, text and the tweet.
	 * @param jsonarray
	 * @throws Exception
	 */
	public void save(JSONArray jsonarray) throws Exception;
}
