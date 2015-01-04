angular.module('nmcc.SermonSeriesControllers', ['ngRoute', 'nmcc.ContentServices'])
	.controller('SermonSeriesCtrl', [function() { }])
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
	.controller('SermonSeriesEditCtrl', ['$scope', '$log', '$routeParams', 'contentApi', '$route', '$filter', '$location', function($scope, $log, $routeParams, contentApi, $route, $filter, $location) {
		var sermonSeriesId = $routeParams.sermonSeriesId;

		$scope.sermonSeriesHandle = contentApi.one('sermon-series', sermonSeriesId);
		$scope.sermonSeriesHandle.get().then(function(data) {
			$log.info('fetched sermon series ' + JSON.stringify(data));

			$scope.sermonSeries = data;
			$scope.pageCtrl.sermonSeriesVersion = data.v;
		});

		$scope.editIdx = 0;

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
		var dateFilter = $filter('date');

		var yyyymmdd = function(dateTime) {
			return dateFilter(dateTime, 'yyyy-MM-dd');
		};

		var datetime = function(yyyymmdd) {
			return new Date(dateFilter(yyyymmdd, 'yyyy-MM-ddTHH:mm:ss.sss', 'UTC') + 'Z');
		};

		$scope.handleAddSermon = function() {
			$log.info('adding sermon');

			$location.path('/' + sermonSeriesId + '/add-sermon');
		};

		$scope.handleEditSermon = function(sermonId) {
			$location.path('/' + sermonSeriesId + '/sermon/' + sermonId);
		}
	}]);
