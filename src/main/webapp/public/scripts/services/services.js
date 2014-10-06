'use strict';

angular.module('jbehave-admin.services', ['ngResource'])
.factory('Story', ['$resource',
  function($resource){
    return $resource(
    		'/api/stories', {}, {
    			query: {method:'GET', isArray:true},
    			create: { method: 'POST' }
    		});
  }])
  .factory('StoryDetail', ['$resource',
  function($resource){
    return $resource(
    		'/api/stories/:id', 
    		{id:'@id'}, {
    			show: { method: 'GET' },
    			createExecution: { method: 'POST' },
    			update: {method: 'PUT', params: {id: '@id'} },
    			delete: {method:'DELETE', params: {id: '@id'} }
    		});
  }])
.factory('HttpPoller', ['$http', function($http){
        var defaultPollingTime = 10000;
        var polls = {};
        return {
            startPolling: function(name, url, pollingTime, callback) {
                // Check to make sure poller doesn't already exist
                if (!polls[name]) {
                    var poller = function() {
                        $http.get(url).then(callback);
                    }
                    poller();
                    polls[name] = setInterval(poller, pollingTime || defaultPollingTime);
                }
            },
            stopPolling: function(name) {
                clearInterval(polls[name]);
                delete polls[name];
            }
        }
    }]);;