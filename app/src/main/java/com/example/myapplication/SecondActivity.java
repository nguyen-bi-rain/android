package com.example.myapplication;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.nio.BufferUnderflowException;
import java.util.ArrayList;

public class SecondActivity extends AppCompatActivity {
    private EditText tid,name,phone;
    private CheckBox check;
    private ImageView img;
    private Button btnadd,btnEdit;
    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        tid = findViewById(R.id.txtid);
        name = findViewById(R.id.txtname);
        phone = findViewById(R.id.txtphone);

        check = findViewById(R.id.status);
        img = findViewById(R.id.image);
        btnadd = findViewById(R.id.btnAdd);
        btnEdit = findViewById(R.id.btnEdit);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if(b != null){
            Integer bid = b.getInt("eid");
            tid.setText(bid.toString());
            name.setText(b.getString("ename"));
            phone.setText(b.getString("ephone"));
            check.setChecked(b.getBoolean("estatus"));
            imagePath = b.getString("eimage");
            Uri image = Uri.parse(imagePath);
            Glide.with(this).load(image).into(img);
        }

        img.setOnClickListener(v ->{
            Intent i = new Intent();
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);

            startActivityForResult(Intent.createChooser(i,"Select Picture"),200);
        });

        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = Integer.parseInt(tid.getText().toString());
                String tname = name.getText().toString();
                String tphone  = phone.getText().toString();
                boolean ts = check.isChecked();
                    Intent i = new Intent();
                    Bundle b = new Bundle();
                    b.putString("Name",tname);
                    b.putInt("id",id);
                    b.putString("Phone",tphone);
                    b.putBoolean("status",ts);
                    b.putString("image",imagePath);
                    i.putExtras(b);
                    setResult(180,i);
                    finish();
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int id = Integer.parseInt(tid.getText().toString());

                String tname = name.getText().toString();
                String tphone  = phone.getText().toString();
                boolean ts = check.isChecked();

                    Intent i = new Intent();
                    Bundle b = new Bundle();
                    b.putString("Name",tname);
                    b.putInt("id",id);
                    b.putString("Phone",tphone);
                    b.putBoolean("status",ts);
                    b.putString("image",imagePath);
                    i.putExtras(b);
                    setResult(210,i);
                    finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == 200){
                Uri selectImage = data.getData();

                if(selectImage != null){
                    imagePath = selectImage.toString();
                    Toast.makeText(this,imagePath,Toast.LENGTH_SHORT).show();
                    img.setImageURI(selectImage);
                }
            }
        }
    }


}
