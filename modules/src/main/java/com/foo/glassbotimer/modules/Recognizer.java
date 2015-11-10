package com.foo.glassbotimer.modules;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by sibigtroth on 7/2/14.
 */
public class Recognizer
{

    private BrainActivity brainActivity;
    private String imageRecognitionRequestToken;

    public Recognizer(BrainActivity brainActivity)
    {
        this.brainActivity = brainActivity;

        this.Init();
    }

    private void Init()
    {
        this.imageRecognitionRequestToken = "";
    }


    /////////////////////////////////////
    //accessors
    /////////////////////////////////////

    private BrainActivity GetBrainActivity() {return this.brainActivity;}


    /////////////////////////////////////
    //callbacks
    /////////////////////////////////////

    private void OnImageRecognitionAnalysisComplete(String filePath_image, String recognizedObject) throws IOException
    {
        this.GetBrainActivity().OnComplete_recognizeScene(filePath_image, recognizedObject);
    }


    /////////////////////////////////////
    //utilities
    /////////////////////////////////////

    public void RecognizeImage(final String filePath_image)
    {
        Log.i("foo", "RecognizeImage");

        //parse out the folder path and file name
        int index_lastSlash = filePath_image.lastIndexOf("/");
        String folderPath_image = filePath_image.substring(0, index_lastSlash + 1);
        String fileName_image = filePath_image.substring(index_lastSlash + 1);
        //Log.i("foo", "folder:   " + folderPath_image + "    file:  " + fileName_image);

        //resize the image for posting to camfind
        String filePath_image_resized = this.CreateResizedCameraCapturedImage(folderPath_image, fileName_image, 640, 480);
        this.PostResizedImageToCamFind(filePath_image_resized);

        //ask camfind to recognize the just-posted image (on the ui thread)
        this.GetBrainActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                AskCamFindToRecognizeImage(filePath_image);
            }
        });
    }

    private String CreateResizedCameraCapturedImage(String folderPath_image, String fileName_image, int w_new, int h_new)
    {
        Log.i("foo", "CreateResizedCameraCapturedImage");

        String filePath_image_original = folderPath_image + fileName_image;
        String filePath_image_resized = folderPath_image + "camFindImageToPost.jpg";

        Bitmap bitmap_orig = BitmapFactory.decodeFile(filePath_image_original);
        int w_bitmap_orig = bitmap_orig.getWidth();
        int h_bitmap_orig = bitmap_orig.getHeight();
        Bitmap bitmap_resized;
        if (w_bitmap_orig > h_bitmap_orig) {bitmap_resized = Bitmap.createScaledBitmap(bitmap_orig, w_new, h_new, false);}
        else {bitmap_resized = Bitmap.createScaledBitmap(bitmap_orig, h_new, w_new, false);}

        File file_resized = new File(filePath_image_resized);

        //Log.i("foo", "resized file exists  >>>>>>>>>>>>>>>>>>>>  " + file_resized.exists());

        FileOutputStream fOut;
        try
        {
            fOut = new FileOutputStream(file_resized);
            bitmap_resized.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
            bitmap_orig.recycle();
            bitmap_resized.recycle();

        }
        catch (Exception e)
        {

        }

        return filePath_image_resized;
    }

    private void PostResizedImageToCamFind(String filePath_image_resized)
    {
        Log.i("foo", "PostResizedImageToCamFind");

        final String filePath_image_resized_ = filePath_image_resized;

        Runnable runnable = new Runnable()
        {
            public void run()
            {

                File file_resized = new File(filePath_image_resized_);

                try
                {
                    HttpResponse<JsonNode> request_uploadImage = Unirest.post("https://camfind.p.mashape.com/image_requests")
                            //.header("X-Mashape-Authorization", "YMQQG7yJ4LsBWIrmnzS19ErBtWOTMHlW")
                            .header("X-Mashape-Authorization", "q5QVimyNMzOHw6VEbGOdxjO7bYfCMAOZ")
                            .field("image_request[locale]", "en_US")
                            .field("image_request[image]", file_resized)
                            .asJson();

                    Log.d("foo", request_uploadImage.toString());
                    String tokenString = request_uploadImage.getBody().toString();

                    //Log.d("foo", tokenString);
                    //String token = tokenString.substring(10, tokenString.length() - 2);
                    //imageRecognitionRequestToken = tokenString.substring(10, tokenString.length() - 2);  //used to work
                    //Log.d("foo", "*****************    " + tokenString);

                    int index_firstComma = tokenString.indexOf(",");
                    imageRecognitionRequestToken = tokenString.substring(10, index_firstComma-1);

                    Log.d("foo", "imageRecognitionRequestToken:   " + imageRecognitionRequestToken);

                } catch (UnirestException e)
                {
                    e.printStackTrace();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }


    private void AskCamFindToRecognizeImage(final String filePath_image)
    {
        Log.i("foo", "AskCamFindToRecognizeImage");

        Handler delayHandler= new Handler();
        Runnable runnable = new Runnable()
        {

            @Override
            public void run()
            {
                Log.d("foo", "delayed call token:  " + imageRecognitionRequestToken);

                Runnable runnable = new Runnable()
                {

                    @Override
                    public void run()
                    {
                        HttpResponse<JsonNode> request_recognized = null;
                        try
                        {
                            request_recognized = Unirest.get("https://camfind.p.mashape.com/image_responses/" + imageRecognitionRequestToken)
                                    .header("X-Mashape-Authorization", "YMQQG7yJ4LsBWIrmnzS19ErBtWOTMHlW")
                                    .asJson();
                        }
                        catch (UnirestException e)
                        {
                            e.printStackTrace();
                        }
                        Log.d("foo", request_recognized.toString());
                        String recognitionString = request_recognized.getBody().toString();
                        Log.d("foo", recognitionString);
                        String recognizedObject = "";
                        if (recognitionString.contains("not completed") == true)
                        {
                            Log.d("foo", "AskCamFindToRecognizeImage:  the server has not completed its image analyses");
                        }
                        else
                        {
                            recognizedObject = recognitionString.substring(30, recognitionString.length() - 2);
                            Log.d("foo", "recognizedObject:  " + recognizedObject);
                        }

                        try {
                            OnImageRecognitionAnalysisComplete(filePath_image, recognizedObject);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };
                Thread thread = new Thread(runnable);
                thread.start();
            }

        };

        long delay = 15000;
        delayHandler.postDelayed(runnable, delay);
    }
}
