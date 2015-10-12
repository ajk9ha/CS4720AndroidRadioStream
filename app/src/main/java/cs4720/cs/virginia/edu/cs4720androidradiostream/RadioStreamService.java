package cs4720.cs.virginia.edu.cs4720androidradiostream;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;

import java.io.IOException;

public class RadioStreamService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {
    private static final String ACTION_PLAY_NORMAL = "cs4720.cs.virginia.edu.cs4720androidradiostream.action.PLAY_NORMAL";
    private static final String ACTION_STOP = "cs4720.cs.virginia.edu.cs4720androidradiostream.action.STOP";
    private static MediaPlayer mMediaPlayer = null;

    public RadioStreamService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        if(!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        if(mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
        } else {
            mMediaPlayer.reset();
        }

        if(intent.getAction().equals(ACTION_PLAY_NORMAL)) {
            String normal_url = getString(R.string.normal_stream_url);
            String tju = "http://streams.wtju.net:8000/wtju-192.mp3";

            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            try {
                mMediaPlayer.setDataSource(tju);
            } catch (IOException e){
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }

            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnErrorListener(this);

            try {
                mMediaPlayer.prepareAsync();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }

        } else if(intent.getAction().equals(ACTION_STOP)) {
            stopSelf();
        }
        return 1;
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        mediaPlayer.reset();
        return false;
    }

    @Override
    public void onDestroy() {
        mMediaPlayer.stop();
        mMediaPlayer.release();
        mMediaPlayer = null;
    }
}
