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
import android.support.annotation.Nullable;

import com.avery.subtitle.model.Subtitle;

import java.util.List;

/**
 * @author AveryZhong.
 */

public interface SubtitleEngine {

    /**
     * 加载字幕
     *
     * @param path 字幕路径（本地路径或者是远程路径）
     */
    void setSubtitlePath(String path);

    /**
     * 开启字幕刷新任务
     */
    void start();

    /**
     * 暂停
     */
    void pause();

    /**
     * 恢复
     */
    void resume();

    /**
     * 停止字幕刷新任务
     */
    void stop();

    /**
     * 重置
     */
    void reset();

    /**
     * 销毁字幕
     */
    void destroy();

    void bindToMediaPlayer(MediaPlayer mediaPlayer);

    void setOnSubtitlePreparedListener(OnSubtitlePreparedListener listener);

    void setOnSubtitleChangeListener(OnSubtitleChangeListener listener);

    interface OnSubtitlePreparedListener {
        void onSubtitlePrepared(@Nullable List<Subtitle> subtitles);
    }

    interface OnSubtitleChangeListener {
        void onSubtitleChanged(@Nullable Subtitle subtitle);
    }

}