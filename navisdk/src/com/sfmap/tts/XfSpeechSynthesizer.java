package com.sfmap.tts;

import android.content.Context;
import android.os.Bundle;

import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechListener;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;

public class XfSpeechSynthesizer implements SpeechListener, InitListener, com.sfmap.tts.SpeechSynthesizer {
    private static boolean inited = false;
    private static SpeechSynthesizer mTts;

    private XfSpeechSynthesizer(Context context) {
        init(context);
    }

    private static XfSpeechSynthesizer instance;

    public static XfSpeechSynthesizer getInstance(Context context) {
        if (instance == null) {
            instance = new XfSpeechSynthesizer(context);
        }
        return instance;
    }

    /**
     * 初始化
     *
     * @param context
     */
    private void init(Context context) {
        if (!inited) {
            inited = true;
            SpeechUtility.createUtility(context, "appid=5b6833dd");//caohai 临时替换最新讯飞语音库
        }
        mTts = SpeechSynthesizer.createSynthesizer(context, null);
        //设置发音人
        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");
        //
        mTts.setParameter(SpeechConstant.SPEED, "50");
        //设置音调
        mTts.setParameter(SpeechConstant.PITCH, "50");
        //设置音量
        mTts.setParameter(SpeechConstant.VOLUME, "50");
    }

    @Override
    public boolean speak(String text) {
        int result = this.startSpeaking(text);
        return result == 0;
    }

    @Override
    public void onBufferReceived(byte[] arg0) {

    }

    @Override
    public void onCompleted(SpeechError arg0) {

    }

    @Override
    public void onEvent(int arg0, Bundle arg1) {

    }

    /**
     * 开始合成语音
     *
     * @param str
     */
    public int startSpeaking(String str) {
        if (mTts != null) {
            return mTts.startSpeaking(str, null);
        }
        return -1;
    }

    /**
     * 停止语音合成
     */
    public void stopSpeaking() {
        if (mTts != null)
            mTts.stopSpeaking();
    }

    public boolean speeking() {
        if (mTts != null) {
            return mTts.isSpeaking();
        }
        return false;
    }

    public void destroy() {
        if (mTts != null)
            mTts.destroy();
        mTts = null;
        instance = null;
    }

    @Override
    public void onInit(int i) {

    }
}
