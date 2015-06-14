package fer.mobileapps.com.earthquakemonitor.views;

import android.app.Activity;
//import android.app.Fragment;
//import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;


import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Date;

import fer.mobileapps.com.earthquakemonitor.R;
import fer.mobileapps.com.earthquakemonitor.views.beans.EarthquakeBean;

/**
 * Created by frochapc on 9/06/15.
 * Fragment that will contains the map and the detail info of each eartquake
 */
public class EarthQuakeDetailFragment extends Fragment implements GoogleMap.OnInfoWindowClickListener,LocationListener {

    //unique instance of the fragment
    private static EarthQuakeDetailFragment earthQuakeDetailFragment;


    private GoogleMap nMap = null;           //Map that will display the marker of the epicenter
    private SupportMapFragment nMapFragment; //SupportMapFragment instead of mapFragment, in order to add compatibility
    private View view;                       //view of the fragment to be created
    private String summary;                  //Detail info of the earthquake
    private LatLng point;                    //Latitude and longitude that the marker will get from
    private float hueColor;                  // HUE color of the marker, the value in rgb is get from the EarthquakeBean.color

    private TextView detailInfo;             //view tha will display the summary string


    /**
     * Singleton design, this will prevent of creating multiple instances of the fragment.
     * is usefull if the fragment is going to be used several times
     * @return
     */
    public static synchronized EarthQuakeDetailFragment getInstance(){
        if (earthQuakeDetailFragment==null)
            return new EarthQuakeDetailFragment();

        return earthQuakeDetailFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //the earthquake selected is pass to the fragment, trough the bundle argument
        Bundle bundle=this.getArguments();
        EarthquakeBean earthquakeBean=bundle.getParcelable("earthquake");

        //once the bean is obtained from the bundle, the summary String is created in html format
        //so we can add some style (bold and lines breaks)
        if (earthquakeBean!=null) {
            summary = "<b>" + getActivity().getString(R.string.summary_magnitude) + "</b> " + earthquakeBean.getMagnitude() + "<br>"
                    + "<b>" + getActivity().getString(R.string.summary_place) + "</b> " + earthquakeBean.getPlace() + "<br>"
                    + (new Date(earthquakeBean.getTime()).toLocaleString()) + "<br>"
                    + "<b>" + getActivity().getString(R.string.summary_depth) + "</b> " + earthquakeBean.getDepth() + "<br>";


            //the LATLON point instance is created with the lat, long of the earthquake
            point=new LatLng(earthquakeBean.getLatitude(),earthquakeBean.getLongitude());

            //transform the rgb to hsv, use for color the marker in the map
            float []hsv=new float[3];
            Color.colorToHSV(earthquakeBean.getColor(),hsv);
            hueColor=hsv[0];
        }else
            summary="<b>"+getActivity().getString(R.string.summary_no_info)+"</b>";



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //to create the map each time the fragment view is created
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null){
                parent.removeView(view); //remove the view, so a new view can be created
                nMap=null;
            }
        }

        try {
            //we get the detail layout xml and inflate it
            view = inflater.inflate(R.layout.earthquake_detail_fragment, container, false);
        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }

        // inicializamos el mapa en caso de ser necesario
        setUpView(savedInstanceState);

        return view;
    }

    /**
     * Set up all the configurations, the map, and the views instances are obtained
     */
    private void setUpView(Bundle savedInstanceState) {
        // se valida que no sea nulo para confirmar que no se ha instanciado el mapa
        if (nMap == null) {
            //beacuse the mapfragmet is a child of this fragment, we get the child fragment manager
            nMapFragment = (SupportMapFragment)  getChildFragmentManager().findFragmentById(R.id.map_detail_fragment);
            nMap = nMapFragment.getMap();
            // inicializamos el mapa
            nMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            // se valida que se obtiene el mapa correctamente
            nMap.setMyLocationEnabled(true);
            nMap.setOnMarkerClickListener(onMarkerClick);

            //the map is center to the epicenter
            if (point!=null) {
                nMap.addMarker(new MarkerOptions().position(point)
                        .icon(BitmapDescriptorFactory.defaultMarker(hueColor)));;
                //move the camera, and then animate the transition
                nMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 8));
                nMap.animateCamera(CameraUpdateFactory.zoomTo(8), 2000, null);

            }
        }
        //the views are obtained
        detailInfo=(TextView) view.findViewById(R.id.text_detail);

        if (summary!= null && !summary.isEmpty())
            detailInfo.setText(Html.fromHtml(summary));



    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    //center the camera to the marker tha has been clicked
    GoogleMap.OnMarkerClickListener onMarkerClick= new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            nMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition())); // center the camero to the marker

            return true; // return true to disable the info window
        }
    };

    //on destroy view, we remove the fragment so it can be reacreated again
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        Fragment fragment = (getChildFragmentManager().findFragmentById(R.id.map_detail_fragment));
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.remove(fragment);
        ft.commit();
    }

    /**
     * when the configurations changes between portrait and landscape
     * a new size of the map is set
     * @param myConfig
     */
    @Override
    public void onConfigurationChanged(Configuration myConfig) {
        super.onConfigurationChanged(myConfig);
        int orient = getResources().getConfiguration().orientation;
        switch(orient) {
            case Configuration.ORIENTATION_LANDSCAPE: //the map size should be smaller
                changeSizeMap(250);
                break;
            case Configuration.ORIENTATION_PORTRAIT:  //the maps size is bigger
                changeSizeMap(320);
                break;
            default:
                changeSizeMap(320);
        }
    }


    /**
     * change the size of the map, get pixels density of the device and transform the desire dp acoordingly
     * of the pixel density
     * @param dp size to change
     */
    private void changeSizeMap(int dp){
        if (nMapFragment!=null){



            //obtiene los pixeles a setear en la altura, basado en 200 dp originales, no es lo mismo 200dp en cada resolución de los moviles
            final float scale = getActivity().getResources().getDisplayMetrics().density;
            int pixels = (int) (dp * scale + 0.5f);

            ViewGroup.LayoutParams params= nMapFragment.getView().getLayoutParams();
            params.height= pixels;
            nMapFragment.getView().setLayoutParams(params);
            nMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 8));
            nMap.animateCamera(CameraUpdateFactory.zoomTo(8), 2000, null);


        }
    }


}
