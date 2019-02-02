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

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.avery.subtitle.widget.SimpleSubtitleView;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String VIDEO_URL = "http://172.16.201.228/movie/Ultra.Pulpe.2018.B.Mandico.DVDRip.Cinetik.mkv";
    private static final String SUBTITLE_URL = "http://172.16.201.228/movie/subtitles/Ultra.Pulpe.2018.B.Mandico.DVDRip.Cinetik.chs.srt";

    private Context mContext;
    private VideoView mVideoView;
    private SimpleSubtitleView mSubtitleView;

    private Button mBtnPlayPause;
    private Button mBtnSettings;
    private Button mBtnForward;
    private Button mBtnRewind;
    private TextView mTvTips;
    private SettingsDialog mSettingsDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        initViews();
        setupEventListeners();
        initPlayer();
    }

    private void initViews() {
        mVideoView = findViewById(R.id.video_view);
        mSubtitleView = findViewById(R.id.subtitle_view);
        mTvTips = findViewById(R.id.tv_tips);
        mBtnPlayPause = findViewById(R.id.btn_play_pause);
        mBtnSettings = findViewById(R.id.btn_settings);
        mBtnForward = findViewById(R.id.btn_forward);
        mBtnRewind = findViewById(R.id.btn_rewind);
        mBtnPlayPause.requestFocus();
    }

    private void setupEventListeners() {
        mBtnPlayPause.setOnClickListener(this);
        mBtnSettings.setOnClickListener(this);
        mBtnForward.setOnClickListener(this);
        mBtnRewind.setOnClickListener(this);

    }

    private void initPlayer() {
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(final MediaPlayer mp) {
                // 绑定MediaPlayer
                mSubtitleView.bindToMediaPlayer(mp);
                // 设置字幕
                mSubtitleView.setSubtitlePath(SUBTITLE_URL);
            }
        });
        mVideoView.setVideoURI(Uri.parse(VIDEO_URL));
    }

    private void play() {
        mVideoView.start();
        mBtnPlayPause.setText("暂停");
        mTvTips.setVisibility(View.GONE);
    }

    private void pause() {
        mVideoView.pause();
        mBtnPlayPause.setText("播放");
    }

    @Override
    protected void onDestroy() {
        mSubtitleView.destroy();
        super.onDestroy();
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.btn_play_pause:
                if (mVideoView.isPlaying()) {
                    pause();
                } else {
                    play();
                }
                break;
            case R.id.btn_settings:
                showSettingsDialog();
                break;
            case R.id.btn_forward:
                forward();
                break;
            case R.id.btn_rewind:
                rewind();
                break;
        }
    }


    private void forward() {
        int position = mVideoView.getCurrentPosition() + 60 * 1000;
        if (position > mVideoView.getDuration()) {
            position = mVideoView.getDuration();
            Toast.makeText(mContext, "快进到头了", Toast.LENGTH_SHORT).show();
        }
        mVideoView.seekTo(position);
    }

    private void rewind() {
        int position = mVideoView.getCurrentPosition() - 60 * 1000;
        if (position < 0) {
            position = 0;
            Toast.makeText(mContext, "快退到头了", Toast.LENGTH_SHORT).show();
        }
        mVideoView.seekTo(position);
    }

    private void showSettingsDialog() {
        if (mSettingsDialog == null) {
            mSettingsDialog = new SettingsDialog();
            mSettingsDialog.setOnSettingListener(new SettingsDialog.OnSettingListener() {
                @Override
                public void onSubtitleColorChange(final String color) {
                    mSubtitleView.setTextColor(Color.parseColor(color));
                }

                @Override
                public void onSubtitleFontSizeChange(final int fontSize) {
                    mSubtitleView.setTextSize(fontSize);
                }

                @Override
                public void onSubtitlePositionChange(final Position position) {
                    RelativeLayout.LayoutParams lp
                            = (RelativeLayout.LayoutParams) mSubtitleView.getLayoutParams();
                    switch (position) {
                        case TOP:
                            mSubtitleView
                                    .setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
                            break;
                        case CENTER:
                            mSubtitleView
                                    .setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
                            break;
                        case BOTTOM:
                            mSubtitleView
                                    .setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
                            break;
                    }
                    mSubtitleView.setLayoutParams(lp);
                }
            });
        }
        mSettingsDialog.show(getSupportFragmentManager(), "SettingsDialog");
    }


}
