package com.sfmap.tts;

import android.content.Context;

public interface SpeechSynthesizer {
    boolean speak(String text);
    void destroy();
}
