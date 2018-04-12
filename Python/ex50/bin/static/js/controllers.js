'use strict';

/* Controllers */

function LoginController($scope, $http, $location) {
    $scope.login = function () {
        var data = {"username":$scope.username, "password":$scope.password};
        $http({
            method: "POST",
            url: "/validateUser",
            data: data
        }).then(successCallback, errorCallback);

        function successCallback(response){
            $scope.admin.loggedIn = "true";

            $location.path('/manage-events');
        }

        function errorCallback(error){
            //error code
            console.log("failed");
        }
    };
}

function EventsMbController($scope, $http, $timeout) {
    $scope.events = {};

    $scope.myDeviceId = "";

    $scope.deviceId = "";
    $scope.eventId = "";
    $scope.fromDate = "";
    $scope.toDate = "";
    $scope.status = "";
    $scope.changedDate = "";
    $scope.changedBy = "";
    $scope.eventType = "";

    $scope.pageLoading = false;
    
    var colorsDynamic = ['ff8a80', //Smell
                             '880e4f', //Algae
                             '4a148c', //Noise
                             '311b92', //Alien species
                             '3d5afe',  //Lake
                             '5d4037'  //Others
        ];

    var data = {
        "device_id" : $scope.deviceId,
        "event_id": $scope.eventId,
        "fromDate": $scope.fromDate,
        "toDate": $scope.toDate,
        "status": $scope.status,
        "changedDate": $scope.changedDate,
        "changedBy": $scope.changedBy,
        "event_type": $scope.eventType
    };

    $scope.loadEvents = function () {
        $scope.pageLoading = true;

        var data = {};

        data.device_id = $scope.deviceId;
        data.event_id = $scope.eventId;
        data.fromDate = $scope.fromDate;
        data.toDate = $scope.toDate;
        data.status = $scope.status;
        data.changedDate = $scope.changedDate;
        data.changedBy = $scope.changedBy;
        data.event_type = $scope.eventType;

        $http({
            method: "POST",
            url: "/getEvent",
            data: data
        }).then(successCallback, errorCallback);

        function successCallback(response){
            $scope.events = response.data.result;
            var markerURL = 'http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=%E2%80%A2|';
            
            for (var i = 0; i < $scope.events.length; i++) {
                var position = [];
                position.push($scope.events[i].latitude);
                position.push($scope.events[i].longitude);
                $scope.events[i].pos = position;
                
                if ($scope.events[i].event_type == 'Smell') {
                    $scope.events[i].markerIcon = markerURL + 'ff8a80';
                } else if ($scope.events[i].event_type == 'Algae') {
                    $scope.events[i].markerIcon = markerURL + '880e4f';
                } else if ($scope.events[i].event_type == 'Noise') {
                    $scope.events[i].markerIcon = markerURL + '4a148c';
                } else if ($scope.events[i].event_type == 'Alien species') {
                    $scope.events[i].markerIcon = markerURL + '311b92';
                } else if ($scope.events[i].event_type == 'Lake') {
                    $scope.events[i].markerIcon = markerURL + '3d5afe';
                } else {
                    $scope.events[i].markerIcon = markerURL + '5d4037';
                }
            }

            $scope.pageLoading = false;
        }

        function errorCallback(error){
            //error code
        }
    };

    $scope.loadEvents();

    $scope.search = function (eventType) {
        if (eventType) {
            $scope.eventType = eventType;
        } else {
            $scope.eventType = '';
        }

        $scope.loadEvents();
    };

    $scope.searchMyEvent = function () {
        $scope.deviceId = $scope.myDeviceId;
        $scope.loadEvents();
    };
}

