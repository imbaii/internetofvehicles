package com.example.internetofvehicles;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.MyLocationStyle;

public class NavigationActivity extends AppCompatActivity implements View.OnClickListener {

    private MapView mMapView;

    private AMap aMap;

    private MyLocationStyle myLocationStyle;

    private UiSettings mUiSettings;

    private FloatingActionButton mFab;

    private MenuItem mStandardMap;

    private MenuItem mSatelliteMap;

    private MenuItem mNightMap;

    private MenuItem mNavigationMap;

    private Toolbar navigationToolbar;

    private boolean isTrafficMap = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        navigationToolbar = (Toolbar) findViewById(R.id.navigation_toolbar);
        aMap = mMapView.getMap();
        navigationToolbar.setTitle("");
        setSupportActionBar(navigationToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back);
        }
        mFab.setOnClickListener(this);
        mapInit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                if (isTrafficMap) {
                    aMap.setTrafficEnabled(false);
                    isTrafficMap = false;
                } else {
                    aMap.setTrafficEnabled(true);
                    isTrafficMap = true;
                }
                break;
            default:
                break;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        mStandardMap = menu.findItem(R.id.standard_map);
        mSatelliteMap = menu.findItem(R.id.satellite_map);
        mNightMap = menu.findItem(R.id.night_map);
        mNavigationMap = menu.findItem(R.id.navigation_map);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.standard_map:
                aMap.setMapType(AMap.MAP_TYPE_NORMAL);
                mStandardMap.setChecked(true);
                mSatelliteMap.setChecked(false);
                mNightMap.setChecked(false);
                mNavigationMap.setChecked(false);
                break;
            case R.id.satellite_map:
                aMap.setMapType(AMap.MAP_TYPE_SATELLITE);
                mStandardMap.setChecked(false);
                mSatelliteMap.setChecked(true);
                mNightMap.setChecked(false);
                mNavigationMap.setChecked(false);
                break;
            case R.id.night_map:
                aMap.setMapType(AMap.MAP_TYPE_NIGHT);
                mStandardMap.setChecked(false);
                mSatelliteMap.setChecked(false);
                mNightMap.setChecked(true);
                mNavigationMap.setChecked(false);
                break;
            case R.id.navigation_map:
                aMap.setMapType(AMap.MAP_TYPE_NAVI);
                mStandardMap.setChecked(false);
                mSatelliteMap.setChecked(false);
                mNightMap.setChecked(false);
                mNavigationMap.setChecked(true);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    private void mapInit() {
        myLocationStyle = new MyLocationStyle();
        mUiSettings = aMap.getUiSettings();
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
        myLocationStyle.interval(2000);
        myLocationStyle.strokeColor(0);
        myLocationStyle.radiusFillColor(0);
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.point);
        myLocationStyle.myLocationIcon(bitmap);
        mUiSettings.setMyLocationButtonEnabled(true);
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setMyLocationEnabled(true);
        aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
    }
}
