"use strict";
let events = [];
var map, infoWindow;
const maxZoom = 10;
const minZoom = 1;

function initMap() {
    map = new google.maps.Map(document.getElementById('map'), {
        center: {lat: -34.397, lng: 150.644},
        zoom: 8
    });
    infoWindow = new google.maps.InfoWindow;


    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(function(position) {
            var pos = {
                lat: position.coords.latitude,
                lng: position.coords.longitude
            };

            infoWindow.open(map);
            map.setCenter(pos);
            var marker = new google.maps.Marker({
                position: pos,
                map: map
            });
        }, function() {
            handleLocationError(true, infoWindow, map.getCenter());
        });
    } else {
        handleLocationError(false, infoWindow, map.getCenter());
    }


    var contentString = '<div id="content">'+
        '<div id="siteNotice">'+
        '</div>'+
        '<h1 id="firstHeading" class="firstHeading">Uluru</h1>'+
        '<div id="bodyContent">'+
        '<p><b>Uluru</b>, also referred to as <b>Ayers Rock</b>, is a large ' +
        'sandstone rock formation in the southern part of the '+
        'Northern Territory, central Australia. It lies 335&#160;km (208&#160;mi) '+
        'south west of the nearest large town, Alice Springs; 450&#160;km '+
        '(280&#160;mi) by road. Kata Tjuta and Uluru are the two major '+
        'features of the Uluru - Kata Tjuta National Park. Uluru is '+
        'sacred to the Pitjantjatjara and Yankunytjatjara, the '+
        'Aboriginal people of the area. It has many springs, waterholes, '+
        'rock caves and ancient paintings. Uluru is listed as a World '+
        'Heritage Site.</p>'+
        '<p>Attribution: Uluru, <a href="https://en.wikipedia.org/w/index.php?title=Uluru&oldid=297882194">'+
        'https://en.wikipedia.org/w/index.php?title=Uluru</a> '+
        '(last visited June 22, 2009).</p>'+
        '</div>'+
        '</div>';

    var infowindow = new google.maps.InfoWindow({
        content: contentString
    });

    events.push( new Event("testOne", contentString, 61.054993, 28.189663, 0, 2, map ));
    events.push( new Event("testTwo", "firstDesc", 61.169598, 28.764546, 0, 2, map ));
    events.push( new Event("testThree", "firstDesc", 61.054769, 28.186903, 3, 5, map ));
    events.push( new Event("testFour", "firstDesc", 61.556079, 28.181947, 8, 16, map ));
    events.push( new Event("testFive", "firstDesc", 61.255045, 26.770833, 8, 16, map ));
    console.log(events[0]);

    events[0].marker.addListener('click', function() {
        infowindow.open(map, marker);
    });
}

function handleLocationError(browserHasGeolocation, infoWindow, pos) {
    infoWindow.setPosition(pos);
    infoWindow.setContent(browserHasGeolocation ?
        'Error: The Geolocation service failed.' :
        'Error: Your browser doesn\'t support geolocation.');
    infoWindow.open(map);
}


function addMarkers( currentZoom ){
    for( let i = 0; i < events.length; i++ )
        if( events[i].minZoom <currentZoom && events[i].maxZoom>currentZoom )
            events[i].marker.setVisible( true );

    marker.addListener('click', function() {
        infowindow.open(map, marker);
    });
}


function clearMarkers(){
    for( let i = 0; i < events.length; i++ )
        events[i].marker.setVisible( false );
}

function getNew( range ){
    let zoom = (range/100) * (maxZoom-minZoom);
    console.log( zoom, range/100);
    clearMarkers();
    addMarkers( zoom );
    alert("Range is: " + range);
}