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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CarControlActivity extends AppCompatActivity implements View.OnClickListener{

    private Button sendInstructButton;

    private Toolbar carControlToolbar;

    private static final String TAG = "CarControlActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_control);
        sendInstructButton = (Button) findViewById(R.id.send_instruct_button);
        carControlToolbar = (Toolbar) findViewById(R.id.car_control_toolbar);
        sendInstructButton.setOnClickListener(this);
        carControlToolbar.setTitle("");
        setSupportActionBar(carControlToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_instruct_button:
                String commandCode = "001";
                requestControl(commandCode);
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
     * 发送控制请求
     */
    private void requestControl(final String commandCode) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("commandCode", commandCode)
                            .build();
                    Request request = new Request.Builder()
                            .url("http://116.62.129.132:8080/OBD/controlCar")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.d(TAG, "responseData is " + responseData);
                    String result = null;
                    try {
                        JSONArray jsonArray = new JSONArray(responseData);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            result = jsonObject.getString("control");
                            Log.d(TAG, "result is " + result);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (result.equals("true")) {
                        CarControlActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(CarControlActivity.this, "指令发送成功",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        CarControlActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(CarControlActivity.this, "指令发送失败",
                                        Toast.LENGTH_SHORT).show();
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
