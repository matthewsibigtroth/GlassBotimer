package com.foo.glassbotimer.modules;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by sibigtroth on 7/10/14.
 */
public class HelpDisplay extends RelativeLayout
{

    private BrainActivity brainActivity;
    private TextView textView_twoFingerTap;
    private TextView textView_sayWhatIs;
    private TextView textView_sayAnything;
    private TextView textView_sayListenToScene;
    private TextView textView_sayWhatDoYouSee;
    private int index_currentModeHelpText;

    public HelpDisplay(Context context)
    {
        super(context);

        this.brainActivity = (BrainActivity) context;

        this.Init();
    }

    private void Init()
    {
        this.index_currentModeHelpText = -1;

        this.InitLayoutParams();
        this.CreateTwoFingerTapText();
        this.CreateSayWhatIsText();
        this.CreateSayAnythingText();
        this.CreateSayListenToSceneText();
        this.CreateSayWhatDoYouSeeText();
    }

    private void InitLayoutParams()
    {
        LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layoutParams.width = this.brainActivity.W_SCREEN;
        layoutParams.height = this.brainActivity.H_SCREEN;
        this.setLayoutParams(layoutParams);
    }

    private void CreateTwoFingerTapText()
    {
        this.textView_twoFingerTap = new TextView(getContext());
        this.textView_twoFingerTap.setText("two finger tap");
        this.addView(this.textView_twoFingerTap);
        this.textView_twoFingerTap.setX(200);
        this.textView_twoFingerTap.setY(275);

    }

    private void CreateSayWhatIsText()
    {
        this.textView_sayWhatIs = new TextView(getContext());
        this.textView_sayWhatIs.setText("\" what is...? \"");
        this.addView(this.textView_sayWhatIs);
        //this.textView_sayWhatIs.setX(200);
        //this.textView_sayWhatIs.setY(275);
        this.textView_sayWhatIs.setVisibility(INVISIBLE);
        this.textView_sayWhatIs.setAlpha(0);
        this.textView_sayWhatIs.setX(215);
        this.textView_sayWhatIs.setY(275);
    }

    private void CreateSayAnythingText()
    {
        this.textView_sayAnything = new TextView(getContext());
        this.textView_sayAnything.setText("say something");
        this.addView(this.textView_sayAnything);
        this.textView_sayAnything.setVisibility(INVISIBLE);
        this.textView_sayAnything.setAlpha(0);
        this.textView_sayAnything.setX(190);
        this.textView_sayAnything.setY(275);
    }

    private void CreateSayListenToSceneText()
    {
        this.textView_sayListenToScene = new TextView(getContext());
        this.textView_sayListenToScene.setText("\" play what you see \"");
        this.addView(this.textView_sayListenToScene);
        this.textView_sayListenToScene.setVisibility(INVISIBLE);
        this.textView_sayListenToScene.setAlpha(0);
        this.textView_sayListenToScene.setX(145);
        this.textView_sayListenToScene.setY(275);
    }

    private void CreateSayWhatDoYouSeeText()
    {
        this.textView_sayWhatDoYouSee = new TextView(getContext());
        this.textView_sayWhatDoYouSee.setText("\" what do you see? \" ");
        this.addView(this.textView_sayWhatDoYouSee);
        this.textView_sayWhatDoYouSee.setVisibility(INVISIBLE);
        this.textView_sayWhatDoYouSee.setAlpha(0);
        this.textView_sayWhatDoYouSee.setX(150);
        this.textView_sayWhatDoYouSee.setY(275);
    }


    /////////////////////////////////////
    //accessors
    /////////////////////////////////////


    /////////////////////////////////////
    //callbacks
    /////////////////////////////////////

    private Animator.AnimatorListener AnimatorListener_hideText = new Animator.AnimatorListener()
    {
        @Override
        public void onAnimationCancel(Animator arg0) {}

        @Override
        public void onAnimationEnd(Animator animator)
        {
            View view = (View) ((ObjectAnimator) animator).getTarget();
        }

        @Override
        public void onAnimationRepeat(Animator arg0) {}

        @Override
        public void onAnimationStart(Animator arg0) {}
    };


    /////////////////////////////////////
    //utilities
    /////////////////////////////////////

    public void ShowTwoFingerTapText()
    {
        this.ShowText(this.textView_twoFingerTap);
    }

    public void HideTwoFingerTapText()
    {
        this.HideText(this.textView_twoFingerTap);
    }

    public void ShowModeHelpText()
    {
        int numModes = 4;
        this.index_currentModeHelpText += 1;
        if (this.index_currentModeHelpText > (numModes-1)) {this.index_currentModeHelpText = 0;}

        //this.index_currentModeHelpText = 2;

        if (this.index_currentModeHelpText == 0) {this.ShowText(this.textView_sayAnything);}
        else if (this.index_currentModeHelpText == 1) {this.ShowText(this.textView_sayWhatIs);}
        else if (this.index_currentModeHelpText == 2) {this.ShowText(this.textView_sayListenToScene);}
        else if (this.index_currentModeHelpText == 3) {this.ShowText(this.textView_sayWhatDoYouSee);}

        Log.i("foo", "ShowModeHelpText   " + this.index_currentModeHelpText);
    }

    public void HideModeHelpText()
    {
        Log.i("foo", "HideModeHelpText   " + this.index_currentModeHelpText);

        if (this.index_currentModeHelpText == 0) {this.HideText(this.textView_sayAnything);}
        else if (this.index_currentModeHelpText == 1) {this.HideText(this.textView_sayWhatIs);}
        else if (this.index_currentModeHelpText == 2) {this.HideText(this.textView_sayListenToScene);}
        else if (this.index_currentModeHelpText == 3) {this.HideText(this.textView_sayWhatDoYouSee);}
    }

    public void HideAllModeHelpText()
    {
        this.HideText(this.textView_sayWhatIs);
        this.HideText(this.textView_sayListenToScene);
        this.HideText(this.textView_sayWhatDoYouSee);
        this.HideText(this.textView_sayAnything);
    }

    private void ShowText(TextView textView)
    {
        textView.setVisibility(VISIBLE);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(textView, "alpha", textView.getAlpha(), 1).setDuration(250);
        objectAnimator.start();
    }

    private void HideText(TextView textView)
    {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(textView, "alpha", textView.getAlpha(), 0).setDuration(250);
        objectAnimator.addListener(AnimatorListener_hideText);
        objectAnimator.start();
    }


}
