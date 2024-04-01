package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.pm.PackageManager;
import android.Manifest;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.content.Intent;
import android.provider.Telephony;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_READ_CONTACTS = 401;
    private Button btnadd,btnDel,btnEdit;
    private ListView listView;
    private ArrayList<Contact>  listContact;
    private Adapter adapter;
    public ConnectionDb db;
    private ContentProvider cp;
    public EditText search;

    private int itemselectID ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView  = findViewById(R.id.list_item);
        btnDel = findViewById(R.id.btnXoa);
        btnadd = findViewById(R.id.btnThem);
        btnEdit = findViewById(R.id.btnSua);
        search = findViewById(R.id.txtSearch);

        listContact = new ArrayList<Contact>();

//        db = new ConnectionDb(this,"ContactDB",null,1);
//        db.addContact(new Contact(1,"The","0001111",false,"https://media.baoquangninh.vn/upload/image/202307/medium/2100199_5fc049b4e26927b1f8e9720acdec299c.jpg"));
//        db.addContact(new Contact(2,"Vu","0001111",false,"https://media.baoquangninh.vn/upload/image/202307/medium/2100199_5fc049b4e26927b1f8e9720acdec299c.jpg"));
//        db.addContact(new Contact(3,"huong","0001111",false,"https://media.baoquangninh.vn/upload/image/202307/medium/2100199_5fc049b4e26927b1f8e9720acdec299c.jpg"));

//        listContact = db.getAllContact();
//        adapter = new Adapter(this,listContact);
//        listView.setAdapter(adapter);

        showContact();
        registerForContextMenu(listView);

        btnDel.setOnClickListener(v ->{
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("Ban Co muon soa khong");

            builder.setPositiveButton("co", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DeleteContact();
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("khong", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        });
        btnadd.setOnClickListener(v ->{
            Intent i = new Intent(this,SecondActivity.class);
            startActivityForResult(i,100);
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                itemselectID  = position;
                return false;
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,SecondActivity.class);
                Bundle b = new Bundle();
                Contact c = listContact.get(itemselectID);
                b.putInt("eid",c.getId());
                b.putString("ename",c.getName());
                b.putString("ephone",c.getPhone());
                b.putString("eimage",c.getImage());
                b.putBoolean("estatus",c.isStatus());

                i.putExtras(b);
                startActivityForResult(i,250);
            }
        });
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    private void DeleteContact(){
        for(int i=0;i<listContact.size();){
            if(listContact.get(i).isStatus() == true){
                listContact.remove(i);
                adapter.notifyDataSetChanged();
            }
            else{
                i++;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && resultCode == 180){
            AddItem(data);
        }
        if(resultCode == 210 && requestCode == 250){
            Bundle b = data.getExtras();
            int id = b.getInt("id");
            String name = b.getString("Name");
            String phone = b.getString("Phone");
            boolean stat = b.getBoolean("status");
            String Image = b.getString("image");
            Contact c = new Contact(id,name,phone,stat,Image);
            ContentProvider cp = new ContentProvider(this);
            cp.EditContact(c);
            listContact.set(itemselectID,c);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater flater = new MenuInflater(this);
        flater.inflate(R.menu.context_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.mnuName) {
            Toast.makeText(MainActivity.this,"hehe",Toast.LENGTH_SHORT).show();
        }
        if(item.getItemId() == R.id.mnuPhone) {
            Toast.makeText(MainActivity.this,"hehe",Toast.LENGTH_SHORT).show();
        }
        if(item.getItemId() == R.id.mnuBroad) {
            Toast.makeText(MainActivity.this,"hehe",Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        Contact c = listContact.get(itemselectID);
        if(item.getItemId() == R.id.mnuSua){
            Intent i = new Intent(MainActivity.this,SecondActivity.class);
            Bundle b = new Bundle();
            b.putInt("eid",c.getId());
            b.putString("ename",c.getName());
            b.putString("ephone",c.getPhone());
            b.putString("eimage",c.getImage());
            b.putBoolean("estatus",c.isStatus());

            i.putExtras(b);
            startActivityForResult(i,250);
        }
        if(item.getItemId() == R.id.mnuXoa){
            try {
                cp.RemoveContact(c.getId());
                listContact.remove(c);
                adapter.notifyDataSetChanged();

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
//            db.DeleteContact(itemselectID);
//            listContact.remove(itemselectID);
//            adapter.notifyDataSetChanged();


        }
        return super.onContextItemSelected(item);
    }
    private void AddItem(Intent data){
        Bundle b = data.getExtras();
        int id = b.getInt("id");
        String name = b.getString("Name");
        String phone = b.getString("Phone");
        boolean stat = b.getBoolean("status");
        String Image = b.getString("image");
        Contact c = new Contact(id,name,phone,stat,Image);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS},301);
        }else{
            cp = new ContentProvider(this);
            cp.AddContact(c);
            listContact.add(c);
            adapter.notifyDataSetChanged();
        }

    }
    public void showContact(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},PERMISSION_REQUEST_READ_CONTACTS);
        }else{
            cp = new ContentProvider(this);
            listContact = cp.getAllContact();
            adapter = new Adapter(this,listContact);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }
}