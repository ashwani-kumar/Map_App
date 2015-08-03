package map.test.eroad.com.app;


import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Ashwani on 25-07-2015.
 */

public class DirectionParser {

    public static final String ROUTES = "routes";
    public static final String LEGS = "legs";
    public static final String DISTANCE = "distance";
    public static final String TEXT = "text";
    public static final String DURATION = "duration";
    public static final String STEPS = "steps";
    public static final String POLYLINE = "polyline";
    public static final String POINTS = "points";
    public static final String LAT = "lat";
    public static final String LNG = "lng";

    /**
     * Receives a JSONObject and returns a list of lists containing latitude and longitude
     */
    public List<List<HashMap<String, String>>> parseJSONData(JSONObject jObject) {

        List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();
        JSONArray routesArray = null;
        JSONArray legsArray = null;
        JSONArray stepsArray = null;
        JSONObject distanceObject = null;
        JSONObject durationObject = null;

        try {

            routesArray = jObject.getJSONArray(ROUTES);

            /** all routes */
            for (int i = 0; i < routesArray.length(); i++) {
                legsArray = ((JSONObject) routesArray.get(i)).getJSONArray(LEGS);

                List<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();

                /** all legs */
                for (int j = 0; j < legsArray.length(); j++) {

                    /** Getting distance from the json data */
                    distanceObject = ((JSONObject) legsArray.get(j)).getJSONObject(DISTANCE);
                    HashMap<String, String> hmDistance = new HashMap<String, String>();
                    hmDistance.put(DISTANCE, distanceObject.getString(TEXT));

                    /** Getting duration from the json data */
                    durationObject = ((JSONObject) legsArray.get(j)).getJSONObject(DURATION);
                    HashMap<String, String> hmDuration = new HashMap<String, String>();
                    hmDuration.put("duration", durationObject.getString(TEXT));

                    /** Adding distance object to the path */
                    path.add(hmDistance);

                    /** Adding duration object to the path */
                    path.add(hmDuration);

                    stepsArray = ((JSONObject) legsArray.get(j)).getJSONArray(STEPS);

                    /** Traversing all steps */
                    for (int k = 0; k < stepsArray.length(); k++) {
                        String polyline = "";
                        polyline = (String) ((JSONObject) ((JSONObject) stepsArray.get(k)).get(POLYLINE)).get(POINTS);
                        List<LatLng> list = decodePolyData(polyline);

                        /** Traversing all points */
                        for (int l = 0; l < list.size(); l++) {
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put(LAT, Double.toString(list.get(l).latitude));
                            hm.put(LNG, Double.toString(list.get(l).longitude));
                            path.add(hm);
                        }
                    }
                }
                routes.add(path);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }
        return routes;
    }

    private List<LatLng> decodePolyData(String encoded) {

        List<LatLng> polyList = new ArrayList<LatLng>();
        int index = 0;
        int len = encoded.length();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            polyList.add(p);
        }
        return polyList;
    }
}
