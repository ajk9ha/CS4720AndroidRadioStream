package cs4720.cs.virginia.edu.cs4720androidradiostream;

import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.IOException;

public class StreamActivity extends AppCompatActivity {

    MediaPlayer mediaPlayer;
    Button streamButton;
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

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(normal_url);
        } catch (IOException e){
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        try {
            mediaPlayer.prepareAsync();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        addListenerOnButton();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_stream, menu);
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

    public void addListenerOnButton() {
        streamButton = (Button) findViewById(R.id.startStream);

        streamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPlaying = !isPlaying;

                if(isPlaying == true) {
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.start();
                        }
                    });
                }

                if(isPlaying == false) {
                    mediaPlayer.pause();
                }
            }
        });
    }
}
