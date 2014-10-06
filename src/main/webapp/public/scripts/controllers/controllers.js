'use strict';

angular.module('jbehave-admin')
  .controller('MainCtrl', function ($scope, Story, StoryDetail, $location, $resource, HttpPoller) {
	  
	//Gets the story from /api/stories
	$scope.stories = Story.query(); 
	
	$scope.addStory = function () {		
        window.location = "/#add-story";
    };
    
    // callback for ng-click 'runStory':
    $scope.runStory = function (id) {
    	angular.forEach($scope.stories, function(story) {
			if(story.name == id){
				story.status = 'RUNNING';
			}
		});
    	StoryDetail.createExecution({id: id});
    	HttpPoller.startPolling(id, '/api/stories/' + id + '/status', 1000, function (result) {
    		console.log("Result: " + JSON.stringify(result));
			if(result.data != 'RUNNING'){    					
				HttpPoller.stopPolling(id)
				angular.forEach($scope.stories, function(story) {
					if(story.name == id){
						story.status = result.data;
					}
				});
			}
    	})   	
    };
    
    // callback for ng-click 'showStoryResult':
    $scope.showStoryResult = function (id) {
    	console.log(id)
        $location.path('/stories/' + id);
    };
    
    // callback for ng-click 'editStory':
    $scope.editStory = function (id) {
        $location.path('/stories/' + id);
    };

    // callback for ng-click 'deleteStory':
    $scope.deleteStory = function (id) {
    	StoryDetail.delete({ id: id });
        $scope.stories = Story.query();
    };
    
  })
  .controller('StoryCreationCtrl', function ($scope, Story, $location) {
      // callback for ng-click 'saveStory':
      $scope.saveStory = function () {
          Story.create($scope.story);
          $location.path('/stories');
      }
      // callback for ng-click 'cancel':
      $scope.cancel = function () {
          $location.path('/stories');
      };
  })
  .controller('StoryDetailCtrl', function ($scope, $routeParams, Story, StoryDetail, $location) {
      // callback for ng-click 'updateStory':
      $scope.saveStory = function () {
    	  StoryDetail.update($scope.story);
          $location.path('/stories');
      }
      // callback for ng-click 'cancel':
      $scope.cancel = function () {
          $location.path('/stories');
      };
      $scope.story = StoryDetail.show({id: $routeParams.id});
  });