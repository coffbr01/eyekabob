com = {};
com.eyekabob = {};

com.eyekabob.cameraSuccessHandler = function(imageURI) {
    document.getElementById("cameraPreview").src = imageURI;
};

com.eyekabob.cameraFailureHandler = function() {
    console.error("camera fail");
};

com.eyekabob.camera = function() {

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
    if (platform.Desktop) {
        var position = {
            coords: {
                latitude: 44.976457,
                longitude: -93.282166
            }
        };
        com.eyekabob.geoLocationSuccessHandler(position);
    }
    else {
        navigator.geolocation.getCurrentPosition(com.eyekabob.geoLocationSuccessHandler, com.eyekabob.geoLocationFailureHandler);
    }
};



// Borrowed from Sencha Touch.
platform = {

    init: function(navigator) {
        var me = this,
            platforms = me.platforms,
            ln = platforms.length,
            i, platform;

        navigator = navigator || window.navigator;

        for (i = 0; i < ln; i++) {
            platform = platforms[i];
            me[platform.identity] = platform.regex.test(navigator[platform.property]);
        }

        /**
         * @property Desktop True if the browser is running on a desktop machine
         * @type {Boolean}
         */
        me.Desktop = me.Mac || me.Windows || (me.Linux && !me.Android);
        /**
         * @property iOS True if the browser is running on iOS
         * @type {Boolean}
         */
        me.iOS = me.iPhone || me.iPad || me.iPod;

        /**
         * @property Standalone Detects when application has been saved to homescreen.
         * @type {Boolean}
         */
        me.Standalone = !!navigator.standalone;

        /**
         * @property androidVersion Returns Android OS version information.
         * @type {Boolean}
         */
        i = me.Android && (/Android\s(\d+\.\d+)/.exec(navigator.userAgent));
        if (i) {
            me.AndroidVersion = i[1];
            me.AndroidMajorVersion = parseInt(i[1], 10);
        }
        /**
         * @property Tablet True if the browser is running on a tablet (iPad)
         */
        me.Tablet = me.iPad || (me.Android && me.AndroidMajorVersion === 3);

        /**
         * @property Phone True if the browser is running on a phone.
         * @type {Boolean}
         */
        me.Phone = !me.Desktop && !me.Tablet;

        /**
         * @property MultiTouch Returns multitouch availability.
         * @type {Boolean}
         */
        me.MultiTouch = !me.Blackberry && !me.Desktop && !(me.Android && me.AndroidVersion < 3);
    },

    /**
     * @property iPhone True when the browser is running on a iPhone
     * @type {Boolean}
     */
    platforms: [{
        property: 'platform',
        regex: /iPhone/i,
        identity: 'iPhone'
    },

    /**
     * @property iPod True when the browser is running on a iPod
     * @type {Boolean}
     */
    {
        property: 'platform',
        regex: /iPod/i,
        identity: 'iPod'
    },

    /**
     * @property iPad True when the browser is running on a iPad
     * @type {Boolean}
     */
    {
        property: 'userAgent',
        regex: /iPad/i,
        identity: 'iPad'
    },

    /**
     * @property Blackberry True when the browser is running on a Blackberry
     * @type {Boolean}
     */
    {
        property: 'userAgent',
        regex: /Blackberry/i,
        identity: 'Blackberry'
    },

    /**
     * @property Android True when the browser is running on an Android device
     * @type {Boolean}
     */
    {
        property: 'userAgent',
        regex: /Android/i,
        identity: 'Android'
    },

    /**
     * @property Mac True when the browser is running on a Mac
     * @type {Boolean}
     */
    {
        property: 'platform',
        regex: /Mac/i,
        identity: 'Mac'
    },

    /**
     * @property Windows True when the browser is running on Windows
     * @type {Boolean}
     */
    {
        property: 'platform',
        regex: /Win/i,
        identity: 'Windows'
    },

    /**
     * @property Linux True when the browser is running on Linux
     * @type {Boolean}
     */
    {
        property: 'platform',
        regex: /Linux/i,
        identity: 'Linux'
    }]
};

platform.init();

