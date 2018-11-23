package com.gzdefine.huangcuangoa.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gzdefine.huangcuangoa.R;
import com.gzdefine.huangcuangoa.entity.Search;
import com.gzdefine.huangcuangoa.listener.ListItemClickHelp;
import com.gzdefine.huangcuangoa.util.ListDataSave;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchAdapter extends BaseAdapter {
    private final Context context;
    private List<Search> list;
    ListDataSave dataSave;
    private String strJson;
    Map<String, Object> map = new HashMap<String, Object>();
    private ArrayList<Map<String, Object>> listMap;
    private ArrayList List = new ArrayList();
    private ListItemClickHelp callback;

    public SearchAdapter(Context context, ListItemClickHelp callback) {
        this.context = context;
        this.list = new ArrayList<Search>();
        listMap = new ArrayList<Map<String, Object>>();
        dataSave = new ListDataSave(context, "baiyu");
        this.callback = callback;
    }


    @Override
    public int getCount() {
        return list.size();
    }


    @Override
    public Search getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class holder {
        private TextView name;
        private RelativeLayout img;
    }

    @Override
    public View getView(final int position, View view, final ViewGroup parent) {
        final holder holder;
        if (view == null) {
            view = View.inflate(context, R.layout.itme_list_search, null);
            holder = new holder();

            holder.name = (TextView) view.findViewById(R.id.name);
            holder.img = (RelativeLayout) view.findViewById(R.id.img);

//            holder.inOffFlag = (TextView) view.findViewById(R.id.inOffFlag);


            view.setTag(holder);
        } else {
            holder = (holder) view.getTag();
        }

        Search search = list.get(position);
        Log.d("reg", "姓名/部门名称:" + search.getDepartOrUser());
        Log.d("reg", "考勤结果:" + search.getQ_regFlag_SN_EQ());
        Log.d("reg", "开始日期:" + search.getQ_registerTime_D_GE());
        Log.d("reg", "结束日期:" + search.getQ_registerTime_D_LE());

        if (null != search.getDepartOrUser() && !search.getDepartOrUser().equals("")) {
            Log.d("reg", "1");
            holder.name.setText("关键字:" + search.getDepartOrUser());
        } else {
            StringBuilder sb = new StringBuilder();
            if (null != search.getQ_regFlag_SN_EQ() && !search.getQ_regFlag_SN_EQ().equals("")) {
                if (search.getQ_regFlag_SN_EQ().equals("1")) {
                    sb.append("结果:正常");
                } else if (search.getQ_regFlag_SN_EQ().equals("2")) {
                    sb.append("结果:迟到");
                } else if (search.getQ_regFlag_SN_EQ().equals("3")) {
                    sb.append("结果:未签到");
                }
            }
            if (null != search.getQ_registerTime_D_GE() && !search.getQ_registerTime_D_GE().equals("")) {
                if (sb.length() > 0) {
                    sb.append("/");
                }
                sb.append("开始:" + search.getQ_registerTime_D_GE());
            }

            if (null != search.getQ_registerTime_D_LE() && !search.getQ_registerTime_D_LE().equals("")) {
                if (sb.length() > 0) {
                    sb.append("/");
                }
                sb.append("结束:" + search.getQ_registerTime_D_LE());
            }

            Log.d("reg", "标题处理结果:" + sb.toString());
            holder.name.setText(sb.toString());
        }


//        if (null != search.getDepartOrUser() && !search.getDepartOrUser().equals("")) {
//            Log.d("reg", "1");
//            holder.name.setText(search.getDepartOrUser());
//        } else if ((null != search.getQ_regFlag_SN_EQ() && !search.getQ_regFlag_SN_EQ().equals("") && null != search.getQ_registerTime_D_LE() &&
//                !search.getQ_registerTime_D_LE().equals("") && null != search.getQ_registerTime_D_GE() && !search.getQ_registerTime_D_GE().equals(""))) {
//            Log.d("reg", "2");
//            if (search.getQ_regFlag_SN_EQ().equals("1")) {
//                holder.name.setText("结果:正常" + "/开始:" + search.getQ_registerTime_D_GE() + "/结束:" + search.getQ_registerTime_D_LE());
//            } else if (search.getQ_regFlag_SN_EQ().equals("2")) {
//                holder.name.setText("结果:迟到" + "/开始:" + search.getQ_registerTime_D_GE() + "/结束:" + search.getQ_registerTime_D_LE());
//            } else if (search.getQ_regFlag_SN_EQ().equals("3")) {
//                holder.name.setText("结果:未签到" + "/开始:" + search.getQ_registerTime_D_GE() + "/结束:" + search.getQ_registerTime_D_LE());
//            }
//        } else if ((null != search.getQ_regFlag_SN_EQ() && !search.getQ_regFlag_SN_EQ().equals("") && null != search.getQ_registerTime_D_GE() && !search.getQ_registerTime_D_GE().equals(""))) {
//            Log.d("reg", "3");
//            if (search.getQ_regFlag_SN_EQ().equals("1")) {
//                holder.name.setText("结果:正常" + "/" + search.getQ_registerTime_D_LE());
//            } else if (search.getQ_regFlag_SN_EQ().equals("2")) {
//                holder.name.setText("结果:迟到" + "/" + search.getQ_registerTime_D_LE());
//            } else if (search.getQ_regFlag_SN_EQ().equals("3")) {
//                holder.name.setText("结果:未签到" + "/" + search.getQ_registerTime_D_LE());
//            }
//        } else if ((null != search.getQ_regFlag_SN_EQ() && !search.getQ_regFlag_SN_EQ().equals("") && null != search.getQ_registerTime_D_LE() && !search.getQ_registerTime_D_LE().equals(""))) {
//            Log.d("reg", "4");
//            if (search.getQ_regFlag_SN_EQ().equals("1")) {
//                holder.name.setText("结果:正常" + "/" + search.getQ_registerTime_D_LE());
//            } else if (search.getQ_regFlag_SN_EQ().equals("2")) {
//                holder.name.setText("结果:迟到" + "/" + search.getQ_registerTime_D_LE());
//            } else if (search.getQ_regFlag_SN_EQ().equals("3")) {
//                holder.name.setText("结果:未签到" + "/" + search.getQ_registerTime_D_LE());
//            }
//        } else if ((null != search.getQ_registerTime_D_LE() && !search.getQ_registerTime_D_LE().equals("") && null != search.getQ_registerTime_D_GE() && !search.getQ_registerTime_D_GE().equals(""))) {
//            Log.d("reg", "5");
//            holder.name.setText("开始:"+search.getQ_registerTime_D_GE() + "/结束:" + search.getQ_registerTime_D_LE());
//        } else if (null != search.getQ_regFlag_SN_EQ() && !search.getQ_regFlag_SN_EQ().equals("")) {
//            Log.d("reg", "6");
//            if (search.getQ_regFlag_SN_EQ().equals("1")) {
//                holder.name.setText("结果:正常" + "/" + search.getQ_registerTime_D_LE());
//            } else if (search.getQ_regFlag_SN_EQ().equals("2")) {
//                holder.name.setText("结果:迟到" + "/" + search.getQ_registerTime_D_LE());
//            } else if (search.getQ_regFlag_SN_EQ().equals("3")) {
//                holder.name.setText("结果:未签到" + "/" + search.getQ_registerTime_D_LE());
//            }
//            holder.name.setText(search.getQ_regFlag_SN_EQ());
//        } else if (null != search.getQ_registerTime_D_GE() && !search.getQ_registerTime_D_GE().equals("")) {
//            Log.d("reg", "7");
//            holder.name.setText(search.getQ_registerTime_D_GE());
//        } else if (null != search.getQ_registerTime_D_LE() && !search.getQ_registerTime_D_LE().equals("")) {
//            Log.d("reg", "8");
//            holder.name.setText(search.getQ_registerTime_D_LE());
//        }


        final View vie = view;
        final int p = position;
        final int one = holder.img.getId();
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("reg", "p:" + p);
                callback.onClick(vie, parent, p, one);
            }
        });


