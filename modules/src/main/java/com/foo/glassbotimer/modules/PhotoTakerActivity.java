package com.foo.glassbotimer.modules;

//based on code found at:
//http://books.google.com/books?id=d-fhAwAAQBAJ&pg=PA82&lpg=PA82&dq=google+glass+custom+camera&source=bl&ots=Vgzloi6bsE&sig=qKITP4WSxUWVVfvwI1-h17YZvUY&hl=en&sa=X&ei=FOO6U9-4F8_ZoASuyIDoBw&ved=0CF0Q6AEwBw#v=onepage&q=google%20glass%20custom%20camera&f=false

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


import java.io.IOException;
import java.util.List;

import android.hardware.Camera;
import android.util.Log;
        import android.view.KeyEvent;
        import android.view.SurfaceHolder;
        import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class PhotoTakerActivity extends Activity
{

    private SurfaceView mPreview;
    private SurfaceHolder mPreviewHolder;
    private Camera mCamera;
    private boolean mInPreview = false;
    private boolean mCameraConfigured = false;

    // code copied from http://developer.android.com/guide/topics/media/camera.html
    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
            //Log.e("foo", "Camera is not available");
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Log.v("foo", "onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_photo_taker);

        this.InitStartText();
        this.DisableAutoSleep();

        // as long as this window is visible to the user, keep the device's screen turned on and bright.
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // WORKS ON GLASS!

        mPreview = (SurfaceView)findViewById(R.id.preview);
        mPreviewHolder = mPreview.getHolder();
        mPreviewHolder.addCallback(surfaceCallback);

        mCamera = getCameraInstance();
        if (mCamera != null)
            startPreview();
    }

    private void InitStartText()
    {
        TextView textView = (TextView) this.findViewById(R.id.captureText);
        textView.setX(200);
        textView.setY(275);
    }

    private void configPreview(int width, int height) {
        //Log.v("foo", "configPreview");
        //Log.v("foo", mCamera == null ? "mCamera is null" : "mCamera is not null");
        //Log.v("foo", mPreviewHolder.getSurface() == null ? "mPreviewHolder.getSurface() is null" : "mPreviewHolder.getSurface() is not null");

        if ( mCamera != null && mPreviewHolder.getSurface() != null) {
            try {
                mCamera.setPreviewDisplay(mPreviewHolder);
            }
            catch (IOException e) {
                Toast.makeText(PhotoTakerActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }

            if ( !mCameraConfigured ) {
                Camera.Parameters parameters = mCamera.getParameters();

                List<int[]> sizes = parameters.getSupportedPreviewFpsRange();
                for (int[] size : sizes) {
                    //Log.v("foo", String.format(">>>> getSupportedPreviewFpsRange: %d, %d", size[0], size[1]));
                }

                List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
                for (int i=0; i<previewSizes.size(); i++)
                {
                    //Log.d("foo", "size=" + previewSizes.get(i).width + "," + previewSizes.get(i).height);
                }


                //1024x576

                parameters.setPictureSize(1024, 576);
                parameters.setPreviewFpsRange(30000, 30000);
                parameters.setPreviewSize(640, 360);

				//parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
				//parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
				//parameters.setJpegQuality(100);
                //Log.i("foo", parameters.getPictureSize().width + "  " + parameters.getPictureSize().height);

                mCamera.setParameters(parameters);

                mCameraConfigured = true;
            }
        }
    }

    private void startPreview() {
        //Log.v("foo", "entering startPreview");

        if ( mCameraConfigured && mCamera != null ) {
            //Log.v("foo", "before calling mCamera.startPreview");
            mCamera.startPreview();
            mInPreview = true;
        }
    }

    /////////////////////////////////////
    //callbacks
    /////////////////////////////////////

    SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        public void surfaceCreated( SurfaceHolder holder ) {
            //Log.v("foo", "surfaceCreated");
        }

        public void surfaceChanged( SurfaceHolder holder, int format, int width, int height ) {
            //Log.v("foo", "surfaceChanged="+width+","+height);
            configPreview(width, height);
            startPreview();
        }

        public void surfaceDestroyed( SurfaceHolder holder ) {
            //Log.v("foo", "surfaceDestroyed");
            if (mCamera != null) {
                mCamera.release();
                mCamera = null;
            }
        }
    };

    @Override
    public void onResume() {
        //Log.v("foo", "onResume");
        super.onResume();

        // Re-acquire the camera and start the preview.
        if (mCamera == null) {
            mCamera = getCameraInstance();
            if (mCamera != null) {
                //Log.v("foo", "mCamera!=null");
                configPreview(640, 360);
                startPreview();
            }
            //else
            //    Log.v("foo", "mCamera==null");
        }
    }

    @Override
    public void onPause() {
        //Log.v("foo", "onPause");
        if ( mInPreview ) {
            //Log.v("foo",  "mInPreview is true");
            mCamera.stopPreview();

            mCamera.release();
            mCamera = null;
            mInPreview = false;
        }
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Log.v("foo",  "onKeyDown");


        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            Log.i("foo", "onKeyDown DPAD_CENTER  taking picture <<<<<<<<");
            mCamera.takePicture(null, null, mPictureCallback);
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN)
        {
            Log.i("foo", "onKeyDown DPAD_DOWN  exiting phototakeractivity <<<<<<");

        }

        return false;
    }

    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback()
    {
        public void onPictureTaken(byte[] data, Camera camera)
        {
            Intent intent =new Intent();
            intent.putExtra("PHOTO_DATA", data);
            setResult(2, intent);
            finish();
        }
    };


    /////////////////////////////////////
    //utilities
    /////////////////////////////////////

    private void DisableAutoSleep()
    {
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

}