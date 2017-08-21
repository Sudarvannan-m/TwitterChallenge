package com.twitter.feed;
/**
 * FeedGrabber class is the root class for Jersey API.
 * Two APIs are hosted by this root class.
 * 1. Get the initial tweets from twitter API, store in H2 DB and display in Home page
 * 2. Search the tweets in H2 DB with the provided input string from front end and display the filtered 
 *    results in the front end 
 */
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.twitter.feed.service.TimelineService;
import com.twitter.feed.service.TimelineServiceImpl;

@Path("/feed")
public class FeedGrabber { 

	Logger logger = LoggerFactory.getLogger(FeedGrabber.class);
	TimelineService tlservice = new TimelineServiceImpl();

	/**
	 * @param twitterHandle
	 * @param count
	 * @return - Retrieves the top 'n' tweets & replies from timeline for the given twitter handle.
	 * @throws Exception
	 */
	@GET
	@Path("/getTweets/{handle}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response fetchTimeline(@PathParam("handle") String twitterHandle,@DefaultValue("10") @QueryParam("count") int count) throws Exception {
		return tlservice.getTweets(twitterHandle,count);
	}

	/**
	 * @param searchStr
	 * @return - Returns the tweets that match the searchstring from the available list of tweets in memory database.
	 * @throws Exception
	 */
	@GET
	@Path("/searchTweets")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTwitterFeed(@DefaultValue("") @QueryParam("searchstring") String searchStr) throws Exception{
			return tlservice.searchTweets(searchStr);
	}
}
