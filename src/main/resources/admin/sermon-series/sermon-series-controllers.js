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

		var listSermonsMode = 'sermons-list';
		var editSermonMode = 'sermon-edit';
		var addSermonMode = 'sermon-add'

		$scope.mode = listSermonsMode;

		$scope.sermonSeries = sermonSeries;

		$scope.editIdx = 0;

		$scope.sermons = [{
			id: 'abc',
			name: 'sermon name',
			description: 'sermon description',
			by: 'PK',
			date: '2014-12-28',
			createdAt: '2014-12-28T12:34:56Z'
		},
		{
			id: 'def',
			name: 'sermon name 2',
			description: 'sermon description 2',
			by: 'PH',
			date: '2014-12-29',
			createdAt: '2014-12-29T12:34:56Z'
		}];

		$scope.handleSaveEdits = function($event) {
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

		$scope.handleSaveNewSermon = function($event) {
			$log.info('saving new sermon');

			$route.reload();
		};

		$scope.handleSaveEditSermon = function($event) {
			$log.info('saving sermon edits');

			$route.reload();
		};

		$scope.handleCancelSermon = function($event) {
			$log.info('canceling sermon edits');

			$route.reload();
		};
	}]);
