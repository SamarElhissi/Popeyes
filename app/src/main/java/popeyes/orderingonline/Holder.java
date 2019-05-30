package popeyes.orderingonline;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Holder extends RecyclerView.ViewHolder {

        TextView name, location;
        ImageView logo,cover;
        Button order;
        CardView container;
        public Holder(final View v) {
                super(v);
                name=(TextView) v.findViewById(R.id.name);
                logo=(ImageView) v.findViewById(R.id.logoImage);
                location=(TextView) v.findViewById(R.id.location);
                cover=(ImageView) v.findViewById(R.id.coverImage2);
                order = (Button) v.findViewById(R.id.orderNow);
                container=(CardView) v.findViewById(R.id.card_view);
        }
}