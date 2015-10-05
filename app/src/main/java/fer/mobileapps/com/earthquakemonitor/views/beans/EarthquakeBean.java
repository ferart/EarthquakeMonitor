package fer.mobileapps.com.earthquakemonitor.views.beans;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Fernando on 08/06/2015.
 * Bean tha will contain all necessary info of the earthquake.
 * implements two interfaces, parcelable is needed so the  bundles can be send in a single argument
 * Comparable is implemented so the Collection.sort() function
 */
public class EarthquakeBean implements Parcelable,Comparable {

    private double magnitude;  //mag of the earthquake
    private int color;         //color to be set accordingly of the magnitude
    private String place;      //place of the epicenter
    private double latitude;   //lat lon of the epicenter
    private double longitude;
    private long time;          //time in miliseconds of the earthquake
    private long generated;     //time in ms when the info was generated
    private double depth;       // depth of the earthquake

    public EarthquakeBean() {
    }

    /**
     * constructor with a parcel as argument, are read in the same order that are written
     *  writeToParcel method.
     * @param in
     */
    public EarthquakeBean(Parcel in){

        this.place=in.readString();
        this.magnitude=in.readDouble();
        this.latitude=in.readDouble();
        this.longitude=in.readDouble();
        this.time=in.readLong();
        this.generated=in.readLong();
        this.depth=in.readDouble();

    }



    public double getMagnitude() {
        return magnitude;
    }

    /**
     * sets the magnitud and the color to be display,
     * each color depends of the magnitude range
     * @param magnitude
     */
    public void setMagnitude(double magnitude) {
        this.magnitude = magnitude;

        double meanValue= (magnitude*100)/9.9; //mean value of a max magnitud, 100% if magnitud is 9.9
        //establishing the colors by this way, make them no a good indicator


        int red=0;  //=(int)(255* magnitude); // if mean value is 1, is darkred
        int green=0;//=(int)((255*(9.9-magnitude))/9.9); //substract the max
        int blue =0;//=0;

        if ( magnitude<=0.99){
            red=0;
            green=100;
            blue=0;
        }else if (magnitude>=1 && magnitude<=1.99){
            red=0;
            green=128;
            blue=0;
        }else if (magnitude>=2 && magnitude<=2.99){
            red=154;
            green=205;
            blue=50;

        }else if (magnitude>=3 && magnitude<=3.99){
            red=154;
            green=205;
            blue=50;

        }else if (magnitude>=4 && magnitude<=4.99){
            red=173;
            green=255;
            blue=47;
        }else if (magnitude>=5 && magnitude<=5.99){
            red=255;
            green=255;
            blue=0;
        }else if (magnitude>=6 && magnitude<=6.99){
            red=255;
            green=215;
            blue=0;
        }else if (magnitude>=7 && magnitude<=7.99){
            red=255;
            green=165;
            blue=0;
        }else if (magnitude>=8 && magnitude<=8.99){
            red=255;
            green=69;
            blue=0;
        }else if (magnitude>=9 && magnitude<=9.99){
            red=255;
            green=0;
            blue=0;
        }

        this.color= Color.rgb(red, green, blue);
        //this.color=android.graphics.Color.HSVToColor(new float[]{(float)((magnitude)/9.9)*120f,1f,1f});
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getGenerated() {
        return generated;
    }

    public void setGenerated(long generated) {
        this.generated = generated;
    }

    public double getDepth() {
        return depth;
    }

    public void setDepth(double depth) {
        this.depth = depth;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Writes each value into the parcel
     * @param parcel
     * @param i
     */

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(place);
        parcel.writeDouble(magnitude);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
        parcel.writeLong(time);
        parcel.writeLong(generated);
        parcel.writeDouble(depth);

    }

    /** Static field used to regenerate object, individually or as arrays */
    public static final Parcelable.Creator<EarthquakeBean> CREATOR = new Parcelable.Creator<EarthquakeBean>() {
        public EarthquakeBean createFromParcel(Parcel pc) {
            return new EarthquakeBean(pc);
        }
        public EarthquakeBean[] newArray(int size) {
            return new EarthquakeBean[size];
        }
    };

    //compareTo is override in order to rearrenge the earthquakes by they magnitude
    @Override
    public int compareTo(Object o) {

        if (o instanceof EarthquakeBean){
            if(((EarthquakeBean) o).magnitude<this.magnitude)
                return -1;
            if(((EarthquakeBean) o).magnitude==this.magnitude)
                return 0;
            if(((EarthquakeBean) o).magnitude>this.magnitude)
                return 1;
        }
       return 0;
    }
}