function ManageEventsController($scope, $http) {
    $scope.events = [];

    $scope.myDeviceId = "";

    $scope.deviceId = "";
    $scope.eventId = "";
    $scope.fromDate = "";
    $scope.toDate = "";
    $scope.status = "";
    $scope.changedDate = "";
    $scope.changedBy = "";
    $scope.eventType = "";
    $scope.pageLoading = false;

    var data = {
        "device_id" : $scope.deviceId,
        "event_id": $scope.eventId,
        "fromDate": $scope.fromDate,
        "toDate": $scope.toDate,
        "status": $scope.status,
        "changedDate": $scope.changedDate,
        "changedBy": $scope.changedBy,
        "event_type": $scope.eventType
    };

    $scope.loadEventTypes = function () {
        $scope.eventTypes = [];
        var data = {};
        $http({
            method: "POST",
            url: "/getEventTypes",
            data: data
        }).then(successCallback, errorCallback);

        function successCallback(response){
            var eventTypes = response.data.result;
            for(var i = 0; i < eventTypes.length; i++){
                var type = eventTypes[i].split("#")[0];
                $scope.eventTypes.push(type);
                console.log(type);
            }
            console.log($scope.eventTypes);
        }

        function errorCallback(error){
            //error code
        }
    };


    $scope.loadEvents = function () {
        $scope.pageLoading = true;

        var data = {};

        data.device_id = $scope.deviceId;
        data.event_id = $scope.eventId;
        data.fromDate = $scope.fromDate;
        data.toDate = $scope.toDate;
        data.status = $scope.status;
        data.changedDate = $scope.changedDate;
        data.changedBy = $scope.changedBy;
        data.event_type = $scope.eventType;

        $http({
            method: "POST",
            url: "/getEvent",
            data: data
        }).then(successCallback, errorCallback);

        function successCallback(response){
            $scope.events = response.data.result;

            for (var i = 0; i < $scope.events.length; i++) {
                var position = [];
                position.push($scope.events[i].latitude);
                position.push($scope.events[i].longitude);
                $scope.events[i].pos = position;
            }

            $scope.pageLoading = false;
        }

        function errorCallback(error){
            //error code
        }
    };

    $scope.loadEvents();
    $scope.loadEventTypes();

    $scope.filter = function () {
        $scope.loadEvents();
    };
}

function ManageEventController($scope, $http, $routeParams, $location, $timeout, $window) {
    $scope.deviceId = "";
    $scope.eventId = $routeParams.id;
    $scope.fromDate = "";
    $scope.toDate = "";
    $scope.status = "";
    $scope.changedDate = "";
    $scope.changedBy = "";
    $scope.eventType = "";

    $scope.pageLoading = false;

    var data = {
        "device_id" : $scope.deviceId,
        "event_id": $scope.eventId,
        "fromDate": $scope.fromDate,
        "toDate": $scope.toDate,
        "status": $scope.status,
        "changedDate": $scope.changedDate,
        "changedBy": $scope.changedBy,
        "event_type": $scope.eventType
    };

    var loadEvent = function () {
        $scope.pageLoading = true;

        var data = {};

        data.device_id = $scope.deviceId;
        data.event_id = $scope.eventId;
        data.fromDate = $scope.fromDate;
        data.toDate = $scope.toDate;
        data.status = $scope.status;
        data.changedDate = $scope.changedDate;
        data.changedBy = $scope.changedBy;
        data.event_type = $scope.eventType;

        $http({
            method: "POST",
            url: "/getEvent",
            data: data
        }).then(successCallback, errorCallback);

        function successCallback(response){
            $scope.event = response.data.result[0];

            var position = [];
            position.push($scope.event.latitude);
            position.push($scope.event.longitude);
            $scope.event.pos = position;

            $scope.zoomToIncludeMarker($scope.event.pos[0], $scope.event.pos[1]);
            $scope.pageLoading = false;
        }

        function errorCallback(error){
            //error code
        }
    }();

    $scope.resolve = function () {
        var updateData = {
            "event_id": $scope.event.event_id,
            "status": "Resolved",
            "updated_by": ""
        };
        $http({
            method: "POST",
            url: "/updateEvent",
            data: updateData
        }).then(successCallback, errorCallback);

        function successCallback(response){
            $window.history.back();
        }

        function errorCallback(error){
            //error code
        }
    };

    $scope.zoomToIncludeMarker = function(lat, lng) {
        var bounds = new google.maps.LatLngBounds();

        var latLng = new google.maps.LatLng(lat,lng);
        bounds.extend(latLng);

        $scope.map.fitBounds(bounds);
        $scope.map.setZoom(14);
    };
}

function UsersController($scope, $http) {
    var data = {};
    $scope.users = [];
    $scope.pageLoading = false;

    $scope.loadUsers = function () {
        $scope.pageLoading = true;
        $http({
            method: "POST",
            url: "/getUsers",
            data: data
        }).then(function successCallback(response) {
            $scope.users = response.data.result;
            $scope.pageLoading = false;
        },function errorCallback(response) {
            //error code
        });
    };

    $scope.loadUsers();
}

