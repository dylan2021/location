package com.sfmap.tts;

import android.content.Context;

public class TtsManager {
    private static final String SPEECH_ENGINE_XUNFEI = "xunfei";
    private static final String SPEECH_ENGINE_YUNZHIYIN = "yunzhiyin";
    private static final String SPEECH_ENGINE_LINGYUN = "lingyun";

//    private static final String ENGINE_USED = SPEECH_ENGINE_YUNZHIYIN;
    private static final String ENGINE_USED = SPEECH_ENGINE_LINGYUN;
//    private static final String ENGINE_USED = SPEECH_ENGINE_XUNFEI;

    private SpeechSynthesizer speechSynthesizer;

    private TtsManager(Context context, String engine) {
        Context application = context.getApplicationContext();
        if(SPEECH_ENGINE_XUNFEI.equals(engine)) {
            speechSynthesizer = XfSpeechSynthesizer.getInstance(application);
        }

        if(SPEECH_ENGINE_YUNZHIYIN.equals(engine)) {
//            speechSynthesizer =YzySpeechSynthesizer.getInstance(application);
        }

        if(SPEECH_ENGINE_LINGYUN.equals(engine)) {
            speechSynthesizer =LYSpeechSynthesizer.getInstance(application);
        }
    }

    public static TtsManager getInstance(Context context) {
        return new TtsManager(context, ENGINE_USED);
    }

    public void destroy() {
        speechSynthesizer.destroy();
    }

    public void startSpeaking(String text) {
        speechSynthesizer.speak(text);
    }
}
