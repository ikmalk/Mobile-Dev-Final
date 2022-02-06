package com.example.mobiledev.activity;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mobiledev.R;

import java.util.List;

public class ScreenColorAdapter extends ArrayAdapter<String> {

    private Activity context;
    private int[] image;
    private LayoutInflater layoutInflater;

    public ScreenColorAdapter(Activity context, int resource, List<String> list){
        super(context, resource, list);
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        View rowView = layoutInflater.inflate(R.layout.text_color_layout, null,true);
        String user = getItem(position);
        ImageView imageView = (ImageView)rowView.findViewById(R.id.spinner_textCol);
        imageView.setImageResource(R.drawable.ic_text_col);
        return rowView;
    }


    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        if(convertView == null)
            convertView = layoutInflater.inflate(R.layout.text_color_layout, parent,false);

        String col = getItem(position);
        ImageView imageView = (ImageView)convertView.findViewById(R.id.spinner_textCol);
        imageView.setImageResource(R.drawable.ic_color_circle);
        setColor(imageView, col);

        return convertView;
    }

    private void setColor(ImageView iv, String col){

        iv.setColorFilter(Color.parseColor(col), PorterDuff.Mode.SRC_ATOP);

    }


}
