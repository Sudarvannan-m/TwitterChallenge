package com.twitter.feed.service;
/**
 * TimelineService class to call the twitter API to get the tweets realted information.
 * The service calls the DAO layer to save the tweets in H2 DB, for future processing
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Properties;
import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.internal.util.Base64;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.twitter.feed.dao.TimelineDAO;
import com.twitter.feed.dao.TimelineDAOImpl;

public class TimelineServiceImpl implements TimelineService{
	Logger logger = LoggerFactory.getLogger(TimelineService.class);
	Properties prop = null;
	TimelineDAO timelinedao = new TimelineDAOImpl();

	public TimelineServiceImpl() {

		try {
			ClassLoader classLoader = getClass().getClassLoader();
			prop = new Properties();
			InputStream resourceStream = classLoader.getResourceAsStream("app.properties");
			prop.load(resourceStream);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Exception when getting connection!", ex);
		}
	
	}
	
	/**
	 * @param searchStr
	 * @return - Search Tweets with the input string
	 * @throws Exception
	 */
	public Response searchTweets(String searchStr) throws Exception {
			return timelinedao.searchTweets(searchStr);
	}
	
	/**
	 * @param twitterHandle
	 * @param count
	 * @return - Gets the initial 'n' number of tweets to display in the home page 
	 * @throws Exception
	 */
	public Response getTweets(String twitterHandle, int count) throws Exception {
		logger.debug("TimelineService getTweets called..");
		if(count <= 0) {
			logger.error("Count is less than 0");
			throw new Exception("Count cannot be less than 1");
		}
		HttpsURLConnection connection = null;
		try {

			String endPointUrl = prop.getProperty("twitterapiURL") + "?screen_name="+twitterHandle+"&count="+count;
			String bearerToken = requestBearerToken(prop.getProperty("twitterAuthURL"));
			if(bearerToken.isEmpty()) {
				logger.error("Failed to Authenticate");
				return Response.serverError().build();
			}
			URL url = new URL(endPointUrl); 
			connection = (HttpsURLConnection) url.openConnection();           
			connection.setDoOutput(true);
			connection.setDoInput(true); 
			connection.setRequestMethod("GET"); 
			connection.setRequestProperty("Host", prop.getProperty("twitterHost"));
			connection.setRequestProperty("User-Agent", "Feed Grabber");
			connection.setRequestProperty("Authorization", "Bearer " + bearerToken);
			connection.setUseCaches(false);

			// Parse the JSON response into a JSON mapped object.
			JSONArray obj = new JSONArray(readResponse(connection));
			String response = "";
			if (obj != null) {
				timelinedao.save(obj);
				response = obj.toString();
			}
			return Response.ok() //200
					.entity(response)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.allow("OPTIONS").build();
		}
		catch (MalformedURLException e) {
			logger.error("Invalid endpoint URL specified.", e);
			throw new IOException("Invalid endpoint URL specified.", e);
		} 
		catch (ConnectException e) {
			logger.error("Connection Failure.", e);
			throw new ConnectException("Connection Failure");
		}
		finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	/**
	 * Constructs the request for requesting a bearer token and returns that token as a string
	 * @param endPointUrl
	 * @return
	 * @throws Exception
	 */
	private String requestBearerToken(String endPointUrl) throws Exception {
		HttpsURLConnection connection = null;
		String consumerKey = prop.getProperty("consumerKey") ;
		String consumerSecret = prop.getProperty("consumerSecret") ;
		String encodedCredentials = encodeKeys(consumerKey,consumerSecret);

		try {

			URL url = new URL(endPointUrl); 
			connection = (HttpsURLConnection) url.openConnection();           
			connection.setDoOutput(true);
			connection.setDoInput(true); 
			connection.setRequestMethod("POST"); 
			connection.setRequestProperty("Host",prop.getProperty("twitterHost"));
			connection.setRequestProperty("User-Agent", "Feed Grabber");
			connection.setRequestProperty("Authorization", "Basic " + encodedCredentials);
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8"); 
			connection.setRequestProperty("Content-Length", "29");
			connection.setUseCaches(false);

			writeRequest(connection, "grant_type=client_credentials");

			// Parse the JSON response into a JSON mapped object.
			String response = readResponse(connection);
			if(!response.isEmpty()) {
				JSONObject obj = new JSONObject(response);
				if (obj != null) {
					String tokenType = (String)obj.get("token_type");
					String token = (String)obj.get("access_token");
					return ((tokenType.equals("bearer")) && (token != null)) ? token : "";
				}
			}
			return new String();
		}
		catch (MalformedURLException e) {
			logger.error("Invalid endpoint URL specified.", e);
			throw new IOException("Invalid endpoint URL specified.", e);
		}
		finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	/**
	 * Reads the response for a given connection and returns it as a string.
	 * @param connection
	 * @return
	 */
	private String readResponse(HttpsURLConnection connection) throws Exception{
		StringBuilder str = new StringBuilder();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line = "";
			while((line = br.readLine()) != null) {
				str.append(line + System.getProperty("line.separator"));
			}
		}
		catch (IOException e) { 
			logger.error("Failed to connect to Rest API",e);
		}
		return str.toString();
	}

	/**
	 * Encodes the consumer key and secret to create the basic authorization key
	 * @param consumerKey
	 * @param consumerSecret
	 * @return
	 */
	private String encodeKeys(String consumerKey, String consumerSecret) {
		try {
			String encodedConsumerKey = URLEncoder.encode(consumerKey, "UTF-8");
			String encodedConsumerSecret = URLEncoder.encode(consumerSecret, "UTF-8");

			String fullKey = encodedConsumerKey + ":" + encodedConsumerSecret;
			byte[] encodedBytes = Base64.encode(fullKey.getBytes());
			return new String(encodedBytes);  
		}
		catch (UnsupportedEncodingException e) {
			return new String();
		}
	}

	/**
	 *  Write a request to a connection
	 * @param connection
	 * @param textBody
	 * @return
	 */
	private static boolean writeRequest(HttpsURLConnection connection, String textBody) {
		try {
			BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
			wr.write(textBody);
			wr.flush();
			wr.close();

			return true;
		}
		catch (IOException e) { 
			return false; 
		}
	}
	
}
