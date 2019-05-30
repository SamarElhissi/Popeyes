package popeyes.orderingonline;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebView;

public class MenuTab extends Fragment{
    View rootView;
    private  boolean flag = false;WebView myWebView;
    AppBarLayout abl;
    CoordinatorLayout coor;
    ViewPager viewPager;
    public MenuTab() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (flag == false) {
            // Initialise your layout here
            rootView = inflater.inflate(R.layout.menu_layout, container, false);
            myWebView = (WebView) rootView.findViewById(R.id.webview);
            Intent intent = getActivity().getIntent();
            String url = intent.getStringExtra("url");

            abl = (AppBarLayout) getActivity().findViewById(R.id.appbar);
            coor = (CoordinatorLayout) getActivity().findViewById(R.id.coordLayout);

            ViewTreeObserver viewTreeObserver = abl.getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        abl.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        int xxx = abl.getMeasuredHeight();
                        int yyy = coor.getMeasuredHeight();
                        int height = yyy-xxx;

                        myWebView.getLayoutParams().height=(int)(height);
                        myWebView.requestLayout();
                    }
                });
            }

            myWebView.getSettings().setJavaScriptEnabled(true);
            myWebView.getSettings().setLoadWithOverviewMode(true);
            myWebView.getSettings().setUseWideViewPort(true);
            myWebView.setWebViewClient(new ourViewClient());

            myWebView.loadUrl(url);
            flag = true;

            String flag2 = intent.getStringExtra("flag");
            if(flag2 != null) {
                if (flag2.equals("1")) {
                    viewPager = (ViewPager) getActivity().findViewById(
                            R.id.viewpager);
                    viewPager.setCurrentItem(1, true);
                }
            }
        }
        // Inflate the layout for this fragment
        return rootView;

    }

}
