package com.gzdefine.huangcuangoa.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gzdefine.huangcuangoa.R;
import com.gzdefine.huangcuangoa.entity.EMConversation;
import com.gzdefine.huangcuangoa.util.ToastUtil;
import com.gzdefine.huangcuangoa.util.ViewHolder;
import com.gzdefine.huangcuangoa.view.CircleImageView;
import com.gzdefine.huangcuangoa.view.swipe.SwipeLayout;

import java.util.List;

public class NewMsgAdpter extends BaseAdapter {
    protected Context context;
    private List<EMConversation> conversationList;

    public NewMsgAdpter(Context ctx, List<EMConversation> objects) {
        context = ctx;
        conversationList = objects;
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
                    R.layout.layout_item_msg, parent, false);
        }
        CircleImageView img_avar = ViewHolder.get(convertView, R.id.contactitem_avatar_iv);
        TextView txt_name = ViewHolder.get(convertView, R.id.txt_name);
        TextView txt_state = ViewHolder.get(convertView, R.id.txt_state);
        TextView txt_del = ViewHolder.get(convertView, R.id.txt_del);
        TextView txt_content = ViewHolder.get(convertView, R.id.txt_content);
        TextView txt_time = ViewHolder.get(convertView, R.id.txt_time);
        TextView unreadLabel = ViewHolder.get(convertView,
                R.id.unread_msg_number);
        SwipeLayout swipe = ViewHolder.get(convertView, R.id.swipe);
        swipe.setShowMode(SwipeLayout.ShowMode.LayDown);
        swipe.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
            @Override
            public void onDoubleClick(SwipeLayout layout, boolean surfac) {

            }
        });

//        Glide.with(context).load("http://img1.imgtn.bdimg.com/it/u=2285269470,2718118513&fm=214&gp=0.jpg").into(img_avar);

        final EMConversation conversation = conversationList.get(position);
        unreadLabel.setText(String.valueOf(conversation
                .getNumber()));
        unreadLabel.setVisibility(View.VISIBLE);
        Glide.with(context).load(conversation.getHead()).into(img_avar);
        txt_name.setText(conversation.getUserName());
        txt_state.setText("未读");
        txt_content.setText(conversation.getMsg());
        txt_time.setText(conversation.getTime());
        txt_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conversationList.remove(position);
                notifyDataSetChanged();
                ToastUtil.showShort(context, "删除该聊天");
            }
        });
        return convertView;
    }


    /**
     * 根据消息内容和消息类型获取消息内容提示
     *
     * @param message
     * @param context
     * @return
     */
    private String getMessageDigest(EMConversation message, Context context) {
        String digest = "";
        switch (message.getType()) {
            case RenWu: // 位置消息
                break;
            case YouJian: // 图片消息
                break;
            case ShenPi:// 语音消息
                break;
            case LiuCheng: // 视频消息
                break;
            default:
                System.err.println("error, unknow type");
                return "";
        }
        return digest;
    }

}
