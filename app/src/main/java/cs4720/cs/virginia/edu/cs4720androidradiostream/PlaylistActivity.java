package cs4720.cs.virginia.edu.cs4720androidradiostream;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

public class PlaylistActivity extends AppCompatActivity {

    WebView playListView;
    final String playlistUrl = "http://www.wtju.net/?station=wtjx";
    String playlist;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private ShakeDetector mShakeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        // Retrieve and update playlist view in AsyncTask
        new HTMLScrapingTask(this).execute();
        final SQLiteDatabase db = new DBHelper(this).getWritableDatabase();



        Button atf = (Button) findViewById(R.id.button);
        atf.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText tf = (EditText) findViewById(R.id.editText);
                String title = tf.getText().toString();
                EditText af = (EditText) findViewById(R.id.editText2);
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
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        final Activity here = this;
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
        getMenuInflater().inflate(R.menu.menu_playlist, menu);
        return true;
    }
    public void beginFavoritesActivity(View view){
        Intent intent = new Intent(this,Favorites.class);
        PlaylistActivity.this.startActivity(intent);
        PlaylistActivity.this.finish();
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