//        holder.img.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (list.size() > 0) {
//                    try {
//
//                        JSONArray jsonArray = new JSONArray(strJson);
//                        JSONObject jsonObject = new JSONObject(jsonArray.getString(0));
//                        Log.d("reg","jsonObject:"+jsonObject);
//                        JSONArray json = jsonObject.getJSONArray("depart");
//                        for (int i = 0; i < json.length(); i++) {
//                            json.remove(position);
//                            String o = json.getString(i);
//                        Log.d("reg","o:"+o);
//                            List.add(o);
//                        }
////                        json.remove(position);
//                        Log.d("reg","json:"+json);
//                        map.put("depart", List);
//                        listMap.add(map);
//                        dataSave.setDataList("listMap", listMap);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    list.remove(position);
//                    notifyDataSetChanged();
//                }
//            }
//        });
        return view;
    }

    // 设置数据
    public void setData(List<Search> data) {
        this.list = data;
    }

    // 添加数据
    public void addData(Search bean) {
        // 下标 数据
        list.add(0, bean);
    }


    public void clear() {
        list.clear();
    }

    public boolean addAll(Collection<? extends Search> collection) {
        boolean pa = list.addAll(collection);
        return pa;
    }

    public void DataSave() {
        notifyDataSetChanged();
    }

    public boolean add(Search object) {
        return list.add(object);
    }

    // 删除一个数据
    public void removeData(int position) {
        list.remove(position);
    }


}
