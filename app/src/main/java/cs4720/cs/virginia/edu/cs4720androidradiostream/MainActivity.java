package cs4720.cs.virginia.edu.cs4720androidradiostream;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import static com.google.android.gms.location.LocationServices.API;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private Button name;
    private EditText textField;
    private TextView helloText;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

@Override
protected void onStart(){
    super.onStart();
    mGoogleApiClient.connect();
}
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        name = (Button) findViewById(R.id.button);
        textField = (EditText) findViewById(R.id.editText);
        helloText = (TextView) findViewById(R.id.textView2);

        name.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        helloText.setText(textField.getText());
                                        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                                                mGoogleApiClient);
                                        TextView mLatitudeText = (TextView) findViewById(R.id.mLatitudeText);
                                        TextView mLongitudeText = (TextView) findViewById(R.id.mLongitudeText);

                                        if (mLastLocation != null) {
                                            mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
                                            mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
                                        }
                                        else{
                                            mLatitudeText.setText("Location Services unavailable");
                                        }
                                    }
                                }
        );
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

    @Override
    public void onConnected(Bundle connectionHint) {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        TextView mLatitudeText = (TextView) findViewById(R.id.mLatitudeText);
        TextView mLongitudeText = (TextView) findViewById(R.id.mLongitudeText);

        if (mLastLocation != null) {
            mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
            mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
        }
        else{
            mLatitudeText.setText("Location Services unavailable");
        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        TextView mLatitudeText = (TextView) findViewById(R.id.mLatitudeText);
        mLatitudeText.setText("Connection to Google Play API is not available");
    }
}
