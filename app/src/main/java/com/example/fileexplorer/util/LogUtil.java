package com.example.fileexplorer.util;

import java.util.regex.Pattern;
import com.example.fileexplorer.BuildConfig;
import android.util.Log;

@SuppressWarnings("JavaDoc")
public class LogUtil {

    /**
     * デバック用ログを出力する。 本番リリース時は出力されない。
     *
     * @param msg 出力するメッセージ
     */
    public static void d(String msg) {
        if (!BuildConfig.DEBUG) return;
        Log.d(getTag(), msg);
    }

    /**
     * エラー用ログを出力する。 &lt;br&gt;
     * catchの中や想定外の動作でログを出力する場合に使用すること。&lt;br&gt;
     * 本番リリース時も、起きたエラーを解析するために本ログは出力される想定。
     *
     * @param msg 出力するメッセージ
     */
    public static void e(String msg) {
        Log.e(getTag(), msg);
    }

    /**
     * 同上
     *
     * @param msg
     * @param t
     */
    public static void e(String msg, Throwable t) {
        Log.e(getTag(), msg, t);
    }

    /**
     * タグを生成する
     *
     * @return className#methodName:line
     */
    private static String getTag() {

        final StackTraceElement trace = Thread.currentThread().getStackTrace()[4];
        final String cla = trace.getClassName();
        Pattern pattern = Pattern.compile("&quot;[\\.]+&quot;");
        final String[] splitedStr = pattern.split(cla);
        final String simpleClass = splitedStr[splitedStr.length - 1];

        final String mthd = trace.getMethodName();
        final int line = trace.getLineNumber();
        final String tag = simpleClass + " ; " + mthd + " ; " + line;

        return tag;
    }
}
