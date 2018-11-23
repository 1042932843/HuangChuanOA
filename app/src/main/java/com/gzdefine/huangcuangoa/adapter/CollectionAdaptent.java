package com.gzdefine.huangcuangoa.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gzdefine.huangcuangoa.Model.Collectio;
import com.gzdefine.huangcuangoa.R;
import com.gzdefine.huangcuangoa.util.Constant;
import com.gzdefine.huangcuangoa.util.SPUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by lenovo on 2017/2/27.
 */
public class CollectionAdaptent extends BaseAdapter {
    private final Context context;
    private List<Collectio> list;
    private HashSet<Integer> hs;
    //    private List<RedFlag> olist;
    // 存储勾选框状态的map集合
    private Map<Integer, Boolean> isCheck = new HashMap<Integer, Boolean>();
    public boolean flage = false;
    private OkHttpClient okHttpClient;
    Handler handler = new Handler();
    public CollectionAdaptent(Context context) {
        this.context = context;
        this.list = new ArrayList<Collectio>();
        this.hs = new HashSet<Integer>();
        okHttpClient = new OkHttpClient();
    }

/*    public void SearchCity(String city) {
        this.list = Search(city);
        notifyDataSetChanged();
    }*/

    /*   private List<Ranks> Search(String city) {
           if (city != null && city.length() > 0) {
               ArrayList<Ranks> area = new ArrayList<Ranks>();
               for (Ranks a : this.olist) {
                   if (a.getNewsTitle().indexOf(city) != -1) {
                       area.add(a);
                   }
               }
               return area;
           } else {
               return this.olist;
           }

       }*/
    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public Collectio getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class holder {
        TextView ranks_tiele;
        TextView ranks_date;
        TextView clickRate;
        ImageView img;
        public CheckBox checkboxOperateData;
        private RelativeLayout rela;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final holder holder;
        if (view == null) {
            view = View.inflate(context, R.layout.itme_list_collection, null);
            holder = new holder();

            holder.ranks_tiele = (TextView) view.findViewById(R.id.text);
            holder.rela = (RelativeLayout) view.findViewById(R.id.rela);
            holder.ranks_date = (TextView) view.findViewById(R.id.text_1);
            holder.clickRate = (TextView) view.findViewById(R.id.clickRate);
            holder.img = (ImageView) view.findViewById(R.id.img);
            holder.checkboxOperateData = (CheckBox) view.findViewById(R.id.checkbox_operate_data);
            view.setTag(holder);
        } else {
            holder = (holder) view.getTag();
        }

        final Collectio item = list.get(position);
        if (null != item) {
            holder.ranks_tiele.setText(item.getSUBJECT_());

            holder.clickRate.setText("浏览数 : " + item.getREAD_TIMES_());


            if (item.getCREATE_TIME_()!=null){
                String str11 = item.getCREATE_TIME_().substring(0, item.getCREATE_TIME_().length() - 3);
                String str = timedate(str11);
                Log.d("reg", "str:" + str);
                //把截取后面的8位截取掉
                String itme=str.substring(0,str.length()-3);
//        String date=str.substring(0,strlastIndexOf("."));
                holder.ranks_date.setText(itme);
            }

            if (item.getIMG_FILE_ID_().isEmpty()) {
                holder.img.setImageDrawable(context.getResources().getDrawable(R.mipmap.mrt));
            } else{
                    final FormBody body = new FormBody.Builder()
                            .add("thumb", "true")
                            .add("fileId", item.getIMG_FILE_ID_())
                            .build();
                    Request request = new Request.Builder()
                            .addHeader("cookie", (String) SPUtils.get(context, "session", ""))
                            .url(Constant.DOMAIN + "sys/core/file/imageView.do")
                            .post(body)
                            .build();
                    Log.d("reg", "request:" + request);
                    Call call2 = okHttpClient.newCall(request);
                    String send = Constant.DOMAIN + "sys/core/file/imageView.do";
                    for (int i = 0; i < body.size(); i++) {
                        if (i == 0) {
                            send = send + "?" + body.encodedName(i) + "=" + body.encodedValue(i);
                        } else {
                            send = send + "&" + body.encodedName(i) + "=" + body.encodedValue(i);
                        }
                    }
                    Log.d("reg", "send: " + send);
                    call2.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.i("info_call2fail", e.toString());
                        }

                        @Override
                        public void onResponse(Call call, final Response response) throws IOException {
                            if (response.isSuccessful()) {
                                final byte bo[] = response.body().bytes();
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        holder.img .setImageBitmap(Bytes2Bimap(bo));
                                    }
                                });
                            }
                            Headers headers = response.headers();
                            Log.i("info_respons.headers", headers + "");

                        }
                    });

            }
        }
        return view;


    }


    public Bitmap Bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }


    /**
     * 调用此方法输入所要转换的时间戳输入例如（1402733340）输出（"2014-06-14  16:09:00"）
     *
     * @param time
     * @return
     */
    public static String timedate(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        @SuppressWarnings("unused")
        long lcc = Long.valueOf(time);
        int i = Integer.parseInt(time);
        String times = sdr.format(new Date(i * 1000L));
        return times;

    }
    // 初始化map集合
    public void initCheck(boolean flag) {
        // map集合的数量和list的数量是一致的
        for (int i = 0; i < list.size(); i++) {
            // 设置默认的显示
            isCheck.put(i, flag);
        }
    }

    // 设置数据
    public void setData(List<Collectio> data) {
        this.list = data;
    }

    // 添加数据
    public void addData(Collectio bean) {
        // 下标 数据
        list.add(0, bean);
    }


    public void setHs(HashSet<Integer> hs) {
        this.hs = hs;
    }

    public void clear() {
        list.clear();
    }

    public boolean addAll(Collection<? extends Collectio> collection) {
        boolean pa = list.addAll(collection);
        return pa;
    }


    public boolean add(Collectio object) {
        return list.add(object);
    }

    // 全选按钮获取状态
    public Map<Integer, Boolean> getMap() {
        // 返回状态
        return isCheck;
    }

    // 删除一个数据
    public void removeData(int position) {
        list.remove(position);
    }


}
