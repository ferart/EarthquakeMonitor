package fer.mobileapps.com.earthquakemonitor.views.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import fer.mobileapps.com.earthquakemonitor.R;

/**
 * Created by Fernando on 18/06/2015.
 */
public class DataBaseDAO extends SQLiteOpenHelper {


    private Context context;

    private static final String DATA_BASE_NAME="earthquakes.db";
    private static final int DATA_BASE_VERSION=1;

    public DataBaseDAO(Context context){
        super(context,DATA_BASE_NAME,null,DATA_BASE_VERSION);

        this.context=context;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(context.getString(R.string.table_eartquake_bean));

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
