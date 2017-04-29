package com.example.internetofvehicles;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements NavigationView
        .OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;

    private LinearLayout mLinearLayout;

    private NavigationView mNavigationView;

    private AlertDialog.Builder alertDialog;

    private Toolbar mainToolbar;

    private boolean isDrawer = false;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //securityCheck();
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
        mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        mainToolbar.setTitle("");
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
                intent = new Intent(this, CarParaActivity.class);
                startActivity(intent);
                break;
            case R.id.vehicle_handle:
                intent = new Intent(this, CarControlActivity.class);
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

    @Override
    public void onBackPressed() {
        alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("");
        alertDialog.setMessage("您要退出程序吗？");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.super.onBackPressed();
                    }
                });
        alertDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        alertDialog.show();
    }

    /**
     * 安全检测
     */
    private void securityCheck() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://116.62.129.132:8080/OBD/securityCheck")
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.d(TAG, "responseData is " + responseData);
                    String security = "true";
                    try {
                        JSONArray jsonArray = new JSONArray(responseData);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            security = jsonObject.getString("security");
                            Log.d(TAG, "security is " + security);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (security.equals("true")) {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "安全检测通过",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                alertDialog = new AlertDialog.Builder(MainActivity.this);
                                alertDialog.setTitle("安全警告");
                                alertDialog.setMessage("您的车辆并不安全，建议立即检查！");
                                alertDialog.setCancelable(false);
                                alertDialog.setPositiveButton("确定",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog,
                                                                int which) {
                                                finish();
                                            }
                                        });
                                alertDialog.setNegativeButton("取消",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog,
                                                                int which) {
                                                finish();
                                            }
                                        });
                                alertDialog.show();
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
