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
                    status.success('--- Number of removed discussions: ' +  posts.length + '\n' + JSON.stringify(posts) + '\n\n\n\n');
                },
                error: function(error) {
                    status.error("--- Error deleting discussions - " + error.code + ": " + error.message + '\n\n\n\n');
                }
            });
        }, function (error) {});
});