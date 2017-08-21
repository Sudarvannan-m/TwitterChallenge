app.collections.TweetList = Backbone.Collection.extend({
    url: "http://localhost:8080/timeline/feed/getTweets/salesforce",
    model: app.models.Tweet
})