module.exports = function(config){
    config.set({

        basePath : './',

        files : [
            'src/main/resources/ext/angular-1.3.9/angular.js',
            'src/main/resources/ext/angular-1.3.9/angular-route.js',
            'src/test/js/**/*.js'
        ],

        autoWatch : true,

        frameworks: ['jasmine'],

        browsers : ['Chrome'],

        plugins : [
            'karma-chrome-launcher',
            'karma-firefox-launcher',
            'karma-jasmine',
            'karma-junit-reporter'
        ],

        junitReporter : {
            outputFile: 'test_out/unit.xml',
            suite: 'unit'
        }
    });
};
