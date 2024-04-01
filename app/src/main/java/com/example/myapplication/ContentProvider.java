package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class ContentProvider {
    private Activity activity;
    public ContentProvider(Activity activity) {this.activity = activity;}
    public ArrayList<Contact> getAllContact(){
        ArrayList<Contact> listContact = new ArrayList<>();
        String [] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI
        };
        Cursor cursor  = activity.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,null,null,null
        );
        if(cursor.moveToFirst()){
            do{
                Contact c = new Contact(cursor.getInt(0),cursor.getString(1),cursor.getString(2),false,cursor.getString(3));
                listContact.add(c);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return listContact;
    }
    public void AddContact(Contact c){
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        int rawContentInsertIndex = ops.size();
        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE,null)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME,null)
                .build());
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).
                withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,rawContentInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,c.getPhone())
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,"1")
                .build());
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,rawContentInsertIndex)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,c.getName())
                .build());

        try{
            InputStream input = activity.getContentResolver().openInputStream(Uri.parse(c.getImage()));
            Bitmap bit = BitmapFactory.decodeStream(input);
            if(bit !=null){
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bit.compress(Bitmap.CompressFormat.JPEG,50,stream);
                byte[] data = stream.toByteArray();
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,rawContentInsertIndex)
                                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                                .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO,data)
                        .build());
            }

            activity.getContentResolver().applyBatch(ContactsContract.AUTHORITY,ops);

        } catch (RemoteException e) {
            throw new RuntimeException(e);
        } catch (OperationApplicationException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public void RemoveContact(int dataId) throws Exception{
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        ops.add(ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                .withSelection(ContactsContract.Data._ID + "=?",new String[]{String.valueOf(dataId)})
                .build());
        activity.getContentResolver().applyBatch(ContactsContract.AUTHORITY,ops);
    }
    public void EditContact(Contact c){
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        // Update contact name
        ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(ContactsContract.Data._ID + "=?", new String[]{String.valueOf(c.getId())})
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, c.getName())
                .build());

        // Update contact phone number
        ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(ContactsContract.Data._ID + "=? AND " + ContactsContract.Data.MIMETYPE + "=?",
                        new String[]{String.valueOf(c.getId()), ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE})
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, c.getPhone())
                .build());

        // Update contact photo (if available)
        if (c.getImage() != null) {
            try {
                InputStream input = activity.getContentResolver().openInputStream(Uri.parse(c.getImage()));
                Bitmap bit = BitmapFactory.decodeStream(input);
                if (bit != null) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bit.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                    byte[] data = stream.toByteArray();
                    ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                            .withSelection(ContactsContract.Data._ID + "=? AND " + ContactsContract.Data.MIMETYPE + "=?",
                                    new String[]{String.valueOf(c.getId()), ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE})
                            .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, data)
                            .build());
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        try {
            activity.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (RemoteException | OperationApplicationException e) {
            throw new RuntimeException(e);
        }
    }
}
