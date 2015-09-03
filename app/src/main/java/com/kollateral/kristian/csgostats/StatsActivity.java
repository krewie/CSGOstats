package com.kollateral.kristian.csgostats;

import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StatsActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    JSONObject playerinfo = null;
    JSONObject playerstats = null;

    private static final String API_KEY = "D388A5A23CED65E14DBA9A0E3922971E";
    //user info
    //http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=D388A5A23CED65E14DBA9A0E3922971E&steamids=76561197960435530

    private static final String INFO_BASE_URL = "http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?";

    //user stats
    private static final String STATS_BASE_URL = "http://api.steampowered.com/ISteamUserStats/GetUserStatsForGame/v0002/?appid=730&";

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_stats);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));


        String steamid64 = getIntent().getExtras().getString("steamid64");

        Log.e("USERID", steamid64);

        RequestParams params = new RequestParams();
        params.add("key", API_KEY);
        params.add("steamids", steamid64);

        updatePlayerInfo(INFO_BASE_URL, params);

        Log.e("URL", INFO_BASE_URL+params.toString());

        params = new RequestParams();
        params.add("key", API_KEY);
        params.add("steamid", steamid64);

       updatePlayerStats(STATS_BASE_URL, params);

        Log.e("URL", STATS_BASE_URL+params.toString());

        if(playerinfo != null)
            Log.e("PLAYERINFO", "SUCCESS!");
        else
            Log.e("PLAYERINFO", "FAILURE!");

        if(playerstats != null)
            Log.e("PLAYERSTATS", "SUCCESS!");
        else
            Log.e("PLAYERSTATS", "FAILURE!");

    }

    public void updatePlayerInfo(String URL, RequestParams params) {
        SteamRestClient.get(URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //TextView profile_name = (TextView)findViewById(R.id.profile_name);
                ImageView profile_av = (ImageView)findViewById(R.id.profile_av);
                /*try {
                    //profile_name.setText(response.getJSONObject("response").getJSONArray("players").getJSONObject(0).getString("personaname"));

                    //profile picture

                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
            }
        });
    }

    public void updatePlayerStats(String URL, RequestParams params) {
        SteamRestClient.get(URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                // Fields which will be updated once information is receieved by the restful API.

                TextView kills, kd, total_deaths,total_time_played,
                        total_wins, total_mvp, total_rescued_hostages;

                kills = (TextView)findViewById(R.id.total_kills);
                kd = (TextView)findViewById(R.id.total_kd);
                total_deaths = (TextView)findViewById(R.id.total_deaths);
                total_time_played = (TextView)findViewById(R.id.total_time_played);
                total_wins = (TextView)findViewById(R.id.total_wins);
                total_mvp = (TextView)findViewById(R.id.total_mvp);
                total_rescued_hostages = (TextView)findViewById(R.id.total_rescued_hostages);

                String response_kills, response_deaths, response_time_played,
                        response_total_wins, response_total_mvp, response_total_rescued_hostages;
                double total_kd;

                response_kills = getJsonValue(response, "total_kills");
                response_deaths = getJsonValue(response, "total_deaths");
                response_time_played = getJsonValue(response, "total_time_played");
                response_total_wins = getJsonValue(response, "total_wins");
                response_total_mvp = getJsonValue(response, "total_mvps");
                response_total_rescued_hostages = getJsonValue(response, "total_rescued_hostages");

               // Log.e("INVALID INT", response_kills);

                total_kd = Integer.parseInt(response_kills) / Integer.parseInt(response_deaths);

                kills.setText(response_kills);
                kd.setText(Double.toString(total_kd));
                total_deaths.setText(response_deaths);
                total_time_played.setText(response_time_played);
                total_wins.setText(response_total_wins);
                total_mvp.setText(response_total_mvp);
                total_rescued_hostages.setText(response_total_rescued_hostages);

                ArrayList<Entry> entries = new ArrayList<>();
                entries.add(new Entry(Integer.parseInt(response_deaths), 0));
                entries.add(new Entry(Integer.parseInt(response_kills), 1));

                PieDataSet dataset = new PieDataSet(entries,"");

                dataset.setDrawValues(false);
                int colors[] = {Color.parseColor("#EF233C"),Color.parseColor("#1B98E0")};

                dataset.setColors(colors);
                ArrayList<String> labels = new ArrayList<String>();
                labels.add("Total_deaths");
                labels.add("Total_kills");

                PieData data = new PieData(labels, dataset);

                PieChart pieChart = (PieChart)findViewById(R.id.kd_chart);

                pieChart.setNoDataText("");
                pieChart.getLegend().setEnabled(false);
                pieChart.setDescription("");
                pieChart.setDrawSliceText(false);
                pieChart.setData(data);

                pieChart.setHoleColorTransparent(true);
                pieChart.setHoleRadius(90f);

                pieChart.animateXY(2000, 2000); // animate horizontal and vertical 3000 milliseconds

                //It appears that "invalidate()" acts as some kind of update function.
                pieChart.invalidate();

            }
        });
    }

    public String getJsonValue(JSONObject response, String name)
    {
        try {
            int stats_length = response.getJSONObject("playerstats").getJSONArray("stats").length();
            String index_item, index_result = null;

            for(int index = 0; index < stats_length; index++)
            {
                index_item = response.getJSONObject("playerstats").getJSONArray("stats").getJSONObject(index).getString("name");
                if(index_item.equals(name))
                {
                    index_result = response.getJSONObject("playerstats").getJSONArray("stats").getJSONObject(index).getString("value");
                    break;
                }
            }
            return index_result;
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.stats, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_stats, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((StatsActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
