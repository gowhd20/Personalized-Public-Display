package com.example.dhaejong.acp2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dhaejong on 5.2.2016.
 */
public class LocalDB extends SQLiteOpenHelper{

    private String TAG = "LocalDB";

    private static final String DATABASE_NAME = "localDb.sqlite";
    public static final String DATABASE_TABLE_NAME_TAGS = "Tags";
    public static final String DATABASE_TABLE_NAME_EVENTS = "Events";
    private static final int DATABASE_VERSION = 4;

    // column names for table tags
    public static final String COLUMN_NAME_TAG_NAME = "name";
    public static final String COLUMN_NAME_TAG_ID = "id";

    // colume names for table events
    public static final String COLUMN_NAME_EVENT_ID = "id";
    public static final String COLUMN_NAME_EVENT_TITLE = "title";
    public static final String COLUMN_NAME_EVENT_CATEGORIES = "categories";
    public static final String COLUMN_NAME_EVENT_DESCRIPTION = "description";
    public static final String COLUMN_NAME_EVENT_ADDRESS = "address";
    public static final String COLUMN_NAME_EVENT_PRICE = "price";
    public static final String COLUMN_NAME_EVENT_IMAGE_URL = "url";
    public static final String COLUMN_NAME_EVENT_START_TIME = "start_time";    // 'YYYY-MM-DD HH:MM:SS' format
    public static final String COLUMN_NAME_EVENT_END_TIME = "end_time";        // 'YYYY-MM-DD HH:MM:SS' format


    private static final String CREATE_TABLE_TAG_PLAYER = "create table if not exists "+DATABASE_TABLE_NAME_TAGS+" " +
            "("+COLUMN_NAME_TAG_ID+" integer primary key, "+COLUMN_NAME_TAG_NAME+" text not null)";

    private static final String CREATE_TABLE_EVENT_PLAYER = "create table if not exists "+DATABASE_TABLE_NAME_EVENTS+" " +
            "("+COLUMN_NAME_EVENT_ID+" integer primary key, "+COLUMN_NAME_EVENT_TITLE+" text not null, "+COLUMN_NAME_EVENT_CATEGORIES+" text not null, " +
            COLUMN_NAME_EVENT_DESCRIPTION+" longtext not null, "+COLUMN_NAME_EVENT_ADDRESS+" text not null, "+COLUMN_NAME_EVENT_PRICE+" text, "+
            COLUMN_NAME_EVENT_IMAGE_URL+ " text, "+COLUMN_NAME_EVENT_START_TIME+" datetime not null, "+COLUMN_NAME_EVENT_END_TIME+" datetime)";

    private Context m_context;
    public SQLiteDatabase m_db;

