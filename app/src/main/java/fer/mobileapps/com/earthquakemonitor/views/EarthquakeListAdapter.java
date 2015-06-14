package fer.mobileapps.com.earthquakemonitor.views;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import fer.mobileapps.com.earthquakemonitor.R;
import fer.mobileapps.com.earthquakemonitor.views.beans.EarthquakeBean;

/**
 * Adapter use for creating the custom row view for each earthquake
 * Created by Fernando on 08/06/2015.
 */
public class EarthquakeListAdapter  extends ArrayAdapter<EarthquakeBean> {


    public EarthquakeListAdapter(Context context, List<EarthquakeBean> earthquakes) {
        super(context, R.layout.earthquake_row,R.id.earthquake_place,earthquakes);
    }

    /**
     * retrieves a custom view
     *
     */

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);

        if (v != convertView && v != null) {
            PedidoViewHolder holder = new PedidoViewHolder();  //creates a new representation of the view

            TextView place = (TextView) v.findViewById(R.id.earthquake_place); // earthquake place
            TextView magnitude = (TextView) v.findViewById(R.id.earthquake_magnitude); // earthquake magnitude
            LinearLayout status = (LinearLayout) v.findViewById(R.id.earthquake_status); // earthquake status

            holder.place = place;
            holder.magnitude = magnitude;
            holder.status = status;
            v.setTag(holder); //set the placeholder into the view
        }

        PedidoViewHolder holder = (PedidoViewHolder) v.getTag();
        String place = getItem(position).getPlace();
        double magnitude = getItem(position).getMagnitude();
        int indicator = getItem(position).getColor();

        //set the text into each text view and the color for the indicator
        holder.place.setText(getContext().getString(R.string.summary_place)+" "+place);
        holder.magnitude.setText(getContext().getString(R.string.summary_magnitude)+" "+String.valueOf(magnitude));
        holder.status.setBackgroundColor(indicator);

        return v;
    }

    /**
     * class that represent the view of the container
     */
    private class PedidoViewHolder {
        public TextView place;
        public TextView magnitude;
        public LinearLayout status;
    }

}
