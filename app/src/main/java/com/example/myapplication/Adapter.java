package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class Adapter extends BaseAdapter implements Filterable {
    private Activity activity;
    private ArrayList<Contact> data;
    private ArrayList<Contact> databackup;
    private LayoutInflater inflate;
    public Adapter(Activity activity,ArrayList<Contact> item){
        this.activity = activity;
        this.data = item;
        inflate = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return data.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if(v == null){
            v = inflate.inflate(R.layout.contact_item,null);
        }

        CheckBox box = v.findViewById(R.id.check);
        TextView name = v.findViewById(R.id.name);
        TextView phone = v.findViewById(R.id.phone);
        ImageView img = v.findViewById(R.id.image);

        name.setText(data.get(position).getName());
        phone.setText(data.get(position).getPhone());
        Uri path = Uri.parse(data.get(position).getImage());
        Glide.with(activity).load(path).into(img);
        box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                data.get(position).setStatus(isChecked);
            }
        });
        return v;
    }

    @Override
    public Filter getFilter() {
        Filter f =  new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults fr = new FilterResults();
                //backup du lieu: luu tam data vao databackup
                if(databackup == null){
                    databackup = new ArrayList<>(data);

                }
                //neu chuoi de filter la rong thi khoi phuc du lieu
                if(constraint == null || constraint.length() == 0){
                    fr.count = databackup.size();
                    fr.values = databackup;
                }
                //neu khong thi thuc hien filter
                else{
                    ArrayList<Contact> newdata = new ArrayList<>();
                    for(Contact c : databackup){
                        if(c.getName().toLowerCase().contains(constraint.toString().toLowerCase())){
                            newdata.add(c);
                        }
                    }
                    fr.count = newdata.size();
                    fr.values=newdata;
                }
                return fr;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                data = new ArrayList<Contact>();
                ArrayList<Contact> tmp = (ArrayList<Contact>) results.values;
                data.addAll(tmp);
                notifyDataSetChanged();
            }
        };
        return f;
    }
}
