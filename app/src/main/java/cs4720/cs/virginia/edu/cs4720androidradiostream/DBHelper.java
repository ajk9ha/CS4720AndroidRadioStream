package cs4720.cs.virginia.edu.cs4720androidradiostream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by Student on 9/26/2015.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DICTIONARY_TABLE_NAME = "favorites";
    private static final String DICTIONARY_TABLE_CREATE =
            "CREATE TABLE " + DICTIONARY_TABLE_NAME + " (" +
                    "Title" + " TEXT, " +
                    "Album" + " TEXT, "+
                    "Artist" + " TEXT);" ;

    DBHelper(Context context) {
        super(context, "mydb.db3" , null, DATABASE_VERSION);
        this.getWritableDatabase();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int old, int current){

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DICTIONARY_TABLE_CREATE);
}


}
