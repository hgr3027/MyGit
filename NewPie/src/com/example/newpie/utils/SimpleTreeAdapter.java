package com.example.newpie.utils;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.newpie.R;


public class SimpleTreeAdapter<T> extends TreeListViewAdapter<T>
{

	public SimpleTreeAdapter(ListView mTree, Context context, List<T> datas,
			int defaultExpandLevel) throws IllegalArgumentException,
			IllegalAccessException
	{
		super(mTree, context, datas, defaultExpandLevel);
	}

	@Override
	public View getConvertView(Node node , int position, View convertView, ViewGroup parent)
	{
		
		ViewHolder viewHolder = null;
		if (convertView == null)
		{
			convertView = mInflater.inflate(R.layout.list_item, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.icon = (ImageView) convertView
					.findViewById(R.id.id_treenode_icon);
			viewHolder.label = (TextView) convertView
					.findViewById(R.id.id_treenode_label);
			viewHolder.userPhoto = (com.example.newpie.utils.RoundImageView) convertView
					.findViewById(R.id.userPhoto);
			convertView.setTag(viewHolder);

		} else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if (node.getIcon() == -1)
		{
			viewHolder.icon.setVisibility(View.GONE);
			viewHolder.userPhoto.setVisibility(View.VISIBLE);
			viewHolder.userPhoto.setImageBitmap(node.getUserPhoto());
		} else
		{
			viewHolder.userPhoto.setVisibility(View.GONE);
			viewHolder.icon.setVisibility(View.VISIBLE);
			viewHolder.icon.setImageResource(node.getIcon());
		}
		viewHolder.label.setText(node.getName());
		
		return convertView;
	}

	private final class ViewHolder
	{
		ImageView icon;
		TextView label;
		com.example.newpie.utils.RoundImageView userPhoto;
	}

}
