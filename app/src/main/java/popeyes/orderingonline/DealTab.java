package popeyes.orderingonline;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RelativeLayout;


public class DealTab extends Fragment{
    Button btn;ViewPager viewPager;RelativeLayout rl;AppBarLayout abl; int screen_height;
    CoordinatorLayout coor;
    public DealTab() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.deal_layout, container, false);
        WebView myWebView = (WebView) rootView.findViewById(R.id.deal_webview);
        btn = (Button) rootView.findViewById(R.id.orderNowDeal);
        rl = (RelativeLayout) rootView.findViewById(R.id.webviewRelative);
        abl = (AppBarLayout) getActivity().findViewById(R.id.appbar);
        coor = (CoordinatorLayout) getActivity().findViewById(R.id.coordLayout);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        screen_height = outMetrics.heightPixels;
     //   int screen_width = outMetrics.widthPixels;
   //     Display display=getActivity().getWindowManager().getDefaultDisplay();

//        DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
//        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
//        float x = dpHeight - 190;
//        int y = dpToPx(61);
//        int xx = rl.getMeasuredHeight();
//        int g = abl.getMeasuredHeight();
       // float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
     //   final int height=display.getHeight();

        ViewTreeObserver viewTreeObserver = abl.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    abl.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    int xxx = abl.getMeasuredHeight();
                    int yyy = coor.getMeasuredHeight();
                    int height = yyy-xxx-dpToPx(61);

                    rl.getLayoutParams().height=(int)(height);
                    rl.requestLayout();
                }
            });
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager = (ViewPager) getActivity().findViewById(
                        R.id.viewpager);
                viewPager.setCurrentItem(0, true);

                // Toast.makeText(getActivity(),"fff" +height, Toast.LENGTH_LONG).show();
            }
        });
        Intent intent = getActivity().getIntent();
        String url = intent.getStringExtra("dealurl");

        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setLoadWithOverviewMode(true);
        myWebView.getSettings().setUseWideViewPort(true);
        myWebView.setWebViewClient(new ourViewClient());

        myWebView.loadUrl(url);

        return rootView;
    }
    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }
}
