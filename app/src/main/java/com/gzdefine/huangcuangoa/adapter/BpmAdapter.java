package com.gzdefine.huangcuangoa.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gzdefine.huangcuangoa.App;
import com.gzdefine.huangcuangoa.R;
import com.gzdefine.huangcuangoa.entity.Bpm;
import com.gzdefine.huangcuangoa.entity.LoginResponse;
import com.gzdefine.huangcuangoa.util.Constant;
import com.gzdefine.huangcuangoa.util.Ok;
import com.gzdefine.huangcuangoa.util.ViewHolder;
import com.gzdefine.huangcuangoa.view.CircleImageView;
import com.gzdefine.huangcuangoa.view.swipe.SwipeLayout;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class BpmAdapter extends BaseAdapter {
    protected Activity context;
    private List<Bpm> conversationList;
    LoginResponse login;
    private  bpmHttp bpm;
    private Bpm conversation;

    public BpmAdapter(Activity ctx, List<Bpm> objects) {
        context = ctx;
        conversationList = objects;
        login=App.me().login();
          bpm=new bpmHttp(context);
    }


    @Override
    public int getCount() {
        return conversationList.size();
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
                    R.layout.layout_item_bpm, parent, false);
        }
        CircleImageView img_avar = ViewHolder.get(convertView, R.id.contactitem_avatar_iv);
        TextView txt_content = ViewHolder.get(convertView, R.id.txt_content);
        TextView txt_time = ViewHolder.get(convertView, R.id.txt_time);
        final TextView txt_state = ViewHolder.get(convertView, R.id.txt_state);
        LinearLayout txt_del = ViewHolder.get(convertView, R.id.txt_del);
        SwipeLayout swipeLayout = ViewHolder.get(convertView, R.id.swipe);
        swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        conversation = conversationList.get(position);
        if (login!=null){
            String imageUrl=Constant.DOMAIN+"sys/core/file/imageView.do?thumb=true&fileId="+login.getPhoto();
            Glide.with(context).load(imageUrl).into(img_avar);
        }
        txt_state.setText(conversation.getStatus());
        txt_content.setText(conversation.getSubject());
        txt_time.setText(conversation.getCreateTime());
        txt_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bpm.delete(conversation.getInstId());
                txt_state.setText("已作废");
            }
        });
        if (conversation.getStatus()==null){
            conversation.setStatus("");
        }
        if (conversation.getStatus().contains("已结束")){
            swipeLayout.setSwipeEnabled(false);
        }
        return convertView;
    }


    // when the data changed , call updateListView() to update
    public void updateListView(List<Bpm> list) {
        this.conversationList = list;
        notifyDataSetChanged();
    }
    class bpmHttp extends Ok {

        public bpmHttp(Context context){
            super(context);
        }
        public void delete(String instId) {
            super.post(Constant.DOMAIN + "mobileOlineController/discardInst.do","instId",instId);
        }

        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String re = response.body().string();
            Log.d("reg", "result:" + re);
            App.me().checklogin(context,re);

                // JSON串转用户对象列表
                try {
                    JSONObject obj=new JSONObject(re);
                    final String message=obj.getString("message");
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            App.me().toast(message);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }


        }
    }
}
