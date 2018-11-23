package com.gzdefine.huangcuangoa.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gzdefine.huangcuangoa.R;
import com.gzdefine.huangcuangoa.entity.BpmType;

import java.util.List;


public class BpmTypeAdapter extends BaseAdapter  {
    private List<BpmType> list = null;
    private Context mContext;

    public BpmTypeAdapter(Context mContext, List<BpmType> list) {
        super();
        this.list = list;
        this.mContext = mContext;
    }

    // when the data changed , call updateListView() to update
    public void updateListView(List<BpmType> list) {
        this.list = list;
        notifyDataSetChanged();
    }


    public int getCount() {
        return this.list.size();
    }

    public Object getItem(int pos) {
        return this.list.get(pos);
    }

    public long getItemId(int pos) {
        return pos;
    }

    public View getView(int pos, View view, ViewGroup group) {
        ViewHolder viewHolder = null;

        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.layout_bpm_type_adapter, null);
            viewHolder.tvName = (TextView) view.findViewById(R.id.name);
            view.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) view.getTag();

        // get position and get the first letter
         BpmType mContent =list.get(pos);
        viewHolder.tvName.setText(mContent.name);
        return view;
    }

    final static class ViewHolder {
        TextView tvName;
    }






}
