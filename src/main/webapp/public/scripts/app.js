'use strict';

angular.module('jbehave-admin', [
  'ngCookies',
  'ngResource',
  'ngSanitize',
  'ngRoute',
  'ngResource',
  'jbehave-admin.services'
])
  .config(function ($routeProvider) {
    $routeProvider
      .when('/', {
        templateUrl: 'views/main.html',
        controller: 'MainCtrl'
      })
      .when('/add-story', {
        templateUrl: 'views/detail.html',
        controller: 'StoryCreationCtrl'
      })
      .when('/stories/:id', {
        templateUrl: 'views/detail.html',
        controller: 'StoryDetailCtrl'
      })
      .otherwise({
        redirectTo: '/'
      });
  });