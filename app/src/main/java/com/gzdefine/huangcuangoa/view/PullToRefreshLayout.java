/*
 * Copyright © 2015 珠海云集软件科技有限公司.
 * Website：http://www.YunJi123.com
 * Mail：dev@yunji123.com
 * Tel：+86-0756-8605060
 * QQ：340022641(dove)
 * Author：dove
 */

package com.gzdefine.huangcuangoa.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.gzdefine.huangcuangoa.R;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * 下拉刷新控件 统一自定义下拉样式
 */
public final class PullToRefreshLayout extends PtrFrameLayout {

    //region 过滤水平滑动, 解决事件冲突
    private float yDistance;
    private float xDistance;
    private float xLast;
    private float yLast;
    //endregion

    public PullToRefreshLayout(Context context) {
        super(context);
        init();
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setKeepScreenOn(true);
//        initStoreHouseHeader();
        initMaterialHeader();
    }

//    private void initStoreHouseHeader() {
//        StoreHouseHeader header = new StoreHouseHeader(getContext());
//        int padding = getResources().getDimensionPixelSize(R.dimen.padding_xlarge);
//        header.setPadding(0, padding, 0, padding);
//        header.setTextColor(0xFFFF7F7F);
//        header.initWithString("YunJi", PtrLocalDisplay.dp2px(24));
//        setHeaderView(header);
//        addPtrUIHandler(header);
//    }

    private void initMaterialHeader() {
        MaterialHeader header = new MaterialHeader(getContext());
        int padding = getResources().getDimensionPixelSize(R.dimen.padding_xlarge);
        header.setPadding(0, padding, 0, padding);
        setHeaderView(header);
        addPtrUIHandler(header);
    }

    @Override // 过滤水平滑动, 解决事件冲突
    public boolean dispatchTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDistance = yDistance = 0f;
                xLast = e.getX();
                yLast = e.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float curX = e.getX();
                final float curY = e.getY();

                xDistance += Math.abs(curX - xLast);
                yDistance += Math.abs(curY - yLast);
                xLast = curX;
                yLast = curY;

                if (xDistance > yDistance) {
                    return dispatchTouchEventSupper(e);
                }
                break;
        }
        return super.dispatchTouchEvent(e);
    }


}
