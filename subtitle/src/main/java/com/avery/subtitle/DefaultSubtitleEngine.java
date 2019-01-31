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

package com.avery.subtitle;

import android.media.MediaPlayer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.avery.subtitle.cache.SubtitleCache;
import com.avery.subtitle.model.Subtitle;
import com.avery.subtitle.model.TimedTextObject;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * @author AveryZhong.
 */

public class DefaultSubtitleEngine implements SubtitleEngine {
    private static final String TAG = DefaultSubtitleEngine.class.getSimpleName();
    private static final int MSG_REFRESH = 0x888;
    private static final int REFRESH_INTERVAL = 100;

    @Nullable
    private List<Subtitle> mSubtitles;
    private UIRenderTask mUIRenderTask;
    private MediaPlayer mMediaPlayer;
    private SubtitleCache mCache;
    private OnSubtitlePreparedListener mOnSubtitlePreparedListener;
    private OnSubtitleChangeListener mOnSubtitleChangeListener;

    private HandlerThread mHandlerThread;
    private Handler mWorkHandler;

    public DefaultSubtitleEngine() {
        mCache = new SubtitleCache();
        init();

    }

    private void init() {
        mHandlerThread = new HandlerThread("SubtitleFindThread");
        mHandlerThread.start();
        mWorkHandler = new Handler(mHandlerThread.getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(final Message msg) {
                long delay = REFRESH_INTERVAL;
                if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                    long position = mMediaPlayer.getCurrentPosition();
                    Subtitle subtitle = SubtitleFinder.find(position, mSubtitles);
                    notifyRefreshUI(subtitle);
                    if (subtitle != null) {
                        delay = subtitle.end.mseconds - position;
                    }

                }
                mWorkHandler.sendEmptyMessageDelayed(MSG_REFRESH, delay);
                return true;
            }
        });
    }

    private void notifyRefreshUI(final Subtitle subtitle) {
        if (mUIRenderTask == null) {
            mUIRenderTask = new UIRenderTask(mOnSubtitleChangeListener);
        }
        mUIRenderTask.execute(subtitle);
    }

    @Override
    public void bindToMediaPlayer(final MediaPlayer mediaPlayer) {
        mMediaPlayer = mediaPlayer;
    }

    @Override
    public void setSubtitlePath(final String path) {
        reset();
        if (TextUtils.isEmpty(path)) {
            Log.w(TAG, "loadSubtitleFromRemote: path is null.");
            return;
        }
        mSubtitles = mCache.get(path);
        if (mSubtitles != null) {
            Log.d(TAG, "from cache.");
            notifyPrepared();
            return;
        }
        SubtitleLoader.loadSubtitle(path, new SubtitleLoader.Callback() {
            @Override
            public void onSuccess(final TimedTextObject timedTextObject) {
                if (timedTextObject == null) {
                    Log.d(TAG, "onSuccess: timedTextObject is null.");
                    return;
                }
                final TreeMap<Integer, Subtitle> captions = timedTextObject.captions;
                if (captions == null) {
                    Log.d(TAG, "onSuccess: captions is null.");
                    return;
                }
                mSubtitles = new ArrayList<>(captions.values());
                notifyPrepared();
                mCache.put(path, mSubtitles);
            }

            @Override
            public void onError(final Exception exception) {
                Log.e(TAG, "onError: " + exception.getMessage());
            }
        });
    }

    @Override
    public void reset() {
        stop();
        if (mSubtitles != null) {
            mSubtitles.clear();
        }
        mSubtitles = null;
        mUIRenderTask = null;
    }

    @Override
    public void start() {
        Log.d(TAG, "start: ");
        if (mMediaPlayer == null) {
            Log.w(TAG, "MediaPlayer is not bind, You must bind MediaPlayer to "
                    + SubtitleEngine.class.getSimpleName()
                    + " before start() method be called,"
                    + " you can do this by call " +
                    "bindToMediaPlayer(MediaPlayer mediaPlayer) method.");
            return;
        }
        stop();
        mWorkHandler.sendEmptyMessageDelayed(MSG_REFRESH, REFRESH_INTERVAL);
    }

    @Override
    public void pause() {
        stop();
    }

    @Override
    public void resume() {
        start();
    }

    @Override
    public void stop() {
        mWorkHandler.removeMessages(MSG_REFRESH);
    }

    @Override
    public void destroy() {
        Log.d(TAG, "destroy: ");
        stop();
        reset();
        mHandlerThread.quit();
    }

    private void notifyPrepared() {
        if (mOnSubtitlePreparedListener != null) {
            mOnSubtitlePreparedListener.onSubtitlePrepared(mSubtitles);
        }
    }

    @Override
    public void setOnSubtitlePreparedListener(final OnSubtitlePreparedListener listener) {
        mOnSubtitlePreparedListener = listener;
    }

    @Override
    public void setOnSubtitleChangeListener(final OnSubtitleChangeListener listener) {
        mOnSubtitleChangeListener = listener;
    }

}