function IndexController($scope, $http, $timeout) {
    $scope.events = {};

    $scope.myDeviceId = "";

    $scope.deviceId = "";
    $scope.eventId = "";
    $scope.fromDate = "";
    $scope.toDate = "";
    $scope.status = "";
    $scope.changedDate = "";
    $scope.changedBy = "";
    $scope.eventType = "";
    $scope.pageLoading = false;

    var data = {
        "device_id" : $scope.deviceId,
        "event_id": $scope.eventId,
        "fromDate": $scope.fromDate,
        "toDate": $scope.toDate,
        "status": $scope.status,
        "changedDate": $scope.changedDate,
        "changedBy": $scope.changedBy,
        "event_type": $scope.eventType
    };

    $scope.loadEvents = function () {
        $scope.pageLoading = true;

        var data = {};

        data.device_id = $scope.deviceId;
        data.event_id = $scope.eventId;
        data.fromDate = $scope.fromDate;
        data.toDate = $scope.toDate;
        data.status = $scope.status;
        data.changedDate = $scope.changedDate;
        data.changedBy = $scope.changedBy;
        data.event_type = $scope.eventType;
        
        var colorsDynamic = ['ff8a80', //Smell
                             '880e4f', //Algae
                             '4a148c', //Noise
                             '311b92', //Alien species
                             '3d5afe',  //Lake
                             '5d4037'  //Others
        ];

        $http({
            method: "POST",
            url: "/getEvent",
            data: data
        }).then(successCallback, errorCallback);

        function successCallback(response){
            $scope.events = response.data.result;
            var markerURL = 'http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=%E2%80%A2|';
            
            for (var i = 0; i < $scope.events.length; i++) {
                var position = [];
                position.push($scope.events[i].latitude);
                position.push($scope.events[i].longitude);
                $scope.events[i].pos = position;
                
                if ($scope.events[i].event_type == 'Smell') {
                    $scope.events[i].markerIcon = markerURL + 'ff8a80';
                } else if ($scope.events[i].event_type == 'Algae') {
                    $scope.events[i].markerIcon = markerURL + '880e4f';
                } else if ($scope.events[i].event_type == 'Noise') {
                    $scope.events[i].markerIcon = markerURL + '4a148c';
                } else if ($scope.events[i].event_type == 'Alien species') {
                    $scope.events[i].markerIcon = markerURL + '311b92';
                } else if ($scope.events[i].event_type == 'Lake') {
                    $scope.events[i].markerIcon = markerURL + '3d5afe';
                } else {
                    $scope.events[i].markerIcon = markerURL + '5d4037';
                }
            }

            $scope.zoomToIncludeMarkers();
            $scope.pageLoading = false;
        }

        function errorCallback(error){
            //error code
        }
    };

    $scope.loadEvents();
        
    $scope.search = function (eventType) {
        if (eventType) {
            $scope.eventType = eventType;
        } else {
            $scope.eventType = '';
        }

        $scope.loadEvents();
    };

    $scope.zoomToIncludeMarkers = function() {
        var bounds = new google.maps.LatLngBounds();

        if ($scope.events.length) {
            $scope.events.forEach(function(c) {
                var latLng = new google.maps.LatLng(c.pos[0],c.pos[1]);
                bounds.extend(latLng);
            });
        } else {
            //zoom and scale the map to center of Lappeenranta when no events there
            var latLng = new google.maps.LatLng("61.050666", "28.160306");
            bounds.extend(latLng);
        }

        $scope.map.fitBounds(bounds);
        $scope.map.setZoom(13);
    };

    $scope.refreshSlider = function () {
        $timeout(function () {
            $scope.$broadcast('rzSliderForceRender');
        });
    };

    $scope.onSelectorSliderChange = function () {
        $scope.refreshSlider();
        var now = moment().format("YYYYMMDD");
        prepareDateRange ($scope.selectorSlider.value, $scope.slider.value);

        $scope.loadEvents();
    };

    var prepareDateRange = function (selector, subSelector) {
        var now = moment();
        $scope.toDate = now.format("YYYYMMDDhhmmss");

        if (subSelector !== 1) {
            if (selector === 'Day') {
                $scope.fromDate = now.subtract(subSelector, 'days').format("YYYYMMDDhhmmss");

                console.log("DAY");
                console.log("from: " + $scope.fromDate + " ---- " + "to: " + $scope.toDate);
            } else if (selector === 'Week') {
                $scope.fromDate = now.subtract(subSelector, 'weeks').startOf('isoWeek').format("YYYYMMDDhhmmss");

                console.log("WEEK");
                console.log("from: " + $scope.fromDate + " ---- " + "to: " + $scope.toDate);
            } else {
                $scope.fromDate = now.subtract(subSelector, 'months').format("YYYYMMDDhhmmss");

                console.log("MONTH");
                console.log("from: " + $scope.fromDate + " ---- " + "to: " + $scope.toDate);
            }
        } else {
            $scope.toDate = "";
            $scope.fromDate = "";
        }
    };

    $scope.selectorSlider = {
        value: 'Day',
        options: {
            showTicksValues: true,
            stepsArray: [
                {value: 'Day'},
                {value: 'Week'},
                {value: 'Month'}
            ],
            onEnd: $scope.onSelectorSliderChange
        }
    };

    $scope.slider = {
        value: 1,
        options: {
            floor: 1,
            ceil: 10,
            step: 1,
            showTicks: true,
            translate: function(value) {
                if ($scope.selectorSlider.value === 'Day') {
                    return value > 1 ?  value + ' days' : 'all';
                } else if ($scope.selectorSlider.value === 'Week') {
                    return value > 1 ?  value + ' weeks' : 'all';
                } else {
                    return value > 1 ?  value + ' months' : 'all';
                }
            },
            onEnd: $scope.onSelectorSliderChange
        }
    };

    $scope.searchMyEvent = function () {
        $scope.deviceId = $scope.myDeviceId;
        $scope.loadEvents();
    };
}

