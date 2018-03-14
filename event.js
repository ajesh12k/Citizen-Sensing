class Event{
    constructor( name, desc, lat, lon, minZoom, maxZoom, map ){
        this.marker = new google.maps.Marker({
            title: name,
            clickable: true,
            label: 'a',
            position: {lat: lat, lng: lon},
            map: map
        });
        this.minZoom = minZoom;
        this.maxZoom = maxZoom;
    }
    
}