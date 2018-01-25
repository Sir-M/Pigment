package slg_aachen.de.shop_v01;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Mirko on 06.05.2017.
 */

class DBHelper extends SQLiteOpenHelper {
     DBHelper(Context context) {
        super(context,"palette.db", null, 1);
        Log.e("ERROR","PublicDBHelper");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e("ERROR","onCreate DBHelper");
       // try{
            Log.e("ERROR","exe create DB");
            db.execSQL("CREATE TABLE palette (c1 INT, c2 INT, c3 INT, c4 INT, c5 INT)");
      //  }
       // catch(SQLException e){
           // Log.e("ERROR", e.getMessage());
       // }

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
