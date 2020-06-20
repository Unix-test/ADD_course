package com.appsnipp.modernlogin;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.appsnipp.modernlogin.fragmentatiions.Explore_activity;
import com.appsnipp.modernlogin.fragmentatiions.Notification_activity;
import com.appsnipp.modernlogin.fragmentatiions.Profile_activity;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class Home extends AppCompatActivity {

    CollapsingToolbarLayout Coltoolbar;
    TabHost tabHost;
    private Toolbar toolbar;
    private AppBarLayout Appbar;
    private TabLayout tabLayout;
    private TextView title;
    private ImageView logo;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.home_activities );

        toolbar = findViewById( R.id.toolbar2 );
        setSupportActionBar( toolbar );

        ViewPager viewPager = findViewById( R.id.view_pager );
        tabLayout = findViewById( R.id.tabs );

        Appbar = findViewById( R.id.appbar );
        Coltoolbar = findViewById( R.id.ctolbar );

        logo = findViewById( R.id.imageView );

        Explore_activity explore_activity = new Explore_activity();
        Notification_activity notification_activity = new Notification_activity();
        Profile_activity profile_activity = new Profile_activity();

        ViewPagerAdapter viewpagerAdapger = new ViewPagerAdapter( getSupportFragmentManager(), 0 );
        viewpagerAdapger.adFragment( explore_activity, "" );
        viewpagerAdapger.adFragment( notification_activity, "" );
        viewpagerAdapger.adFragment( profile_activity, "" );

        viewPager.setAdapter( viewpagerAdapger );
        tabLayout.setupWithViewPager( viewPager );

        Home.this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Home.this.getWindow().setStatusBarColor( 0x000000 );
        }

        tabLayout.addOnTabSelectedListener( new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 1 || (tab.getPosition() == 2)) {
                    toolbar.setVisibility( View.GONE );
                    logo.setVisibility( View.GONE );
                } else {
                    toolbar.setVisibility( View.VISIBLE );
                    logo.setVisibility( View.VISIBLE );
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        } );
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onResume() {
        super.onResume();
        Objects.requireNonNull( tabLayout.getTabAt( 0 ) ).setIcon( R.drawable.ic_newsfeeds );
        Objects.requireNonNull( tabLayout.getTabAt( 1 ) ).setIcon( R.drawable.ic_notifications );
        Objects.requireNonNull( tabLayout.getTabAt( 2 ) ).setIcon( R.drawable.ic_profile );

        Appbar.setOutlineProvider( null );

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

