package promoz.com.br.promoz.dao.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by vallux on 23/01/17.
 */

public class MySQLiteDatabase extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "promoz.db";

    public MySQLiteDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        for(int i = 0; i < PromozContract.tablesCreationList.length; i++){
            Log.v("SQL", PromozContract.tablesCreationList[i]);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.v("\nSQL", "CRIANDO DB\n");
        for(int i = 0; i < PromozContract.tablesCreationList.length; i++){
            db.execSQL(PromozContract.tablesCreationList[i]);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}