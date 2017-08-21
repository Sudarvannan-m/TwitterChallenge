app.views.Home = Backbone.View.extend({
	template: _.template('$("#feed-area").html()'),
    initialize: function() {
        var self = this;
       _.bindAll(this, 'render');
       this.listenTo(this.collection, "add", this.render, this);
       this.listenTo(this.collection, "remove", this.render, this);
       this.listenTo(this.collection, "reset", this.render, this);
       this.on("save:view:",this.search);
    },
    errorHandler : function() {
        var template = _.template($("#error-area").html());
        var html = template();
        this.$el.hide();
        this.$el.html(html);
        this.$el.slideDown("fast");
    },
    render: function(){
        var html = '';
       
        this.collection.each(function(tweet) {
            
            var feedtemplate = _.template($("#feed-area").html());
            var m_names = new Array("Jan", "Feb", "Mar", 
                                    "Apr", "May", "Jun", "Jul", "Aug", "Sep", 
                                    "Oct", "Nov", "Dec"); 
            var created_at_formatt = new Date(Date.parse(tweet.attributes.created_at.replace(/( \+)/, ' UTC$1')));
            tweet.attributes.created_time = created_at_formatt.getDate() + " " + m_names[created_at_formatt.getMonth()] + " " + created_at_formatt.getFullYear();
            var mediaUrl = tweet.attributes.entities.media;
            if(mediaUrl != undefined) {
                tweet.attributes.url = mediaUrl[0].media_url;
                tweet.attributes.hiddenImg = 'block';
            } else {
                tweet.attributes.url = '';
                tweet.attributes.hiddenImg = 'none';
            }
           var firstLink = tweet.attributes.text.indexOf("http");
           if(firstLink != -1) {
               var secondLink = tweet.attributes.text.indexOf("http",firstLink+1);
               if(secondLink != -1) {
                   tweet.attributes.text = tweet.attributes.text.substr(0,secondLink);
               }
           }
            if(tweet.attributes.entities.urls != undefined && tweet.attributes.entities.urls[0] != null) {
                var startInd = tweet.attributes.entities.urls[0].indices[0];
                var endInd = tweet.attributes.entities.urls[0].indices[1];
                tweet.attributes.tweetText = tweet.attributes.text.replace(tweet.attributes.text.substr(startInd,endInd-startInd),"http://"+tweet.attributes.entities.urls[0].display_url);
            } else {
                tweet.attributes.tweetText = tweet.attributes.text;
            }
            var pattern = /((?:http|ftp|https):\/\/[\w\-_]+(?:\.[\w\-_]+)+(?:[\w\-\.,@?^=%&:/~\+#]*[\w\-\@?^=%&/~\+#])?)/gi;
            tweet.attributes.tweetText =  tweet.attributes.tweetText.replace(pattern, ' <a href="$1" target="_blank">$1</a>');
            var linkText = tweet.attributes.tweetText.substr(firstLink+15,tweet.attributes.tweetText.length);
            linkText = linkText.replace("http://", "");
            tweet.attributes.tweetText =  tweet.attributes.tweetText.substr(0,firstLink+15) +linkText;
            tweet.attributes.tweetText =  tweet.attributes.tweetText.replace(/#(\S*)/g, '<a href="http://twitter.com/#!/search/$1" target="_blank">#$1</a>')
            tweet.attributes.tweetText =  tweet.attributes.tweetText.replace(/@(\S*)/g, '<a href="http://twitter.com/#!/search/$1" target="_blank">@$1</a>')            
            html+= feedtemplate(tweet.attributes);

        });
    
        this.$el.hide();
        this.$el.html(html);
        this.$el.slideDown("fast");
    },

    
});

