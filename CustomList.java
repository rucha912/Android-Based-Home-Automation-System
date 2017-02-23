package com.example.wirelessharddrive;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomList extends ArrayAdapter<String>
{
private final Activity context;
private final String[] substr;
private final Integer[] imageId;

public CustomList(Activity context,String[] substr, Integer[] imageId) 
{
	super(context, R.layout.activity_listview, substr);
	this.context = context;
	this.substr = substr;
	this.imageId = imageId;
}
@Override
public View getView(int position, View view, ViewGroup parent) 
{
	LayoutInflater inflater = context.getLayoutInflater();
	View rowView= inflater.inflate(R.layout.activity_listview, null, true);
	TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
	ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
	txtTitle.setText(substr[position]);
	imageView.setImageResource(imageId[position]);
	return rowView;
}
}
