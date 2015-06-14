package fer.mobileapps.com.earthquakemonitor.views;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;

import fer.mobileapps.com.earthquakemonitor.R;
import fer.mobileapps.com.earthquakemonitor.views.utils.ImageLoader;


/**
 * Created by Fernando on 08/06/2015.
 * Adapter for the listview used in the drawer.
 * Display a custom view of a image and a label
 */

public class OpcionAdapter extends BaseAdapter {

    // atributos publicos
    public static final String OPC_LABEL = "label";
    public static final String OPC_IMAGE = "image";

    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater = null;
    public ImageLoader imageLoader;

    public OpcionAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data = d;
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(activity.getApplicationContext());
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null)

            vi = inflater.inflate(R.layout.opc_row_2, null);

        // asignamos el valor mínimo de la altura del componente
        vi.setMinimumHeight(100);
        //the views to display
        TextView label = (TextView) vi.findViewById(R.id.opc_title); // label_opc
        ImageView thumb_image = (ImageView) vi.findViewById(R.id.opc_image); // imagen_opc

        HashMap<String, String> opcion = new HashMap<String, String>();
        opcion = data.get(position);

        // set the values
        label.setText(opcion.get(OPC_LABEL));
        if (opcion.get(OPC_IMAGE) != null)
            imageLoader.DisplayImage(
                    Integer.parseInt(opcion.get(OPC_IMAGE)),
                    thumb_image);
        else
            imageLoader.DisplayImage(opcion.get(OPC_IMAGE),
                    thumb_image);
        return vi;

    }

    public void setData(ArrayList<HashMap<String, String>> data) {
        this.data = data;
    }
}
