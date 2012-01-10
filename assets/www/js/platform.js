// Borrowed from Sencha Touch.
com.eyekabob.platform = {

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

com.eyekabob.platform.init();

