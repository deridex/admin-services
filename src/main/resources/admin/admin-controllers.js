angular.module('nmcc.ContentControllers', ['ngRoute', 'nmcc.ContentServices'])
		.config(['$routeProvider', function($routeProvider) {
				$routeProvider
						.when('/', {
							controller: 'SermonSeriesListCtrl',
							templateUrl: '/sermon-series/list.html'
						})
						.when('/sermon-series-add', {
							controller: 'SermonSeriesAddCtrl',
							templateUrl: '/sermon-series/add.html'
						})
					.when('/sermon-series/:sermonSeriesId', {
						controller: 'SermonSeriesEditCtrl',
						templateUrl: '/sermon-series/edit.html'
					})
					.when('/sermon-series/:sermonSeriesId/sermon-add', {
						controller: 'SermonAddCtrl',
						templateUrl: '/sermon/add.html'
					})
					.otherwise({ redirectTo: '/' })
		}])
		.controller('SermonSeriesListCtrl', ['$scope', 'adminApi', '$log', function($scope, adminApi, $log) {
			$scope.sermonSeriesList = adminApi.all('sermon-series').getList().$object;
		}])
		.controller('SermonAddCtrl', ['$scope', '$routeParams', 'contentApi', '$location', '$log', function($scope, $routeParams, contentApi, $location, $log) {
			$scope.onSave = function($event) {
				var transientSermon = { name: $scope.name, description: $scope.description, passages: $scope.passages };

				$log.info('adding sermon ' + JSON.stringify(transientSermon));

				contentApi.one('sermon-series', $routeParams.sermonSeriesId).all('sermons').customPOST(transientSermon).then(
					function(persistentSermon) {
						$log.info('added sermon series ' + persistentSermon.id);

						$location.path('/sermon-series/' + $routeParams.sermonSeriesId);
					},
					function(reason) {
						$log.error('could not add sermon: ' + reason);
					});
			};

			$scope.onCancel = function($event) {
				$log.info("canceling add sermon");

				$location.path('/sermon-series/' + $routeParams.sermonSeriesId);
			}
		}]);
