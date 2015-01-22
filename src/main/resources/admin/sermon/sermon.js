angular.module('nmcc.Sermon', ['nmcc.ContentServices', 'nmcc.SermonControllers', 'nmcc.SermonAsset'])
    .directive('nmccSermonForm', [function() {
            return {
                    restrict: 'E',
                    templateUrl: '/sermon/sermon-form.html',
                    scope: {
                            sermon: '=',
                            onSave: '&',
                            onCancel: '&'
                    }
            };
    }]);
