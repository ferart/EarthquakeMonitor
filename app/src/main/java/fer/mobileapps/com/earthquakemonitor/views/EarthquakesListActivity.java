package fer.mobileapps.com.earthquakemonitor.views;

import android.app.AlertDialog;
//import android.app.Fragment;
//import android.app.FragmentManager;
//import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;


import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import fer.mobileapps.com.earthquakemonitor.R;
import fer.mobileapps.com.earthquakemonitor.views.beans.EarthquakeBean;
import fer.mobileapps.com.earthquakemonitor.views.model.EarthquakeWebService;

/**
 * Main activity that displays the list view
 * that contains each earthquake retrieved, also functions as the container for the fragment
 * detail.
 */
public class EarthquakesListActivity extends ActionBarActivity {


    //views
    public static TextView earthquakeSummary;
    public static TextView timeSummary;

    // variables for the menu drawer
    private DrawerLayout mDrawerLayout;         //drawer layout that contains a Listview
    private ListView mDrawerList;               // uses the OpcionAdapter
    private ActionBarDrawerToggle mDrawerToggle; //toggle for display th drawer
    public static OpcionAdapter opc_adapter;
    public static ArrayList<HashMap<String, String>> opcionList;


    EarthquakeListAdapter earthquakesListAdapter; //adapter for the earthquakes list
    ListView earthquakesList;                     //earthquake list

    EarthquakeWebService earthquakeService;       //AsyncTask use for call the earthquakes feed service

    // This is needed in order to manage the fragments.
    public static Stack<String> mFragmentStack;  //stack that keeps control of the fragment display
    private FragmentManager fragmentManager;     //fragmentmanager use for transactions






    public EarthquakesListActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earthquakes_list);

        //init all configurations, listeners and start earthquake async task
        init();


    }

    /**
     * init all configurations
     */

    private void init(){

        //display a button use for display the drawer in our case
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        //gets each view
        earthquakeSummary=(TextView) findViewById(R.id.earthquake_info_summary);
        timeSummary=(TextView) findViewById(R.id.time_summary);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_menu_drawer);
        earthquakesList=(ListView) findViewById(R.id.earthquakes_listView);

        fragmentManager = getSupportFragmentManager();
        // allocate memory for fragments stack
        mFragmentStack = new Stack<String>();


        // Drawer init
        //  a new list of hashmaps that will contain the option about me
        opcionList = new ArrayList<HashMap<String, String>>();


        HashMap<String, String> opc_about = new HashMap<String, String>();
        //the option about me is added
        opc_about.put(OpcionAdapter.OPC_LABEL, getString(R.string.menu_about));
        opc_about.put(OpcionAdapter.OPC_IMAGE, Integer.toString(R.drawable.importance_b));
        // agregamos el HashMap a la Lista
        opcionList.add(opc_about);

        // the data is set to the opcionAdapter
        opc_adapter = new OpcionAdapter(this, opcionList);
        //set the optionAdapter to the list of the drawer
        mDrawerList.setAdapter(opc_adapter);
        mDrawerList.setOnItemClickListener(mOpcionClickListener);
        earthquakesList.setOnItemClickListener(onClickRow);  //a listener for each click into the eartquakes list

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);



        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer_white,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
