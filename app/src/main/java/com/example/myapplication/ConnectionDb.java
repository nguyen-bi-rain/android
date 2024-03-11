package com.example.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.media.MediaMetadata;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintAttribute;
import androidx.core.content.ContentValuesKt;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ConnectionDb extends SQLiteOpenHelper {
    public static final String TableName = "ContactTable";
    public static final String Id = "Id";
    public static final String Name = "FullName";
    public static final String Phone = "PhoneNumber";
    public static final String Status = "Status";
    public static final String Image = "Image";

    public ConnectionDb(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlCreate = "Create table if not exists "+ TableName + "(" + Id + " Integer Primary key, " + Name +" Text, " + Phone +" Text, "+ Image + " Text)";
        db.execSQL(sqlCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("Drop table if exists " + TableName);
        onCreate(db);
    }
    public ArrayList<Contact> getAllContact(){
        ArrayList<Contact> list = new ArrayList<Contact>();
        String sql = "Select * from " + TableName;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cusor = db.rawQuery(sql,null);
        if(cusor != null){
            while (cusor.moveToNext()){
                Contact contact = new Contact(cusor.getInt(0),cusor.getString(1),cusor.getString(2),false,cusor.getString(3));
                list.add(contact);
            }
        }
        return list;
    }
    public void addContact(Contact contact){
        SQLiteDatabase db=  this.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put(Id,contact.getId());
        value.put(Image,contact.getImage());
        value.put(Name,contact.getName());
        value.put(Phone,contact.getPhone());
        db.insert(TableName,null,value);
        db.close();

    }
}
