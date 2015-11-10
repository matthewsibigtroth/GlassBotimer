package com.foo.glassbotimer.modules;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Random;

/**
 * Created by sibigtroth on 7/2/14.
 */
public class ListeningDisplay extends RelativeLayout
{

    private BrainActivity brainActivity;
    private ImageView listeningIcon;
    private ImageView listeningCircle;
    private boolean shouldPulse;
    private String pulseDirection;

    public ListeningDisplay(Context context)
    {
        super(context);

        this.brainActivity = (BrainActivity) context;

        this.Init();
    }

    private void Init()
    {
        this.shouldPulse = false;
        this.pulseDirection = "up";

        //this.CreateListeningCircle();
        this.CreateListeningIcon();

        this.setVisibility(INVISIBLE);
        this.setScaleX(.01f);
        this.setScaleY(.01f);
    }


    private void CreateListeningIcon()
    {
        this.listeningIcon = new ImageView(getContext());
        this.listeningIcon.setImageResource(R.drawable.mic64x64);
        this.addView(this.listeningIcon);
    }

    private void CreateListeningCircle()
    {
        this.listeningCircle = new ImageView(getContext());
        this.listeningCircle.setImageResource(R.drawable.circle70x70);
        this.addView(this.listeningCircle);
        this.listeningCircle.setX(-2);
        this.listeningCircle.setY(-2);
    }


    ///////////////////////////
    //accessors
    ///////////////////////////

    private BrainActivity GetBrainActivity() {return this.brainActivity;}


    ///////////////////////////
    //callbacks
    ///////////////////////////

    private Animator.AnimatorListener AnimatorListener_hide = new Animator.AnimatorListener()
    {
        @Override
        public void onAnimationCancel(Animator arg0) {}

        @Override
        public void onAnimationEnd(Animator Animator)
        {
            shouldPulse = false;
            setVisibility(INVISIBLE);
        }

        @Override
        public void onAnimationRepeat(Animator arg0) {}

        @Override
        public void onAnimationStart(Animator arg0) {}
    };

    private Animator.AnimatorListener AnimatorListener_show = new Animator.AnimatorListener()
    {
        @Override
        public void onAnimationCancel(Animator arg0) {}

        @Override
        public void onAnimationEnd(Animator Animator)
        {
            shouldPulse = true;
            pulseDirection = "up";
            Pulse();
        }

        @Override
        public void onAnimationRepeat(Animator arg0) {}

        @Override
        public void onAnimationStart(Animator arg0) {}
    };

    private Animator.AnimatorListener AnimatorListener_pulse = new Animator.AnimatorListener()
    {
        @Override
        public void onAnimationCancel(Animator arg0) {}

        @Override
        public void onAnimationEnd(Animator Animator)
        {
            Pulse();
        }

        @Override
        public void onAnimationRepeat(Animator arg0) {}

        @Override
        public void onAnimationStart(Animator arg0) {}
    };

    public void OnActivityPause()
    {
        this.shouldPulse = false;
    }

    public void OnActivityResume()
    {
        this.shouldPulse = false;
        this.Hide();
    }


    ///////////////////////////
    //utilities
    ///////////////////////////

    public void Show()
    {
        Log.i("foo", "SHOW listening display");

        this.setVisibility(VISIBLE);
        ObjectAnimator objectAnimator_scaleX = ObjectAnimator.ofFloat(this, "scaleX", 0, 1).setDuration(250);
        ObjectAnimator objectAnimator_scaleY = ObjectAnimator.ofFloat(this, "scaleY", 0, 1).setDuration(250);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.addListener(AnimatorListener_show);
        animatorSet.playTogether(objectAnimator_scaleX, objectAnimator_scaleY);
        animatorSet.start();
    }

    public void Hide()
    {
        Log.i("foo", "HIDE listening display");

        this.shouldPulse = false;

        ObjectAnimator objectAnimator_scaleX = ObjectAnimator.ofFloat(this, "scaleX", this.getScaleX(), .01f).setDuration(250);
        ObjectAnimator objectAnimator_scaleY = ObjectAnimator.ofFloat(this, "scaleY", this.getScaleY(), .01f).setDuration(250);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.addListener(AnimatorListener_hide);
        animatorSet.playTogether(objectAnimator_scaleX, objectAnimator_scaleY);
        animatorSet.start();
    }

    private void Pulse()
    {
        if (this.shouldPulse == false) {return;}

        //if (this.getScaleX() == 1) {this.PulseUp();}
        //else {this.PulseDown();}
        if (this.pulseDirection == "up")
        {
            this.PulseUp();
            this.pulseDirection = "down";
        }
        else
        {
            this.PulseDown();
            this.pulseDirection = "up";
        }
    }

    private void PulseUp()
    {
        Log.i("foo", "PulseUp");

        ObjectAnimator objectAnimator_scaleX = ObjectAnimator.ofFloat(this, "scaleX", this.getScaleX(), 1.3f).setDuration(1000);
        ObjectAnimator objectAnimator_scaleY = ObjectAnimator.ofFloat(this, "scaleY", this.getScaleY(), 1.3f).setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.addListener(AnimatorListener_pulse);
        animatorSet.playTogether(objectAnimator_scaleX, objectAnimator_scaleY);
        animatorSet.start();
    }

    private void PulseDown()
    {
        Log.i("foo", "PulseDown");

        ObjectAnimator objectAnimator_scaleX = ObjectAnimator.ofFloat(this, "scaleX", this.getScaleX(), 1.0f).setDuration(1000);
        ObjectAnimator objectAnimator_scaleY = ObjectAnimator.ofFloat(this, "scaleY", this.getScaleY(), 1.0f).setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.addListener(AnimatorListener_pulse);
        animatorSet.playTogether(objectAnimator_scaleX, objectAnimator_scaleY);
        animatorSet.start();
    }


}