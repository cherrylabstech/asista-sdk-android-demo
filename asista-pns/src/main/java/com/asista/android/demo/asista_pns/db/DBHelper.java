package com.asista.android.demo.asista_pns.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.asista.android.demo.asista_pns.model.Message;
import com.asista.android.pns.exceptions.AsistaException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Benjamin J on 31-05-2019.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String TAG = DBHelper.class.getSimpleName();

    /* DB */
    public static final String DB_NAME = "AsistaPNS.db";
    public static final int DB_VERSION = 1;

    /* TABLE NAME*/
    public static final String TABLE_NAME = "notification_details";

    /* COLUMNS */
    public static final String COLUMN_MESSAGE_ID = "id";
    public static final String COLUMN_MESSAGE_TITLE = "title";
    public static final String COLUMN_MESSAGE_BODY = "body";

    /* QUERIES*/
    public static final String CREATE_TABLE_NOTIFICATION_DETAILS = "CREATE TABLE " + TABLE_NAME + "("
            + COLUMN_MESSAGE_ID + " TEXT PRIMARY KEY, "
            + COLUMN_MESSAGE_TITLE + " TEXT, "
            + COLUMN_MESSAGE_BODY + " TEXT "
            + ")";
    private static final String DELETE_TABLE_NOTIFICATION_DETAILS =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_NOTIFICATION_DETAILS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DELETE_TABLE_NOTIFICATION_DETAILS);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public static DBHelper getInstance(Context context){
        return new DBHelper(context);
    }

    public long saveMessage(Message message){
        Log.i(TAG, "saveMessage: saving... ");
        try {
            if (null != message) {
                SQLiteDatabase db = this.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(COLUMN_MESSAGE_ID, message.getId());
                values.put(COLUMN_MESSAGE_TITLE, message.getTitle());
                values.put(COLUMN_MESSAGE_BODY, message.getBody());
                return db.insert(TABLE_NAME, null, values);
            }else
                new AsistaException("Message object is null").printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public Message getMessage(String messageId){
        Log.i(TAG, "getMessage: getting message... ");
        try {
            SQLiteDatabase db = this.getReadableDatabase();
//            String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_MESSAGE_ID + " = " + messageId;
//            Cursor c = db.rawQuery(query, null);
            String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_MESSAGE_ID + " =? ";
            Cursor c = db.rawQuery(query, new String[]{messageId});
            if (null != c) {
                c.moveToFirst();
                Message message = new Message();
                message.setId(c.getString(c.getColumnIndex(COLUMN_MESSAGE_ID)));
                message.setTitle(c.getString(c.getColumnIndex(COLUMN_MESSAGE_TITLE)));
                message.setBody(c.getString(c.getColumnIndex(COLUMN_MESSAGE_BODY)));
                return message;
            }else
                new AsistaException("Cursor is null").printStackTrace();
        }catch (Exception e){e.printStackTrace();}
        return null;
    }

    public List<Message> fetchMessages(){
        Log.i(TAG, "fetchMessages: fetching messages... ");
        try {
            List<Message> messageList = new ArrayList<>();
            String selectQuery = "SELECT  * FROM " + TABLE_NAME;
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c = db.rawQuery(selectQuery, null);

            if (c.moveToFirst()) {
                do {
                    Message message = getMessage(c.getString(c.getColumnIndex(COLUMN_MESSAGE_ID)));
                    if (null != message) {
                        messageList.add(message);
                    }else
                        new AsistaException("message is null").printStackTrace();
                } while (c.moveToNext());
            }
            return messageList;
        }catch (Exception e){e.printStackTrace();}
        return null;
    }
}
