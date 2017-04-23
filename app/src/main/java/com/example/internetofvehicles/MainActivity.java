package com.example.internetofvehicles;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity implements NavigationView
        .OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;

    private LinearLayout mLinearLayout;

    private NavigationView mNavigationView;

    private boolean isDrawer = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mLinearLayout = (LinearLayout) findViewById(R.id.main_layout);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                isDrawer=true;
                WindowManager manager = (WindowManager) getSystemService(
                        Context.WINDOW_SERVICE);
                Display display = manager.getDefaultDisplay();
                mLinearLayout.layout(mNavigationView.getRight(), 0,
                        mNavigationView.getRight() + display.getWidth(), display.getHeight());
            }
            @Override
            public void onDrawerOpened(View drawerView) {
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                isDrawer=false;
            }
            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
        Toolbar mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.home);
        }
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        mDrawerLayout.closeDrawers();
        Intent intent;
        switch (item.getItemId()) {
            case R.id.dynamic_parameter:
                intent = new Intent(this, DynamicParameterActivity.class);
                startActivity(intent);
                break;
            case R.id.vehicle_handle:
                intent = new Intent(this, VehicleHandleActivity.class);
                startActivity(intent);
                break;
            case R.id.navigation:
                intent = new Intent(this, NavigationActivity.class);
                startActivity(intent);
                break;
            case R.id.safety_effect:
                intent = new Intent(this, SafetyEffectActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
                break;
        }
        return true;
    }
}
