package com.timkonieczny.runner;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLES31;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.microedition.khronos.opengles.GL10;

public class Renderer implements GLSurfaceView.Renderer {

    private static Context context;

    public Renderer(Context context) {
        Renderer.context = context;
    }

    private Cube[] mCubes;

    @Override
    public void onSurfaceCreated(GL10 unused, javax.microedition.khronos.egl.EGLConfig config) {
        // Set the background frame colors
        GLES31.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

        mCubes = new Cube[]{
                new Cube(-1.0f),
                new Cube(1.0f),
                new Cube(0.0f)
        };
    }

    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];

    private float ratio;

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES31.glViewport(0, 0, width, height);

        ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 1.0f, 4);	// z clipping starts at 0 and 3
        farClippingPlane = 3.0f;
        mCubes[0].refresh(farClippingPlane);
        mCubes[1].refresh(farClippingPlane);
    }

    private float[] mModelMatrix = new float[16];
    private long timeDelta = SystemClock.uptimeMillis();
    private long lastFrame = SystemClock.uptimeMillis();
    private float farClippingPlane;

    public static boolean[] IS_LANE_FREE = new boolean[]{true, true, true};

    private float mPlayerYPos = 0.0f;

    private boolean mPlayerJumping = false;
    private float mJumpingAnimTime = 0.0f;
    private float mJumpingAnimDuration = 1000.0f;

    public void onDrawFrame(GL10 unused) {

        timeDelta = SystemClock.uptimeMillis() - lastFrame;
        lastFrame = SystemClock.uptimeMillis();

        if(RenderingView.GET_CLICK_STATUS() && !mPlayerJumping){
            mPlayerJumping = true;
        }

        if(mPlayerJumping){
            mJumpingAnimTime += timeDelta;
            mPlayerYPos = (float)Math.sin((mJumpingAnimTime / mJumpingAnimDuration) * Math.PI);
            if(mPlayerYPos <= 0.0f){
                mPlayerYPos = 0.0f;
                mPlayerJumping = false;
                mJumpingAnimTime = 0.0f;
            }
        }

        float[] scratch = new float[16];
        mModelMatrix = new float[16];

        // Redraw background colors
        GLES31.glClear(GLES31.GL_COLOR_BUFFER_BIT);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0,
                0, mPlayerYPos, -1.0f,
                0f, mPlayerYPos, 0f,
                0f, 1.0f, 0.0f);

        for (Cube cube:mCubes) {
            cube.update(mModelMatrix, farClippingPlane, timeDelta);

            // Combine the rotation matrix with the projection and camera view
            // Note that the mMVPMatrix factor *must be first* in order
            // for the matrix multiplication product to be correct.
            Matrix.multiplyMM(scratch, 0, mViewMatrix, 0, mModelMatrix, 0);

            cube.draw(scratch, mProjectionMatrix);
        }
    }

    public static int loadShader(int type, String fileName) {

        AssetManager assetManager = context.getAssets();
        try {
            InputStream inputStream = assetManager.open(fileName);
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String shaderString;
            while ((shaderString=bufferedReader.readLine()) != null) {
                stringBuilder.append(shaderString);
            }
            bufferedReader.close();

            int shader = GLES31.glCreateShader(type);

            GLES31.glShaderSource(shader, stringBuilder.toString());
            GLES31.glCompileShader(shader);

            return shader;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

}
