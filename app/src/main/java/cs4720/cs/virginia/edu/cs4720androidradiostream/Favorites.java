package cs4720.cs.virginia.edu.cs4720androidradiostream;

import cs4720.cs.virginia.edu.cs4720androidradiostream.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.widget.DrawerLayout;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class Favorites extends Activity {
    private String[] ListItems;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private ShakeDetector mShakeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_favorites);


       SQLiteDatabase db = new DBHelper(this).getWritableDatabase();
        Cursor c = db.query(false, "favorites", null, null, null, null, null, null, null);
        String s = "";
        TextView textViewTitle = (TextView) this.findViewById(R.id.textView4);
        textViewTitle.setMovementMethod(new ScrollingMovementMethod());
        if(c.getCount()==0){
            s = "No Favorites yet! Add some from the Home screen!";
        }
        else {
            c.moveToNext();
            while (!c.isAfterLast()) {
                s += c.getString(0) + " By " +  c.getString(2) + "\n";
                c.moveToNext();
            }
        }
        textViewTitle.setText(s);
        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.




        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
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
                if (position == 3){
                    Intent intent = new Intent(here, MainActivity.class);
                    Favorites.this.startActivity(intent);
                    Favorites.this.finish();
                }
                if(position==1){
                    Intent intent = new Intent(here, StreamActivity.class);
                    Favorites.this.startActivity(intent);
                    Favorites.this.finish();
                }
                if(position==4){
                    Intent intent = new Intent(here, PlaylistActivity.class);
                    Favorites.this.startActivity(intent);
                    Favorites.this.finish();
                }
            }

        });
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
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(Favorites.this, MainActivity.class));
        finish();

    }



    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    @Override
    public void onPause(){
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
