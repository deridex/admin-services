angular.module('nmcc.SermonSeriesControllers', ['ngRoute', 'nmcc.ContentServices'])
	.controller('SermonSeriesCtrl', ['$scope', '$log', '$location', function($scope, $log, $location) {
		$log.info('building sermon series control');

		$scope.reload = function($event) {
			$log.info('abandoning data and reloading');

			$location.path('/');
		};
	}])
	.controller('SermonSeriesAddCtrl', ['$scope', '$log', '$location', 'contentApi', function($scope, $log, $location, contentApi) {
		$log.info('building sermon series add control');

		var sermonSeries = {};

		$scope.sermonSeries = sermonSeries;

		$scope.saveNew = function($event) {
			var transientSermonSeries = { name: sermonSeries.name, description: sermonSeries.description, imageUrl: sermonSeries.imageUrl };

			$log.info('adding sermon series ' + JSON.stringify(transientSermonSeries));

			contentApi.all('sermon-series').customPOST(transientSermonSeries).then(
				function(persistentSermonSeries) {
					$log.info('added sermon series ' + persistentSermonSeries.id);

					$location.path('/' + persistentSermonSeries.id);
				},
				function(reason) {
					$log.error('could not add sermon series: ' + reason);
				});
		};
	}])
	.controller('SermonSeriesEditCtrl', ['$scope', '$log', '$routeParams', 'contentApi', '$route', function($scope, $log, $routeParams, contentApi, $route) {
		var sermonSeriesId = $routeParams.sermonSeriesId;

		$log.info('fetching sermon series ' + sermonSeriesId);

		var sermonSeries = contentApi.one('sermon-series', sermonSeriesId).get().$object;

		$scope.sermonSeries = sermonSeries;

		$scope.saveEdits = function($event) {
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
	}]);
