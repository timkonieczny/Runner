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

    private Cube mCube;

    @Override
    public void onSurfaceCreated(GL10 unused, javax.microedition.khronos.egl.EGLConfig config) {
        // Set the background frame colors
        GLES31.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        mCube = new Cube();
    }

    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES31.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 2, 4);	// z clipping starts at -1 and 1

    }

    private float[] mRotationMatrix = new float[16];

    public void onDrawFrame(GL10 unused) {
        float[] scratch = new float[16];

        // Redraw background colors
        GLES31.glClear(GLES31.GL_COLOR_BUFFER_BIT);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0,
                0, 0, -3,
                0f, 0f, 0f,
                0f, 1.0f, 0.0f);

        // Create a rotation transformation for the triangle
        long time = SystemClock.uptimeMillis() % 4000L;
        float angle = 0.090f * ((int) time);
        Matrix.setRotateM(mRotationMatrix, 0, angle, 1.0f, 1.0f, 1.0f);

        // Combine the rotation matrix with the projection and camera view
        // Note that the mMVPMatrix factor *must be first* in order
        // for the matrix multiplication product to be correct.
        Matrix.multiplyMM(scratch, 0, mViewMatrix, 0, mRotationMatrix, 0);


        mCube.draw(scratch, mProjectionMatrix);
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
