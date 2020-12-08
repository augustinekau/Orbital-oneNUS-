//package com.twocoders.onenus_v1;
//
//import android.annotation.SuppressLint;
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//
//import androidx.annotation.Nullable;
//
//public class LocationFavouriteStatusDatabase extends SQLiteOpenHelper {
//    public static final String DATABASE_NAME = "locationFavouriteStatuses.db";
//    public static final String TABLE_NAME = "locationFavouriteStatus_table";
//    public static final String COL_1 = "Location";
//    public static final String COL_2 = "Favourited";
//
//
//
//    public LocationFavouriteStatusDatabase(@Nullable Context context) {
//        super(context, DATABASE_NAME, null, 1);
//        SQLiteDatabase db = this.getWritableDatabase();
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        db.execSQL("create table " + TABLE_NAME + "(Location TEXT PRIMARY KEY, Favourited BOOLEAN)");
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
//        onCreate(db);
//    }
//
//    public void insertData(String location) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(COL_1, location);
//        contentValues.put(COL_2, false);
//        db.insert(TABLE_NAME, null, contentValues);
//    }
//
//    public boolean updateFavourite(String location, Boolean newFavourited) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(COL_1, location);
//        contentValues.put(COL_2, newFavourited);
//        db.update(TABLE_NAME, contentValues, "Location = ?", new String[] {location});
//        return true;
//    }
//
//    public boolean getFavStatus(String location) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        Integer boo = null;
//
//        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_1 + " =?", new String[] {location});
//
//        if (cursor.moveToFirst()) {
//            boo = cursor.getInt(1);
//        }
//        return boo == 1;
//    }
//
//    public boolean isEmpty() {
//        SQLiteDatabase db = this.getWritableDatabase();
//        int count = 0;
//        Cursor cursor = db.rawQuery("SELECT count(*) FROM " + TABLE_NAME, null);
//        if (cursor.moveToFirst()) {
//            count = cursor.getInt(0);
//        }
//        return count == 0;
//    }
//}
