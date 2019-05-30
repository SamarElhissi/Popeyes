package popeyes.orderingonline;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainResturantList extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeView;

    private RecyclerView listView;
    private TextView errorText;
    private RecyclerView.Adapter adapter;

    private ArrayList<String> namesList = new ArrayList<String>();
    private ArrayList<String> logoImages = new ArrayList<String>();
    private ArrayList<ArrayList<String> > idsList = new ArrayList<ArrayList<String>>();
    private ArrayList<String> locationsList = new ArrayList<String>();
    private ArrayList<String> coverImages = new ArrayList<String>();
    private ArrayList<ArrayList<String> > infoList = new ArrayList<ArrayList<String> >();

    private ArrayList<String > contactList = new ArrayList< String>();
    private ArrayList<String > socialList = new ArrayList< String>();
    private RecyclerView.LayoutManager mLayoutManager;
    ProgressBar progressBar;
    private RelativeLayout layout, listLayout;
    boolean isLoading = false;boolean isStarting = true;boolean isRefresh = false;
    int limit = 0;
    double latitude=0;
    double longitude=0;
    private boolean isLoadMore = false;
    private static final int MY_PERMISSIONS_REQUEST = 0;

    // Resgistration Id from GCM
    private static final String PREF_GCM_REG_ID = "PREF_GCM_REG_ID";
    private SharedPreferences prefs;
    // Your project number and web server url. Please change below.
    private static final String GCM_SENDER_ID = "999992737868";
    private static final String WEB_SERVER_URL = "http://globalfoodsystem.com/Api/gcm/register_user";

    GoogleCloudMessaging gcm;
    private static final int ACTION_PLAY_SERVICES_DIALOG = 100;
    protected static final int MSG_REGISTER_WITH_GCM = 101;
    protected static final int MSG_REGISTER_WEB_SERVER = 102;
    protected static final int MSG_REGISTER_WEB_SERVER_SUCCESS = 103;
    protected static final int MSG_REGISTER_WEB_SERVER_FAILURE = 104;
    private String gcmRegId;

    //LoadMoreListView x;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resturant_list_layout);
        //  cont = getApplicationContext();
        swipeView = (SwipeRefreshLayout) findViewById(R.id.swipe_view);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        listView = (RecyclerView) findViewById(R.id.listView);

        layout = (RelativeLayout) findViewById(R.id.relativeL) ;
        errorText = (TextView) findViewById(R.id.errorText);
        swipeView.setOnRefreshListener(this);
        swipeView.setColorSchemeColors(Color.RED, Color.WHITE);
        swipeView.setDistanceToTriggerSync(20);// in dips
        swipeView.setSize(SwipeRefreshLayout.DEFAULT);// LARGE also can be used

        mLayoutManager = new LinearLayoutManager(this);
        //   LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        //  listView.setLayoutManager(layoutManager);
        listView.setLayoutManager(mLayoutManager);
        adapter = new CustomAdapter(MainResturantList.this, idsList,namesList, logoImages,locationsList, coverImages,infoList, contactList, socialList);
        // adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
        //  progressBar.getIndeterminateDrawable().setColorFilter(0xf08b19, android.graphics.PorterDuff.Mode.MULTIPLY);
        // footerView = ((LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.listfooter, null, false);
        // LoadMore button
        //    Button btnLoadMore = new Button(this);
        //   btnLoadMore.setText("Load More");

        // Adding Load More button to lisview at bottom
        // listView.addFooterView(btnLoadMore);

