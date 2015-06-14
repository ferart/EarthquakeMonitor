package fer.mobileapps.com.earthquakemonitor.views.model;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

import fer.mobileapps.com.earthquakemonitor.R;
import fer.mobileapps.com.earthquakemonitor.views.beans.EarthquakeBean;

/**
 * Created by frochapc on 9/06/15.
 * AsyncTask class that calls the eartquakes feed service
 */
public class EarthquakeWebService extends AsyncTask<Void,Void,ArrayList<EarthquakeBean>> {

    public static final int EARTHQUAKES_SUCCES = 1;
    public static final int EARTHQUAKES_NO_DATA = 2;
    public static final int EARTHQUAKES_COMPLETION = 1;

    //handler to pass a bundle when is done doing in background
    private Handler mHandler;
    private Context context;


    // componente para el progress dialog
    public ProgressDialog progDailog;


    public EarthquakeWebService(Handler mHandler,Context context) {
        this.mHandler = mHandler;
        this.context=context;
    }

    /**
     * REST call to the service
     *
     * @return
     */
    public ArrayList<EarthquakeBean> getDataFromWebService(){

        ArrayList<EarthquakeBean> earthquakes=new ArrayList<EarthquakeBean>();

        //url to connect
        String urlWebService = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson";


        try {
            URL url= new URL(urlWebService);
            //the connection to the url is open and we get the inputstream
            InputStream inputStreamWebService= url.openConnection().getInputStream();

            String geoJsonUnformat =convertStreamToString(inputStreamWebService);
            //Tranform the string into a json object
            final JSONObject geoJsonObject = new JSONObject(geoJsonUnformat);
            JSONArray arrayFeatures=geoJsonObject.getJSONArray("features");


            //gets the values to fill the EartquakeBean, these values are going to be use into the list
            //of earthquakes also into the detail fragment
            for (int i=0;i<arrayFeatures.length();i++){
                JSONObject feature=arrayFeatures.getJSONObject(i);
                JSONObject geometry=feature.getJSONObject("geometry");
                JSONObject properties=feature.getJSONObject("properties");  //the principal info the eartquake
                JSONArray puntos=geometry.getJSONArray("coordinates");      //contains the lat lon and depth info

                EarthquakeBean earthquakeBean=new EarthquakeBean();

                earthquakeBean.setPlace(properties.getString("place"));     //place of the epicenter
                earthquakeBean.setMagnitude(properties.getDouble("mag"));   //magnitude of the earthquake
                earthquakeBean.setLongitude((Double) puntos.get(0));        //lat, lon and depth of the earthquake
                earthquakeBean.setLatitude((Double) puntos.get(1));
                earthquakeBean.setDepth(puntos.getDouble(2));
                earthquakeBean.setTime(properties.getLong("time"));

                earthquakeBean.setGenerated(geoJsonObject.getJSONObject("metadata").getLong("generated")); //time in ms when the info was generated

                //the earthquake is added to the list
                earthquakes.add(earthquakeBean);

            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return earthquakes;


    }



    /**
     * Convert an inputstream to a string.
     *
     * Code taken from stackoverflow
     *
     * @param input inputstream to convert.
     * @return a String of the inputstream.
     */

    private  String convertStreamToString(final InputStream input) {
        if(input != null) {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            final StringBuilder sBuf = new StringBuilder();

            String line = null;
            try {
                while ((line = reader.readLine()) != null) {
                    sBuf.append(line);
                }
            } catch (IOException e) {
                Log.e("Routing Error", e.getMessage());
            } finally {
                try {
                    input.close();
                } catch (IOException e) {
                    Log.e("Routing Error", e.getMessage());
                }
            }
            return sBuf.toString();
        }else{
            return "";
        }
    }

    //displays a Progress Dialog showing the task is being executed
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (progDailog == null || !progDailog.isShowing()){
            // iniciamos la progress
            progDailog = new ProgressDialog(context);
            progDailog.setMessage(context.getString(R.string.progess_message));
            progDailog.setIndeterminate(true);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(false);
            progDailog.show();
        }
    }

    //creates a new process
    @Override
    protected ArrayList<EarthquakeBean> doInBackground(Void... voids) {
        //start searching earthquakes
        return getDataFromWebService();

    }

    /**
     * Close the progress dialog and pass the result info to the handler's message
     * @param earthquakes
     */
    @Override
    protected void onPostExecute(ArrayList<EarthquakeBean> earthquakes) {
        super.onPostExecute(earthquakes);

        if(progDailog != null && progDailog.isShowing()){
            try {
                progDailog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //send the list of earthquakes if any
        if (earthquakes!=null && !earthquakes.isEmpty()){
            Message mensaje=mHandler.obtainMessage();
            Bundle b = new Bundle();
            //beacuse the Earthquake bean implemented parcelable interface, we can send the entire bean
            b.putParcelableArrayList("earthquakes",earthquakes);

            mensaje.what=EARTHQUAKES_COMPLETION;
            mensaje.arg1=EARTHQUAKES_SUCCES;  //there was a result
            mensaje.setData(b);
            mHandler.sendMessage(mensaje);
        }else{ //no earthquake found
            Message mensaje=mHandler.obtainMessage();
            mensaje.what=EARTHQUAKES_COMPLETION;
            mensaje.arg1=EARTHQUAKES_NO_DATA; //there wasn't earthquakes
            mHandler.sendMessage(mensaje);

        }



    }
}
