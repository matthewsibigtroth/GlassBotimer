package com.foo.glassbotimer.modules;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.CountDownTimer;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by sibigtroth on 6/25/14.
 */


public class BusyDisplay extends RelativeLayout
{

    private BrainActivity brainActivity;
    private ProgressBar busyIndicator;


    public BusyDisplay(Context context)
    {
        super(context);

        this.brainActivity = (BrainActivity) context;

        this.Init();
    }

    private void Init()
    {
        this.CreateBusyIndicator();
    }

    private void CreateBusyIndicator()
    {
        this.busyIndicator = new ProgressBar(this.getContext(), null, android.R.attr.progressBarStyle);
        this.addView(this.busyIndicator);
        this.busyIndicator.setAlpha(0);
    }

    ///////////////////////////
    //accessors
    ///////////////////////////

    private BrainActivity GetBrainActivity() {return this.brainActivity;}


    ///////////////////////////
    //callbacks
    ///////////////////////////


    ///////////////////////////
    //utilities
    ///////////////////////////

    public void ShowBusyIndicator()
    {
        this.GetBrainActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int duration = 500;
                float alpha_start = busyIndicator.getAlpha();
                float alpha_stop = 1;
                ObjectAnimator ObjectAnimator_alpha = ObjectAnimator.ofFloat(busyIndicator, "alpha", alpha_start, alpha_stop);
                ObjectAnimator_alpha.setDuration(duration);
                ObjectAnimator_alpha.start();
            }
        });
    }

    public void HideBusyIndicator()
    {
        this.GetBrainActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int duration = 500;
                float alpha_start = busyIndicator.getAlpha();
                float alpha_stop = 0;
                ObjectAnimator ObjectAnimator_alpha = ObjectAnimator.ofFloat(busyIndicator, "alpha", alpha_start, alpha_stop);
                ObjectAnimator_alpha.setDuration(duration);
                ObjectAnimator_alpha.start();
            }
        });
    }
}
