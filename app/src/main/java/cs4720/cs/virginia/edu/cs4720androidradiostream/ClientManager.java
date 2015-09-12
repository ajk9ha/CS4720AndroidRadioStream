package cs4720.cs.virginia.edu.cs4720androidradiostream;

import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import static com.google.android.gms.location.LocationServices.*;

/**
 * Created by andrewkovalenko on 9/12/15.
 */
public class ClientManager {

    protected synchronized void buildGoogleApiClient(Context context) {
            GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(API)
                .build();
    }

}

