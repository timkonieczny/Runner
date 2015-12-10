package com.timkonieczny.runner;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class RenderingView extends GLSurfaceView {

    public RenderingView(Context context){
        super(context);

        setEGLContextClientVersion(3);

        setRenderer(new com.timkonieczny.runner.Renderer(this.getContext()));
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

    }
}