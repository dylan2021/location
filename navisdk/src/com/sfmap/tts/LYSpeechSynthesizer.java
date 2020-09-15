package com.sfmap.tts;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.sfmap.tbt.util.LogUtil;
import com.sinovoice.hcicloudsdk.android.tts.player.TTSPlayer;
import com.sinovoice.hcicloudsdk.api.tts.HciCloudTts;
import com.sinovoice.hcicloudsdk.common.tts.TtsSynthSyllable;
import com.sinovoice.hcicloudsdk.player.AudioPlayer;
import com.sinovoice.hcicloudsdk.player.PlayerEvent;
import com.sinovoice.hcicloudsdk.player.TTSPlayerListener;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LYSpeechSynthesizer implements SpeechSynthesizer {
    private String TAG = this.getClass().getSimpleName();
    private static boolean inited = false;
    private static TTSPlayer mTts;
    String capkey;
    private final Queue<String> pendingTextQueue = new ConcurrentLinkedQueue<>();

    private LYSpeechSynthesizer(Context context) {
        initSDK(context);
    }

    private static LYSpeechSynthesizer instance;

    public static LYSpeechSynthesizer getInstance(Context context) {
        if (instance == null) {
            instance = new LYSpeechSynthesizer(context);
        }
        return instance;
    }

    /**
     * 初始化
     *
     * @param context
     */
    private TTSPlayerListener ttsPlayerListener;
    private void initSDK(Context appContext) {
        AccountInfo.getInstance().loadAccountInfo(appContext);
        int errCode = HciCloudSysHelper.getInstance().init(appContext);
        errCode = HciCloudTts.hciTtsInit(HciCloudTtsHelper.getInitConfig(appContext));
        initDataAndAction(appContext);
    }

    private void initDataAndAction(Context appContext) {

        ttsPlayerListener = new MyTTSPlayerListener();
        mTts = new TTSPlayer(ttsPlayerListener);
        mTts.setContext(appContext);
        mTts.setRouteFlag(AudioPlayer.PLAYER_FLAG_SPEAKER);
        capkey = AccountInfo.getInstance().getCapKey();
    }

    @Override
    public boolean speak(String text) {
        this.startSpeaking(text);
        return true;
    }

    /**
     * 开始合成语音
     *
     * @param str
     */
    public void startSpeaking(String str) {
        if (mTts != null) {
            LogUtil.d(TAG,"startSpeaking"+mTts.getPlayerState());
            if(mTts.getPlayerState() !=1) {
                addTextToPendingQueue(str);
            }else {
                mTts.play(str, HciCloudTtsHelper.getSynthConfig(capkey, "v8/WangJing_Common/"));
            }
        }
    }

    /**
     * 停止语音合成
     */
    public void stopSpeaking() {
        if (mTts != null)
            mTts.stop();
    }

    public void destroy() {
        if (mTts != null)
            mTts.stop();
            mTts = null;
            instance = null;
        pendingTextQueue.clear();
    }

    private class MyTTSPlayerListener implements TTSPlayerListener {

        @Override
        public void onPlayerEventStateChange(PlayerEvent playerEvent) {
            Log.i("TTSPlayer1", playerEvent.name());
            if(playerEvent == PlayerEvent.END) {
                onTextSpeakComplete();
            }
        }

        @Override
        public void onPlayerEventProgressChange(PlayerEvent playerEvent,
                                                int playPos, int synthPos, int total) {
            Log.i("TTSPlayer2", "progress: " + playPos + ", " + synthPos
                    + ", " + total + ", ");
        }

        @Override
        public void onPlayerEventProgressChange(PlayerEvent playerEvent,
                                                int textStart, int textEnd, String sentence,
                                                TtsSynthSyllable syllable) {
            Log.i("TTSPlayer3", "syllable: " + textStart + ", " + textEnd
                    + ", " + sentence + ", " + syllable.getText() + ", "
                    + syllable.getPronounciationText());
        }

        @Override
        public void onPlayerEventPlayerError(PlayerEvent playerEvent,
                                             int errorCode) {
            Log.i("TTSPlayer4", playerEvent.name() + " " + errorCode);
        }

        @Override
        public void onPlayerEventSeek(PlayerEvent playerEvent, int seekPos) {
            Log.i("TTSPlayer5", playerEvent.name() + " " + seekPos);
        }
    }

    private void addTextToPendingQueue(String text) {
        pendingTextQueue.offer(text);
    }

    private void onTextSpeakComplete() {
        if(!pendingTextQueue.isEmpty()) {
            String text = pendingTextQueue.poll();
            if(!TextUtils.isEmpty(text)) {
                mTts.play(text, HciCloudTtsHelper.getSynthConfig(capkey, "v8/WangJing_Common/"));
            }
        }
    }
}
