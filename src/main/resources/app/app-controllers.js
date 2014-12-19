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
					.when('/sermon-series/:sermonSeriesId', {
						controller: 'SermonSeriesEditCtrl',
						templateUrl: '/app/sermon-series/edit.html'
					})
					.when('/sermon-series/:sermonSeriesId/sermon-add', {
						controller: 'SermonAddCtrl',
						templateUrl: '/app/sermon/add.html'
					})
					.otherwise({ redirectTo: '/' })
		}])
		.controller('SermonSeriesListCtrl', ['$scope', 'adminApi', '$log', function($scope, adminApi, $log) {
			$scope.sermonSeriesList = adminApi.all('sermon-series').getList().$object;
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
			$scope.sermonSeries = contentApi.one('sermon-series', $routeParams.sermonSeriesId).get().$object;
		}])
		.controller('SermonAddCtrl', ['$scope', '$routeParams', 'contentApi', '$location', '$log', function($scope, $routeParams, contentApi, $location, $log) {
			$scope.onSave = function($event) {
				var transientSermon = { name: $scope.name, description: $scope.description, passages: $scope.passages };

				$log.info('adding sermon ' + JSON.stringify(transientSermon));

				contentApi.one('sermon-series', $routeParams.sermonSeriesId).all('sermons').customPOST(transientSermon).then(
					function(persistentSermon) {
						$log.info('added sermon series ' + persistentSermon.id);

						$location.path('/sermon-series/' + $routeParams.sermonSeriesId);
					},
					function(reason) {
						$log.error('could not add sermon: ' + reason);
					});
			};

			$scope.onCancel = function($event) {
				$log.info("canceling add sermon");

				$location.path('/sermon-series/' + $routeParams.sermonSeriesId);
			}
		}]);
