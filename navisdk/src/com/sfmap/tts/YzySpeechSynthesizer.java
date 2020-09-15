package com.sfmap.tts;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.unisound.client.SpeechConstants;
import com.unisound.client.SpeechSynthesizerListener;

import java.io.File;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

//云之音搜索引擎
public class YzySpeechSynthesizer implements  SpeechSynthesizer{
    private static final String TAG = "YzySpeechSynthesizer";
    private final com.unisound.client.SpeechSynthesizer mSynthesizer;
    private String appKey = "sfiiac6rzxl2uy2szmp6d5kdpy22xovinp25vvaq";
    private String secret = "5a0493338db00cd4a42976c7830a8a67";

    /*assets中前端声学模型文件名*/
    private final String FRONTEND_MODEL = "frontend_model_offline_v10.0.3";
    /*assets中后端声学模型文件名*/
    private final String BACKEND_MODEL = "backend_kiyo_lpc2wav_22k_pf_mixed_v1.0.0";
    private String MODEL_PATH_NAME;
    private String mFrontendModel;
    private String mBackendModel;
    private final Queue<String> pendingTextQueue = new ConcurrentLinkedQueue<>();


    private YzySpeechSynthesizer(Context context) {
        // 初始化语音合成对象
        mSynthesizer = new com.unisound.client.SpeechSynthesizer(context, Config.appKey, Config.secret);
        setModel(context);

        // 设置本地合成
        mSynthesizer.setOption(SpeechConstants.TTS_SERVICE_MODE, SpeechConstants.TTS_SERVICE_MODE_LOCAL);
        mSynthesizer.setOption(SpeechConstants.TTS_KEY_SAMPLE_RATE, 22050);
        // 设置回调监听
        mSynthesizer.setTTSListener(new SpeechSynthesizerListener() {
            @Override
            public void onEvent(int event) {
                switch (event) {
                    case SpeechConstants.TTS_EVENT_INIT:
                        // 初始化成功回调
                        break;
                    case SpeechConstants.TTS_EVENT_SYNTHESIZER_START:
                        // 开始合成回调
                        break;
                    case SpeechConstants.TTS_EVENT_SYNTHESIZER_END:
                        // 合成结束回调
                        break;
                    case SpeechConstants.TTS_EVENT_BUFFER_BEGIN:
                        // 开始缓存回调
                        break;
                    case SpeechConstants.TTS_EVENT_BUFFER_READY:
                        // 缓存完毕回调
                        break;
                    case SpeechConstants.TTS_EVENT_PLAYING_START:
                        // 开始播放回调
                        break;
                    case SpeechConstants.TTS_EVENT_PLAYING_END:
                        // 播放完成回调
                        onTextSpeakComplete();
                        break;
                    case SpeechConstants.TTS_EVENT_PAUSE:
                        // 暂停回调
                        break;
                    case SpeechConstants.TTS_EVENT_RESUME:
                        // 恢复回调
                        break;
                    case SpeechConstants.TTS_EVENT_STOP:
                        // 停止回调
                        break;
                    case SpeechConstants.TTS_EVENT_RELEASE:
                        // 释放资源回调
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onError(int errorType, String message) {
                Log.v(TAG, "yunzhisheng error message:" + message);
            }
        });
        // 初始化合成引擎
        mSynthesizer.init("");

    }

    private void onTextSpeakComplete() {
        if(!pendingTextQueue.isEmpty()) {
           String text = pendingTextQueue.poll();
           if(!TextUtils.isEmpty(text)) {
               mSynthesizer.playText(text);
           }
        }
    }

    private void setModel(Context context) {
        MODEL_PATH_NAME = new File(context.getFilesDir(), "YunZhiSheng/tts/").getAbsolutePath();
        //从assets加载前后端模型
        Utils.copyFile(context, FRONTEND_MODEL, MODEL_PATH_NAME);
        Utils.copyFile(context, BACKEND_MODEL, MODEL_PATH_NAME);

        mFrontendModel = MODEL_PATH_NAME + "/"+ FRONTEND_MODEL;
        mBackendModel = MODEL_PATH_NAME + "/" + BACKEND_MODEL;
        // 设置前端模型
        mSynthesizer.setOption(SpeechConstants.TTS_KEY_FRONTEND_MODEL_PATH, mFrontendModel);
        // 设置后端模型
        mSynthesizer.setOption(SpeechConstants.TTS_KEY_BACKEND_MODEL_PATH, mBackendModel);
    }

    public static YzySpeechSynthesizer getInstance(Context context) {
        return new YzySpeechSynthesizer(context);
    }

    @Override
    public boolean speak(String text) {
        if(mSynthesizer.isPlaying()) {
            addTextToPendingQueue(text);
        } else {
            mSynthesizer.playText(text);
        }
        return true;
    }

    private void addTextToPendingQueue(String text) {
        pendingTextQueue.offer(text);
    }

    @Override
    public void destroy() {
        if(mSynthesizer.isPlaying()) {
            mSynthesizer.stop();
        }
        pendingTextQueue.clear();
    }
}
