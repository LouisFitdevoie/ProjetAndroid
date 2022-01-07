package be.heh.fitdevoie.projetandroidstudio.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Locale;

public class UserAccessDB {
    private static final int VERSION = 1;
    private static final String NAME_DB = "Users.db";

    private static final String TABLE_USER = "table_user";
    private static final String COL_ID = "USERID";
    private static final int NUM_COL_ID = 0;
    private static final String COL_FIRSTNAME = "FIRSTNAME";
    private static final int NUM_COL_FIRSTNAME = 1;
    private static final String COL_LASTNAME = "LASTNAME";
    private static final int NUM_COL_LASTNAME = 2;
    private static final String COL_EMAIL = "EMAIL_ADDRESS";
    private static final int NUM_COL_EMAIL = 3;
    private static final String COL_PASSWORD = "PASSWORD";
    private static final int NUM_COL_PASSWORD = 4;
    private static final String COL_RIGHTS = "RIGHTS";
    private static final int NUM_COL_RIGHTS = 5;

    private SQLiteDatabase db;
    private UserBddSqlite userdb;

    public UserAccessDB(Context context) {
        userdb = new UserBddSqlite(context, NAME_DB, null, VERSION);
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

    public long insertUser(User user) {
        ContentValues content = new ContentValues();
        content.put(COL_FIRSTNAME, user.getFirstName());
        content.put(COL_LASTNAME, user.getLastName());
        content.put(COL_EMAIL, user.getEmailAddress());
        content.put(COL_PASSWORD, user.getPassword());
        content.put(COL_RIGHTS, user.getRights());

        return db.insert(TABLE_USER, null, content);
    }

    public int updateUser(int id, User user) {
        ContentValues content = new ContentValues();
        content.put(COL_FIRSTNAME, user.getFirstName());
        content.put(COL_LASTNAME, user.getLastName());
        content.put(COL_EMAIL, user.getEmailAddress());
        content.put(COL_PASSWORD, user.getPassword());
        content.put(COL_RIGHTS, user.getRights());

        return db.update(TABLE_USER, content, COL_ID + " = " + id, null);
    }

    public int removeUser(String email) {
        return db.delete(TABLE_USER, COL_EMAIL + " = " + email, null);
    }

    public ArrayList<User> getAllUser() {
        Cursor cursor = db.query(TABLE_USER, new String[] { COL_ID, COL_FIRSTNAME, COL_LASTNAME, COL_EMAIL, COL_PASSWORD, COL_RIGHTS }, null, null, null, null, COL_ID);

        ArrayList<User> tabUser = new ArrayList<User>();

        if(cursor.getCount() == 0) {
            cursor.close();
            return tabUser;
        }

        while (cursor.moveToNext()) {
            User user = new User();
            user.setUserId(cursor.getInt(NUM_COL_ID));
            user.setFirstName(cursor.getString(NUM_COL_FIRSTNAME));
            user.setLastName((cursor.getString(NUM_COL_LASTNAME)));
            user.setEmailAddress(cursor.getString(NUM_COL_EMAIL));
            user.setPassword(cursor.getString(NUM_COL_PASSWORD));
            user.setRights(cursor.getInt(NUM_COL_RIGHTS));

            tabUser.add(user);
        }
        cursor.close();
        return tabUser;
    }

    public User getUserWithEmail(String emailToSearch) {
        Cursor cursor = db.query(TABLE_USER, new String[] { COL_ID, COL_FIRSTNAME, COL_LASTNAME, COL_EMAIL, COL_PASSWORD, COL_RIGHTS }, null, null, null, null, COL_ID);

        User userToReturn = new User();

        if(cursor.getCount() == 0) {
            cursor.close();
            return null;
        }

        while (cursor.moveToNext()) {

            String emailFound = cursor.getString(NUM_COL_EMAIL);

            if(emailFound.equalsIgnoreCase("'" + emailToSearch + "'")) {
                userToReturn.setUserId(cursor.getInt(NUM_COL_ID));
                userToReturn.setFirstName(cursor.getString(NUM_COL_FIRSTNAME));
                userToReturn.setLastName(cursor.getString(NUM_COL_LASTNAME));
                userToReturn.setEmailAddress(cursor.getString(NUM_COL_EMAIL));
                userToReturn.setPassword(cursor.getString(NUM_COL_PASSWORD));
                userToReturn.setRights(cursor.getInt(NUM_COL_RIGHTS));
                break;
            }
        }
        cursor.close();

        return userToReturn;
    }

    public User getUserWithId(int userIdToSearch) {
        Cursor cursor = db.query(TABLE_USER, new String[] { COL_ID, COL_FIRSTNAME, COL_LASTNAME, COL_EMAIL, COL_PASSWORD, COL_RIGHTS}, null, null, null, null, COL_ID);
        User userToReturn = new User();

        if(cursor.getCount() == 0) {
            cursor.close();
            return null;
        }

        while (cursor.moveToNext()) {

            int idFound = cursor.getInt(NUM_COL_ID);

            if(idFound == userIdToSearch) {
                userToReturn.setUserId(cursor.getInt(NUM_COL_ID));
                userToReturn.setFirstName(cursor.getString(NUM_COL_FIRSTNAME));
                userToReturn.setLastName(cursor.getString(NUM_COL_LASTNAME));
                userToReturn.setEmailAddress(cursor.getString(NUM_COL_EMAIL));
                userToReturn.setPassword(cursor.getString(NUM_COL_PASSWORD));
                userToReturn.setRights(cursor.getInt(NUM_COL_RIGHTS));
                break;
            }
        }
        cursor.close();

        return userToReturn;
    }

    public int getAdminNumber() {
        Cursor cursor = db.query(TABLE_USER, new String[] { COL_ID, COL_FIRSTNAME, COL_LASTNAME, COL_EMAIL, COL_PASSWORD, COL_RIGHTS }, null, null, null, null, COL_ID);

        ArrayList<User> tabUser = new ArrayList<User>();

        int adminNumber = 0;
        if(cursor.getCount() == 0) {
            cursor.close();
            return 0;
        }

        while (cursor.moveToNext()) {
            int rights = cursor.getInt(NUM_COL_RIGHTS);

            if(rights == 0) {
                adminNumber++;
            }
        }

        cursor.close();

        return adminNumber;
    }

}
