package com.foo.glassbotimer.modules;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.glass.touchpad.GestureDetector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;


/*
TODO:
scroll to next freebase node card (right now the cardscrollview just discretely jumps to that card)
scrub through palette sounds with compass?
find out where frame skipping is being caused
use autofocus on camera
progress bar for object reco?
swipe down to exit from PhotoTakerActivity
*/

public class BrainActivity extends Activity
{

    private Speaker speaker;
    private Listener listener;
    private Thinker thinker;
    private Knower knower;
    public int W_SCREEN;
    public int H_SCREEN;
    private RelativeLayout topContainer;
    private BusyDisplay busyDisplay;
    private SpeakingDisplay speakingDisplay;
    private KnowerDisplay knowerDisplay;
    private ImageAnalyzer imageAnalyzer;
    private SynesthesiaDisplay synesthesiaDisplay;
    private GestureDetector gestureDetector;
    private Recognizer recognizer;
    private ListeningDisplay listeningDisplay;
    private String currentMode;
    private RecognizerDisplay recognizerDisplay;
    private int RESULT_CODE_PHOTO_TAKER_ACTIVITY;
    private Handler handler_instructionalTextVisibilityCycles;
    private boolean amShowingInstructionalText;
    private Random random;
    private HelpDisplay helpDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brain);

        this.Init();
    }

    private void Init()
    {
        this.topContainer = (RelativeLayout) this.findViewById(R.id.topContainer);
        this.W_SCREEN = getWindowManager().getDefaultDisplay().getWidth();
        this.H_SCREEN = getWindowManager().getDefaultDisplay().getHeight();
        this.currentMode = "chat";
        this.RESULT_CODE_PHOTO_TAKER_ACTIVITY = 2;
        this.amShowingInstructionalText = false;
        this.random = new Random();

        this.CreateHelpDisplay();
        this.CreateThinker();
        this.CreateKnower();
        this.CreateSpeaker();
        this.CreateListener();
        this.CreateImageAnalyzer();
        this.CreateRecognizer();
        this.CreateSpeakingDisplay();
        this.CreateKnowerDisplay();
        this.CreateSynesthesiaDisplay();
        this.CreateListeningDisplay();
        this.CreateRecognizerDisplay();
        this.CreateBusyDisplay();
        this.CreateGestureDetector();
        this.DisableAutoSleep();
    }


    private void CreateSpeaker()
    {
        this.speaker = new Speaker(this);
    }

    private void CreateThinker()
    {
        this.thinker = new Thinker(this);
    }

    private void CreateListener()
    {
        this.listener = new Listener(this);
    }

    private void CreateKnower()
    {
        this.knower = new Knower(this);
    }

    private void CreateImageAnalyzer() {this.imageAnalyzer = new ImageAnalyzer(this);}

    private void CreateRecognizer() {this.recognizer = new Recognizer(this);}


    private void CreateHelpDisplay()
    {
        this.helpDisplay = new HelpDisplay(this);
        this.topContainer.addView(this.helpDisplay);
    }

    private void CreateBusyDisplay()
    {
        this.busyDisplay = new BusyDisplay(this);
        this.topContainer.addView(this.busyDisplay);
        float x = this.W_SCREEN/2 - 40;
        float y = this.H_SCREEN/2 - 40;
        this.busyDisplay.setX(x);
        this.busyDisplay.setY(y);
    }

    private void CreateSpeakingDisplay()
    {
        this.speakingDisplay = new SpeakingDisplay(this);
        this.topContainer.addView(this.speakingDisplay);
    }

    private void CreateKnowerDisplay()
    {
        this.knowerDisplay = new KnowerDisplay(this);
        this.topContainer.addView(this.knowerDisplay);
    }

    private void CreateSynesthesiaDisplay()
    {
        this.synesthesiaDisplay = new SynesthesiaDisplay(this);
        this.synesthesiaDisplay.setFocusable(true);
        this.synesthesiaDisplay.setFocusableInTouchMode(true);
        this.synesthesiaDisplay.requestFocus();
        this.topContainer.addView(this.synesthesiaDisplay);
    }

    private void CreateListeningDisplay()
    {
        this.listeningDisplay = new ListeningDisplay(this);
        this.topContainer.addView(this.listeningDisplay);
        float x = this.W_SCREEN/2 - 28;
        float y = this.H_SCREEN/2 - 28;
        this.listeningDisplay.setX(x);
        this.listeningDisplay.setY(y);
    }

    private void CreateRecognizerDisplay()
    {
        this.recognizerDisplay = new RecognizerDisplay(this);
        this.topContainer.addView(this.recognizerDisplay);
    }

    private void CreateGestureDetector()
    {
        this.gestureDetector = new GestureDetector(this);
        this.gestureDetector.setFingerListener(GestureDetectorFingerListener);
    }


    /////////////////////////////////////
    //accessors
    /////////////////////////////////////

    public Listener GetListener() {return this.listener;}
    public Speaker GetSpeaker() {return this.speaker;}
    public Thinker GetThinker() {return this.thinker;}
    public Knower GetKnower() {return this.knower;}
    public BusyDisplay GetBusyDisplay() {return this.busyDisplay;}
    public SpeakingDisplay GetSpeakingDisplay() {return this.speakingDisplay;}
    public KnowerDisplay GetKnowerDisplay() {return this.knowerDisplay;}
    public ImageAnalyzer GetImageAnalyzer() {return this.imageAnalyzer;}
    public SynesthesiaDisplay GetSynesthesiaDisplay() {return this.synesthesiaDisplay;}
    public Recognizer GetRecognizer() {return this.recognizer;}
    private String GetCurrentMode() {return this.currentMode;}
    private void SetCurrentMode(String newMode) {this.currentMode = newMode;}
    public ListeningDisplay GetListeningDisplay() {return this.listeningDisplay;}
    public RecognizerDisplay GetRecognizerDisplay() {return this.recognizerDisplay;}
    public HelpDisplay GetHelpDisplay() {return this.helpDisplay;}


    /////////////////////////////////////
    //callbacks
    /////////////////////////////////////

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (this.gestureDetector != null) {
            return gestureDetector.onMotionEvent(event);
        }
        return false;
    }

    private GestureDetector.FingerListener GestureDetectorFingerListener = new GestureDetector.FingerListener()
    {
        @Override
        public void onFingerCountChanged(int previousCount, int currentCount)
        {
            //Log.d("foo", "onFingerCountChanged   currentNumFingers " + currentCount);
            if (currentCount == 2) {OnTwoFingerTouch();}
            if (currentCount == 3) {OnThreeFingerTouch();}
        }
    };


    private void OnTwoFingerTouch()
    {
        Log.d("foo", "OnTwoFingerTouch");

        if (this.GetCurrentMode() == "chat") {this.Listen();}

        //this.SynesthesizeScene();
        //this.AskKnowerAbout("mathematics");
        //this.SayToBot("have you ever been to europe");
    }

    private void OnThreeFingerTouch()
    {
        Log.d("foo", "OnThreeFingerTouch");

        //this.SayToBot("where are you");
        //this.AskKnowerAbout("mathematics");
        //this.TakePicture();
        //this.Listen();
        //this.SynesthesizeScene();
        //this.RecognizeScene();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            boolean shouldPerformNativeBackNav = this.CheckIfSHouldPerformNativeBack();
            if (shouldPerformNativeBackNav == false)
            {
                this.NavBack();
                return false;
            }
        }

        super.onKeyDown(keyCode, event);

        return true;
    }

    private boolean CheckIfSHouldPerformNativeBack()
    {
        if (this.GetCurrentMode() == "chat") {return true;}

        return false;
    }

    public void OnError_speechRecognitionListener(int error)
    {
        Log.i("foo", "OnError_speechRecognitionListener  code:  " + error);
        if ( (error == 6) || (error == 5) || (error == 8) )
        {
            //this.GetHelpDisplay().ShowTwoFingerTapText();
            this.GetHelpDisplay().HideModeHelpText();
            this.GetListeningDisplay().Hide();
        }
    }

    public void OnSpeechRecognized(String recognizedSpeech)
    {
        Log.i("foo", "robot heard:  " + recognizedSpeech);
        //this.GetHelpDisplay().ShowTwoFingerTapText();
        this.GetHelpDisplay().HideModeHelpText();
        this.GetListeningDisplay().Hide();
        this.HandleRecognizedSpeech(recognizedSpeech);
    }

    public void OnStart_ttsSpeak()
    {
        Log.i("foo", "OnStart_ttsSpeak");
        this.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                GetSpeakingDisplay().StartAnimatingTtsIndicators();
            }
        });
    }

    public void OnDone_ttsSpeak()
    {
        this.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                GetSpeakingDisplay().StopAnimatingTtsIndicators();
                if (GetCurrentMode() == "chat")
                {
                    GetBusyDisplay().HideBusyIndicator();
                    Listen();
                }
            }
        });
    }

    public void OnComplete_findFreebaseNodeDataForInputText(final Knower.FreebaseNodeData freebaseNodeData, final String inputText) throws IOException
    {
        Log.i("foo", "OnComplete_findFreebaseNodeDataForInputText");

        this.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                GetBusyDisplay().HideBusyIndicator();
                GetKnowerDisplay().Show();

                if (freebaseNodeData != null)
                {
                    try {
                        GetKnowerDisplay().CreateFreebaseNodeCard(freebaseNodeData, inputText);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    GetKnowerDisplay().requestFocus();
                    SpeakFreebaseNodeText(freebaseNodeData);
                }
            }
        });
    }

    public void OnComplete_findRelatedFreebaseNodeDataForInputText(final Knower.FreebaseNodeData freebaseNodeData, final String inputText) throws IOException
    {
        Log.i("foo", "OnComplete_findFreebaseNodeDataForInputText");

        this.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                GetBusyDisplay().HideBusyIndicator();

                if (freebaseNodeData != null)
                {
                    try {
                        GetKnowerDisplay().CreateFreebaseNodeCard(freebaseNodeData, inputText);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    GetKnowerDisplay().requestFocus();
                    SpeakFreebaseNodeText(freebaseNodeData);
                }
            }
        });
    }

    public void OnItemClick_freebaseNodeCard(KnowerDisplay.FreebaseCard card)
    {
        this.GetBusyDisplay().ShowBusyIndicator();
        String name = card.freebaseNodeData.name;
        this.GetKnower().FindRelatedFreebaseNodeDataForInputText(name);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.i("foo", "onActivityResult resultCode:   " + resultCode);

        if (resultCode == this.RESULT_CODE_PHOTO_TAKER_ACTIVITY) {this.HandleActivityResult_photoTakerActivity(data);}

        //super.onActivityResult(requestCode, resultCode, data);
    }

    public void OnTakenPictureReady(String filePath_image)
    {
        Log.i("foo", "OnPictureReady  "  + filePath_image);

        if (this.GetCurrentMode() == "synesthesia")
        {
            //this.GetSpeaker().SpeakAfterDelay("Analyzing", 1000);
            this.GetSpeaker().Speak("Analyzing");
            this.GetSynesthesiaDisplay().UpdateImageListenerDisplay_onPictureReady(filePath_image);
            this.GetSynesthesiaDisplay().HidePaletteDisplay();
            //this.GetSynesthesiaDisplay().Show();
            this.GetSynesthesiaDisplay().ShowAfterDelay(1000);
            this.FindTakenPictureImagePalette(filePath_image);
        }
        else if (this.GetCurrentMode() == "objectReco")
        {
            this.GetSpeaker().Speak("Analyzing");
            this.GetRecognizer().RecognizeImage(filePath_image);
        }
    }

    private void OnComplete_findImagePalette(final ArrayList<Integer> colors, String filePath_image)
    {
        Log.i("foo","OnComplete_findImagePalette");

        this.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                GetBusyDisplay().HideBusyIndicator();
                GetSynesthesiaDisplay().UpdateTonePlayer_onPaletteFound(colors);
                GetSynesthesiaDisplay().UpdateImageListenerDisplay_onPaletteFound(colors);
                GetSynesthesiaDisplay().ShowPaletteDisplayAfterDelay(500);
                GetSynesthesiaDisplay().requestFocus();
            }
        });

    }

    public void OnComplete_recognizeScene(final String filePath_image, final String recognizedObject) throws IOException
    {
        this.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                GetBusyDisplay().HideBusyIndicator();
                try {
                    GetRecognizerDisplay().CreateObjectRecoCard(filePath_image, recognizedObject);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                GetRecognizerDisplay().requestFocus();
                GetRecognizerDisplay().Show();
                SpeakObjectRecoText(recognizedObject);
            }
        });
    }

    @Override
    public void onPause()
    {
        Log.v("foo", "onPause");
        this.GetListeningDisplay().OnActivityPause();
        super.onPause();
    }

    @Override
    public void onResume()
    {
        Log.v("foo", "onResume");
        this.GetListeningDisplay().OnActivityResume();
        super.onResume();
    }

    @Override
    protected void onDestroy()
    {
        Log.v("foo", "onDestroy");
        this.GetSpeaker().ShutDown();
        this.GetListener().ShutDown();
        super.onDestroy();
    }

    /////////////////////////////////////
    //utilities
    /////////////////////////////////////

    private void HandleRecognizedSpeech(String recognizedSpeech)
    {
        this.GetBusyDisplay().ShowBusyIndicator();

        String hotPhrase_knowledge = this.CheckForHotPhrase_knowledge(recognizedSpeech);
        String hotPhrase_synesthesia = this.CheckForHotPhrase_synesthesia(recognizedSpeech);
        String hotPhrase_objectRecognition = this.CheckForHotPhrase_objectRecognition(recognizedSpeech);
        if (hotPhrase_knowledge != "") {this.HandleRecognizedSpeech_knowledge(hotPhrase_knowledge, recognizedSpeech);}
        else if (hotPhrase_synesthesia != "") {this.HandleRecognizedSpeech_photo(hotPhrase_synesthesia, recognizedSpeech);}
        else if (hotPhrase_objectRecognition != "") {this.HandleRecognizedSpeech_objectRecognition(hotPhrase_objectRecognition, recognizedSpeech);}
        else {this.GetThinker().SayToBot(recognizedSpeech);}
    }

    private String CheckForHotPhrase_knowledge(String recognizedSpeech)
    {
        ArrayList<String> hotPhrases_knowledge = new ArrayList<String>();
        hotPhrases_knowledge.add("what is");
        hotPhrases_knowledge.add("what are");
        hotPhrases_knowledge.add("show me");
        hotPhrases_knowledge.add("what do you know about");
        hotPhrases_knowledge.add("who is");
        hotPhrases_knowledge.add("where is");
        hotPhrases_knowledge.add("tell me about");
        hotPhrases_knowledge.add("have you ever heard of");

        for (int i=0; i<hotPhrases_knowledge.size(); i++)
        {
            String hotPhrase = hotPhrases_knowledge.get(i);
            if (recognizedSpeech.contains(hotPhrase) == true)
            {
                return hotPhrase;
            }
        }
        return "";
    }

    private String CheckForHotPhrase_synesthesia(String recognizedSpeech)
    {
        ArrayList<String> hotPhrases_synesthesia = new ArrayList<String>();
        hotPhrases_synesthesia.add("play what you see");

        for (int i=0; i<hotPhrases_synesthesia.size(); i++)
        {
            String hotPhrase = hotPhrases_synesthesia.get(i);
            if (recognizedSpeech.contains(hotPhrase) == true)
            {
                return hotPhrase;
            }
        }
        return "";
    }

    private String CheckForHotPhrase_objectRecognition(String recognizedSpeech)
    {
        ArrayList<String> hotPhrases_objectRecognition = new ArrayList<String>();
        hotPhrases_objectRecognition.add("what do you see");

        for (int i=0; i<hotPhrases_objectRecognition.size(); i++)
        {
            String hotPhrase = hotPhrases_objectRecognition.get(i);
            if (recognizedSpeech.contains(hotPhrase) == true)
            {
                return hotPhrase;
            }
        }
        return "";
    }


    private void HandleRecognizedSpeech_knowledge(String hotPhrase, String recognizedSpeech)
    {
        int index_hotPhrase = recognizedSpeech.indexOf(hotPhrase);
        int index_start = index_hotPhrase + hotPhrase.length();
        int index_stop = recognizedSpeech.length();
        String contentString = recognizedSpeech.substring(index_start, index_stop);
        this.AskKnowerAbout(contentString);
    }

    private void HandleRecognizedSpeech_objectRecognition(String hotPhrase, String recognizedSpeech)
    {
        int index_hotPhrase = recognizedSpeech.indexOf(hotPhrase);
        int index_start = index_hotPhrase + hotPhrase.length();
        int index_stop = recognizedSpeech.length();
        String contentString = recognizedSpeech.substring(index_start, index_stop);
        this.RecognizeScene();
    }

    private void HandleRecognizedSpeech_photo(String hotPhrase, String recognizedSpeech)
    {
        this.SynesthesizeScene();
    }

    private void SpeakFreebaseNodeText(Knower.FreebaseNodeData freebaseNodeData)
    {
        String text = freebaseNodeData.text;
        String[] Sentences = text.split("\\.");
        String firstSentence = Sentences[0];
        this.GetSpeaker().Speak(firstSentence);
    }

    private void FindTestImagePalette()
    {
        Log.i("foo", "FindTestImagePalette");
        Runnable runnable = new Runnable()
        {
            @Override
            public void run()
            {
                //String imageName = "20140627_191047_273.jpg";
                //String imageName = "20140627_113633_648.jpg";
                //String imageName = "20140703_092409_325.jpg";
                //String imageName = "20140703_101320_319.jpg";
                String imageName = "20140703_101329_448.jpg";
                //String imageName = "testImage.jpg";
                String folderPath = "/mnt/sdcard/DCIM/Camera/";
                String filePath_image = folderPath + imageName;
                ArrayList<Integer> colors = GetImageAnalyzer().FindImagePalette(filePath_image);
                OnComplete_findImagePalette(colors, filePath_image);
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void FindTakenPictureImagePalette(final String filePath_image)
    {
        Log.i("foo", "FindTakenPictureImagePalette");

        Runnable runnable = new Runnable()
        {
            @Override
            public void run()
            {
                ArrayList<Integer> colors = GetImageAnalyzer().FindImagePalette(filePath_image);
                OnComplete_findImagePalette(colors, filePath_image);
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }


    private void SynesthesizeScene()
    {
        this.SetCurrentMode("synesthesia");
        this.GetHelpDisplay().HideTwoFingerTapText();
        this.StartPhotoTakerActivity();
    }

    private void NavBack()
    {
        if (this.GetCurrentMode() == "synesthesia") {this.GetSynesthesiaDisplay().Hide();}
        if (this.GetCurrentMode() == "knowledge") {this.GetKnowerDisplay().Hide();}
        if (this.GetCurrentMode() == "objectReco") {this.GetRecognizerDisplay().Hide();}

        this.SetCurrentMode("chat");
        this.GetHelpDisplay().ShowTwoFingerTapText();
    }

    private void AskKnowerAbout(String inputText)
    {
        this.SetCurrentMode("knowledge");
        this.GetBusyDisplay().ShowBusyIndicator();
        this.GetHelpDisplay().HideTwoFingerTapText();
        this.GetKnower().FindFreebaseNodeDataForInputText(inputText);
    }

    private void SayToBot(String textToSpeak)
    {
        this.GetBusyDisplay().ShowBusyIndicator();
        this.GetThinker().SayToBot(textToSpeak);
    }

    private void Listen()
    {
        this.GetHelpDisplay().HideTwoFingerTapText();
        this.GetHelpDisplay().ShowModeHelpText();
        this.GetListeningDisplay().Show();
        this.GetListener().Listen();
    }

    private void RecognizeScene()
    {
        this.SetCurrentMode("objectReco");
        this.GetHelpDisplay().HideTwoFingerTapText();
        this.StartPhotoTakerActivity();
    }

    private void SpeakObjectRecoText(String recognizedObject)
    {
        String textToSpeak = "This looks like a " + recognizedObject;
        if (recognizedObject == "") {textToSpeak = "I'm not sure what this is";}
        this.GetSpeaker().Speak(textToSpeak);
    }

    private void StartPhotoTakerActivity()
    {
        Intent intent = new Intent(this, PhotoTakerActivity.class);
        startActivityForResult(intent, 3);
    }

    private void HandleActivityResult_photoTakerActivity(Intent data)
    {
        Log.i("foo", "HandleActivityResult_photoTakerActivity");

        //get the bitmap data for the just-taken picture
        byte[] photoData = data.getByteArrayExtra("PHOTO_DATA");
        Bitmap bitmap = BitmapFactory.decodeByteArray(photoData, 0, photoData.length);

        //save the photo to file
        Log.i("foo", "taken picture dimensions  w  " + bitmap.getWidth() + "   h  " + bitmap.getHeight());
        String folderPath = "/mnt/sdcard/DCIM/Camera/";
        String imageName = "testImage.jpg";
        String filePath_image = folderPath + imageName;
        this.SaveJustTakenPhotoToFile(bitmap, filePath_image);

        this.OnTakenPictureReady(filePath_image);
    }

    private void SaveJustTakenPhotoToFile(Bitmap bitmap, String filePath_image)
    {
        File file = new File(filePath_image);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        try {
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void DisableAutoSleep()
    {
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void EnableAutoSleep()
    {
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }



}
