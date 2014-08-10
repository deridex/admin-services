angular.module('contentServices', ['restangular'])
		.factory('sermonSeriesService', ['Restangular', function(Restangular) {
			var restangular = Restangular.withConfig(function(RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl('/api/v1');
			});
			
			return restangular.service('sermonseries');
		}]);
