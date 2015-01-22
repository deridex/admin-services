angular.module('nmcc.SermonControllers', ['ngRoute', 'nmcc.ContentServices', 'angularFileUpload'])
	.controller('SermonAddCtrl', ['$scope', '$log', 'contentApi', '$routeParams', '$filter', '$location', function($scope, $log, contentApi, $routeParams, $filter, $location) {
		var sermonSeriesVersion = $scope.pageCtrl.versions['sermon-series'];

		if (!sermonSeriesVersion) {
			$log.info('sermon series metadata not found so redirecting to sermon series');

			$location.path('/' + $routeParams.sermonSeriesId);
		}

		var dateFilter = $filter('date');

		var yyyymmdd = function(dateTime) {
			return dateFilter(dateTime, 'yyyy-MM-dd');
		};

		var sermonSeriesId = $routeParams.sermonSeriesId;

		var sermonsSeriesApi = contentApi.one('sermon-series', sermonSeriesId);

		$scope.sermonData = {};

		$scope.handleSave = function() {
			var transientSermon = {
				name: $scope.sermonData.name,
				date: yyyymmdd($scope.sermonData.datetime),
				by: $scope.sermonData.by,
				description: $scope.sermonData.description
			};

			$log.info('saving new sermon ' + JSON.stringify(transientSermon));

			sermonsSeriesApi.post('sermons', transientSermon, { v: sermonSeriesVersion }).then(
				function(data) {
					$log.info('save sermon ' + JSON.stringify(data));

					$location.path('/' + sermonSeriesId + '/sermon/' + data.id);
				},
				function(error) {
					$log.info('failed to save sermon because ' + JSON.stringify(error));
				}
			);
		};

		$scope.handleCancel = function() {
			$log.info('cancelling new sermon');

			$location.path('/' + $routeParams.sermonSeriesId);
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

		$scope.sermonData = {};

		sermonHandle.get().then(
			function(data) {
				$log.info('fetched sermon ' + JSON.stringify(data));

				$scope.sermonData.name = data.name;
				$scope.sermonData.datetime = datetime(data.date);
				$scope.sermonData.by = data.by;
				$scope.sermonData.description = data.description;

				$scope.sermonEntity = data;
			},
			function(error) {
				$log.error('could not fetch sermon ' + JSON.stringify(error));
			}
		);

		$scope.handleSaveSermon = function() {
			$scope.sermonEntity.name = $scope.sermonData.name;
			$scope.sermonEntity.date = yyyymmdd($scope.sermonData.datetime);
			$scope.sermonEntity.by = $scope.sermonData.by;
			$scope.sermonEntity.description = $scope.sermonData.description;

			$log.info('saving edited sermon ' + JSON.stringify($scope.sermonData));

			$scope.sermonEntity.put({ v: $scope.sermonEntity.v }).then(
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

		var assetsHandle = sermonHandle.all('assets');

		$scope.assets = [];

		assetsHandle.getList().then(
			function(data) {
				$scope.assets = data;
			},
			function(error) {
				$log.error('could not get assets :' + JSON.stringify(error));
			}
		);

		$scope.handleFileChange = function(files) {
			$log.info('in handleAddAsset(' + JSON.stringify(files) + ')');
		};

		$scope.handleEditAsset = function(asset) {
			$log.info('in handleEditAsset(' + JSON.stringify(asset) + ')');
		}
	}]);
