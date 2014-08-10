angular.module('contentControllers', ['contentServices'])
		.controller('sermonSeriesCtrl', ['$scope', 'contentApi', '$log', function($scope, contentApi, $log) {
			$scope.sermonSeriesList = contentApi.all('sermonseries').getList().$object;
		}]);
