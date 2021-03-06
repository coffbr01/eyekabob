/**
 * © 2014 Brien Coffield
 *
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE', which is part of this source code package.
 */
package com.eyekabob.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.eyekabob.CheckinSearchList;
import com.eyekabob.OAuthWebView;
import com.eyekabob.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.DOMException;

import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EyekabobHelper {
    public static Location getLocation(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // Gets the last known location from the best service.
        String bestLocationProvider = locationManager.getBestProvider(new Criteria(), true /*enabled only*/);
        return locationManager.getLastKnownLocation(bestLocationProvider);
    }

    public static class LastFM {
        public static final String USER = "eyekabob";
        public static final String PASS = "eyekabob";
        public static final String API_KEY = "d9cd2f150cf10b139ab481e462272f3f";
        public static final String SECRET = "3a0bdf658071649c7a2594aa63ec1062";
        public static final String AUTH_TOKEN = "167550e2be8e2989ab56e63b03dd1db6";
        public static final String SERVICE_URL = "http://ws.audioscrobbler.com/2.0/";

        /**
         * Do not encode params. The param values will be encoded for you.
         * @param method
         * @param params
         * @return
         */
        public static Uri getUri(String method, Map<String, String> params) {
            if (params == null) {
                params = new HashMap<String, String>();
            }

            if (!params.containsKey("limit")) {
                params.put("limit", "50");
            }

            if (!params.containsKey("format")) {
                params.put("format", "json");
            }

            params.put("api_key", API_KEY);

            String url = SERVICE_URL;
            url += "?method=" + method;
            for (String param : params.keySet()) {
                url += "&" + param + "=" + URLEncoder.encode(params.get(param));
            }
            return Uri.parse(url);
        }

        public static String toReadableDate(String unparsedDate) {
            String result = unparsedDate;
            try {
                DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
                Date d = df.parse(unparsedDate);
                DateFormat readableDF = new SimpleDateFormat("EEE, MMM d, h:mm a");
                result = readableDF.format(d);
            } catch (DOMException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return result;
        }

        public static JSONObject getJSONImage(String size, JSONArray images) throws JSONException {
            JSONObject image = null;
            for (int i = 0; i < images.length(); i++) {
                image = images.getJSONObject(i);
                if (size.equals(image.getString("size"))) {
                    return image;
                }
            }
            return image;
        }

        public static JSONObject getLargestJSONImage(JSONArray images) throws JSONException {
            JSONObject image = getJSONImage("mega", images);
            if (!image.getString("#text").equals("")) {
                return image;
            }

            image = getJSONImage("extralarge", images);
            if (!image.getString("#text").equals("")) {
                return image;
            }

            image = getJSONImage("large", images);
            if (!image.getString("#text").equals("")) {
                return image;
            }

            image = getJSONImage("medium", images);
            if (!image.getString("#text").equals("")) {
                return image;
            }

            image = getJSONImage("small", images);
            if (!image.getString("#text").equals("")) {
                return image;
            }

            return null;
        }
    }

    public static class GeoNames {
        public static final String SERVICE_URL = "http://api.geonames.org/postalCodeLookupJSON";
        public static Uri getUri(String zip) {
            String url = SERVICE_URL + "?username=eyekabob&country=US&postalcode=" + zip;
            return Uri.parse(url);
        }
    }

    public static class Facebook {
        private static com.eyekabob.util.facebook.Facebook facebook;
        public static final String SERVICE_URL = "https://graph.facebook.com";
        public static final String APP_ID = "328167973871652";
        public static final String APP_SECRET = "6d2e4d3a73a78117e46d95540ce6d24e";
        public static final String APP_NAMESPACE = "eyekabob";

        public static com.eyekabob.util.facebook.Facebook getInstance() {
            if (facebook == null) {
                facebook = new com.eyekabob.util.facebook.Facebook(APP_ID);
            }
            return facebook;
        }
    }

    public static class Foursquare {
        public static final String CLIENT_ID = "WCRBSK0WI3HTK0OCLECBWKKJPNEJVATUOVZ0PHI0ONFUP145";
        public static final String SECRET = "0ASOSBCQZHRRYRXQCD1CWX3BLSXTNTFR50DYR5TODVE32FHY";
        public static final String ACCESS_TOKEN_URL = "https://foursquare.com/oauth2/access_token";
        public static final String AUTHORIZE_URL = "https://foursquare.com/oauth2/authorize";
        public static final String AUTHENTICATE_URL = "https://foursquare.com/oauth2/authenticate";
        public static final String SERVICE_URL = "https://api.foursquare.com/v2";
        public static final String CALLBACK_URL = "http://bcoffield.dyndns.org/eyekabob/foursquare";
        public static String ACCESS_TOKEN;

        public static final void authenticate(Context context, Class<?> callbackClass) {
            Intent oAuthIntent = new Intent(context, OAuthWebView.class);
            oAuthIntent.putExtra("callbackClass", callbackClass);
            oAuthIntent.putExtra("url", getAuthUrl());
            context.startActivity(oAuthIntent);
        }

        public static final String getAuthUrl() {
            return AUTHENTICATE_URL + "?client_id=" + CLIENT_ID + "&response_type=token" + "&redirect_uri=" + CALLBACK_URL;
        }

        public static final void setAccessToken(String accessToken) {
            ACCESS_TOKEN = accessToken;
        }

        /**
         * Do not encode params. This will encode the param values for you.
         * @param api
         * @param params
         * @return
         */
        public static final String getUri(String api, Map<String, String> params) {
            if (params == null) {
                params = new HashMap<String, String>();
            }

            if (!params.containsKey("limit")) {
                params.put("limit", "2");
            }

            if (!params.containsKey("v")) {
                // Date of implementation, YYYYMMDD.
                params.put("v", "20120415");
            }

            String url = SERVICE_URL + "/" + api;
            url += "?oauth_token=" + ACCESS_TOKEN;
            for (String param : params.keySet()) {
                url += "&" + param + "=" + URLEncoder.encode(params.get(param));
            }
            return url;
        }

        public static final void searchNearby(Context context) {
            if (ACCESS_TOKEN == null) {
                Log.d(EyekabobHelper.class.getName(), "Foursquare access token was null");
                authenticate(context, CheckinSearchList.class);
                return;
            }

            Log.d(EyekabobHelper.class.getName(), "Foursquare access token was defined. Starting checkin search activity.");
            Intent checkinSearchIntent = new Intent(context, CheckinSearchList.class);
            context.startActivity(checkinSearchIntent);
        }
    }

    /**
     * Eyekabob web service helper class
     */
    public static class WebService {
        private static final String SERVICE_URL = "http://bcoffield.dyndns.org:8080/eyekabob";
        public static Uri getURI(String api, String method, Map<String, String> params) {
            String url = SERVICE_URL + "?api=" + api;
            url += "&method=" + method;

            for (String key : params.keySet()) {
                String param = params.get(key);
                url += "&" + key + "=" + URLEncoder.encode(param);
            }

            return Uri.parse(url);
        }
    }

    public static class TicketMaster {
        private static final String SERVICE_URL = "http://www.ticketmaster.com/search";
        public static final Uri getURI(Map<String, String> params) {
            String url = SERVICE_URL + "?";
            for (String key : params.keySet()) {
                String value = params.get(key);
                url += key + "=" + value + "&";
            }
            return Uri.parse(url);
        }
    }

    public static class GoogleMaps {
        private static final String SERVICE_URL = "http://maps.googleapis.com/maps/api/geocode/json";
        public static final Uri getURI(Map<String, String> params) {
            String url = SERVICE_URL + "?";
            if (!params.containsKey("sensor")) {
                params.put("sensor", "true");
            }
            for (String key : params.keySet()) {
                url += "&" + key + "=" + params.get(key);
            }
            return Uri.parse(url);
        }
    }

    /**
     * Gets distance from current location to given lat/lon.
     * @param lat
     * @param lon
     * @param context
     * @return
     */
    public static long getDistance(double lat, double lon, Context context) {
        Location location = EyekabobHelper.getLocation(context);

        if (location == null) {
            return -1;
        }

        double currentLat = location.getLatitude();
        double currentLon = location.getLongitude();
        int R = 3959; // Earth radius in miles.
        double dLat = Math.toRadians(currentLat - lat);
        double dLon = Math.toRadians(currentLon - lon);
        lat = Math.toRadians(lat);
        currentLat = Math.toRadians(currentLat);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(lat) * Math.cos(currentLat);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return Math.round(R * c);
    }

    public static long getDistance(String lat, String lon, Context context) {
        return getDistance(Double.parseDouble(lat), Double.parseDouble(lon), context);
    }

    public static void launchEmail(Activity activity) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        String to[] = {"philj21@yahoo.com", "adam.sill01@gmail.com", "coffbr01@gmail.com"};
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Eyekabob Advertising");
        String label = activity.getResources().getString(R.string.write_email);
        activity.startActivity(Intent.createChooser(emailIntent, label));
    }

    public static URL getLargestImageURL(Map<String, URL> imageURLs) {
        if (imageURLs == null) {
            return null;
        }

        URL result = imageURLs.get("large");

        if (result != null) {
            return result;
        }

        result = imageURLs.get("medium");

        if (result != null) {
            return result;
        }

        return imageURLs.get("small");
    }

    public static Dialog createAboutDialog(Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.about_dialog);
        dialog.setTitle(R.string.about_eyekabob_caps);

        ImageView imageView = (ImageView)dialog.findViewById(R.id.aboutDialogImage);
        imageView.setImageResource(R.drawable.ic_launcher);

        String message = context.getResources().getString(R.string.eyekabob_version);
        String versionName = "";
        try {
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(context.getClass().getSimpleName(), e.getMessage());
        }

        message += versionName;
        message += "\n\n" + context.getResources().getString(R.string.about_eyekabob_app);
        message += "\n\n" + context.getResources().getString(R.string.about_last_fm);

        TextView textView = (TextView)dialog.findViewById(R.id.aboutDialogText);
        textView.setText(message);

        dialog.findViewById(R.id.aboutDialogButton).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                     dialog.dismiss();
                }
            }
        );

        return dialog;
    }
}
