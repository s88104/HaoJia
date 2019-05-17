package com.example.haojia;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;
import android.content.Intent;


import com.androidquery.AQuery;

public class ImageAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	AQuery aQuery;
	// private static final int[] ids = { R.drawable.test1, R.drawable.test2,
	// R.drawable.test3 };
	
	private String[] ids;

	public ImageAdapter(Context context) {
		mContext = context;
		aQuery = new AQuery(context);
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public ImageAdapter(Context context, String[] ids) {
		mContext = context;
		aQuery = new AQuery(context);
		this.ids = ids;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return Integer.MAX_VALUE; // 返回最大值来实现循环
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.image_item, null);
		}

		aQuery.id(((ImageView) convertView.findViewById(R.id.imgView))).image(
				ids[position % ids.length]);

		
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Intent intent = new Intent(mContext,DetailActivity.class);
				// Bundle bundle = new Bundle();
				// bundle.putInt("image_id", ids[position%ids.length]);
				// intent.putExtras(bundle);
				// mContext.startActivity(intent);
				
				
				Uri uri = Uri.parse("https://686b7c72e12dd7eb61467c66e4f4a08c50413c8e.googledrive.com/host/0B6Q-ykLT4CtpaXJBdDFVUDdYa0U/viewfolw_photo1.png");  
				Intent itt = new Intent(Intent.ACTION_VIEW, uri);  
				mContext.startActivity(itt);
				
				//Toast.makeText(mContext, "点击了第" + (position%ids.length + 1) + "张图片",
					//	Toast.LENGTH_SHORT).show();

			}
			
		});
		return convertView;
	}

}
