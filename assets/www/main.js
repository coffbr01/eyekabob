com = {};
com.eyekabob = {};

com.eyekabob.cameraSuccessHandler = function() {
    notification.alert("camera success");
};

com.eyekabob.cameraFailureHandler = function() {
    notification.alert("camera fail");
};

com.eyekabob.camera = function() {
    var opts = {
        destinationType: Camera.DestinationType.FILE_URI,
        sourceType: Camera.PictureSourceType.PHOTOLIBRARY
    };
    navigator.camera.getPicture(com.eyekabob.cameraSuccessHandler, com.eyekabob.cameraFailureHandler, opts);
};

// Handles successful geographical location from phonegap.
com.eyekabob.geoLocationSuccessHandler = function(position) {
    var lat = position.coords.latitude;
    var lon = position.coords.longitude;
    var url = fm.last.SERVICE_URL + "?method=geo.getevents&api_key=" + fm.last.auth.api_key + "&lat=" + lat + "&long=" + lon;
    $.ajax({
        url: url,
        success: fm.last.nearbyEventsSuccessHandler
    });
};

// Handles a failure to get the geographical location from phonegap.
com.eyekabob.geoLocationFailureHandler = function() {
    console.warn("geo location failed! creating mock data for development");

    $.mobile.hidePageLoadingMsg();
    notification.alert("Failed to get geolocation. You may need to turn on your device's GPS.");


    var position = {
        coords: {
            latitude: 44.7665274,
            longitude: -91.4087871
        }
    };
    //TODO: workaround/mock for the browser.
    //com.eyekabob.geoLocationSuccessHandler(position);
};

fm = {};
fm.last = {};

fm.last.SERVICE_URL = "http://ws.audioscrobbler.com/2.0/";

fm.last.auth = {
    user: "eyekabob", // last.fm username
    pass: "eyekabob", // last.fm password
    api_key: "d9cd2f150cf10b139ab481e462272f3f", // last.fm api key for eyekabob user
    secret: "3a0bdf658071649c7a2594aa63ec1062", // last.fm secret for eyekabob user
    authtoken: "167550e2be8e2989ab56e63b03dd1db6" // last.fm auth token: md5(user + md5(pass))
};

// Handles successful response from last.fm for events in the area.
fm.last.nearbyEventsSuccessHandler = function(xml, successStr, response) {
    $.mobile.hidePageLoadingMsg();
    var nearbyList = $("#nearbyList");
    nearbyList.children().remove("li");

    var events = xml.getElementsByTagName("event");
    var i = 0;
    for (; i < events.length; i++) {
        var anEvent = events[i];
        var title = anEvent.getElementsByTagName("title")[0].firstChild.data;
        var venue = anEvent.getElementsByTagName("venue")[0];
        var venueName = venue.getElementsByTagName("name")[0].firstChild.data;
        var venueUrl = venue.getElementsByTagName("url")[0].firstChild.data;
        var startDate = anEvent.getElementsByTagName("startDate")[0].firstChild.data;
        nearbyList.append("<li><a href='" + venueUrl + "'>" + title + "<br/>" + venueName + "<br/>" + startDate + "</a></li>");
    }

    $.mobile.changePage("#nearbyEvents");
    nearbyList.listview("refresh");
};

// Get nearby events from last.fm and display them in a list.
com.eyekabob.nearbyEvents = function() {
    $.mobile.showPageLoadingMsg();
    navigator.geolocation.getCurrentPosition(com.eyekabob.geoLocationSuccessHandler, com.eyekabob.geoLocationFailureHandler);
};

