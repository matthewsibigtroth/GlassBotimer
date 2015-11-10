package com.foo.glassbotimer.modules;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by sibigtroth on 6/25/14.
 */
public class Listener
{

    private BrainActivity brainActivity;
    public SpeechRecognizer speechRecognizer;
    private RecognitionListenerExtended recognitionListenerExtended;

    public Listener(BrainActivity brainActivity)
    {
        this.brainActivity = brainActivity;

        this.Init();
    }

    private void Init()
    {
        this.CreateSpeechRecognizer();
    }


    private void CreateSpeechRecognizer()
    {
        this.speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this.brainActivity);
        this.recognitionListenerExtended = new RecognitionListenerExtended(this, this.brainActivity);
        this.speechRecognizer.setRecognitionListener(this.recognitionListenerExtended);
        //this.Listen();
    }


    ///////////////////////////
    //accessors
    ///////////////////////////

    private BrainActivity GetBrainActivity() {return this.brainActivity;}


    ///////////////////////////
    //callbacks
    ///////////////////////////

    public void OnSpeechRecognized(String recognizedSpeech)
    {
        Log.d("foo", "speech recognized   " + recognizedSpeech);
        this.GetBrainActivity().OnSpeechRecognized(recognizedSpeech);
    }


    ///////////////////////////
    //utilities
    ///////////////////////////

    public void Listen()
    {
        Log.d("foo", "Listen");
        //this.converserActivity.MuteSystemStream();

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        //intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"voice.recognition.test");
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        this.recognitionListenerExtended.shouldContinuoslyListen = true;
        this.speechRecognizer.startListening(intent);
    }

    public void StopListening()
    {
        //this.converserActivity.UnMuteSystemStream();

        this.brainActivity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                recognitionListenerExtended.shouldContinuoslyListen = false;
                speechRecognizer.stopListening();
            }
        });
    }

    public void ShutDown()
    {
        this.speechRecognizer.destroy();
    }
}


class RecognitionListenerExtended implements RecognitionListener
{

    private BrainActivity brainActivity;
    private Listener listener;

    public RecognitionListenerExtended(Listener listener, BrainActivity brainActivity)
    {
        this.brainActivity = brainActivity;
        this.listener = listener;
    }

    public boolean shouldContinuoslyListen = false;

    private BrainActivity GetBrainActivity() {return this.brainActivity;}

    public void onReadyForSpeech(Bundle params) {}

    public void onBeginningOfSpeech() {}

    public void onRmsChanged(float rmsdB) {}

    public void onBufferReceived(byte[] buffer) {}

    public void onEndOfSpeech() {}

    public void onError(int error)
    {
        Log.d("foo", "onError " + String.valueOf(error));

        this.GetBrainActivity().OnError_speechRecognitionListener(error);

        //if (shouldContinuoslyListen == true) {this.listener.Listen();}
    }

    public void onResults(Bundle results)
    {
        String str = new String();
        //Log.d(TAG, "onResults " + results);
        ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        for (int i = 0; i < data.size(); i++)
        {
            //Log.d("foo", "result " + data.get(i));
            str += data.get(i).toString();
            this.listener.OnSpeechRecognized(str);
            return;
        }
    }

    public void onPartialResults(Bundle partialResults) {}

    public void onEvent(int eventType, Bundle params) {}
}
