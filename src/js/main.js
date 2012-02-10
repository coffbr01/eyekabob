// Get nearby events from last.fm and display them in a list.
com.eyekabob.nearbyEvents = function() {
    $.mobile.showPageLoadingMsg();
    console.log("starting nearby events");
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

com.eyekabob.dispatchToLiveMusicForm = function() {
    $.mobile.changePage("#liveMusicForm");
};

com.eyekabob.findLiveMusicByBand = function() {
    var band = $("#findByBandInput")[0].value;
    var url = fm.last.SERVICE_URL + "?method=artist.getEvents&api_key=" + fm.last.auth.api_key + "&artist=" + band;
    $.ajax({
        url: url,
        success: com.eyekabob.eventsSuccessHandler,
        failure: com.eyekabob.eventsFailureHandler
    });
    $.mobile.changePage("#eventsPage");
    $.mobile.showPageLoadingMsg();
};

com.eyekabob.findLiveMusicByVenue = function() {
    alert("NOT YET IMPLEMENTED");
};

com.eyekabob.findLiveMusicByLocation = function() {
    alert("NOT YET IMPLEMENTED");
};

// Success handlers.

// Handles successful geographical location from phonegap.
com.eyekabob.geoLocationSuccessHandler = function(position) {
    console.log("geo success");
    var lat = position.coords.latitude;
    var lon = position.coords.longitude;
    var url = fm.last.SERVICE_URL + "?method=geo.getevents&api_key=" + fm.last.auth.api_key + "&lat=" + lat + "&long=" + lon + "&limit=50";
    $.ajax({
        url: url,
        success: com.eyekabob.eventsSuccessHandler,
        failure: com.eyekabob.eventsFailureHandler
    });
};

com.eyekabob.eventsSuccessHandler = function(xml, successStr, response) {
    var eventsList = $("#eventsList");
    eventsList.children().remove("li");

    com.eyekabob.eventsResponseJson = com.eyekabob.util.xmlToJson(xml);
    var i = 0;
    for (; i < com.eyekabob.eventsResponseJson.lfm.events.event.length; i++) {
        var anEvent = com.eyekabob.eventsResponseJson.lfm.events.event[i];
        var title = anEvent.title.data;
        var startDate = anEvent.startDate.data;
        var venue = anEvent.venue;
        var venueName = venue.name.data;
        var venueUrl = venue.url.data;
        eventsList.append("<li><a href='" + venueUrl + "'>" + title + "<br/>" + venueName + "<br/>" + startDate + "</a></li>");
    }

    eventsList.listview("refresh");
    $.mobile.hidePageLoadingMsg();
};

// Failure handlers.

// Handles a failure to get the geographical location from phonegap.
com.eyekabob.geoLocationFailureHandler = function() {
    console.log("geo location failed! creating mock data for development");
    $.mobile.hidePageLoadingMsg();
};

com.eyekabob.eventsFailureHandler = function() {
    console.log("events failed");
    $.mobile.hidePageLoadingMsg();
};
