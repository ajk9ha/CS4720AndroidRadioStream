package cs4720.cs.virginia.edu.cs4720androidradiostream;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.IOException;

public class StreamActivity extends AppCompatActivity {
    private String[] ListItems;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ImageView streamIndicator;
    MediaPlayer mediaPlayer;
    ToggleButton streamButton;
    protected boolean isPlaying = false;

    //GPS Coordinates of WXTJ Station
    private double wxtjLat = 38.042128;
    private double wxtjLong = -78.503459;

    private double lastLong;
    private double lastLat;

    private float distanceArray[] = new float[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream);

        isPlaying = isRadioServiceRunning(RadioStreamService.class);
        if(isPlaying) {
            streamButton = (ToggleButton) findViewById(R.id.startStream);
            streamButton.toggle();

            streamIndicator = (ImageView) findViewById(R.id.streamIndicator);
            streamIndicator.setImageDrawable(getDrawable(R.drawable.wxtj_no_background));
        }

        Intent intent = getIntent();
        lastLong = intent.getDoubleExtra("lastLong", 0);
        lastLat = intent.getDoubleExtra("lastLat", 0);

        Location.distanceBetween(lastLat, lastLong, wxtjLat, wxtjLong, distanceArray);
        Float distToStation = distanceArray[0];

        TextView stationDist = (TextView) findViewById(R.id.stationDist);

        /* 5600 meters is approximately the range of a low power FM transmitter */
        if(distToStation <= 5600) {
            stationDist.setText("You are within " + distToStation.toString() + " meters of the WXTJ station.  Try tuning in on 100.1FM");
        } else {
            stationDist.setText("You are " + distToStation.toString() + " meters away from the WXTJ station. Unfortunately this is out of range to receive FM, but thanks for tuning in!");
        }


        String normal_url = getString(R.string.normal_stream_url);


        // Handle MediaPlayerService Logic in startRadioStreamService method
        startRadioStreamService();

        ListItems = getResources().getStringArray(R.array.menu_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, ListItems));
        final Activity here = this;
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                if (position == 2) {
                    Intent intent = new Intent(here, MainActivity.class);
                    StreamActivity.this.startActivity(intent);
                    StreamActivity.this.finish();
                }
                if (position == 1) {
                    Intent intent = new Intent(here, Favorites.class);
                    StreamActivity.this.startActivity(intent);
                    StreamActivity.this.finish();
                }
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_stream, menu);
        return true;
    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(StreamActivity.this, MainActivity.class));
        //finish();

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

    public void startPlaylistActivity(View view) {
        Intent intent = new Intent(this, PlaylistActivity.class);

        StreamActivity.this.startActivity(intent);
    }

    public void startRadioStreamService() {

        streamButton = (ToggleButton) findViewById(R.id.startStream);
        streamIndicator = (ImageView) findViewById(R.id.streamIndicator);

        streamButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isPlaying) {
                if (isPlaying) {
                    Intent streamIntent = new Intent(buttonView.getContext(), RadioStreamService.class);
                    streamIntent.setAction("cs4720.cs.virginia.edu.cs4720androidradiostream.action.PLAY_NORMAL");
                    startService(streamIntent);
                    Log.d("test", "Stream service Intent sent");

                    // Make image colored
                    streamIndicator.setImageDrawable(getDrawable(R.drawable.wxtj_no_background));
                } else {
                    Intent stopIntent = new Intent(buttonView.getContext(), RadioStreamService.class);
                    stopIntent.setAction("cs4720.cs.virginia.edu.cs4720androidradiostream.action.STOP");
                    stopService(stopIntent);

                    // Set image back to greyscale
                   streamIndicator.setImageDrawable(getDrawable(R.drawable.wxtj_greyscale_no_background));

                }
            }
        });

    }

    public  boolean isRadioServiceRunning(Class<?> RadioStreamService) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (RadioStreamService.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
