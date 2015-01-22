angular.module('nmcc.SermonAsset', [])
    .directive('nmccSermonAssetForm', [function () {
        return {
            restrict: 'E',
            templateUrl: '/sermon-asset/sermon-asset-form.html',
            controller: ['$scope', '$log', function($scope, $log) {
                $scope.onFileChange = function(files) {
                    $log.info('onFileChange(' + JSON.stringify(files) +')');

                    $log.info('invoking ' + $scope.handleFileChange);
                    $scope.handleFileChange(files);
                };
            }],
            scope: {
                handleFileChange: '&'
            }
        };
    }]);
