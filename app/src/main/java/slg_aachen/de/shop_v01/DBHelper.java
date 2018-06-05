package slg_aachen.de.shop_v01;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author Mirko
 * @version 0.1
 * <p>
 * This class creates a SQLite database on the mobile device and is later used to retrieve this database.
 */

class DBHelper extends SQLiteOpenHelper {
    DBHelper(Context context) {
        super(context, "palette.db", null, 1);
        Log.i("info", "DBHelper called");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Log.i("info","onCreate DBHelper");
        try {
            Log.i("info", "exec create DB");
            db.execSQL("CREATE TABLE palette (c1 INT, c2 INT, c3 INT, c4 INT, c5 INT)");
        } catch (SQLException e) {
            Log.e("ERROR", e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
