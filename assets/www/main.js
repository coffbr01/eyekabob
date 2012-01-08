// TODO: this doesn't work
auth = (function() {
    var user = "eyekabob";
    var pass = "eyekabob";
    var apiKey = "c0b1a4a13aa88f9d1de45a7f28c59b90";
    var secret = "bc64ee5597b58ffa9b8d7c182fd8cf2d";

    return {
        pub: function() {
            return secret;
        }
    };
});
