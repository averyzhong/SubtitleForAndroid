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

import android.text.TextUtils;
import android.util.Log;

import com.avery.subtitle.exception.FatalParsingException;
import com.avery.subtitle.format.FormatASS;
import com.avery.subtitle.format.FormatSRT;
import com.avery.subtitle.format.FormatSTL;
import com.avery.subtitle.model.TimedTextObject;
import com.avery.subtitle.runtime.AppTaskExecutor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author AveryZhong.
 */

public class SubtitleLoader {
    private static final String TAG = SubtitleLoader.class.getSimpleName();

    private SubtitleLoader() {
        throw new AssertionError("No instance for you.");
    }

    public static void loadSubtitle(final String path, final Callback callback) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        if (path.startsWith("http://")
                || path.startsWith("https://")) {
            loadFromRemoteAsync(path, callback);
        } else {
            loadFromLocalAsync(path, callback);
        }
    }

    private static void loadFromRemoteAsync(final String remoteSubtitlePath,
                                           final Callback callback) {
        AppTaskExecutor.deskIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final TimedTextObject timedTextObject = loadFromRemote(remoteSubtitlePath);
                    if (callback != null) {
                        AppTaskExecutor.mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                callback.onSuccess(timedTextObject);
                            }
                        });
                    }

                } catch (final Exception e) {
                    e.printStackTrace();
                    if (callback != null) {
                        AppTaskExecutor.mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                callback.onError(e);
                            }
                        });
                    }

                }
            }
        });
    }

    private static void loadFromLocalAsync(final String localSubtitlePath,
                                          final Callback callback) {
        AppTaskExecutor.deskIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final TimedTextObject timedTextObject = loadFromLocal(localSubtitlePath);
                    if (callback != null) {
                        AppTaskExecutor.mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                callback.onSuccess(timedTextObject);
                            }
                        });
                    }

                } catch (final Exception e) {
                    e.printStackTrace();
                    if (callback != null) {
                        AppTaskExecutor.mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                callback.onError(e);
                            }
                        });
                    }

                }
            }
        });
    }

    public TimedTextObject loadSubtitle(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        try {
            if (path.startsWith("http://")
                    || path.startsWith("https://")) {
               return loadFromRemote(path);
            } else {
                return loadFromLocal(path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static TimedTextObject loadFromRemote(final String remoteSubtitlePath)
            throws IOException, FatalParsingException {
        Log.d(TAG, "parseRemote: remoteSubtitlePath = " + remoteSubtitlePath);
        URL url = new URL(remoteSubtitlePath);
        return loadAndParse(url.openStream(), url.getPath());
    }

    private static TimedTextObject loadFromLocal(final String localSubtitlePath)
            throws IOException, FatalParsingException {
        Log.d(TAG, "parseLocal: localSubtitlePath = " + localSubtitlePath);
        File file = new File(localSubtitlePath);
        return loadAndParse(new FileInputStream(file), file.getPath());
    }

    private static TimedTextObject loadAndParse(final InputStream is, final String filePath)
            throws IOException, FatalParsingException {
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        String ext = fileName.substring(fileName.lastIndexOf("."));
        Log.d(TAG, "parse: name = " + fileName + ", ext = " + ext);
        if (".srt".equalsIgnoreCase(ext)) {
            return new FormatSRT().parseFile(fileName, is);
        } else if (".ass".equalsIgnoreCase(ext)) {
            return new FormatASS().parseFile(fileName, is);
        } else if (".stl".equalsIgnoreCase(ext)) {
            return new FormatSTL().parseFile(fileName, is);
        } else if (".ttml".equalsIgnoreCase(ext)) {
            return new FormatSTL().parseFile(fileName, is);
        }
        return new FormatSRT().parseFile(fileName, is);
    }

    public interface Callback {
        void onSuccess(TimedTextObject timedTextObject);

        void onError(Exception exception);
    }
}
