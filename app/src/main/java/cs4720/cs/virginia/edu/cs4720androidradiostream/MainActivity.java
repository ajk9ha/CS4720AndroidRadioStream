package cs4720.cs.virginia.edu.cs4720androidradiostream;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.CharArrayBuffer;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import static com.google.android.gms.location.LocationServices.API;

public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private Button name;
    private Button goToStream;
    private EditText textField;
    private TextView helloText;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double mLongitude;
    private double mLatitude;
    private ShakeDetector mShakeDetector;
    Cursor c;
    private String[] ListItems;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private double maxValue = 0;


    @Override
    protected void onStart(){
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up Google Client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


        // Nav Drawer logic
        ListItems = getResources().getStringArray(R.array.menu_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, ListItems));

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
                getActionBar().setTitle("WXTJ Student Radio");
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

        final Activity here = this;

        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                if (position == 1) {
                    Intent intent = new Intent(here, StreamActivity.class);
                    intent.putExtra("lastLong", mLongitude);
                    intent.putExtra("lastLat", mLatitude);

                    MainActivity.this.startActivity(intent);
                    MainActivity.this.finish();
                }
                if (position == 2) {
                    Intent intent = new Intent(here, Favorites.class);
                    MainActivity.this.startActivity(intent);
                    MainActivity.this.finish();
                }
                if (position == 3) {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);

                }
                if (position == 4) {
                    Intent intent = new Intent(here, PlaylistActivity.class);
                    MainActivity.this.startActivity(intent);
                    MainActivity.this.finish();
                }
            }

        });


        // SQLite logic
        final SQLiteDatabase db = new DBHelper(this).getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("Title", "Buddy Holly");
        cv.put("Artist", "Weezer");

        Button atf = (Button) findViewById(R.id.addToFavorites);
        atf.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText tf = (EditText) findViewById(R.id.Title);
                String title = tf.getText().toString();
                EditText af = (EditText) findViewById(R.id.Artist);
                String artist = af.getText().toString();
                ContentValues cv = new ContentValues();
                cv.put("Title", title);
                cv.put("Artist", artist);
                long error = db.insert("favorites", null, cv);
                CharSequence toasttext = title;
                if (error > -1) {
                    tf.setText("");
                    af.setText("");

                    toasttext = title + " by " + artist + " has been added to your favorites!";
                } else {
                    toasttext = "An error has occurred, please try again.";
                }
                Toast toast = Toast.makeText(getApplicationContext(), toasttext, Toast.LENGTH_SHORT);
                toast.show();
            }

        });

        // Shake sensor logic
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {
                Context context = getApplicationContext();
                CharSequence text = "";
                int duration = Toast.LENGTH_LONG;
                if(isRadioServiceRunning(RadioStreamService.class)) {
                    text = "Stream has stopped";
                    Intent stopIntent = new Intent(here, RadioStreamService.class);
                    stopIntent.setAction("cs4720.cs.virginia.edu.cs4720androidradiostream.action.STOP");
                    stopService(stopIntent);
                }
                else{
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

    @Override
    public void onConnected(Bundle connectionHint) {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (mLastLocation != null) {
            mLatitude = mLastLocation.getLatitude();
            mLongitude = mLastLocation.getLongitude();
        }
        else{
//            mLatitudeText.setText("Location Services unavailable");
        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
//        TextView mLatitudeText = (TextView) findViewById(R.id.mLatitudeText);
//        mLatitudeText.setText("Connection to Google Play API is not available");
    }

    public void startFavoritesActivity(View view){
        Intent intent = new Intent(this,Favorites.class);
        MainActivity.this.startActivity(intent);
        MainActivity.this.finish();
    }

    public void startPlaylistActivity(View view) {
        Intent intent = new Intent(this, PlaylistActivity.class);

        MainActivity.this.startActivity(intent);
    }

    public void startStreamActivity(View view) {
        Intent intent = new Intent(this, StreamActivity.class);
        intent.putExtra("lastLong", mLongitude);
        intent.putExtra("lastLat", mLatitude);

        MainActivity.this.startActivity(intent);
        MainActivity.this.finish();
    }


    @Override
    public void onPause() {
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();

    }
    @Override
    public void onResume(){
        super.onResume();
        mSensorManager.registerListener(mShakeDetector, mSensor, SensorManager.SENSOR_DELAY_UI);
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
