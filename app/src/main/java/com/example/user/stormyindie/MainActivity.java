package com.example.user.stormyindie;

import android.app.DownloadManager;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    private CurrentWeather currentWeather=new CurrentWeather();
    double latitude = 36.7783;
    double longitude = 119.4179;
    @BindView(R.id.timeLabel) TextView mTimeLabel;
    @BindView(R.id.temperatureLabel) TextView mTemperatureLabel;
    @BindView(R.id.humidityValue) TextView mHumidityValue;
    @BindView(R.id.precipValue) TextView mPrecipValue;
    @BindView(R.id.summaryLabel) TextView mSummaryLabel;
    @BindView(R.id.iconImageView) ImageView mIconImageView;
    @BindView(R.id.RefreshImageView) ImageView mRefreshImageView;
    @BindView(R.id.progressBar) ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mProgressBar.setVisibility(View.INVISIBLE);

        mRefreshImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getForecast(latitude,longitude);

            }
        });


        getForecast(latitude,longitude);

Log.v(TAG, "main ui is running");

    }

    private void getForecast(double latitude,double longitude) {
        String apikey = "1852d37b5472a696a3eb03092a032747";

        String forecastUrl = "https://api.darksky.net/forecast/" + apikey + "/" + latitude + "," + longitude;

        if (isAvailable()) {
           runOnUiThread(new Runnable() {
               @Override
               public void run() {
                   toggleRefresh();

               }
           });
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder().url(forecastUrl).build();

            Call call = client.newCall(request);

            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();

                        }
                    }); alertdialog();

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();

                        }
                    });
                    try {
                        String jsonData =  response.body().string();
                        Log.v(TAG,jsonData);
                        if (response.isSuccessful()) {
                            currentWeather =getCurrentWeather(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDisplay();
                                }
                            });

                        } else {
                            alertdialog();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "exception occured", e);
                    }catch (JSONException e){
                        Log.e(TAG, "exception occured", e);

                    }

                }
            });}
        else{
            Toast.makeText(this,getString(R.string.network_unavailable_message),Toast.LENGTH_LONG).show();
        }
    }

    private void toggleRefresh() {
        if(mProgressBar.getVisibility() == View.INVISIBLE) {
            mProgressBar.setVisibility(View.VISIBLE);
            mRefreshImageView.setVisibility(View.INVISIBLE);
        }else {
            mProgressBar.setVisibility(View.INVISIBLE);
            mRefreshImageView.setVisibility(View.VISIBLE);
        }
    }

    private void updateDisplay() {
        mTemperatureLabel.setText(currentWeather.getTemperature() + "");
        mTimeLabel.setText("At" +currentWeather.getformattedTime()+ "it will be");
        mHumidityValue.setText(currentWeather.getHumidity()+"");
        mPrecipValue.setText(currentWeather.getPrecipChance()+ "%");
        mSummaryLabel.setText(currentWeather.getSummary());
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), currentWeather.getIconId(), null);
         mIconImageView.setImageDrawable(drawable);
    }


    private boolean isAvailable() {
        boolean isavailable = false;
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            isavailable = true;
        }
        return isavailable;
    }


    private void alertdialog() {
        AlertDialog dialog = new AlertDialog();
        dialog.show(getFragmentManager(), "error dialog");
    }
    private CurrentWeather getCurrentWeather(String jsonData ) throws JSONException{
        JSONObject forecast= new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        Log.i(TAG,"timezone"+timezone);
        JSONObject currently = forecast.getJSONObject("currently");
        CurrentWeather currentweather= new CurrentWeather();
        currentweather.setHumidity(currently.getDouble("humidity"));
        currentweather.setTime(currently.getLong("time"));
        currentweather.setIcon(currently.getString("icon"));
        currentweather.setPrecipChance(currently.getDouble("precipProbablity"));
        currentweather.setSummary(currently.getString("summary"));
        currentweather.setTemperature(currently.getDouble("temperature"));
        currentweather.setTimezone(timezone);
        return currentweather;

    }
}
