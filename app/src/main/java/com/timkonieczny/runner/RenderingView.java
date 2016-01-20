package com.timkonieczny.runner;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class RenderingView extends GLSurfaceView {

    public RenderingView(Context context){
        super(context);

        setEGLContextClientVersion(3);

        setRenderer(new com.timkonieczny.runner.Renderer(this.getContext()));
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    private float mDownX;
    private float mDownY;

    public static int SWIPE_UP = 0;
    public static int SWIPE_LEFT = 1;
    public static int SWIPE_RIGHT = 2;
    public static int SWIPE_DOWN = 3;

    public static int CURRENT_SWIPE = 3;

    @Override
    public boolean onTouchEvent(MotionEvent e){
        int action = e.getAction();
        if(action == MotionEvent.ACTION_DOWN){
            mDownX = e.getX();
            mDownY = e.getY();
        }else if(action == MotionEvent.ACTION_UP){
            if(mDownY - e.getY() < 0){  // swipe down
                CURRENT_SWIPE = SWIPE_DOWN;
            }else{
                if(mDownX - e.getX() < -getWidth() / 3){
                    Log.d("RenderingView", "SWIPE_RIGHT");
                    CURRENT_SWIPE = SWIPE_RIGHT;
                }else if(mDownX - e.getX() > getWidth() / 3){
                    Log.d("RenderingView", "SWIPE_LEFT");
                    CURRENT_SWIPE = SWIPE_LEFT;
                }else{
                    Log.d("RenderingView", "SWIPE_UP");
                    CURRENT_SWIPE = SWIPE_UP;
                }
            }
        }
            return true;
    }
}