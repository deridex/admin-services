angular.module('nmcc.SermonControllers', ['ngRoute', 'nmcc.ContentServices'])
	.controller('SermonAddCtrl', ['$scope', '$log', 'contentApi', '$routeParams', '$filter', '$location', function($scope, $log, contentApi, $routeParams, $filter, $location) {
		var dateFilter = $filter('date');

		var yyyymmdd = function(dateTime) {
			return dateFilter(dateTime, 'yyyy-MM-dd');
		};

		var datetime = function(yyyymmdd) {
			return new Date(dateFilter(yyyymmdd, 'yyyy-MM-ddTHH:mm:ss.sss', 'UTC') + 'Z');
		};

		var sermonSeriesId = $routeParams.sermonSeriesId;

		var sermonsApi = contentApi.one('sermon-series', sermonSeriesId).all('sermons');

		$scope.transientSermon = {};

		$scope.handleSave = function() {
			$scope.transientSermon.date = yyyymmdd($scope.transientSermon.datetime);

			delete $scope.transientSermon.datetime;

			$log.info('saving new sermon ' + JSON.stringify($scope.transientSermon));

			sermonsApi.post($scope.transientSermon).then(
				function(data) {
					$log.info('save sermon ' + JSON.stringify(data));

					$location.path('/' + sermonSeriesId);
				},
				function(error) {
					$log.info('failed to save sermon because ' + JSON.stringify(error));
				}
			);
		};

		$scope.handleCancel = function() {
			$log.info('cancelling new sermon');
		};
	}])
	.controller('SermonEditCtrl', ['$scope', '$log', 'contentApi', '$routeParams', '$filter', '$location', '$route', function($scope, $log, contentApi, $routeParams, $filter, $location, $route) {
		var dateFilter = $filter('date');

		var yyyymmdd = function(dateTime) {
			return dateFilter(dateTime, 'yyyy-MM-dd');
		};

		var datetime = function(yyyymmdd) {
			return new Date(dateFilter(yyyymmdd, 'yyyy-MM-ddTHH:mm:ss.sss', 'UTC') + 'Z');
		};

		var sermonSeriesId = $routeParams.sermonSeriesId;
		var sermonId = $routeParams.sermonId;

		var sermonHandle = contentApi.one('sermon-series', sermonSeriesId).one('sermons', sermonId);

		sermonHandle.get().then(
			function(data) {
				$log.info('fetched sermon ' + JSON.stringify(data));

				data.datetime = datetime(data.date);

				delete data.date;

				$scope.sermon = data;
			},
			function(error) {
				$log.error('could not fetch sermon ' + JSON.stringify(error));
			}
		);

		$scope.handleSave = function() {
			$scope.sermon.date = yyyymmdd($scope.sermon.datetime);

			delete $scope.sermon.datetime;

			$log.info('saving edited sermon ' + JSON.stringify($scope.sermon));

			$scope.sermon.put({ v: $scope.sermon.v }).then(
				function(data) {
					$log.info('saved edited sermon ' + JSON.stringify(data));

					$route.reload();
				},
				function(error) {
					$log.info('failed to edited save sermon because ' + JSON.stringify(error));
				}
			);
		};

		$scope.handleCancel = function() {
			$log.info('cancelling new sermon');

			$location.path('/' + sermonSeriesId);
		};
	}]);
