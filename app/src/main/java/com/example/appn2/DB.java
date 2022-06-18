package com.example.appn2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.annotation.Nullable;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DB extends SQLiteOpenHelper {

    public DB(@Nullable Context context) {
        super(context, "apkDb", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE EVENT (ID INTEGER PRIMARY KEY AUTOINCREMENT, date DATE, time DATE, title TEXT, review INTEGER, image BLOB)");
        ContentValues cv = new ContentValues();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldV, int newV) {

    }

    public EventEntity addOne(EventEntity model){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        if(Objects.nonNull(model.getDate())) {
            cv.put("date", model.getDate().toString());
        }
        if(Objects.nonNull(model.getTime())) {
            cv.put("time", model.getTime().toString());
        }
        cv.put("review", model.getReview());
        cv.put("title", model.getTitle());
        cv.put("image", getBitmapAsByteArray(model.getImage()));
        Long id = db.insert("EVENT", null, cv);
        return this.getOne(id.intValue());
    }

    public List<EventEntity> getAllByDate(LocalDate date){
        List<EventEntity> entries = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] params = new String[]{ date.toString() };
        Cursor cursor = db.rawQuery( "SELECT * FROM EVENT WHERE date = ?",params);
        if (cursor.moveToFirst()) {
            do {
                EventEntity entry = new EventEntity();
                entry.setId(cursor.getInt(0));
                if(Objects.nonNull(cursor.getString(1))) {
                    entry.setDate(LocalDate.parse(cursor.getString(1)));
                }
                if(Objects.nonNull(cursor.getString(2))) {
                    entry.setTime(LocalTime.parse(cursor.getString(2)));
                }
                entry.setTitle(cursor.getString(3));
                entry.setReview(cursor.getInt(4));
                byte[] blob = cursor.getBlob(5);
                entry.setImage(BitmapFactory.decodeByteArray(blob, 0, blob.length));
                entries.add(entry);

            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return entries;
    }

    public void deleteOne(Integer id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("EVENT", "ID=?", new String[] { String.valueOf(id) });
    }

    public EventEntity getOne(Integer id){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] params = new String[]{ id.toString() };
        Cursor cursor = db.rawQuery( "SELECT * FROM EVENT WHERE ID = ?",params);
        EventEntity entry = new EventEntity();
        try {
            if (cursor.moveToFirst()) {
                entry.setId(cursor.getInt(0));
                if(Objects.nonNull(cursor.getString(1))) {
                    entry.setDate(LocalDate.parse(cursor.getString(1)));
                }
                if(Objects.nonNull(cursor.getString(2))) {
                    entry.setTime(LocalTime.parse(cursor.getString(2)));
                }
                entry.setTitle(cursor.getString(3));
                entry.setReview(cursor.getInt(4));
                byte[] blob = cursor.getBlob(5);
                entry.setImage(BitmapFactory.decodeByteArray(blob, 0, blob.length));
            }
        } finally {
            cursor.close();
        }
        return entry;
    }

    public void updateReview(Integer id, Integer review){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] params = new String[]{ id.toString() };
        ContentValues cv = new ContentValues();
        cv.put("review", review);
        db.update("EVENT", cv, "ID = ?", params);
    }

    private static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }
}