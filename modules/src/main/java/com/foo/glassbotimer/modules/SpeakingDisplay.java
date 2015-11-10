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

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by sibigtroth on 6/25/14.
 */
public class SpeakingDisplay extends RelativeLayout
{

    private BrainActivity brainActivity;
    private ArrayList<TtsIndicator> ttsIndicators;
    private boolean shouldAnimateTtsIndicators;
    private Random random;


    public SpeakingDisplay(Context context)
    {
        super(context);

        this.brainActivity = (BrainActivity) context;

        this.Init();
    }

    private void Init()
    {
        this.ttsIndicators = new ArrayList<TtsIndicator>();
        this.shouldAnimateTtsIndicators = false;
        this.random = new Random();

        this.CreateTtsIndicators();
    }

    private void CreateTtsIndicators()
    {
        int deltaX_betweenIndicators = 0;
        int numRows = 1;
        int numColumns = 15;
        int w = 40;
        int h = 2;
        int counter_indicator = 0;
        int x_offset = w/2;
        for (int i=0; i<numRows; i++)
        {
            for (int j=0; j<numColumns; j++)
            {
                float x = (j * w) + (counter_indicator * deltaX_betweenIndicators) + x_offset;
                float y = i * h;
                this.CreateTtsIndicator(x, y, w, h);
                counter_indicator += 1;
            }
        }
    }

    private void CreateTtsIndicator(float x, float y, int w, int h)
    {
        TtsIndicator ttsIndicator = new TtsIndicator(getContext(), w, h);
        this.addView(ttsIndicator);
        ttsIndicator.setX(x);
        ttsIndicator.setY(y);
        this.ttsIndicators.add(ttsIndicator);
    }


    ///////////////////////////
    //accessors
    ///////////////////////////

    private BrainActivity GetBrainActivity() {return this.brainActivity;}


    ///////////////////////////
    //callbacks
    ///////////////////////////

    private Animator.AnimatorListener AnimatorListener_ttsIndicator_scaleUp = new Animator.AnimatorListener()
    {
        @Override
        public void onAnimationCancel(Animator arg0) {}

        @Override
        public void onAnimationEnd(Animator Animator)
        {
            TtsIndicator TtsIndicator = (TtsIndicator) ((ObjectAnimator) Animator).getTarget();
            ScaleDownTtsIndicator(TtsIndicator);
        }

        @Override
        public void onAnimationRepeat(Animator arg0) {}

        @Override
        public void onAnimationStart(Animator arg0) {}
    };

    private Animator.AnimatorListener AnimatorListener_ttsIndicator_scaleDown = new Animator.AnimatorListener()
    {
        @Override
        public void onAnimationCancel(Animator arg0) {}

        @Override
        public void onAnimationEnd(Animator Animator)
        {
            TtsIndicator TtsIndicator = (TtsIndicator) ((ObjectAnimator) Animator).getTarget();
            ScaleUpTtsIndicator(TtsIndicator);
        }

        @Override
        public void onAnimationRepeat(Animator arg0) {}

        @Override
        public void onAnimationStart(Animator arg0) {}
    };


    ///////////////////////////
    //utilities
    ///////////////////////////


    private void ScaleUpTtsIndicator(TtsIndicator ttsIndicator)
    {
        int delay = (new Random()).nextInt(250);
        int duration  = (new Random()).nextInt(300) + 300;
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(ttsIndicator, "y", ttsIndicator.getY(), 7);
        objectAnimator.setDuration(duration);
        objectAnimator.setStartDelay(delay);
        objectAnimator.addListener(AnimatorListener_ttsIndicator_scaleUp);
        objectAnimator.start();
    }

    private void ScaleDownTtsIndicator(TtsIndicator ttsIndicator)
    {
        int delay = (new Random()).nextInt(250);
        int duration  = (new Random()).nextInt(300) + 300;
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(ttsIndicator, "y", ttsIndicator.getY(), 0);
        objectAnimator.setDuration(duration);
        objectAnimator.setStartDelay(delay);
        if (this.shouldAnimateTtsIndicators == true) {objectAnimator.addListener(AnimatorListener_ttsIndicator_scaleDown);}
        objectAnimator.start();
    }

    public void StartAnimatingTtsIndicators()
    {
        Log.i("foo", "StartAnimatingTtsIndicators");
        this.shouldAnimateTtsIndicators = true;

        for (int i=0; i<this.ttsIndicators.size(); i++)
        {
            TtsIndicator TtsIndicator = this.ttsIndicators.get(i);
            this.ScaleUpTtsIndicator(TtsIndicator);
        }
    }

    public void StopAnimatingTtsIndicators()
    {
        Log.i("foo", "StopAnimatingTtsIndicators");
        this.shouldAnimateTtsIndicators = false;
    }






    class TtsIndicator extends View
    {

        public int w;
        public int h;
        private Paint paint = new Paint();

        public TtsIndicator(Context context, int w, int h)
        {
            super(context);

            this.w = w;
            this.h = h;

            this.Init();
        }

        private void Init()
        {

        }

        @Override
        public void onDraw(Canvas canvas)
        {
            this.paint.setStrokeWidth(0);
            this.paint.setColor(0xFF777777);
            canvas.drawRect(-this.w/2f, -this.h/2f, this.w, this.h, this.paint);
        }

    }

}
