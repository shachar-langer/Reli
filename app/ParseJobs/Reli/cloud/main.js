Parse.Cloud.job('deleteExpiredPosts', function(request, status) {

    // All access
    Parse.Cloud.useMasterKey();

    var today = new Date();

    var query = new Parse.Query('Discussions');
        // All posts which are expired
        query.lessThan('discussionexpirationDate', today);

        query.find().then(function (posts) {
            Parse.Object.destroyAll(posts, {
                success: function() {
                    var b = "***";
                    for (var post in posts) {
                        for (var bla in posts[post]) {
                            if (JSON.stringify(bla) === "objectId") {
                                b += JSON.stringify(posts[post][objectId]);
                            }
                        }

    //                    if (post.hasOwnProperty("objectId")) {
      //                      b += JSON.stringify(post.objectId);
                        //} else {
//                            b += "no "
  //                      }
                    }
                    status.success('Number of removed discussions: ' +  posts.length + '\n' + JSON.stringify(posts) + '\n' + b);
                },
                error: function(error) {
                    status.error("Error deleting discussions - " + error.code + ": " + error.message);
                }
            });
        }, function (error) {});
});