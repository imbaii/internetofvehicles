package com.example.internetofvehicles;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CarParaActivity extends AppCompatActivity implements View.OnClickListener{

    private Button getCarParaButton;

    private TextView paraText;

    private Toolbar carParaToolbar;

    private static final String TAG = "CarParaActivity";

    private String result = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_para);
        getCarParaButton = (Button) findViewById(R.id.get_car_Para_button);
        paraText = (TextView) findViewById(R.id.para_text);
        carParaToolbar = (Toolbar) findViewById(R.id.car_para_toolbar);
        carParaToolbar.setTitle("");
        setSupportActionBar(carParaToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back);
        }
        getCarParaButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.get_car_Para_button:
                getCarPara();
                break;
            default:
                break;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 获取车辆参数
     */
    private void getCarPara() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://116.62.129.132:8080/OBD/getCarPara")
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.d(TAG, "responseData is " + responseData);
                    String latitude = null;
                    String speed = null;
                    String longitude = null;
                    String pressure = null;
                    String rpm = null;
                    String temperature = null;
                    try {
                        JSONArray jsonArray = new JSONArray(responseData);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            latitude = jsonObject.getString("纬度");
                            speed = jsonObject.getString("车速");
                            longitude = jsonObject.getString("经度");
                            pressure = jsonObject.getString("进气管绝对压力");
                            rpm = jsonObject.getString("发动机转速");
                            temperature = jsonObject.getString("发动机冷却液温度");
                            result = "纬度：" + latitude + '\n'
                                    + "车速：" + speed + '\n'
                                    + "经度：" + longitude + '\n'
                                    + "进气管绝对压力：" + pressure + '\n'
                                    + "发动机转速：" + rpm + '\n'
                                    + "发动机冷却液温度：" + temperature;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (result != null) {
                        CarParaActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                paraText.setText(result);
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
