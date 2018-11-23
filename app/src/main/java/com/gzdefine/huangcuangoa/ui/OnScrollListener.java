/*
 * Copyright © 2015 珠海云集软件科技有限公司.
 * Website：http://www.YunJi123.com
 * Mail：dev@yunji123.com
 * Tel：+86-0756-8605060
 * QQ：340022641(dove)
 * Author：dove
 */

package com.gzdefine.huangcuangoa.ui;

import android.widget.AbsListView;

public class OnScrollListener implements AbsListView.OnScrollListener {

    private final OnScrollLastItemListener listener;
    private int scrollItemCount;

    public OnScrollListener(OnScrollLastItemListener listener) {
        this.listener = listener;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        int count = view.getCount();
        if (scrollState == SCROLL_STATE_IDLE && scrollItemCount == count) {
            listener.onScrollLastItem(view);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        scrollItemCount = firstVisibleItem + visibleItemCount;
    }

}
