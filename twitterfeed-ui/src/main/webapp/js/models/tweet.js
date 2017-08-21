app.models.Tweet = Backbone.Model.extend({
    defaults : {
        
        user:{
            profile_image_url: 'xhttp://pbs.twimg.com/profile_images/2284174872/7df3h38zabcvjylnyfe3_normal.png',
            name: 'Twitter API',
            screen_name: 'twitterapi'
        },
        entities: {
            media: [{
                media_url:'http://pbs.twimg.com/media/DHmrAUGXkAI3X2h.png'
            }],
            urls: [{
                display_url: 'sforce.co/2fWhwqx',
                indices: [72, 95]
            }]
        },
        created_at: 'Wed May 23 06:01:13 +0000 2007',
        created_time: '20-Aug-2017',
        tweetText: '@Jmsdcb @slpng_giants Salesforce does not support Breitbart. We immediately pulled the ad in question, which appeared in error ',
        text: '@Jmsdcb @slpng_giants Salesforce does not support Breitbart. We immediately pulled the ad in question, which appeared in error ',
        retweet_count: '1'
			
    }
})