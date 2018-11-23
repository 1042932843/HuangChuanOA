package com.gzdefine.huangcuangoa.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.gzdefine.huangcuangoa.App;
import com.gzdefine.huangcuangoa.R;
import com.gzdefine.huangcuangoa.entity.SortModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ProcessAdapter extends BaseAdapter implements SectionIndexer {
    private List<SortModel> list = null;
    private Context mContext;
    public HashMap<String, Boolean> ischecks;
    public ArrayList<String> peoples;
    private String pr;

    String userId;
    String userName;

    public ProcessAdapter(Context mContext, List<SortModel> list, String Pro) {
        super();
        this.list = list;
        this.mContext = mContext;
        ischecks = new HashMap<>();
        peoples = new ArrayList<>();
        userId = App.me().login().getUserId();
        userName = App.me().login().getFullname();
        pr = Pro;
    }

    //取消全选
    public void checkNoAll(List<SortModel> list) {
        updateListView(list);
        for (int i = 0; i < list.size(); i++) {
            ischecks.put("" + list.get(i).getId(), false);
        }
        ischecks.put(userId, true);
        peoples.add(userName);
    }

    //全选
    public void checkAll(List<SortModel> list) {
        for (int i = 0; i < list.size(); i++) {
            ischecks.put("" + list.get(i).getId(), true);
            peoples.remove("" + list.get(i).getName());
            peoples.add("" + list.get(i).getName());
        }
        updateListView(list);
    }


    // when the data changed , call updateListView() to update
    public void updateListView(List<SortModel> list) {
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

    public View getView(final int pos, View view, ViewGroup group) {
        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.peopel_list_item, null);
            viewHolder.tvName = (TextView) view.findViewById(R.id.txt_user_name);
            viewHolder.checkbox = (CheckBox) view.findViewById(R.id.checkbox_peopel);
            view.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) view.getTag();
        viewHolder.tvName.setText(this.list.get(pos).getName());
        if (pr.equals("1")) {
            viewHolder.checkbox.setVisibility(View.GONE);
        } else if (pr.equals("2")) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    check(pos);
                }
            });
            viewHolder.checkbox.setVisibility(View.VISIBLE);
        }

        if (ischecks.get("" + this.list.get(pos).getId()) != null) {
            viewHolder.checkbox.setChecked(ischecks.get("" + this.list.get(pos).getId()));
        }

        return view;
    }

    public void check(int position) {
        Log.d("max", "position:" + position);
        SortModel Sortmodel = list.get(position);
        if (ischecks.get(Sortmodel.getId())) {
            ischecks.put(Sortmodel.getId(), false);
            peoples.remove(Sortmodel.getName());
        } else {
            ischecks.put(Sortmodel.getId(), true);
            peoples.add(Sortmodel.getName());
        }
        for (int i = 0; i < peoples.size(); i++) {
            Log.d("max", ":=======" + peoples.get(i));
        }
        notifyDataSetChanged();
    }

    final static class ViewHolder {
        CheckBox checkbox;
        TextView tvName;
    }

    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = list.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section)
                return i;
        }

        return -1;
    }

    public int getSectionForPosition(int arg0) {
        return this.list.get(arg0).getSortLetters().charAt(0);
    }


    public Object[] getSections() {
        return null;
    }

    private String getAlpha(String str) {
        String sortStr = str.trim().substring(0, 1).toUpperCase();
        if (sortStr.matches("[A-Z]"))
            return sortStr;
        else
            return "#";
    }

}
