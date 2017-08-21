Displays the most recent 10 tweets from Salesforce twitter handle.

Instructions to deploy the project:
1)	Open TwitterFeed in command prompt and use Mvn compile to compile both the projects tf-web and tf-data-service.
2)	Type Mvn package to generate separate war for both folders which are located in their respective folders
3)	Navigate to tf-data-service and run mvn jetty:run to start the rest api server. 
4)	Navigate to tf-web and open index.html to access the application directly.
a.	Alternate way : Deploy the war generated for web project and modify the context path in server.xml to access the folder inside webapps. This will enable accessing the application over a server like http://localhost/TwitterFeed

Instructions for Testing UI:
1)	Run the SpecRunner.html to run the configured test cases for the UI project
Instructions for Testing Service Api:
1)	Mvn Package command on parent project runs the JUnit test cases for the service and dao classes.

Rest API’s

	String getTweets(int count) 
		Returns the last ‘n’ tweets for the twitterHandle in JSON format

	String searchTweets(String searchString)
		Returns the tweets that matches the search string in JSON format.
	
Assumptions:
	Both Tweets and Replies for the timeline are considered for the count
	Video and Gif are not handled as per requirement spec. Image and text for tweet are handled.
	
