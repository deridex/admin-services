angular.module('nmcc.SermonSeriesControllers', ['ngRoute', 'nmcc.ContentServices'])
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
	.controller('SermonSeriesEditCtrl', ['$scope', '$log', '$routeParams', 'contentApi', '$route', '$filter', function($scope, $log, $routeParams, contentApi, $route, $filter) {
		var sermonSeriesId = $routeParams.sermonSeriesId;

		$scope.sermonSeriesHandle = contentApi.one('sermon-series', sermonSeriesId);
		$scope.sermonSeriesHandle.get().then(function(data) {
			$log.info('fetched sermon series ' + JSON.stringify(data));

			$scope.sermonSeries = data;
		});

		var listSermonsMode = 'sermons-list';
		var editSermonMode = 'sermon-edit';
		var addSermonMode = 'sermon-add'

		$scope.mode = listSermonsMode;

		$scope.editIdx = 0;

		$scope.sermonsHandle = contentApi.one('sermon-series', sermonSeriesId).all('sermons');
		$scope.sermonsHandle.getList().then(function(data) {
			$log.info('fetched sermons ' + JSON.stringify(data));

			if (data) {
				$scope.sermons = data;
			} else {
				$scope.sermons = [];
			}
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
		var dateFilter = $filter('date');

		var yyyymmdd = function(dateTime) {
			return dateFilter(dateTime, 'yyyy-MM-dd');
		};

		var datetime = function(yyyymmdd) {
			return new Date(dateFilter(yyyymmdd, 'yyyy-MM-ddTHH:mm:ss.sss', 'UTC') + 'Z');
		};

		$scope.handleEditSermon = function($event, $index) {
			$scope.editIdx = $index;
			$scope.mode = editSermonMode;

			var sermon = $scope.sermons[$index];
			sermon.datetime = datetime(sermon.date);

			$log.info('editing sermon ' + $index + ' '  + JSON.stringify(sermon));
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
			var transientSermon = $scope.sermons[$scope.editIdx];

			transientSermon.date = yyyymmdd(transientSermon.datetime);

			delete transientSermon.datetime;

			$log.info('saving new sermon ' + JSON.stringify(transientSermon));

			$scope.sermonsHandle.post(transientSermon).then(
				function(data) {
					$log.info('save sermon ' + JSON.stringify(data));

					$route.reload();
				},
				function(error) {
					$log.info('failed to save sermon because ' + JSON.stringify(error));
				}
			);
		};

		$scope.handleSaveEditedSermon = function($event) {
			var sermon = $scope.sermons[$scope.editIdx];
			sermon.date = yyyymmdd(sermon.date);

			delete sermon.datetime;

			$log.info('saving sermon edits ' + JSON.stringify(sermon));

			sermon.put().then(
				function() {
					$log.info('saved sermon edits');

					$route.reload();
				},
				function(error) {
					$log.error('could not save sermon edits because ' + JSON.stringify(error));
				}
			);
		};

		$scope.handleCancelSermon = function($event) {
			$log.info('canceling sermon edits');

			$route.reload();
		};
	}]);
