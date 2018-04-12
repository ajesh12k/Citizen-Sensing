'use strict';

var app = angular.module('CsenseFlask', ['CsenseFlask.filters', 'ngRoute', 'ngMap', 'rzModule', 'twitter.timeline']);
app.config(function ($routeProvider, $locationProvider) {
    $routeProvider
        .when('/', {
            templateUrl: 'static/partials/landing.html',
            controller: IndexController
        })
        .when('/login', {
            templateUrl: 'static/partials/login.html',
            controller: LoginController
        })
        .when('/about', {
            templateUrl: 'static/partials/about.html',
            controller: AboutController
        })
        .when('/help', {
            templateUrl: 'static/partials/help.html',
            controller: HelpController
        })
        .when('/devices', {
            templateUrl: 'static/partials/devices.html',
            controller: DevicesController
        })
        .when('/device/:id', {
            templateUrl: 'static/partials/device.html',
            controller: DeviceController
        })
        .when('/device-detail/:id', {
            templateUrl: 'static/partials/device-detail.html',
            controller: DeviceDetailController
        })
        .when('/manage-events', {
            templateUrl: 'static/partials/manage-events.html',
            controller: ManageEventsController
        })
        .when('/manage-event/:id', {
            templateUrl: 'static/partials/manage-event.html',
            controller: ManageEventController
        })
        .when('/users', {
            templateUrl: 'static/partials/users.html',
            controller: UsersController
        })
        .when('/lost-report', {
            templateUrl: 'static/partials/lost-report.html',
            controller: LostReportController
        })
        .when('/events-mb', {
            templateUrl: 'static/partials/events-mb.html',
            controller: EventsMbController
        });

    $locationProvider.html5Mode({
        enabled: true,
        requireBase: false
    });
});

app.controller('MainController', function ($scope) {
    $scope.admin = {};
    $scope.admin.loggedIn = "false";
});
