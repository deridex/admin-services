angular.module('nmcc.SermonSeriesControllers', ['ngRoute', 'nmcc.ContentServices'])
	.controller('SermonSeriesCtrl', [function() {
		this.versions = {};
	}])
	.controller('SermonSeriesAddCtrl', ['$scope', '$log', '$location', 'contentApi', function($scope, $log, $location, contentApi) {
		$log.info('building sermon series add control');

		var sermonSeries = {};

		$scope.sermonSeries = sermonSeries;

		$scope.handleSave = function($event) {
			var transientSermonSeries = { name: sermonSeries.name, description: sermonSeries.description, imageUrl: sermonSeries.imageUrl };

			$log.info('adding sermon series ' + JSON.stringify(transientSermonSeries));

			contentApi.all('sermon-series').post(transientSermonSeries).then(
				function(persistentSermonSeries) {
					$log.info('added sermon series ' + persistentSermonSeries.id);

					$location.path('/' + persistentSermonSeries.id);
				},
				function(reason) {
					$log.error('could not add sermon series: ' + reason);
				});
		};
	}])
	.controller('SermonSeriesEditCtrl', ['$scope', '$log', '$routeParams', 'contentApi', '$route', '$location', function($scope, $log, $routeParams, contentApi, $route, $location) {
		var sermonSeriesId = $routeParams.sermonSeriesId;

		$scope.sermonSeriesHandle = contentApi.one('sermon-series', sermonSeriesId);
		$scope.sermonSeriesHandle.get().then(function(data) {
			$log.info('fetched sermon series ' + JSON.stringify(data));

			$scope.sermonSeries = data;
			$scope.pageCtrl.versions['sermon-series'] = data.v;
		});

		$scope.editIdx = 0;

		$scope.sermons = [];

		$scope.sermonsHandle = contentApi.one('sermon-series', sermonSeriesId).all('sermons');
		$scope.sermonsHandle.getList().then(function(data) {
			$log.info('fetched sermons ' + JSON.stringify(data));

			$scope.sermons = data;
		});

		$scope.handleSaveEdits = function($event) {
			$log.info('saving sermon series edits ' + JSON.stringify($scope.sermonSeries));

			$scope.sermonSeries.put({ v: $scope.sermonSeries.v }).then(
				function() {
					$log.info('saved sermon edits');

					$route.reload();
				},
				function() {
					$log.info('failed to save sermon edits');
				}
			);
		};

		$scope.handleAddSermon = function() {
			$location.path('/' + sermonSeriesId + '/sermons');
		};

		$scope.handleEditSermon = function(sermonId) {
			$location.path('/' + sermonSeriesId + '/sermons/' + sermonId);
		}
	}]);
