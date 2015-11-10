package com.foo.glassbotimer.modules;

import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by sibigtroth on 6/25/14.
 */

public class Speaker
{

    private BrainActivity brainActivity;
    private TextToSpeech textToSpeech;
    private HashMap<String, String> TtsUtteranceMap;

    public Speaker(BrainActivity brainActivity)
    {
        this.brainActivity = brainActivity;

        this.Init();
    }

    private void Init()
    {
        this.CreateTextToSpeech();
    }

    private void CreateTextToSpeech()
    {
        this.TtsUtteranceMap = new HashMap<String, String>();
        this.textToSpeech = new TextToSpeech(this.brainActivity, new TtsInitListener(this));
        this.textToSpeech.setOnUtteranceProgressListener(new TtsUtteranceListener(this));
    }

    ///////////////////////////
    //accessors
    ///////////////////////////

    private BrainActivity GetBrainActivity() {return this.brainActivity;}


    ///////////////////////////
    //callbacks
    ///////////////////////////

    public void OnStart_ttsSpeak()
    {
        this.GetBrainActivity().OnStart_ttsSpeak();
    }

    public void OnDone_ttsSpeak()
    {
        this.GetBrainActivity().OnDone_ttsSpeak();
    }


    ///////////////////////////
    //utilities
    ///////////////////////////

    public void Speak(String textToSpeak)
    {
        Log.d("foo", "Speak:    " + textToSpeak);
        this.TtsUtteranceMap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID");
        this.textToSpeech.speak(textToSpeak, TextToSpeech.QUEUE_ADD, this.TtsUtteranceMap);
    }

    /*
    public void SpeakAfterDelay(final String textToSpeak, int delay)
    {
        Looper.prepare();
        Handler delayHandler= new Handler();
        Runnable r=new Runnable()
        {
            @Override
            public void run()
            {
                Speak(textToSpeak);
            }

        };
        delayHandler.postDelayed(r, delay);
    }
    */

    public void ShutDown()
    {
        this.textToSpeech.shutdown();
    }

}

class TtsInitListener implements TextToSpeech.OnInitListener
{

    private Speaker speaker;

    public TtsInitListener(Speaker speaker)
    {
        this.speaker = speaker;
    }

    @Override
    public void onInit(int status)
    {

        if (status == TextToSpeech.SUCCESS) {}
        else {}
    }
}

class TtsUtteranceListener extends UtteranceProgressListener
{

    private Speaker speaker;

    public TtsUtteranceListener(Speaker speaker)
    {
        this.speaker = speaker;
    }

    @Override
    public void onDone(String utteranceId)
    {
        this.speaker.OnDone_ttsSpeak();
    }

    @Override
    public void onError(String utteranceId) {
    }

    @Override
    public void onStart(String utteranceId)
    {
        this.speaker.OnStart_ttsSpeak();
    }
}