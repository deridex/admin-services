angular.module('contentServices', ['restangular'])
		.factory('contentApi', ['Restangular', function(Restangular) {
			return Restangular.withConfig(function(RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl('/api/v1');
				RestangularConfigurer.setDefaultHeaders({
					Accept: 'application/json',
					'Content-Type': 'application/json'
				});
			});
		}])
		.factory('adminApi', ['Restangular', function(Restangular) {
			return Restangular.withConfig(function(RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl('/admin');
				RestangularConfigurer.setDefaultHeaders({
					Accept: 'application/json',
					'Content-Type': 'application/json'
				});
			});
		}]);
