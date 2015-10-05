package fer.mobileapps.com.earthquakemonitor.views.dao;

import java.util.List;

import fer.mobileapps.com.earthquakemonitor.views.beans.EarthquakeBean;

/**
 * Created by Fernando on 18/06/2015.
 */
public interface EarthquakeDAO {

    public void open();
    public void close();

    public boolean insertEarthquake(EarthquakeBean e);
    public List<EarthquakeBean> getListEarthquakes();
}
