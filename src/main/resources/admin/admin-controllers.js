angular.module('nmcc.ContentControllers', ['ngRoute', 'nmcc.ContentServices'])
		.config(['$routeProvider', function($routeProvider) {
				$routeProvider
						.when('/', {
							controller: 'SermonSeriesListCtrl',
							templateUrl: '/sermon-series/list.html'
						})
						.when('/sermon-series-add', {
							controller: 'SermonSeriesAddCtrl',
							templateUrl: '/sermon-series/add.html'
						})
					.when('/sermon-series/:sermonSeriesId', {
						controller: 'SermonSeriesEditCtrl',
						templateUrl: '/sermon-series/edit.html'
					})
					.when('/sermon-series/:sermonSeriesId/sermon-add', {
						controller: 'SermonAddCtrl',
						templateUrl: '/sermon/add.html'
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

							$location.path('/sermon-series/' + persistentSermonSeries.id);
						},
						function(reason) {
							$log.error('could not add sermon series: ' + reason);
						});
			};

			$scope.onCancel = function($event) {
				$log.info('discarding sermon series');

				$location.path('/');
			};
		}])
		.controller('SermonSeriesEditCtrl', ['$scope', '$routeParams', '$log', 'contentApi', '$route', '$location', function($scope, $routeParams, $log, contentApi, $route, $location) {
			var sermonSeriesId = $routeParams.sermonSeriesId;

			var sermonSeries = contentApi.one('sermon-series', sermonSeriesId).get().$object;

			$scope.sermonSeries = sermonSeries;

			$scope.onSave = function($event) {
				var editedSermonSeries = { name: sermonSeries.name, description: sermonSeries.description, imageUrl: sermonSeries.imageUrl };

				$log.info('saving sermon series changes ' + JSON.stringify(editedSermonSeries));

				contentApi.all('sermon-series').customPUT(editedSermonSeries, sermonSeries.id, { v: sermonSeries.v }).then(
					function(persistentSermonSeries) {
						$log.info('saved sermon series ' + sermonSeriesId);

						$route.reload();
					},
					function(reason) {
						$log.error('could not add sermon series: ' + reason);
					});
			};

			$scope.onCancel = function($event) {
				$log.info('discarding sermon series changes');

				$location.path('/');
			};

			$scope.sermonsView = 'list';
			$scope.sermons = [];

			$scope.onAddSermon = function($event) {
				$log.info('showing add sermon dialog');

				$scope.sermonsView = 'add';
			}

			$scope.onAddSermonSave = function($event) {
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

			$scope.onCancelSermon = function($event) {
				$log.info('returning to sermon list');

				$scope.sermonsView = 'list';
			}
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
