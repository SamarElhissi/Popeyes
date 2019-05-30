package popeyes.orderingonline;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class InfoTab extends Fragment implements OnMapReadyCallback {
    MapView mapView;
    GoogleMap map;
    TextView locationTxt, infoTxt, hoursTxt, contactTxt;
    ImageView facebook, twitter , linkin,youtube,google,instog;
    double lat=0, log=0;String[] social_array;
    public InfoTab() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.info_layout, container, false);

        mapView = (MapView) v.findViewById(R.id.mapview);
        locationTxt = (TextView) v.findViewById(R.id.locationTxt);
        infoTxt = (TextView) v.findViewById(R.id.infoTxt);
        hoursTxt = (TextView) v.findViewById(R.id.hoursTxt);
        contactTxt = (TextView) v.findViewById(R.id.contactTxt);
        facebook = (ImageView) v.findViewById(R.id.facebook);
        twitter = (ImageView) v.findViewById(R.id.twitter);
        linkin = (ImageView) v.findViewById(R.id.linkin);
        youtube = (ImageView) v.findViewById(R.id.youtube);
        google = (ImageView) v.findViewById(R.id.google);
        instog = (ImageView) v.findViewById(R.id.instogram);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this); //this is important
        Intent intent = getActivity().getIntent();
        lat = Double.parseDouble(intent.getStringExtra("x_coor"));
        log =  Double.parseDouble(intent.getStringExtra("y_coor"));
        String location = intent.getStringExtra("location");
        String info = intent.getStringExtra("info");
        String workHours = intent.getStringExtra("workHours");
        locationTxt.setText(location);
        infoTxt.setText(info);
        String[] array = workHours.split(",", -1);
        hoursTxt.setText(""); contactTxt.setText("");
        for(int i =0;i<array.length;i++){
            if(i == array.length-1) hoursTxt.append(array[i]+"");
            else
                hoursTxt.append(array[i]+"\n");
        }

        String contact_list = intent.getStringExtra("contacts");
        String[] con_array = contact_list.split("%", -1);

        if(!con_array[0].equals(""))contactTxt.append("Phone: "+con_array[0]+"\n");
        if(!con_array[1].equals(""))contactTxt.append("Fax: "+con_array[1]+"\n");
        if(!con_array[2].equals(""))contactTxt.append("Mobile: "+con_array[2]+"\n");
        if(!con_array[3].equals(""))contactTxt.append("Email: "+con_array[3]);

        String social_list = intent.getStringExtra("social");
        social_array = social_list.split(",", -1);
        if(social_array[0].equals("false")){
            facebook.setVisibility(View.GONE);
        }else{
            facebook.setVisibility(View.VISIBLE);
            facebook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(social_array[0]));
                    startActivity(browserIntent);
                }
            });
        }
        if(social_array[1].equals("false")){
            twitter.setVisibility(View.GONE);
        }else{
            twitter.setVisibility(View.VISIBLE);
            twitter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(social_array[1]));
                    startActivity(browserIntent);
                }
            });
        }
        if(social_array[2].equals("false")){
            google.setVisibility(View.GONE);
        }else{

            google.setVisibility(View.VISIBLE);

            google.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(social_array[2]));
                    startActivity(browserIntent);
                }
            });
        }
        if(social_array[3].equals("false")){
            youtube.setVisibility(View.GONE);
        }else{
            youtube.setVisibility(View.VISIBLE);
            youtube.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(social_array[3]));
                    startActivity(browserIntent);
                }
            });
        }
        if(social_array[4].equals("false")){
            linkin.setVisibility(View.GONE);
        }else{
            linkin.setVisibility(View.VISIBLE);
            linkin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(social_array[4]));
                    startActivity(browserIntent);
                }
            });
        }
        if(social_array[5].equals("false")){
            instog.setVisibility(View.GONE);
        }else{
            instog.setVisibility(View.VISIBLE);
            instog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(social_array[5]));
                    startActivity(browserIntent);
                }
            });
        }

        return v;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);
        map.addMarker(new MarkerOptions().position(new LatLng(lat, log)));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, log), 10));
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

}
