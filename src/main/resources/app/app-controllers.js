angular.module('contentControllers', ['ngRoute', 'contentServices'])
		.config(['$routeProvider', function($routeProvider) {
				$routeProvider
						.when('/', {
							controller: 'SermonSeriesListCtrl',
							templateUrl: '/app/sermon-series/list.html'
						})
						.when('/sermon-series-add', {
							controller: 'SermonSeriesAddCtrl',
							templateUrl: '/app/sermon-series/add.html'
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
		.controller('SermonSeriesAddCtrl', ['$scope', 'contentApi', '$location', '$log', function($scope, contentApi, $location, $log) {
			$scope.onSave = function() {
				var transientSermonSeries = { name: $scope.name, description: $scope.description, imageUrl: $scope.imageUrl };

				$log.info('adding sermon series ' + JSON.stringify(transientSermonSeries));

				contentApi.all('sermon-series').customPOST(transientSermonSeries).then(
						function(persistentSermonSeries) {
							$log.info('added sermon series ' + persistentSermonSeries.id);

							$location.path('/');
						},
						function(reason) {
							$log.error('could not add sermon series: ' + reason);
						});
			};
		}])
		.controller('SermonSeriesEditCtrl', ['$scope', '$routeParams', '$log', 'contentApi', function($scope, $routeParams, $log, contentApi) {
			$scope.sermonSeries = contentApi.one('sermon-series', $routeParams.id).get().$object;
		}]);
