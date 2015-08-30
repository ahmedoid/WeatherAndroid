package com.darfahad.weatherandroid;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.darfahad.weatherandroid.parser.JSONParser;
import com.github.pwittchen.weathericonview.WeatherIconView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Ahmed S
 * website : www.a7med.name
 * Email : ahmed.s.alotibi@gmail.com
 **/
public class MainActivity extends AppCompatActivity {
    String url = "";
    JSONParser jParser = new JSONParser();
    JSONObject jObject;
    // the reference to the views using ButterKnife library for android
    //http://jakewharton.github.io/butterknife/
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.temp)
    TextView temp;
    @Bind(R.id.description)
    TextView description;
    @Bind(R.id.clouds)
    TextView clouds;
    @Bind(R.id.humidity)
    TextView humidity;
    @Bind(R.id.speed)
    TextView speed;
    WeatherIconView icon;
    @Bind(R.id.search)
    AppCompatEditText search;
    @Bind(R.id.search_input)
    TextInputLayout searchInput;
    @Bind(R.id.progressbar)
    ProgressBar progressbar;
    @Bind(R.id.cardview)
    CardView cardview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        TextView temp = (TextView) findViewById(R.id.temp);
        // replace actionbar with toolbar
        setSupportActionBar(toolbar);
        // for change icon with weather and info
        icon = (WeatherIconView) findViewById(R.id.weather_icon);
        // add action 'enter' in edittext :\
        search.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            // on click enter go inside
                            if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                                //check if edittext is empty
                                if (search.getText().toString().equals("")) {
                                    //enable error before display a message
                                    searchInput.setErrorEnabled(true);
                                    // set message
                                    searchInput.setError("اكتب اسم المدينة");
                                } else {

                                    // hide error message
                                    searchInput.setErrorEnabled(false);
                                    // connect to url .
                                    new AsyncForAll().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "http://api.openweathermap.org/data/2.5/weather?q=" + search.getText().toString() + "&units=metric&lang=ar");
                                }
                                // hide keyboard after user click enter or done :)
                                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                in.hideSoftInputFromWindow(search
                                                .getApplicationWindowToken(),
                                        InputMethodManager.HIDE_NOT_ALWAYS);
                                return true;
                            }
                        }
                        return false;
                    }
                });

    }

    // for weather material icon
    public int icon(String icon) {
        // check url for get more info about weather  code http://openweathermap.org/weather-conditions
        if (icon.equals("01n")) {
            return R.string.wi_night_clear;
        } else if (icon.equals("01d")) {
            return R.string.wi_day_sunny;
        } else if (icon.equals("01d")) {
            return R.string.wi_day_sunny;
        } else if (icon.equals("02n")) {
            return R.string.wi_night_cloudy;
        } else if (icon.equals("03d")) {
            return R.string.wi_day_cloudy_gusts;
        } else if (icon.equals("03n")) {
            return R.string.wi_night_cloudy_gusts;
        } else if (icon.equals("04d")) {
            return R.string.wi_day_storm_showers;
        } else if (icon.equals("04n")) {
            return R.string.wi_night_storm_showers;
        } else if (icon.equals("09d")) {
            return R.string.wi_day_showers;
        } else if (icon.equals("09n")) {
            return R.string.wi_night_showers;
        } else if (icon.equals("10n")) {
            return R.string.wi_night_rain;
        } else if (icon.equals("10d")) {
            return R.string.wi_day_rain;
        } else if (icon.equals("11n")) {
            return R.string.wi_night_lightning;
        } else if (icon.equals("11d")) {
            return R.string.wi_day_lightning;
        } else if (icon.equals("13d")) {
            return R.string.wi_day_snow;
        } else if (icon.equals("13n")) {
            return R.string.wi_night_snow;
        } else if (icon.equals("50d")) {
            return R.string.wi_day_rain_mix;
        } else if (icon.equals("50n")) {
            return R.string.wi_night_rain_mix;
        } else {
            return R.string.wi_wind_north;
        }


    }


    //.... AsyncTask .........//
    public class AsyncForAll extends AsyncTask<String, Void, JSONObject> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // show loading progress
            progressbar.setVisibility(View.VISIBLE);
            // hide cardview before display weather information
            cardview.setVisibility(View.GONE);
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            // check if url contain space or hash.
            url = params[0].replaceAll(" ", "%20").replaceAll("#", "%23")
                    .replaceAll("\n", "%20").trim();
            System.out.println("------- Async Task Url  " + params[0].replaceAll(" ", "%20").replaceAll("#", "%23")
                    .replaceAll("\n", "%20").trim());
            // connect to url and return data
            jObject = jParser.getJSONFromUrl(url);


            return jObject;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            // hide progressbar after return information
            progressbar.setVisibility(View.GONE);
            // show cardview info for user
            cardview.setVisibility(View.VISIBLE);
            //check if user enter city not exist!
            if (jObject.toString().contains("Error: Not found city")) {
                Snackbar.make(toolbar, "تاكد من اسم المدينة", Snackbar.LENGTH_LONG)
                        .show();
            } else {

                try {
                    // get JsonArray for key weather
                    JSONArray  info = jObject.getJSONArray("weather");
                    // display text for user
                    description.setText(info.getJSONObject(0).getString("description"));
                    //..
                    JSONObject main = jObject.getJSONObject("main");
                    //..
                    Log.w("jObject.getJSONObject()", main.toString());
                    //..
                    temp.setText(String.valueOf(main.getDouble("temp")));
                    //..
                    humidity.setText(String.valueOf(main.getInt("humidity")));
                    //..
                    icon.setIconResource(getString(icon(info.getJSONObject(0).getString("icon"))));
                    //.
                    speed.setText(String.valueOf(jObject.getJSONObject("wind").getString("speed")));
                    //..
                    clouds.setText(String.valueOf(jObject.getJSONObject("clouds").getString("all")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

    }
}
