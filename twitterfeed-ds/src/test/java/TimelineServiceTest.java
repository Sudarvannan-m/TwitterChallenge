import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.twitter.feed.service.TimelineService;
import com.twitter.feed.service.TimelineServiceImpl;

public class TimelineServiceTest  {
	Logger logger = LoggerFactory.getLogger(TimelineServiceTest.class);
	TimelineService timelineservice = null;

	@Before
	public void initialize() throws Exception{
		timelineservice = new TimelineServiceImpl();
	}

	@Test
	public void testGetTweetsService() throws Exception{
		Response resp = null;
		resp = timelineservice.getTweets("salesforce",10);
		logger.debug("Get Tweets Response :" + resp.toString());
		assertEquals(200, resp.getStatus());
	}
	
	@Test(expected=Exception.class)
	public void testGetTweetsWithInvalidCount() throws Exception{
		Response resp = null;
		resp = timelineservice.getTweets("salesforce",-10);
		logger.debug("Get Tweets Response with invalid count :" + resp.toString());
	}

	@Test
	public void testSearchTweetsService() throws Exception{
		Response resp = null;
		resp = timelineservice.searchTweets("");
		logger.debug("Search Tweets Response :" + resp.toString());
		assertEquals(200, resp.getStatus());
	}
	
	

}