package com.codepath.simpletodo.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.codepath.simpletodo.models.NoteItem;

import java.util.ArrayList;


public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "ToDoList_v1.db"; // Version the DB esp when changing fields (for now)
    public static final String TODOLIST_TABLE_NAME = "notes";
    public static final String TODO_COLUMN_ID = "id";
    public static final String TODO_COLUMN_DETAIL = "detail";
    public static final String TODO_COLUMN_IS_IMPORTANT = "is_important";
    public static final String TODO_COLUMN_IS_URGENT = "is_urgent";
    public static final String TODO_COLUMN_DUEDATE = "due_date";

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //db.execSQL("DROP TABLE IF EXISTS " + TODOLIST_TABLE_NAME);
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TODOLIST_TABLE_NAME +
                        "(id integer primary key, detail text, is_important int, is_urgent int, due_date int)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TODOLIST_TABLE_NAME);
        onCreate(db);
    }

    public long insertItem(String detail, Integer isImportant, Integer isUrgent, Integer dueDate)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TODO_COLUMN_DETAIL, detail);
        contentValues.put(TODO_COLUMN_IS_IMPORTANT, isImportant);
        contentValues.put(TODO_COLUMN_IS_URGENT, isUrgent);
        contentValues.put(TODO_COLUMN_DUEDATE, dueDate);
        long id = db.insert(TODOLIST_TABLE_NAME, null, contentValues);

        return id;
    }

    public boolean updateItem(Long id, String detail, Integer isImportant, Integer isUrgent, Integer dueDate)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TODO_COLUMN_DETAIL, detail);
        contentValues.put(TODO_COLUMN_IS_IMPORTANT, isImportant);
        contentValues.put(TODO_COLUMN_IS_URGENT, isUrgent);
        contentValues.put(TODO_COLUMN_DUEDATE, dueDate);
        db.update(TODOLIST_TABLE_NAME, contentValues, "id = ? ", new String[] { Long.toString(id) } );

        return true;
    }

    public Integer deleteItem(Long id)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(TODOLIST_TABLE_NAME, "id = ? ", new String[] { Long.toString(id) });
    }

    public ArrayList<NoteItem> getAllToDos()
    {
        ArrayList<NoteItem> todos_list = new ArrayList<NoteItem>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM " + TODOLIST_TABLE_NAME, null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            long id = res.getLong(res.getColumnIndex(TODO_COLUMN_ID));
            String detail = res.getString(res.getColumnIndex(TODO_COLUMN_DETAIL));
            int isImportant = res.getInt(res.getColumnIndex(TODO_COLUMN_IS_IMPORTANT));
            int isUrgent = res.getInt(res.getColumnIndex(TODO_COLUMN_IS_URGENT));
            int dueDate = res.getInt(res.getColumnIndex(TODO_COLUMN_DUEDATE));
            todos_list.add(new NoteItem(id, detail, isImportant, isUrgent, dueDate));
            res.moveToNext();
        }

        return todos_list;
    }
}