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
    var venue = $("#findByVenueInput")[0].value;
    var url = fm.last.SERVICE_URL + "?method=venue.search&api_key=" + fm.last.auth.api_key + "&venue=" + venue;
    $.ajax({
        url: url,
        success: com.eyekabob.venueSearchSuccessHandler,
        failure: com.eyekabob.eventsFailureHandler
    });
    $.mobile.showPageLoadingMsg();
};

com.eyekabob.venueSearchSuccessHandler = function(xml, successStr, response) {
    var json = com.eyekabob.util.xmlToJson(xml);
    var matches = json.lfm.results.venuematches;
    if (!matches.venue) {
        alert("No venues matched your search");
        return;
    }
    if (typeof(matches.venue) === "array" && matches.venue.length > 1) {
        // TODO: go to the venues page so they can choose which one they want.
        alert("More than one venue matched your search. The venue chooser isn't implemented yet!!");
        return;
    }

    var id = matches.venue.id.data;
    var url = fm.last.SERVICE_URL + "?method=venue.getEvents&api_key=" + fm.last.auth.api_key + "&venue=" + id;
    $.ajax({
        url: url,
        success: com.eyekabob.eventsSuccessHandler,
        failure: com.eyekabob.eventsFailureHandler
    });
    $.mobile.changePage("#eventsPage");
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

    var json = com.eyekabob.util.xmlToJson(xml);
    var i = 0;
    for (; i < json.lfm.events.event.length; i++) {
        var anEvent = json.lfm.events.event[i];
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
