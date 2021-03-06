package be.heh.fitdevoie.projetandroidstudio.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserBddSqlite extends SQLiteOpenHelper {
    private static final String TABLE_USER = "table_user";
    private static final String COL_ID = "USERID";
    private static final String COL_FIRSTNAME = "FIRSTNAME";
    private static final String COL_LASTNAME = "LASTNAME";
    private static final String COL_EMAIL = "EMAIL_ADDRESS";
    private static final String COL_PASSWORD = "PASSWORD";
    private static final String COL_RIGHTS = "RIGHTS";

    private static final String CREATE_DB = "CREATE TABLE " + TABLE_USER + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_FIRSTNAME + " TEXT NOT NULL, " + COL_LASTNAME + " TEXT NOT NULL, " + COL_EMAIL + " TEXT NOT NULL, " + COL_PASSWORD + " TEXT NOT NULL," + COL_RIGHTS + " INTEGER);";

    public UserBddSqlite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DB);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + TABLE_USER);
        onCreate(db);
    }
}