    LocalDB(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        m_context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // add tables
        db.execSQL(CREATE_TABLE_TAG_PLAYER);
        db.execSQL(CREATE_TABLE_EVENT_PLAYER);
        m_db = db;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_NAME_TAGS);
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_NAME_EVENTS);
        onCreate(db);
    }

    public boolean addNewEvent(ArrayList<String> dataArray) {
        // event info

        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_NAME_EVENT_ID, dataArray.get(0));
            contentValues.put(COLUMN_NAME_EVENT_TITLE, dataArray.get(1));
            contentValues.put(COLUMN_NAME_EVENT_CATEGORIES, dataArray.get(2));
            contentValues.put(COLUMN_NAME_EVENT_DESCRIPTION, dataArray.get(3));
            contentValues.put(COLUMN_NAME_EVENT_ADDRESS, dataArray.get(4));
            contentValues.put(COLUMN_NAME_EVENT_PRICE, dataArray.get(5));
            contentValues.put(COLUMN_NAME_EVENT_IMAGE_URL, dataArray.get(6));
            contentValues.put(COLUMN_NAME_EVENT_START_TIME, dataArray.get(7));
            contentValues.put("end_time", dataArray.get(8));

            db.insert(DATABASE_TABLE_NAME_EVENTS, null, contentValues);
            db.close();
            Log.i(TAG, "event info is stored");

            return true;
        }catch(Exception e){
            e.printStackTrace();
            Log.e(TAG, "something went wrong with inserting data");
            return false;
        }
    }


    public boolean addNewTag(String id, String name) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("name", name);
            contentValues.put("id", id);
            db.insert(DATABASE_TABLE_NAME_TAGS, null, contentValues);
            db.close();
            Log.i(TAG, "tag is stored");
            return true;
        }catch(Exception e){
            e.printStackTrace();
            Log.e(TAG, "something went wrong with inserting data");
            return false;
        }
    }

    public ArrayList<String> getAllIds(String tableName){
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select id from " + tableName, null);
            res.moveToFirst();
            int count = res.getColumnCount();
            int count2 = res.getCount();
            Log.i(TAG, count + " count of getcolumncount");
            Log.i(TAG, count2 + " count of getcount");
            while(!res.isAfterLast()){
                arrayList.add(res.getString(res.getColumnIndex(COLUMN_NAME_EVENT_ID)));
                res.moveToNext();
            }
            res.close();
            db.close();
        }catch(Exception e){
            Log.e(TAG, "wrong with table name or data stored in it");
        }

        return arrayList;
    }

    public ArrayList<String> getAllItemsById(int id, String tableName){

        ArrayList<String> arrayList = new ArrayList<>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select * from " + tableName + " where id =" + id + "", null);
            res.moveToFirst();
            int count = res.getColumnCount();
            Log.d(TAG, count + " count of items");
            while (count > 0) {
                arrayList.add(res.getString(--count));
                Log.d(TAG, count + " value of count");
            }
            res.close();
            db.close();
        }catch(Exception e){
            Log.e(TAG, "not exist info what you are looking for");
        }
        return arrayList;

    }

    public int numberOfRows(String tableName){
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, tableName);
    }

    public Integer deleteTag (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(DATABASE_TABLE_NAME_TAGS,
                "id = ? ",
                new String[] {
                        Integer.toString(id)
                });
    }



    public ArrayList<String> getAllTagNames(){
        ArrayList<String> arrayList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+DATABASE_TABLE_NAME_TAGS+"", null );
        res.moveToFirst();

        while(!res.isAfterLast()){
            arrayList.add(res.getString(res.getColumnIndex(COLUMN_NAME_TAG_NAME)));
            res.moveToNext();
        }
        res.close();
        db.close();
        return arrayList;
    }

    public ArrayList<String> getAllEventTitles(){
        ArrayList<String> arrayList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from " + DATABASE_TABLE_NAME_EVENTS + "", null);
        res.moveToFirst();

        while(!res.isAfterLast()){
            arrayList.add(res.getString(res.getColumnIndex(COLUMN_NAME_EVENT_TITLE)));
            res.moveToNext();
        }
        res.close();
        db.close();
        return arrayList;
    }

    public ArrayList<MetaDataForEvents> getMetaDataForEvents(){
        ArrayList<MetaDataForEvents> arrayList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from " + DATABASE_TABLE_NAME_EVENTS + "", null);
        res.moveToFirst();

        // find all ids and titles and init by MetaDataForEvents class
        while(!res.isAfterLast()){
            arrayList.add(new MetaDataForEvents(res.getString(res.getColumnIndex(COLUMN_NAME_EVENT_ID)), res.getString(res.getColumnIndex(COLUMN_NAME_EVENT_TITLE))));
            res.moveToNext();
        }
        res.moveToFirst();
        res.close();
        db.close();
        return arrayList;
    }

    public boolean dropDB() {
        try {
            m_context.deleteDatabase(DATABASE_NAME);
            return true;
        }catch(Exception e){
            Log.e(TAG, "Something went wrong with deleting");
            return false;
        }
    }



}