//                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                supportInvalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
//                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                supportInvalidateOptionsMenu();

            }
        };

        //set the drawer toggle to the drawer layout
        mDrawerLayout.setDrawerListener(mDrawerToggle);




        //start the search of earthquakes
        earthquakeService=new EarthquakeWebService(mHandler,EarthquakesListActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            earthquakeService.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,(Void[]) null);
        else
            earthquakeService.execute((Void[]) null);


    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_earthquakes_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //when home button is selected
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        //noinspection SimplifiableIfStatement
        //when the refresh button of the menu is clicked, start a new search of earthquakes
        if (id == R.id.action_settings) {
            earthquakeService=new EarthquakeWebService(mHandler,EarthquakesListActivity.this);

            //execute the asynctask in different ways deppending of the android version
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
                earthquakeService.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,(Void[]) null);
            else
                earthquakeService.execute((Void[]) null);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // el listener para on-click para todos las opciones del menu
    private AdapterView.OnItemClickListener mOpcionClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> av, View v, int position, long id) {


            switch (position) {
                case 0: // about me
                    mDrawerLayout.closeDrawer(mDrawerList);
                    aboutDialog();  //display the custom alert dialog

                    break;
            }
        }
    };


    /**
     *Handler used to notify when the services info has already been loaded
     */
    private transient final Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case EarthquakeWebService.EARTHQUAKES_COMPLETION: //data has finish transfer

                    switch (msg.arg1) {
                        case EarthquakeWebService.EARTHQUAKES_SUCCES: //is complete with positive result

                            Bundle bundle=msg.getData();
                            ArrayList<EarthquakeBean> earthquakes= bundle.getParcelableArrayList("earthquakes");

                            //the earthquakes are rearrenge according to magnitude
                            Collections.sort(earthquakes);

                            //set the adapter to the list, so the earthquakes are displayed
                            earthquakesListAdapter=new EarthquakeListAdapter(getApplicationContext(),earthquakes);
                            earthquakesList.setAdapter(earthquakesListAdapter);

                            //sets the overrall data
                            earthquakeSummary.setText(getString(R.string.summary_there) +" "+ earthquakes.size() + " "+getString(R.string.summary_documented));
                            timeSummary.setText(getString(R.string.summary_since) +" " + new Date(earthquakes.get(0).getGenerated()).toLocaleString()); //time the info was generated

                            break;

                        case EarthquakeWebService.EARTHQUAKES_NO_DATA: //there was no results
                            //info couldn't be retrieved, or there is not info at all
                            earthquakeSummary.setText(getString(R.string.summary_no_info));
                            timeSummary.setText(getString(R.string.summary_since) +" " + new Date().toLocaleString()); //time the info was generated

                            break;

                    }

                    break;
            }

        }
    };


    /**
     * on item click of a row
     */
    ListView.OnItemClickListener onClickRow= new ListView.OnItemClickListener(){


        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            //display the detail fragment
            EarthquakeBean earthquake=(EarthquakeBean)earthquakesList.getItemAtPosition(i);
            displayEarthquakeDetailFragment(earthquake);

        }
    };

    /**
     * pass the earthquake bean to de detail fragment
     */
    private void displayEarthquakeDetailFragment(EarthquakeBean earthquake){

        //hide the list, and place the detail fragment instead
        earthquakesList.setVisibility(View.GONE);

        //send a bundle to onCreate of the fragment detail
        Fragment earthQuakeDetailFragment = EarthQuakeDetailFragment.getInstance();
        Bundle bundle= new Bundle();
        bundle.putParcelable("earthquake",earthquake);
        earthQuakeDetailFragment.setArguments(bundle);

        String tag = "EarthQuakeDetailFragment";  //tag of the fragment
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.add(R.id.content, earthQuakeDetailFragment, tag);
        transaction.addToBackStack(tag);
        mFragmentStack.add(tag);

        transaction.commit();
    }


    /**
     * when back pressed is done.
     * if is into the fragment, remove it
     */

    @Override
    public void onBackPressed() {
        // from the stack we can get the latest fragment
        if (!mFragmentStack.empty()) {
            Fragment fragment = fragmentManager.findFragmentByTag(mFragmentStack.peek());


            if (fragment instanceof EarthQuakeDetailFragment) {
                // regresamos a la vista original
                mDrawerLayout.closeDrawer(mDrawerList);
                earthquakesList.setVisibility(View.VISIBLE);

                try {
                    backToActivity();
                } catch (EmptyStackException e) {
                }
            }
        }
      else{//there is no fragment at the stack, so we are in main Activity
            mDrawerLayout.closeDrawer(mDrawerList);
        }

    }

    /**
     * Removes the current fragment at the top of the stack
     */
    private void backToActivity(){
        // remove the current fragment from the stack.
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // get fragment that is to be shown (in our case fragment1).
        Fragment fragment = fragmentManager.findFragmentByTag(mFragmentStack.peek());
        transaction.detach(fragment);
        fragmentManager.popBackStack();
        transaction.commit();
        mFragmentStack.pop(); //remove from the stack
    }


    /**
     * About alert dialog to show info about me
     * Fernando Rocha Flores
     */
    public void aboutDialog() {
        PackageInfo pinfo;
        String versionName = "";
        try {

            // obtenemos la información de la versión de la App
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            if (pinfo != null)
                versionName = pinfo.versionName;
            String content = "\n " + getString(R.string.aboout);

            content = content + " \n\n " + "https://mx.linkedin.com/pub/fernando-rocha/b2/117/916"+ "\n";
            final SpannableString s = new SpannableString(content);
            Linkify.addLinks(s, Linkify.WEB_URLS);

            final TextView message = new TextView(this);
            message.setText(s);
            message.setMovementMethod(LinkMovementMethod.getInstance());
            message.setTextColor(Color.parseColor("#000000"));

            final AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .create();

            // Setting Dialog Title
            alertDialog.setTitle(getString(R.string.app_name));

            // Setting Dialog Message
            alertDialog.setView(message);

            // Setting alert dialog icon
            alertDialog.setIcon(R.drawable.android_me);

            // Setting OK Button
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                }
            });

            // Showing Alert Message
            alertDialog.show();

        } catch (Exception e) {

        }

    }

}
