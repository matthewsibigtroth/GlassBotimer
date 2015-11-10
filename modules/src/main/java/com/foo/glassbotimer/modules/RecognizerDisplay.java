package com.foo.glassbotimer.modules;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;

import com.google.android.glass.app.Card;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by sibigtroth on 7/4/14.
 */



public class RecognizerDisplay extends RelativeLayout
{

    private BrainActivity brainActivity;
    private ArrayList<ObjectRecoCard> cards;
    private CardScrollView cardScrollView;


    public RecognizerDisplay(Context context)
    {
        super(context);

        this.brainActivity = (BrainActivity) context;

        this.Init();
    }

    private void Init()
    {
        this.cards = new ArrayList<ObjectRecoCard>();

        this.CreateCardScrollView();

        this.setVisibility(INVISIBLE);
        this.setY(this.GetBrainActivity().H_SCREEN);
    }

    private void CreateCardScrollView()
    {
        this.cardScrollView = new CardScrollView(getContext());
        CardScrollAdapter_extended cardScrollAdapter_extended = new CardScrollAdapter_extended();
        this.cardScrollView.setAdapter(cardScrollAdapter_extended);
        //this.cardScrollView.activate();
        this.cardScrollView.setOnItemClickListener(OnItemClickListener_cardScrollView);
        this.addView(this.cardScrollView);
    }

    private class CardScrollAdapter_extended extends CardScrollAdapter
    {

        @Override
        public int getPosition(Object item) {
            return cards.indexOf(item);
        }

        @Override
        public int getCount() {
            return cards.size();
        }

        @Override
        public Object getItem(int position) {
            return cards.get(position);
        }

        @Override
        public int getViewTypeCount() {
            return Card.getViewTypeCount();
        }

        @Override
        public int getItemViewType(int position){
            return cards.get(position).getItemViewType();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return  cards.get(position).getView(convertView, parent);
        }
    }


    ///////////////////////////
    //accessors
    ///////////////////////////

    private BrainActivity GetBrainActivity() {return this.brainActivity;}
    private RecognizerDisplay GetSelf() {return this.GetBrainActivity().GetRecognizerDisplay();}
    private ImageAnalyzer GetImageAnalyzer() {return this.GetBrainActivity().GetImageAnalyzer();}


    ///////////////////////////
    //callbacks
    ///////////////////////////

    private AdapterView.OnItemClickListener OnItemClickListener_cardScrollView = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Log.d("foo", "onItemClick");
            //ObjectRecoCard card = (ObjectRecoCard)adapterView.getSelectedItem();
        }
    };

    private Animator.AnimatorListener AnimatorListener_this = new Animator.AnimatorListener()
    {
        @Override
        public void onAnimationStart(Animator animator) {}

        @Override
        public void onAnimationEnd(Animator animator)
        {
            setVisibility(INVISIBLE);
        }

        @Override
        public void onAnimationCancel(Animator animator) {}

        @Override
        public void onAnimationRepeat(Animator animator) {}
    };

    ///////////////////////////
    //utilities
    ///////////////////////////

    public void CreateObjectRecoCard(String filePath_image, String objectName) throws IOException
    {
        Log.d("foo", "CreateObjectRecoCard");

        File file = new File(filePath_image);
        //final Bitmap bitmap_ = this.GetImageAnalyzer().ResizeImage(filePath_image, .15f);
        final Bitmap bitmap_ = BitmapFactory.decodeFile(filePath_image);
        if (objectName == "") {objectName = "Not sure what this is";}
        final String objectName_ = objectName;

        this.GetBrainActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                ObjectRecoCard card = new ObjectRecoCard(getContext());
                card.objectName = objectName_;

                card.setText(objectName_);
                //card.setFootnote(snippetText);

                //card.setImageLayout(Card.ImageLayout.FULL);
                card.setImageLayout(Card.ImageLayout.LEFT);
                card.addImage(bitmap_);

                //View view = card.getView();
                //addView((view));
                cards.add(card);
                cardScrollView.activate();

                cardScrollView.setSelection(cards.size() - 1);
                //ScrollToEndOfCards();
            }
        });
    }


    public void Show()
    {
        Log.i("foo", "SHOW cards");
        this.GetBrainActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i("foo", "cards y:  " + getY());
                setVisibility(VISIBLE);
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(GetSelf(), "y", getY(), 0).setDuration(250);
                objectAnimator.setStartDelay(1200); //add this delay to give display time to draw the view before animation starts
                objectAnimator.start();
            }
        });
    }

    public void Hide()
    {
        this.GetBrainActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(GetSelf(), "y", getY(), GetBrainActivity().H_SCREEN).setDuration(250);
                objectAnimator.addListener(AnimatorListener_this);
                objectAnimator.start();
            }
        });
    }








    class ObjectRecoCard extends Card
    {

        public String objectName;

        public ObjectRecoCard(Context context)
        {
            super(context);

            this.Init();
        }

        private void Init()
        {
            this.objectName = null;
        }

    }
}