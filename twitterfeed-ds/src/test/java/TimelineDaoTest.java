import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.twitter.feed.dao.TimelineDAO;
import com.twitter.feed.dao.TimelineDAOImpl;

public class TimelineDaoTest  {
	Logger logger = LoggerFactory.getLogger(TimelineDaoTest.class);
	TimelineDAO timelinedao = null;
	
	@Before
	public void initialize() throws Exception{
		timelinedao = new TimelineDAOImpl();
	}

	@Test
	public void testSearchTweets() throws Exception{
		Response resp = null;
		resp = timelinedao.searchTweets("company");
		logger.debug("Get Tweets DAO Response :" + resp.toString());
		assertEquals(200, resp.getStatus());
	}

}