package fer.mobileapps.com.earthquakemonitor.views.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import fer.mobileapps.com.earthquakemonitor.views.beans.EarthquakeBean;

/**
 * Created by Fernando on 18/06/2015.
 */
public class EarthquakeImpDAO implements EarthquakeDAO {

    private Context context;
    private SQLiteDatabase sqLiteDatabase;
    private DataBaseDAO database;

    public EarthquakeImpDAO(Context context) {
        this.context = context;
        database=new DataBaseDAO(context);
    }

    @Override
    public void open(){
        sqLiteDatabase=database.getWritableDatabase();
    }

    @Override
    public void close(){
        database.close();
    }

    @Override
    public boolean insertEarthquake(EarthquakeBean earthquake) {
        sqLiteDatabase=database.getWritableDatabase();

        ContentValues values=new ContentValues();

        values.put("place",earthquake.getPlace());
        values.put("magnitude",earthquake.getMagnitude());
        values.put("latitude",earthquake.getLatitude());
        values.put("longitude",earthquake.getLongitude());

        sqLiteDatabase.insert("eartquake", null, values);



        return false;
    }

    @Override
    public List<EarthquakeBean> getListEarthquakes() {
            sqLiteDatabase=database.getReadableDatabase();

            List<EarthquakeBean> earthquakes=new ArrayList<EarthquakeBean>();
            String[] columns={"id","place","magnitude","latitude","longitude"};

        Cursor cursor=sqLiteDatabase.query("eartquake", columns, null, null, null, null, "magnitude");

        if (cursor!=null) {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                 EarthquakeBean earthquakeBean=new EarthquakeBean();
                earthquakeBean.setPlace(cursor.getString(1));
                earthquakeBean.setMagnitude(cursor.getFloat(2));
                earthquakeBean.setLatitude(cursor.getFloat(3));
                earthquakeBean.setLongitude(cursor.getFloat(4));

                earthquakes.add(earthquakeBean);

                cursor.moveToNext();
            }

            cursor.close();

        }


        return earthquakes;
    }
}
