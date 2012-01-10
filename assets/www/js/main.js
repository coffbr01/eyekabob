// Get nearby events from last.fm and display them in a list.
com.eyekabob.nearbyEvents = function() {
    $.mobile.showPageLoadingMsg();
    // TODO: no need for desktop check when development is over.
    if (com.eyekabob.platform.Desktop) {
        var position = {
            coords: {
                latitude: 44.976457,
                longitude: -93.282166
            }
        };
        com.eyekabob.geoLocationSuccessHandler(position);
        return;
    }

    navigator.geolocation.getCurrentPosition(com.eyekabob.geoLocationSuccessHandler, com.eyekabob.geoLocationFailureHandler);
};

com.eyekabob.camera = function() {
    // TODO: no need for desktop check when development is over.
    if (platform.Desktop) {
        com.eyekabob.cameraSuccessHandler("images/4800test.jpg");
        return;
    }

    var opts = {
        destinationType: Camera.DestinationType.FILE_URI,
        sourceType: Camera.PictureSourceType.PHOTOLIBRARY
    };
    navigator.camera.getPicture(com.eyekabob.cameraSuccessHandler, com.eyekabob.cameraFailureHandler, opts);
};

com.eyekabob.cameraSuccessHandler = function(imageURI) {
    document.getElementById("cameraPreview").src = imageURI;
};

// Success handlers.

// Handles successful geographical location from phonegap.
com.eyekabob.geoLocationSuccessHandler = function(position) {
    var lat = position.coords.latitude;
    var lon = position.coords.longitude;
    var url = fm.last.SERVICE_URL + "?method=geo.getevents&api_key=" + fm.last.auth.api_key + "&lat=" + lat + "&long=" + lon + "&limit=50";
    $.ajax({
        url: url,
        success: com.eyekabob.nearbyEventsSuccessHandler
    });
};

// Handles successful response from last.fm for events in the area.
com.eyekabob.nearbyEventsSuccessHandler = function(xml, successStr, response) {
    $.mobile.hidePageLoadingMsg();
    var nearbyList = $("#nearbyList");
    nearbyList.children().remove("li");

    com.eyekabob.nearbyResponseJson = com.eyekabob.util.xmlToJson(xml);
    var i = 0;
    for (; i < com.eyekabob.nearbyResponseJson.lfm.events.event.length; i++) {
        var anEvent = com.eyekabob.nearbyResponseJson.lfm.events.event[i];
        var title = anEvent.title.data;
        var startDate = anEvent.startDate.data;
        var venue = anEvent.venue;
        var venueName = venue.name.data;
        var venueUrl = venue.url.data;
        nearbyList.append("<li><a href='" + venueUrl + "'>" + title + "<br/>" + venueName + "<br/>" + startDate + "</a></li>");
    }

    $.mobile.changePage("#nearbyEvents");
    nearbyList.listview("refresh");
};

// Failure handlers.

com.eyekabob.cameraFailureHandler = function() {
    console.error("camera fail");
};

// Handles a failure to get the geographical location from phonegap.
com.eyekabob.geoLocationFailureHandler = function() {
    console.warn("geo location failed! creating mock data for development");
    $.mobile.hidePageLoadingMsg();
};

