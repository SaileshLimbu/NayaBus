package np.com.nayabus.utils;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MapUtils {
    public static String getDistanceUrl(LatLng origin, LatLng dest, String mode) {
        String str_en_origin = "origins=" + origin.latitude + "," + origin.longitude;
        String str_en_dest = "destinations=" + dest.latitude + "," + dest.longitude;
        String travelMode = "mode=" + mode;
        String parameters = str_en_origin + "&" + str_en_dest + "&" + travelMode + "&key=AIzaSyD_MsMPNajIW4kv30zPWdGXfrKPmwkKkc4";
        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&" + parameters;
        return url;
    }
    public static String downloadUrl(String strUrl) {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } catch (Exception e) {
            Log.d("Error", e.toString());
        } finally {
            try {
                iStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            urlConnection.disconnect();
        }
        return data;
    }
}
