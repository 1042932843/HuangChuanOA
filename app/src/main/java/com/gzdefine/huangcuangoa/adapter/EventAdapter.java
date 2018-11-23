package com.gzdefine.huangcuangoa.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gzdefine.huangcuangoa.R;
import com.gzdefine.huangcuangoa.entity.Event;
import com.gzdefine.huangcuangoa.util.ViewHolder;

import java.util.List;

public class EventAdapter extends BaseAdapter {
    protected Context context;
    private List<Event> eventsList;

    public EventAdapter(Context context, List<Event> eventsList) {
        this.context = context;
        this.eventsList = eventsList;
    }
    // when the data changed , call updateListView() to update
    public void updateListView(List<Event> list) {
        this.eventsList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return eventsList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.layout_item_metting, parent, false);
        }
        TextView txt_text = ViewHolder.get(convertView, R.id.tv_text);
        TextView txt_time = ViewHolder.get(convertView, R.id.tv_time);
        if (eventsList.get(position)!=null){
            txt_text.setText(""+eventsList.get(position).getTitle());
            txt_time.setText(""+eventsList.get(position).getStartTime()+" 到 "+eventsList.get(position).getEndTime());
        }
        return convertView;
    }



}
