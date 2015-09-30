package cs4720.cs.virginia.edu.cs4720androidradiostream;

import cs4720.cs.virginia.edu.cs4720androidradiostream.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class Favorites extends Activity {
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
    }




    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */

}
