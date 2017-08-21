var app = (function () {
    var api = {
		views: {},
		models: {},
		collections: {},
		content: null,
		router: null,
		todos: null,
		init: function() {
            return this;
		},
		changeContent: function(el) {
		},
		title: function(str) {
		}
	};

    return api;
})();

var initialize = function() {
    this.content = $("#content");
    var tweetList = new app.collections.TweetList();
    var homeView = new app.views.Home({
        el: this.content,
        collection: tweetList
    });
    tweetList.fetch({
        error : function(){
            clearInterval(timer);
            homeView.errorHandler();
            }
    });
    this.timer = setInterval(function(){
            loadHome();
        }, 60000);
    var searchText = function(){
        var textToSearch = $("#global-search-01").val()
        tweetList.fetch({
            url: "http://localhost:8080/timeline/feed/searchTweets?searchstring="+textToSearch,
            error : function(){
                clearInterval(timer);
                homeView.errorHandler();
                }
        });
    };
    var loadHome = function(){
       $("#global-search-01").val('');
        tweetList.fetch({
            error : function(){
                clearInterval(timer);
                homeView.errorHandler();
                }
        });
    };
    $("#global-search-01").on('change',searchText);
    $('#global-search-01').keypress(function (e) {
        var key = e.which;
        if(key == 13)  // the enter key code
        {
            searchText(); 
        }
    });
    $("#homeImg").on('click',loadHome);
}








