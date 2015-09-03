package com.kollateral.kristian.csgostats;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kollateral.kristian.csgostats.SteamRestClient;

import org.apache.http.Header;
import org.json.*;
import com.loopj.android.http.*;


public class MainActivity extends Activity {

    private static final String BASE_URL = "http://api.steampowered.com/ISteamUser/ResolveVanityURL/v0001/?";
    private static final String API_KEY = "D388A5A23CED65E14DBA9A0E3922971E";

    Button   mButton;
    EditText mEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mEdit = (EditText)findViewById(R.id.steamuser);
        mButton = (Button) findViewById(R.id.sign_button);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(mEdit.getText().length() != 0) {
                    RequestParams params = new RequestParams();
                    params.put("key", API_KEY);
                    params.put("vanityurl", mEdit.getText());


                    Log.e("request params", params.toString());
                    Log.e("Push", "Sign button clicked!");

                    try {
                        getPublicSteamID(params);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Please fill in your steam id.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void getPublicSteamID(RequestParams params) throws JSONException {
        SteamRestClient.get(BASE_URL, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response)
            {


                try {
                    Integer responseValue = response.getJSONObject("response").getInt("success");

                    if(responseValue == 42)
                    {
                        Toast.makeText(getApplicationContext(), "No such user with the given steam id.", Toast.LENGTH_SHORT).show();

                    }else
                    {
                        // We have found the user, move to next activity and make a new lookup
                        String steamid64 = response.getJSONObject("response").getString("steamid");
                        Intent goToNextActivity = new Intent(getApplicationContext(), StatsActivity.class);
                        goToNextActivity.putExtra("steamid64", steamid64);

                        startActivity(goToNextActivity);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("ERROR", "JSONEXCEPTION");
                }


                // If the response is JSONObject instead of expected JSONArray
                Log.e("SUCCESS", "success");
                Log.e("RESULT", response.toString());
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
