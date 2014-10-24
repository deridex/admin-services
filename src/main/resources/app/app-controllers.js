angular.module('contentControllers', ['ngRoute', 'contentServices'])
		.config(['$routeProvider', function($routeProvider) {
				$routeProvider
						.when('/', {
							controller: 'SermonSeriesListCtrl',
							templateUrl: '/app/sermon-series/list.html'
						})
						.when('/sermon-series/:id', {
							controller: 'SermonSeriesEditCtrl',
							templateUrl: '/app/sermon-series/edit.html'
						})
						.otherwise({ redirectTo: '/' })
		}])
		.controller('SermonSeriesListCtrl', ['$scope', 'contentApi', '$log', function($scope, contentApi, $log) {
			$scope.sermonSeriesList = contentApi.all('sermon-series').getList().$object;
		}])
		.controller('SermonSeriesAddCtrl', ['$scope', 'contentApi', '$log', function($scope, contentApi, $log) {
			$scope.save = function() {
				var transientSermonSeries = { name: $scope.name, description: $scope.description };
				
				$log.info("saving " + transientSermonSeries);
				
				contentApi.post('sermon-series', transientSermonSeries);
			};
		}])
		.controller('SermonSeriesEditCtrl', ['$scope', '$routeParams', '$log', 'contentApi', function($scope, $routeParams, $log, contentApi) {
			$scope.sermonSeries = contentApi.one('sermon-series', $routeParams.id).get().$object;
		}]);
