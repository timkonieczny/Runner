package com.timkonieczny.runner;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLES31;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

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
                new Cube(0.0f),
                new Cube(1.0f)
        };
    }

    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES31.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

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
    private float mPlayerXPos = 0.0f;

    private boolean mPlayerJumping = false;
    private float mJumpingAnimTime = 0.0f;
    private final float mJumpingAnimDuration = 1000.0f;

    private int mJumpingDirection = RenderingView.SWIPE_DOWN;
    private boolean mDrawOrderSet = false;

    public void onDrawFrame(GL10 unused) {

        timeDelta = SystemClock.uptimeMillis() - lastFrame;
        lastFrame = SystemClock.uptimeMillis();

        if(RenderingView.CURRENT_SWIPE != RenderingView.SWIPE_DOWN && !mPlayerJumping){
            mPlayerJumping = true;
            mJumpingDirection = RenderingView.CURRENT_SWIPE;
            RenderingView.CURRENT_SWIPE = RenderingView.SWIPE_DOWN; // assign invalid value
            mDrawOrderSet = false;
        }

        if(mPlayerJumping){
            mJumpingAnimTime += timeDelta;
            mPlayerYPos = (float)Math.sin((mJumpingAnimTime / mJumpingAnimDuration) * Math.PI);
            if(mJumpingDirection == RenderingView.SWIPE_LEFT){
                mPlayerXPos -= timeDelta / mJumpingAnimDuration;    // TODO: multiply by right distance between cubes
            }else if(mJumpingDirection == RenderingView.SWIPE_RIGHT){
                mPlayerXPos += timeDelta / mJumpingAnimDuration;
            }
            if(mJumpingAnimTime / mJumpingAnimDuration >= 0.5f && !mDrawOrderSet){  // Change draw order to new lane
                if(mJumpingDirection == RenderingView.SWIPE_LEFT){
                    mPlayerOnLane--;
                }else if(mJumpingDirection == RenderingView.SWIPE_RIGHT){
                    mPlayerOnLane++;
                }
                mDrawOrderSet = true;
            }
            if(mPlayerYPos <= 0.0f){    // landed
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
                mPlayerXPos, mPlayerYPos, 1.0f,
                mPlayerXPos, mPlayerYPos, 0f,
                0f, 1.0f, 0.0f);

        switch (mPlayerOnLane){
            case 0:     // left lane
                updateAndDrawCube(mCubes[2], scratch);
                updateAndDrawCube(mCubes[1], scratch);
                updateAndDrawCube(mCubes[0], scratch);
                break;
            case 1:     // middle lane
                updateAndDrawCube(mCubes[0], scratch);
                updateAndDrawCube(mCubes[2], scratch);
                updateAndDrawCube(mCubes[1], scratch);
                break;
            case 2:     // right lane
                updateAndDrawCube(mCubes[0], scratch);
                updateAndDrawCube(mCubes[1], scratch);
                updateAndDrawCube(mCubes[2], scratch);
                break;
        }
    }

    private void updateAndDrawCube(Cube cube, float[] mvMatrix) {
        cube.update(mModelMatrix, farClippingPlane, timeDelta);
        Matrix.multiplyMM(mvMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        cube.draw(mvMatrix, mProjectionMatrix);
    }

    private int mPlayerOnLane = 1;

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
