package be.heh.fitdevoie.projetandroidstudio.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class UserAccessDB {
    private static final int VERSION = 1;
    private static final String NOM_DB = "User.db";

    private static final String TABLE_USER = "table_user";
    private static final String COL_ID = "ID";
    private static final int NUM_COL_ID = 0;
    private static final String COL_FIRSTNAME = "FIRSTNAME";
    private static final int NUM_COL_FIRSTNAME = 1;
    private static final String COL_LASTNAME = "LASTNAME";
    private static final int NUM_COL_LASTNAME = 2;
    private static final String COL_EMAIL = "EMAIL";
    private static final int NUM_COL_EMAIL = 3;
    private static final String COL_PASSWORD = "PASSWORD";
    private static final int NUM_COL_PASSWORD = 4;

    private SQLiteDatabase db;
    private UserBddSqlite userdb;

    public UserAccessDB(Context c) {
        userdb = new UserBddSqlite(c, NOM_DB, null, VERSION);
    }
    public void openForWrite() {
        db = userdb.getWritableDatabase();
    }
    public void openForRead() {
        db = userdb.getReadableDatabase();
    }
    public void Close() {
        db.close();
    }
    public long insertUser(User u) {
        ContentValues content = new ContentValues();
        content.put(COL_FIRSTNAME,u.getFirstName());
        content.put(COL_LASTNAME,u.getLastName());
        content.put(COL_EMAIL,u.getEmail());
        content.put(COL_PASSWORD,u.getPassword());
        return db.insert(TABLE_USER, null, content);
    }

    public int updateUser(int i, User u) {
        ContentValues content = new ContentValues();
        content.put(COL_FIRSTNAME,u.getFirstName());
        content.put(COL_LASTNAME,u.getLastName());
        content.put(COL_EMAIL,u.getEmail());
        content.put(COL_PASSWORD,u.getPassword());
        return db.update(TABLE_USER,content,COL_ID + " = " + i, null);
    }

    public int removeUser(String email) {
        return db.delete(TABLE_USER, COL_EMAIL + " = " + email, null);
    }

    public ArrayList<User> getAllUser() {
        Cursor c = db.query(TABLE_USER, new String[] { COL_ID, COL_FIRSTNAME, COL_LASTNAME, COL_EMAIL, COL_PASSWORD }, null, null, null, null, COL_ID);
        ArrayList<User> tabUser = new ArrayList<User>();
        if (c.getCount() == 0) {
            c.close();
            return tabUser;
        }
        while(c.moveToNext()) {
            User user1 = new User();
            user1.setId(c.getInt(NUM_COL_ID));
            user1.setFirstName(c.getString(NUM_COL_FIRSTNAME));
            user1.setLastName(c.getString(NUM_COL_LASTNAME));
            user1.setEmail(c.getString(NUM_COL_EMAIL));
            user1.setPassword(c.getString(NUM_COL_PASSWORD));
            tabUser.add(user1);
        }
        c.close();
        return tabUser;
    }
}
