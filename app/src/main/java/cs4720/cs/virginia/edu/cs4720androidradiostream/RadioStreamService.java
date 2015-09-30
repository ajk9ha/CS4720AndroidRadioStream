package cs4720.cs.virginia.edu.cs4720androidradiostream;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class RadioStreamService extends IntentService implements MediaPlayer.OnPreparedListener {
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_PLAY_NORMAL = "cs4720.cs.virginia.edu.cs4720androidradiostream.action.PLAY_NORMAL";
    private static final String ACTION_PLAY_HD = "cs4720.cs.virginia.edu.cs4720androidradiostream.action.PLAY_HD";

    private static final String NORMAL_STREAM_URL = "cs4720.cs.virginia.edu.cs4720androidradiostream.extra.PARAM1";
    private static final String HD_STREAM_URL = "cs4720.cs.virginia.edu.cs4720androidradiostream.extra.PARAM2";

    MediaPlayer mMediaPlayer = null;

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
//    public static void startActionNormalStream(Context context, String url) {
//        Intent intent = new Intent(context, RadioStreamService.class);
//        intent.setAction(ACTION_PLAY_NORMAL);
//        intent.putExtra(NORMAL_STREAM_URL, url);
//        context.startService(intent);
//    }
//
//    /**
//     * Starts this service to perform action Baz with the given parameters. If
//     * the service is already performing a task this action will be queued.
//     *
//     * @see IntentService
//     */
//    public static void startActionHDStream(Context context, String url) {
//        Intent intent = new Intent(context, RadioStreamService.class);
//        intent.setAction(ACTION_PLAY_HD);
//        intent.putExtra(HD_STREAM_URL, url);
//        context.startService(intent);
//    }

    public RadioStreamService() {
        super("RadioStreamService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

            final String action = intent.getAction();

            // Check for Normal vs HD stream url
            if (ACTION_PLAY_NORMAL.equals(action)) {
                String normal_url = getString(R.string.normal_stream_url);

                // Initialize MediaPlayer
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                try {
                    mMediaPlayer.setDataSource(normal_url);
                } catch (IOException e){
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }

                mMediaPlayer.setOnPreparedListener(this);

                try {
                    Log.d("test", "Preparing stream...");
                    mMediaPlayer.prepareAsync();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }

//                final String streamUrl = intent.getStringExtra(url);
//                handleActionFoo(streamUrl);
            } else {
                String hd_url = getString(R.string.hd_stream_url);

                // Initialize MediaPlayer
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                try {
                    mMediaPlayer.setDataSource(hd_url);
                } catch (IOException e){
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }

                mMediaPlayer.setOnPreparedListener(this);

                try {
                    Log.d("test", "Preparing stream...");
                    mMediaPlayer.prepareAsync();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }

//                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
//                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
//                handleActionFoo(param1, param2);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }
}