/////////////////////////////////////////////////////////////////

        getLocation();


        listView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                super.onScrolled(recyclerView, dx, dy);
                int pastVisiblesItems, visibleItemCount, totalItemCount;
                if (!isRefresh && !isLoading) {
                    if (dy > 0) //check for scroll down
                    {

                        if (recyclerView.getAdapter().getItemCount() != 0) {
                            LinearLayoutManager mLayoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
                            visibleItemCount = mLayoutManager.getChildCount();
                            totalItemCount = mLayoutManager.getItemCount();
                            pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                            if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                                limit = limit + 10;

                                new RetrieveFeedTask().execute();

                            }

//                        if(lastVisibleItemPosition==recyclerView.getAdapter().getItemCount() - 1){
//                            new loadMoreListView().execute();
//                        }
//                        if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1);
//                        //  new loadMoreListView().execute();
                        }
                    }
                }
            }

        });

        if (isGoogelPlayInstalled()) {
            gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
            // Read saved registration id from shared preferences.
            gcmRegId = getSharedPreferences().getString(PREF_GCM_REG_ID, "");

            if (TextUtils.isEmpty(gcmRegId)) {
                GCMhandler.sendEmptyMessage(MSG_REGISTER_WITH_GCM);
            }
            else{
                //   regIdView.setText(gcmRegId);
                //   Toast.makeText(getApplicationContext(), "Already registered with GCM", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
    Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {

//            listView.setRecycledViewPool(new RecyclerView.RecycledViewPool());

            swipeView.postDelayed(new Runnable() {

                @Override
                public void run() {

//					Toast.makeText(getApplicationContext(),
//							"city list refreshed", Toast.LENGTH_SHORT).show();
                    swipeView.setRefreshing(false);
                    if(!isLoading){
                        namesList.clear();idsList.clear();logoImages.clear();locationsList.clear();coverImages.clear();contactList.clear();
                        infoList.clear();socialList.clear();
//                        namesList=new ArrayList<String>();
//                        idsList=new ArrayList<ArrayList<String>>();
//                        logoImages=new ArrayList<String>();
//                        locationsList=new ArrayList<String>();
//                        coverImages=new ArrayList<String>();
//                        contactList=new ArrayList<String>();
//                        infoList=new ArrayList<ArrayList<String>>();
//                        socialList=new ArrayList<String>();
                        //  adapter = new CustomAdapter(MainResturantList.this, idsList,namesList, logoImages,locationsList, coverImages,infoList, contactList, socialList);
                        //  listView.setAdapter(adapter);
                        //  listView.requestLayout();
                        //   adapter.notifyDataSetChanged();
                        //   listView.requestLayout();
                        layout.setBackgroundResource(0);
                        limit = 0;
                        getLocation();

                        //   new RetrieveFeedTask().execute();
                    }
                    //  namesList.clear();idsList.clear();logoImages.clear();locationsList.clear();coverImages.clear();contactList.clear();
                    //      adapter = new CustomAdapter(MainActivity.this,idsList,namesList,logoImages,locationsList,coverImages, contactList);

                    //    adapter.notifyDataSetChanged();
                }
            }, 1000);


        };
    };


    @Override
    public void onRefresh() {
        isRefresh = true;

        swipeView.postDelayed(new Runnable() {

            @Override
            public void run() {
                swipeView.setRefreshing(true);
                handler.sendEmptyMessage(0);
            }
        }, 1000);
        listView.setVisibility(View.GONE);

    }

    public void getLocation(){
        if (Build.VERSION.SDK_INT >=23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                new RetrieveFeedTask().execute();


            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }

        }else {
            GPSTracker gps = new GPSTracker(this);
            if (gps.canGetLocation()) {

                latitude = gps.getLatitude();
                longitude = gps.getLongitude();

                new RetrieveFeedTask().execute();

                // \n is for new line
                //    Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            } else {
                // can't get location
                // GPS or Network is not enabled
                // Ask user to enable GPS/network in settings
                gps.showSettingsAlert();
            }
        }
    }

    private class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            //   progressBar.setVisibility(View.VISIBLE);
            //  progressBar = new ProgressBar(
            //    MainActivity.this);

            progressBar.setVisibility(View.VISIBLE);


            //   progressBar.setMessage("Please wait..");
            //    progressBar.setIndeterminate(true);
            //    progressBar.setCancelable(false);
            //    progressBar.show();
            super.onPreExecute();
            this.exception = null;
            isLoading = true;
        }

        protected String  doInBackground(Void... urls) {


            // Do some validation here
            //   String API_URL = "http://globalfoodsystem.com/Api/RestaurantsDirectory/Restaurants/?restaurant=1&latitude=42.9797824&longitude=-81.2443568&limit="+ limit;
            String API_URL = "http://globalfoodsystem.com/Api/RestaurantsDirectory/Restaurants/?restaurant=1&latitude="+
                    latitude+"&longitude="+longitude+"&limit="+ limit+"&registerationid="+gcmRegId;
            JSONObject object = null;
            InputStream inStream = null;
            HttpURLConnection urlConnection = null;
            ArrayList<String> cont = new ArrayList<>();
            try {


                URL url = new URL(API_URL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.connect();
                inStream = urlConnection.getInputStream();
                BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));

                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }


                return stringBuilder.toString();
            }catch (Exception e) {

                this.exception = e;


            } finally {
                if (inStream != null) {
                    try {
                        // this will close the bReader as well
                        inStream.close();
                    } catch (IOException ignored) {
                    }
                }
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }

        protected void onPostExecute(String  response) {
            if(response == null) {
                setErrorText("Connection Error!");
                layout.setBackgroundResource(0);
            }else {

                ArrayList<String> id = new ArrayList<>();
                ArrayList<String> info = new ArrayList<>();
                try{

                    JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
                    JSONArray resturants = object.getJSONArray("restaurants");

                    for(int i=0;i<resturants.length();i++){
                        id=new ArrayList<String>();info = new ArrayList<String>();
                        JSONObject jsonobject= (JSONObject) resturants.get(i);
                        id.add(jsonobject.optString("branch_orderlink"));
                        id.add(jsonobject.optString("branch_deal"));
                        id.add(jsonobject.optString("branch_x_coordinate"));
                        id.add(jsonobject.optString("branch_y_coordinate"));

                        namesList.add(jsonobject.optString("restaurant_name")+ "-" + jsonobject.optString("branch_name"));
                        logoImages.add(jsonobject.optString("restaurant_logo"));
                        locationsList.add(jsonobject.optString("province_name") + ", " + jsonobject.optString("branch_address")+ ", " + jsonobject.optString("city_name")+", "+jsonobject.optString("branch_postalcode"));
                        coverImages.add(jsonobject.optString("branch_image"));
                        info.add(jsonobject.optString("branch_text"));
                        info.add(jsonobject.optString("branch_workhours"));
                        socialList.add(jsonobject.optString("branch_facebook")+","+jsonobject.optString("branch_twitter")+","+
                                jsonobject.optString("branch_google")+","+jsonobject.optString("branch_youtube")+","+
                                jsonobject.optString("branch_linkedin")+","+jsonobject.optString("branch_instagram"));

                        contactList.add(jsonobject.optString("branch_phone")+"%"+jsonobject.optString("branch_fax")+"%"+
                                jsonobject.optString("branch_mobile")+"%"+jsonobject.optString("branch_email"));

                        idsList.add(id);
                        infoList.add(info);
                    }
                    if(resturants.length() == 0 && limit == 0){
                        //   setErrorText("Sorry, no restaurants are available near your location!");
                        layout.setBackgroundResource(R.drawable.error_page);


                    }else {
                        errorText.setVisibility(View.GONE);
                        //  adapter.notifyDataSetChanged();
                        layout.setBackgroundResource(0);
                        //   listView.requestLayout();
                    }

                    //  adapter = new CustomAdapter(MainActivity.this, idsList,namesList, logoImages,locationsList, coverImages, contactList);

                    //    listView.requestLayout();


                }catch(Exception e){
                    layout.setBackgroundResource(0);
                }

            }
            adapter.notifyDataSetChanged();

            //  listView.setAdapter(adapter);

            //  listView.requestLayout();
            //    progressBar.dismiss();
            progressBar.setVisibility(View.GONE);
            // Log.i("INFO", response.toString());
            isLoading= false;
            isStarting=false;
            isRefresh = false;

            listView.setVisibility(View.VISIBLE);
        }
    }

    void setErrorText(String msg){
        errorText.setText(msg);
        errorText.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    GPSTracker gps = new GPSTracker(this);
                    if (gps.canGetLocation()) {

                        latitude = gps.getLatitude();
                        longitude = gps.getLongitude();

                        new RetrieveFeedTask().execute();

                        // \n is for new line
                        //     Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                    } else {
                        // can't get location
                        // GPS or Network is not enabled
                        // Ask user to enable GPS/network in settings
                        gps.showSettingsAlert();
                    }

                } else {
                    new RetrieveFeedTask().execute();

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);

    }

    private boolean isGoogelPlayInstalled() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        ACTION_PLAY_SERVICES_DIALOG).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "Google Play Service is not installed",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;

    }

    private SharedPreferences getSharedPreferences() {
        if (prefs == null) {
            prefs = getApplicationContext().getSharedPreferences(
                    "AndroidSRCDemo", Context.MODE_PRIVATE);
        }
        return prefs;
    }

    public void saveInSharedPref(String result) {
        // TODO Auto-generated method stub
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(PREF_GCM_REG_ID, result);
        editor.commit();
    }

    Handler GCMhandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_REGISTER_WITH_GCM:
                    new GCMRegistrationTask().execute();
                    break;
                case MSG_REGISTER_WEB_SERVER:
                    new WebServerRegistrationTask().execute();
                    break;
                case MSG_REGISTER_WEB_SERVER_SUCCESS:
                    //   Toast.makeText(getApplicationContext(),
                    //         "registered with web server", Toast.LENGTH_LONG).show();
                    break;
                case MSG_REGISTER_WEB_SERVER_FAILURE:
                    Toast.makeText(getApplicationContext(),
                            "registration with web server failed",
                            Toast.LENGTH_LONG).show();
                    break;
            }
        };
    };

    private class GCMRegistrationTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub
            if (gcm == null && isGoogelPlayInstalled()) { gcm = GoogleCloudMessaging.getInstance(getApplicationContext());

            }
            try {
                gcmRegId = gcm.register(GCM_SENDER_ID);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return gcmRegId;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
//                Toast.makeText(getApplicationContext(), "registered with GCM",
//                        Toast.LENGTH_LONG).show();
                //   regIdView.setText(result);
                saveInSharedPref(result);
                GCMhandler.sendEmptyMessage(MSG_REGISTER_WEB_SERVER);
            }
        }

    }

    private class WebServerRegistrationTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            URL url = null;
            try {
                url = new URL(WEB_SERVER_URL);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                GCMhandler.sendEmptyMessage(MSG_REGISTER_WEB_SERVER_FAILURE);
            }
            Map<String, String> dataMap = new HashMap<String, String>();
            dataMap.put("regId", gcmRegId);
            dataMap.put("os", "android");
            dataMap.put("restaurant_id", "1");
            dataMap.put("x_coordinate", latitude+"");
            dataMap.put("y_coordinate", longitude+"");

            StringBuilder postBody = new StringBuilder();
            Iterator iterator = dataMap.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry param = (Map.Entry) iterator.next();
                postBody.append(param.getKey()).append('=')
                        .append(param.getValue());
                if (iterator.hasNext()) {
                    postBody.append('&');
                }
            }
            String body = postBody.toString();
            byte[] bytes = body.getBytes();

            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setFixedLengthStreamingMode(bytes.length);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded;charset=UTF-8");

                OutputStream out = conn.getOutputStream();
                out.write(bytes);
                out.close();

                int status = conn.getResponseCode();
                if (status == 200) {
                    // Request success
                    GCMhandler.sendEmptyMessage(MSG_REGISTER_WEB_SERVER_SUCCESS);
                } else {
                    InputStream error = conn.getErrorStream();
                    throw new IOException("Request failed with error code "
                            + status);
                }
            } catch (ProtocolException pe) {
                pe.printStackTrace();
                GCMhandler.sendEmptyMessage(MSG_REGISTER_WEB_SERVER_FAILURE);
            } catch (IOException io) {
                io.printStackTrace();
                GCMhandler.sendEmptyMessage(MSG_REGISTER_WEB_SERVER_FAILURE);
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }

            return null;
        }
    }
}


