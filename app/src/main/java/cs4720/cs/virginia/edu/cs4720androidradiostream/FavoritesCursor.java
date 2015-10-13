package cs4720.cs.virginia.edu.cs4720androidradiostream;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import cs4720.cs.virginia.edu.cs4720androidradiostream.R;


public class FavoritesCursor extends CursorAdapter {
    private LayoutInflater cursorInflater;

    // Default constructor
    public FavoritesCursor(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        cursorInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    public void bindView(View view, Context context, Cursor cursor) {
        String title = cursor.getString(0)+" By "+cursor.getString(1)+ " Off of "+cursor.getString(2)+"\n";

    }

    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // R.layout.list_row is your xml layout for each row
        return cursorInflater.inflate(R.layout.activity_favorites, parent, false);
    }
}