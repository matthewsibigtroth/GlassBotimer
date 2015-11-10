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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


/**
 * Created by sibigtroth on 6/26/14.
 */

public class KnowerDisplay extends RelativeLayout
{

    private BrainActivity brainActivity;
    private ArrayList<FreebaseCard> cards;
    private CardScrollView cardScrollView;


    public KnowerDisplay(Context context)
    {
        super(context);

        this.brainActivity = (BrainActivity) context;

        this.Init();
    }

    private void Init()
    {
        this.cards = new ArrayList<FreebaseCard>();

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
    private KnowerDisplay GetSelf() {return this.GetBrainActivity().GetKnowerDisplay();}


    ///////////////////////////
    //callbacks
    ///////////////////////////


    private AdapterView.OnItemClickListener OnItemClickListener_cardScrollView = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            //GetBrainActivity().OnItemClick_freebaseNodeCard();
            //Log.d("foo", String.valueOf(adapterView.getSelectedItem()));
            //GetBrainActivity().GetKnower().FindFreebaseNodeDataForInputText("fruit");
            Log.d("foo", "onItemClick");
            FreebaseCard card = (FreebaseCard)adapterView.getSelectedItem();
            GetBrainActivity().OnItemClick_freebaseNodeCard(card);
            //GetBrainActivity().GetKnower().FindRelatedFreebaseNodeDataForInputText(card.freebaseNodeData.name);
            //Log.d("foo", card.freebaseNodeData.name);
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

    public void CreateFreebaseNodeCard(final Knower.FreebaseNodeData freebaseNodeData, String inputText) throws IOException
    {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                URL Url = null;
                Bitmap bitmap = null;

                try
                {
                    Log.d("foo", "CreateFreebaseNodeCard image url:   " + freebaseNodeData.url_image);

                    if (freebaseNodeData.url_image != "")
                    {
                        Url = new URL(freebaseNodeData.url_image);
                        try {
                            bitmap = BitmapFactory.decodeStream(Url.openConnection().getInputStream());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                catch (MalformedURLException e)
                {
                    e.printStackTrace();
                }

                final Bitmap bitmap_ = bitmap;
                final Knower.FreebaseNodeData freebaseNodeData_ = freebaseNodeData;
                final String nodeName = freebaseNodeData.name;
                final String snippetText = freebaseNodeData.text;

                GetBrainActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        FreebaseCard card = new FreebaseCard(getContext());
                        card.type = "freebaseNode";
                        card.freebaseNodeData = freebaseNodeData_;
                        card.setText(nodeName.toUpperCase());
                        card.setFootnote(snippetText);
                        if (freebaseNodeData_.url_image != "")
                        {
                            card.setImageLayout(Card.ImageLayout.FULL);
                            card.addImage(bitmap_);
                        }
                        //View view = card.getView();
                        //addView((view));
                        cards.add(card);
                        cardScrollView.activate();

                        cardScrollView.setSelection(cards.size() - 1);
                        //ScrollToEndOfCards();
                    }
                });
            }
        });

        thread.start();
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

    /*
    private void ScrollToEndOfCards()
    {
        Handler delayHandler= new Handler();
        Runnable r=new Runnable()
        {
            @Override
            public void run()
            {
                Log.d("foo", "ScrollToEndOfCards");
                cardScrollView.scrollBy(200, 0);
                cardScrollView.setSelection(cards.size() - 1);
            }

        };
        delayHandler.postDelayed(r, 3000);
    }
    */

    /*
        Handler delayHandler= new Handler();
        Runnable r=new Runnable()
        {
               @Override
                public void run() {

               // Call this method after 1000 milliseconds
                showCountryListViewIntent();

               }

        };
        delayHandler.postDelayed(r, 1000);
     */

    class FreebaseCard extends Card
    {

        public String type;
        public Knower.FreebaseNodeData freebaseNodeData;

        public FreebaseCard(Context context)
        {
            super(context);

            this.Init();
        }

        private void Init()
        {
            this.type = null;
            this.freebaseNodeData = null;
        }

    }



}
