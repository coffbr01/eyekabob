com = {};
com.eyekabob = {};

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
    //TODO: workaround/mock for the browser.
    //$.mobile.hidePageLoadingMsg();
    //alert("Failed to get geolocation. You may need to turn on your device's GPS.");

    console.warn("geo location failed! creating mock data for development");

    var position = {
        coords: {
            latitude: 44.7665274,
            longitude: -91.4087871
        }
    };
    com.eyekabob.geoLocationSuccessHandler(position);
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
    alert("SUCCESS! Now need to handle the nearby events response");
};

// Get nearby events from last.fm and display them in a list.
nearbyEvents = function() {
    $.mobile.showPageLoadingMsg();
    navigator.geolocation.getCurrentPosition(com.eyekabob.geoLocationSuccessHandler, com.eyekabob.geoLocationFailureHandler);
};

