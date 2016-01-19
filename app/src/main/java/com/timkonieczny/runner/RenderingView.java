package com.timkonieczny.runner;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.View;

public class RenderingView extends GLSurfaceView {

    public RenderingView(Context context){
        super(context);

        setEGLContextClientVersion(3);

        setRenderer(new com.timkonieczny.runner.Renderer(this.getContext()));
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        setOnClickListener(mOnClickListener);
    }

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d("RenderingView", "Click");
            CLICKED = true;
        }
    };

    public static boolean CLICKED = false;
}