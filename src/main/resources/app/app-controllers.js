angular.module('contentControllers', ['contentServices'])
		.controller('sermonSeriesListCtrl', ['$scope', 'contentApi', '$log', function($scope, contentApi, $log) {
			$scope.sermonSeriesList = contentApi.all('sermon-series').getList().$object;
		}])
		.controller('sermonSeriesAddCtrl', ['$scope', 'contentApi', '$log', function($scope, contentApi, $log) {
			$scope.save = function() {
				var transientSermonSeries = { name: $scope.name, description: $scope.description };
				
				$log.info("saving " + transientSermonSeries);
				
				contentApi.post('sermon-series', transientSermonSeries);
			};
		}]);
