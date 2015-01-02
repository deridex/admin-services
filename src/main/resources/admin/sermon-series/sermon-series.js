angular.module('nmcc.SermonSeries', ['ngRoute', 'nmcc.SermonSeriesControllers', 'nmcc.Sermon'])
    .config(['$routeProvider', function($routeProvider) {
        $routeProvider
            .when('/', {
                controller: 'SermonSeriesAddCtrl as viewCtrl',
                templateUrl: 'add.html'
            })
            .when('/:sermonSeriesId', {
                controller: 'SermonSeriesEditCtrl as viewCtrl',
                templateUrl: 'edit.html'
            })
            .when('/:sermonSeriesId/add-sermon', {
                controller: 'SermonAddCtrl',
                templateUrl: '/sermon/add.html'
            })
            .when('/:sermonSeriesId/sermon/:sermonId', {
                controller: 'SermonEditCtrl',
                templateUrl: '/sermon/edit.html'
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
    }]);
