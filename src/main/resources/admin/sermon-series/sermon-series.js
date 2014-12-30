angular.module('nmcc.SermonSeries', ['ngRoute', 'nmcc.SermonSeriesControllers'])
    .config(['$routeProvider', function($routeProvider) {
        $routeProvider
            .when('/', {
                controller: 'SermonSeriesAddCtrl as viewCtrl',
                templateUrl: 'add.html'
            })
            .when('/:sermonSeriesId', {
                controller: 'SermonSeriesEditCtrl as viewCtrl',
                templateUrl: 'edit.html'
            });
    }])
    .directive('nmccSermonSeriesForm', [function() {
        return {
            restrict: 'E',
            templateUrl: 'sermon-series-form.html',
            scope: {
                sermonSeries: '=',
                onSave: '&',
                onCancel: '&'
            }
        };
    }])
    .directive('nmccSermonForm', [function() {
        return {
            restrict: 'E',
            templateUrl: 'sermon-form.html',
            scope: {
                sermon: '=',
                onSave: '&',
                onCancel: '&'
            }
        };
    }]);
