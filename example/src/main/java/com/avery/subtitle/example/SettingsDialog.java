/*
 *                       Copyright (C) of Avery
 *
 *                              _ooOoo_
 *                             o8888888o
 *                             88" . "88
 *                             (| -_- |)
 *                             O\  =  /O
 *                          ____/`- -'\____
 *                        .'  \\|     |//  `.
 *                       /  \\|||  :  |||//  \
 *                      /  _||||| -:- |||||-  \
 *                      |   | \\\  -  /// |   |
 *                      | \_|  ''\- -/''  |   |
 *                      \  .-\__  `-`  ___/-. /
 *                    ___`. .' /- -.- -\  `. . __
 *                 ."" '<  `.___\_<|>_/___.'  >'"".
 *                | | :  `- \`.;`\ _ /`;.`/ - ` : | |
 *                \  \ `-.   \_ __\ /__ _/   .-` /  /
 *           ======`-.____`-.___\_____/___.-`____.-'======
 *                              `=- -='
 *           ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
 *              Buddha bless, there will never be bug!!!
 */

package com.avery.subtitle.example;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * @author AveryZhong.
 */

public class SettingsDialog extends DialogFragment implements View.OnClickListener {

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Button btnColorWhite = view.findViewById(R.id.btn_white);
        final Button btnColorRed = view.findViewById(R.id.btn_red);
        final Button btnColorPurple = view.findViewById(R.id.btn_purple);

        final Button btnFontSizeSmall = view.findViewById(R.id.btn_small);
        final Button btnFontSizeMiddle = view.findViewById(R.id.btn_middle);
        final Button btnFontSizeBig = view.findViewById(R.id.btn_big);

        final Button btnPositionTop = view.findViewById(R.id.btn_top);
        final Button btnPositionCenter = view.findViewById(R.id.btn_center);
        final Button btnPositionBottom = view.findViewById(R.id.btn_bottom);


        btnColorWhite.setOnClickListener(this);
        btnColorRed.setOnClickListener(this);
        btnColorPurple.setOnClickListener(this);

        btnFontSizeSmall.setOnClickListener(this);
        btnFontSizeMiddle.setOnClickListener(this);
        btnFontSizeBig.setOnClickListener(this);

        btnPositionTop.setOnClickListener(this);
        btnPositionCenter.setOnClickListener(this);
        btnPositionBottom.setOnClickListener(this);

    }

    @Override
    public void onClick(final View v) {
        if (mOnSettingListener == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.btn_white:
                mOnSettingListener.onSubtitleColorChange("#ffffff");
                break;
            case R.id.btn_red:
                mOnSettingListener.onSubtitleColorChange("#ff0000");
                break;
            case R.id.btn_purple:
                mOnSettingListener.onSubtitleColorChange("#7F0E92");
                break;

            case R.id.btn_small:
                mOnSettingListener.onSubtitleFontSizeChange(12);
                break;
            case R.id.btn_middle:
                mOnSettingListener.onSubtitleFontSizeChange(16);
                break;
            case R.id.btn_big:
                mOnSettingListener.onSubtitleFontSizeChange(22);
                break;

            case R.id.btn_top:
                mOnSettingListener
                        .onSubtitlePositionChange(OnSettingListener.Position.TOP);
                break;
            case R.id.btn_center:
                mOnSettingListener
                        .onSubtitlePositionChange(OnSettingListener.Position.CENTER);
                break;
            case R.id.btn_bottom:
                mOnSettingListener
                        .onSubtitlePositionChange(OnSettingListener.Position.BOTTOM);
                break;
        }
    }

    private OnSettingListener mOnSettingListener;

    public void setOnSettingListener(final OnSettingListener onSettingListener) {
        mOnSettingListener = onSettingListener;
    }

    public interface OnSettingListener {
        void onSubtitleColorChange(String color);

        void onSubtitleFontSizeChange(int fontSize);

        void onSubtitlePositionChange(Position position);

        enum Position {
            TOP, CENTER, BOTTOM
        }
    }
}
