package com.twitter.feed.service;

import javax.ws.rs.core.Response;

/**
 * TimelineService to list timeline service interfaces.
 * 
 */

public interface TimelineService {
	/**
	 * @param searchStr
	 * @return - Search Tweets with the input string
	 * @throws Exception
	 */
	public Response searchTweets(String searchStr) throws Exception;
	
	/**
	 * @param twitterHandle
	 * @param count
	 * @return - Gets the initial 'n' number of tweets to display in the home page 
	 * @throws Exception
	 */
	public Response getTweets(String twitterHandle, int count) throws Exception;
}
