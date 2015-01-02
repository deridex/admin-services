angular.module('nmcc.Sermon', ['nmcc.ContentServices', 'nmcc.SermonControllers'])
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
