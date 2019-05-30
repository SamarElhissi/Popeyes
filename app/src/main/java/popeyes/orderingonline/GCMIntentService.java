package popeyes.orderingonline;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class GCMIntentService extends IntentService {

    public static final int NOTIFICATION_ID = 1000;
    NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    String branch_id="";
    String msg = "";
    String title = "";
    public GCMIntentService() {
        super(GCMIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        if (!extras.isEmpty()) {

            // read extras as sent from server
             branch_id = extras.getString("branch_id");
             msg = extras.getString("gcm.notification.body");
             title = extras.getString("gcm.notification.title");
            if (branch_id == null || branch_id == "") {
                sendNotification(msg, title );
            }
            else{
                sendNotification(msg, title, branch_id );
            }

        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String msg ,String title) {
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(this, MainActivity.class), 0);


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                getApplicationContext()).setSmallIcon(R.drawable.popeyeslogo)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg);
        mBuilder.setAutoCancel(true);
        mBuilder.setContentIntent(contentIntent);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(alarmSound);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
    private void sendNotification(String msg ,String title, String branch_id) {
       new RetrieveFeedTask().execute();
    }


    private class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {

            super.onPreExecute();
            this.exception = null;

        }

        protected String  doInBackground(Void... urls) {


            // Do some validation here
            //   String API_URL = "http://globalfoodsystem.com/Api/RestaurantsDirectory/Restaurants/?restaurant=1&latitude=42.9797824&longitude=-81.2443568&limit="+ limit;
            String API_URL = "http://globalfoodsystem.com/Api/RestaurantsDirectory/GetBrancheInfo/"+branch_id;
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

            }else {

                try{

                    JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
                    JSONArray resturants = object.getJSONArray("branche");

                    for(int i=0;i<resturants.length();i++){

                        JSONObject jsonobject= (JSONObject) resturants.get(i);


                        Intent intent = new Intent(getApplicationContext(), ResturantTabs.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        // intent.putExtra("url", "https://www.restaurantlogin.com/mobile/menu?company_uid=cd6801cb-200c-495d-9b31-66637e177162");//+IdsList.get(position));
                        intent.putExtra("url", "https://www.restaurantlogin.com/mobile/menu?company_uid="+jsonobject.optString("branch_orderlink"));
                        intent.putExtra("dealurl", "http://globalfoodsystem.com/Deals/"+jsonobject.optString("branch_deal"));

                        intent.putExtra("location",jsonobject.optString("province_name") + ", " + jsonobject.optString("branch_address")+ ", " + jsonobject.optString("city_name")+", "+jsonobject.optString("branch_postalcode"));
                        intent.putExtra("info",jsonobject.optString("branch_text"));
                        intent.putExtra("workHours",jsonobject.optString("branch_workhours"));
                        intent.putExtra("social",jsonobject.optString("branch_facebook")+","+jsonobject.optString("branch_twitter")+","+
                                jsonobject.optString("branch_google")+","+jsonobject.optString("branch_youtube")+","+
                                jsonobject.optString("branch_linkedin")+","+jsonobject.optString("branch_instagram"));
                        intent.putExtra("contacts",jsonobject.optString("branch_phone")+"%"+jsonobject.optString("branch_fax")+"%"+
                                jsonobject.optString("branch_mobile")+"%"+jsonobject.optString("branch_email"));
                        intent.putExtra("x_coor",jsonobject.optString("branch_x_coordinate"));
                        intent.putExtra("y_coor",jsonobject.optString("branch_y_coordinate"));
                        intent.putExtra("flag","1");

                        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);


                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                                getApplicationContext()).setSmallIcon(R.drawable.popeyeslogo)
                                .setContentTitle(title)
                                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                                .setContentText(msg);
                        mBuilder.setAutoCancel(true);
                        mBuilder.setContentIntent(contentIntent);

                        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        mBuilder.setSound(alarmSound);
                        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
                    }

                }catch(Exception e){

                }

            }

        }
    }

}
