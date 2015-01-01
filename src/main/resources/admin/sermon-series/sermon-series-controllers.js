angular.module('nmcc.SermonSeriesControllers', ['ngRoute', 'nmcc.ContentServices'])
	.controller('SermonSeriesCtrl', ['$scope', '$log', '$location', function($scope, $log, $location) {
		$log.info('building sermon series control');

		$scope.reload = function($event) {
			$log.info('abandoning data and sermon series list');

			$location.path('/');
		};
	}])
	.controller('SermonSeriesAddCtrl', ['$scope', '$log', '$location', 'contentApi', function($scope, $log, $location, contentApi) {
		$log.info('building sermon series add control');

		var sermonSeries = {};

		$scope.sermonSeries = sermonSeries;

		$scope.handleSave = function($event) {
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

		$scope.sermonSeriesEntity = contentApi.one('sermon-series', sermonSeriesId);
		$scope.sermonSeriesEntity.get().then(function(data) {
			$log.info('fetched sermon series ' + JSON.stringify(data));

			$scope.sermonSeries = data;
		});

		var listSermonsMode = 'sermons-list';
		var editSermonMode = 'sermon-edit';
		var addSermonMode = 'sermon-add'

		$scope.mode = listSermonsMode;

		$scope.editIdx = 0;

		$scope.sermonsEntity = contentApi.one('sermon-series', sermonSeriesId).getList('sermons');
		$scope.sermonsEntity.get().then(function(data) {
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

					$route.reload();
				}
			);
		};

		$scope.handleEditSermon = function($event, $index) {
			$scope.editIdx = $index;
			$scope.mode = editSermonMode;

			$log.info('editing sermon ' + $index + ' '  + $scope.sermons[$index]);
		};

		$scope.handleDeleteSermon = function($event, $index) {
			$log.info('deleting sermon ' + $index + ' ' + $scope.sermons[$index].id);
		};

		$scope.handleAddSermon = function() {
			$log.info('adding sermon');

			$scope.editIdx = $scope.sermons.length;
			$scope.sermons.push({});
			$scope.mode = addSermonMode;
		};

		$scope.handleSaveTransientSermon = function($event) {
			$log.info('saving new sermon');

			$route.reload();
		};

		$scope.handleSaveEditedSermon = function($event) {
			$log.info('saving sermon edits');

			$route.reload();
		};

		$scope.handleCancelSermon = function($event) {
			$log.info('canceling sermon edits');

			$route.reload();
		};
	}]);
