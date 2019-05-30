package popeyes.orderingonline;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;




public class CustomAdapter extends RecyclerView.Adapter<Holder> {

    ArrayList<ArrayList<String>> IdsList;
    Context context;
    ArrayList<String> NameList;
    ArrayList<String> LogoImages;
    ArrayList<String> LocationList;
    ArrayList<String> CoverImages;
    ArrayList<ArrayList<String> > infoList;
    ArrayList<String > contact;
    ArrayList<String > social;
    //private static LayoutInflater inflater = null;
    private int lastPosition = -1;

    public CustomAdapter(Context mainActivity, ArrayList<ArrayList<String>> Ids_List, ArrayList < String > Name_List, ArrayList < String > Logo_Images, ArrayList < String > Location_List, ArrayList < String > Cover_Images,ArrayList < ArrayList<String>  > info_List,   ArrayList<String> contactList,  ArrayList<String> socialList)
    {
        // TODO Auto-generated constructor stub
        IdsList = Ids_List;
        context = mainActivity;
        NameList = Name_List;
        LogoImages = Logo_Images;
        LocationList = Location_List;
        CoverImages = Cover_Images;
        contact=contactList;
        infoList = info_List;
        social= socialList;
        //  inflater = (LayoutInflater) context.
        //    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        Holder holder = new Holder(v);
        return holder;

    }

    @Override
    public void onBindViewHolder(final Holder holder, final int position) {

        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView

        holder.name.setText(NameList.get(position));
        holder.location.setText(LocationList.get(position));

        Picasso.with(context).load("http://globalfoodsystem.com/upload/images/"+ LogoImages.get(position)).resize(60,60).centerCrop().into(holder.logo);
        Picasso.with(context).load("http://globalfoodsystem.com/upload/images/"+ CoverImages.get(position)).fit().centerCrop().into(holder.cover);

        holder.order.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //holder.order.setBackgroundColor(Color.BLACK);
                Intent intent = new Intent(v.getContext(), ResturantTabs.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // intent.putExtra("url", "https://www.restaurantlogin.com/mobile/menu?company_uid=cd6801cb-200c-495d-9b31-66637e177162");//+IdsList.get(position));
                intent.putExtra("url", "https://www.restaurantlogin.com/mobile/menu?company_uid="+IdsList.get(position).get(0));
                intent.putExtra("dealurl", "http://globalfoodsystem.com/Deals/"+IdsList.get(position).get(1));

                intent.putExtra("location",LocationList.get(position));
                intent.putExtra("info",infoList.get(position).get(0));
                intent.putExtra("workHours",infoList.get(position).get(1));
                intent.putExtra("social",social.get(position));
                intent.putExtra("contacts",contact.get(position));
                intent.putExtra("x_coor",IdsList.get(position).get(2));
                intent.putExtra("y_coor",IdsList.get(position).get(3));
                v.getContext().startActivity(intent);
            }
        });

        setAnimation(holder.container, position);

    }

    @Override
    public int getItemCount() {
        //returns the number of elements the RecyclerView will display
        return IdsList.size();
    }

    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}