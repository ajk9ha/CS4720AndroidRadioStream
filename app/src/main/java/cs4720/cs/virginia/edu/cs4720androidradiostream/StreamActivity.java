package cs4720.cs.virginia.edu.cs4720androidradiostream;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;

public class StreamActivity extends Activity {
    private String[] ListItems;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private ImageView streamIndicator;
    MediaPlayer mediaPlayer;
    ToggleButton streamButton;
    protected boolean isPlaying = false;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private ShakeDetector mShakeDetector;

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

        // Check to see if Stream is already playing
        isPlaying = isRadioServiceRunning(RadioStreamService.class);
        if(isPlaying) {
            streamButton = (ToggleButton) findViewById(R.id.startStream);
            streamButton.toggle();

            streamIndicator = (ImageView) findViewById(R.id.streamIndicator);
            streamIndicator.setImageDrawable(getDrawable(R.drawable.wxtj_no_background));
        }

        // Get Intent data
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

        // Nav Drawer Logic
        ListItems = getResources().getStringArray(R.array.menu_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getActionBar().setTitle(getString(R.string.title_activity_stream));
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle("Select Destination");
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, ListItems));
        final Activity here = this;
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                if (position == 1) {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                }

                if (position == 2) {
                    Intent intent = new Intent(here, Favorites.class);
                    StreamActivity.this.startActivity(intent);
                    StreamActivity.this.finish();
                }

                if (position == 3) {
                    Intent intent = new Intent(here, MainActivity.class);
                    StreamActivity.this.startActivity(intent);
                    StreamActivity.this.finish();
                }

                if(position == 4){
                    Intent intent = new Intent(here, PlaylistActivity.class);
                    StreamActivity.this.startActivity(intent);
                    StreamActivity.this.finish();
                }
            }

        });

        // Shake Sensor logic
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {
                Context context = getApplicationContext();
                CharSequence text = "";
                int duration = Toast.LENGTH_LONG;
                if (isRadioServiceRunning(RadioStreamService.class)) {
                    text = "Stream has stopped";
                    Intent stopIntent = new Intent(here, RadioStreamService.class);
                    stopIntent.setAction("cs4720.cs.virginia.edu.cs4720androidradiostream.action.STOP");
                    stopService(stopIntent);
                } else {
                    text = "Stream has started!";
                    Intent streamIntent = new Intent(here, RadioStreamService.class);
                    streamIntent.setAction("cs4720.cs.virginia.edu.cs4720androidradiostream.action.PLAY_NORMAL");
                    startService(streamIntent);

                }
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
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
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(StreamActivity.this, MainActivity.class));
        //finish();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

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