function AboutController($scope, $http) {

}

function HelpController($scope, $http) {

}

function DevicesController($scope, $http, $location) {
    $scope.devices = {};

    $scope.deviceId = ""; //for fetching all devices
    $scope.deviceStatus = "";

    $scope.pageLoading = false;

    var data = {
        "device_id" : $scope.deviceId,
        "status": $scope.deviceStatus
    };

    $scope.loadDevices = function () {
        $scope.pageLoading = true;

        var data = {};

        data.device_id = $scope.deviceId;
        data.status = $scope.deviceStatus;

        $http({
            method: "POST",
            url: "/getDevice",
            data: data
        }).then(successCallback, errorCallback);

        function successCallback(response){
            $scope.devices = response.data.result;
            $scope.pageLoading = false;
        }

        function errorCallback(error){
            //error code
        }
    }();
}

function DeviceController($scope, $http, $routeParams, $location, $window) {
    if ($routeParams.id === '-1') {
        $scope.id = 0;
    } else {
        $scope.id = $routeParams.id;
    }

    $scope.pageTitle = $scope.id ? "Update Device" : "Create A New Device";
    $scope.alerts = {};

    var deviceInfo = function () {
        if ($scope.id) {
            var data = {
                "device_id" : $scope.id,
                "status": ""
            };
            $http({
                method: "POST",
                url: "/getDevice",
                data: data
            }).then(function successCallback(response) {
                $scope.device = response.data.result[0];
            },function errorCallback(response) {
                //error code
            });
        }
    }();

    $scope.update = function () {
        $scope.alerts.length = 0;

        if (!$scope.editForm.$invalid) {
            var data = {
                "device_id" : $scope.device.deviceId,
                "status": $scope.device.status,
                "device_mac": $scope.device.device_mac
            };

            if ($scope.id != 0) {
                $http({
                    method: "POST",
                    url: "/updateDevice",
                    data: data
                }).then(function successCallback(response) {
                    $window.history.back();
                },function errorCallback(response) {
                    //error code
                });
            } else {
                $http({
                    method: "POST",
                    url: "/saveDevice",
                    data: data
                }).then(function successCallback(response) {
                    $window.history.back();
                },function errorCallback(response) {
                    //error code
                });
            }
        }
    }

    $scope.back = function () {
        $scope.device = {};
        $window.history.back();
    }
}

function DeviceDetailController($scope, $http, $routeParams, $location, $window) {
    $scope.device = {};

    var data = {
        "device_id" : $routeParams.id,
        "status": ""
    };

    $scope.loadDevice = function () {
        $http({
            method: "POST",
            url: "/getDevice",
            data: data
        }).then(successCallback, errorCallback);

        function successCallback(response){
            $scope.device = response.data.result[0];
        }

        function errorCallback(error){
            //error code
        }
    };

    $scope.loadDevice();

    $scope.back = function () {
        $scope.device = {};
        $window.history.back();
    }
}

function LostReportController($scope, $http, $location, $window) {
    $scope.alerts = [];
    $scope.reportSent = false;

    $scope.report = function () {
        $scope.reportSent = false;
        $scope.alerts.length = 0;

        if ($scope.deviceId) {
            var data = {
                "device_id" : $scope.deviceId,
                "status": ""
            };
            $http({
                method: "POST",
                url: "/getDevice",
                data: data
            }).then(function successCallback(response) {
                $scope.device = response.data.result[0];

                if ($scope.device) {
                    var data = {
                        "device_id" : $scope.deviceId,
                        "status": "Lost",
                        "device_mac": $scope.device.device_mac
                    };

                    $http({
                        method: "POST",
                        url: "/updateDevice",
                        data: data
                    }).then(function successCallback(response) {
                        $scope.deviceId = "";
                        $scope.reportSent = true;
                    },function errorCallback(response) {
                        //error code
                    });
                } else {
                    $scope.alerts.push("No Device Found Named - " + $scope.deviceId);
                }

            },function errorCallback(response) {
                //error code
            });
        }


    };

    $scope.back = function () {
        $scope.device = {};
        $window.history.back();
    }
}