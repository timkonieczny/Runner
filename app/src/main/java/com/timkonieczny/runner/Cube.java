package com.timkonieczny.runner;

import android.opengl.GLES31;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Cube {
    public float zPosition;
    public float zScale;

    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;
    private FloatBuffer normalBuffer;
    private ShortBuffer drawListBuffer;

    private final int VALUES_PER_VERTEX = 3;
    private final float vertices[] = {    // front
            -0.5f, -0.5f,  0.5f,    // bottom left front
             0.5f, -0.5f,  0.5f,    // bottom right front
            -0.5f,  0.5f,  0.5f,    // top left front
             0.5f,  0.5f,  0.5f,    // top right front
                                    // back
             0.5f, -0.5f, -0.5f,    // bottom right back
            -0.5f, -0.5f, -0.5f,    // bottom left back
             0.5f,  0.5f, -0.5f,    // top right back
            -0.5f,  0.5f, -0.5f,    // top left back
                                    // top
            -0.5f,  0.5f,  0.5f,    // top left front
             0.5f,  0.5f,  0.5f,    // top right front
            -0.5f,  0.5f, -0.5f,    // top left back
             0.5f,  0.5f, -0.5f,    // top right back
                                    // bottom
            -0.5f, -0.5f, -0.5f,    // bottom left back
             0.5f, -0.5f, -0.5f,    // bottom right back
            -0.5f, -0.5f,  0.5f,    // bottom left front
             0.5f, -0.5f,  0.5f,    // bottom right front
                                    // left
            -0.5f, -0.5f, -0.5f,    // bottom left back
            -0.5f, -0.5f,  0.5f,    // bottom left front
            -0.5f,  0.5f, -0.5f,    // top left back
            -0.5f,  0.5f,  0.5f,    // top left front
                                    // right
             0.5f, -0.5f,  0.5f,    // bottom right front
             0.5f, -0.5f, -0.5f,    // bottom right back
             0.5f,  0.5f,  0.5f,    // top right front
             0.5f,  0.5f, -0.5f,    // top right back
    };

    private final short drawOrder[] = {
             0,  1,  2,  1,  3,  2,       // front
             4,  5,  6,  5,  7,  6,       // back
             8,  9, 10,  9, 11, 10,       // top
            12, 13, 14, 13, 15, 14,       // bottom
            16, 17, 18, 17, 19, 18,       // left
            20, 21, 22, 21, 23, 22        // right
    };

    private final int VALUES_PER_COLOR = 4;
    private final float colors[] = {
            1.0f, 0.0f, 0.0f, 1.0f, // red
            1.0f, 0.0f, 0.0f, 1.0f, // red
            1.0f, 0.0f, 0.0f, 1.0f, // red
            1.0f, 0.0f, 0.0f, 1.0f, // red

            1.0f, 0.0f, 0.0f, 1.0f, // red
            1.0f, 0.0f, 0.0f, 1.0f, // red
            1.0f, 0.0f, 0.0f, 1.0f, // red
            1.0f, 0.0f, 0.0f, 1.0f, // red

            1.0f, 0.0f, 0.0f, 1.0f, // red
            1.0f, 0.0f, 0.0f, 1.0f, // red
            1.0f, 0.0f, 0.0f, 1.0f, // red
            1.0f, 0.0f, 0.0f, 1.0f, // red

            1.0f, 0.0f, 0.0f, 1.0f, // red
            1.0f, 0.0f, 0.0f, 1.0f, // red
            1.0f, 0.0f, 0.0f, 1.0f, // red
            1.0f, 0.0f, 0.0f, 1.0f, // red

            1.0f, 0.0f, 0.0f, 1.0f, // red
            1.0f, 0.0f, 0.0f, 1.0f, // red
            1.0f, 0.0f, 0.0f, 1.0f, // red
            1.0f, 0.0f, 0.0f, 1.0f, // red

            1.0f, 0.0f, 0.0f, 1.0f, // red
            1.0f, 0.0f, 0.0f, 1.0f, // red
            1.0f, 0.0f, 0.0f, 1.0f, // red
            1.0f, 0.0f, 0.0f, 1.0f, // red
    };

    private final int VALUES_PER_NORMAL = 3;
    private final float normals[] = {
             0.0f,  0.0f,  1.0f,       // front
             0.0f,  0.0f,  1.0f,
             0.0f,  0.0f,  1.0f,
             0.0f,  0.0f,  1.0f,

             0.0f,  0.0f, -1.0f,       // back
             0.0f,  0.0f, -1.0f,
             0.0f,  0.0f, -1.0f,
             0.0f,  0.0f, -1.0f,

             0.0f,  1.0f,  0.0f,       // top
             0.0f,  1.0f,  0.0f,
             0.0f,  1.0f,  0.0f,
             0.0f,  1.0f,  0.0f,

             0.0f, -1.0f,  0.0f,       // bottom
             0.0f, -1.0f,  0.0f,
             0.0f, -1.0f,  0.0f,
             0.0f, -1.0f,  0.0f,

            -1.0f,  0.0f,  0.0f,       // left
            -1.0f,  0.0f,  0.0f,
            -1.0f,  0.0f,  0.0f,
            -1.0f,  0.0f,  0.0f,

             1.0f,  0.0f,  0.0f,       // right
             1.0f,  0.0f,  0.0f,
             1.0f,  0.0f,  0.0f,
             1.0f,  0.0f,  0.0f,
    };

    private final int mProgram;

    public Cube() {

        // Initialize buffers

        ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        ByteBuffer cb = ByteBuffer.allocateDirect(colors.length * 4);
        cb.order(ByteOrder.nativeOrder());
        colorBuffer = cb.asFloatBuffer();
        colorBuffer.put(colors);
        colorBuffer.position(0);

        ByteBuffer nb = ByteBuffer.allocateDirect(normals.length * 4);
        nb.order(ByteOrder.nativeOrder());
        normalBuffer = nb.asFloatBuffer();
        normalBuffer.put(normals);
        normalBuffer.position(0);

        // Initialize shaders

        int vertexShader = Renderer.loadShader(GLES31.GL_VERTEX_SHADER, "cube_vert.glsl");
        int fragmentShader = Renderer.loadShader(GLES31.GL_FRAGMENT_SHADER, "cube_frag.glsl");
        mProgram = GLES31.glCreateProgram();
        GLES31.glAttachShader(mProgram, vertexShader);
        GLES31.glAttachShader(mProgram, fragmentShader);
        GLES31.glLinkProgram(mProgram);

        // Get uniform locations

        mViewMatrixLocation = GLES31.glGetUniformLocation(mProgram, "uViewMatrix");
        mProjectionMatrixLocation = GLES31.glGetUniformLocation(mProgram, "uProjectionMatrix");
        mNormalMatrixLocation = GLES31.glGetUniformLocation(mProgram, "uNormalMatrix");

        // Get attribute locations

		mPositionLocation = GLES31.glGetAttribLocation(mProgram, "aPosition");
		mColorLocation = GLES31.glGetAttribLocation(mProgram, "aColor");
        mNormalLocation = GLES31.glGetAttribLocation(mProgram, "aNormal");

        // Enable features

        GLES31.glEnable(GLES31.GL_CULL_FACE);
        GLES31.glCullFace(GLES31.GL_BACK);
	}

    private int mPositionLocation;
    private int mColorLocation;
    private int mViewMatrixLocation;
    private int mProjectionMatrixLocation;
    private int mNormalLocation;
    private int mNormalMatrixLocation;
    private android.graphics.Matrix normalMatrix = new android.graphics.Matrix();
    private float[] mInvertedNMatrix = new float[9];
    private float[] mTransposedNMatrix = new float[9];

    public void draw(float[] viewMatrix, float[] projectionMatrix) {
        GLES31.glUseProgram(mProgram);

        GLES31.glUniformMatrix4fv(mViewMatrixLocation, 1, false, viewMatrix, 0);
        GLES31.glUniformMatrix4fv(mProjectionMatrixLocation, 1, false, projectionMatrix, 0);

        normalMatrix.setValues(Matrix3.fromMatrix4(viewMatrix));
        normalMatrix.invert(normalMatrix);
        normalMatrix.getValues(mInvertedNMatrix);
        Matrix3.transpose(mInvertedNMatrix, mTransposedNMatrix);

        GLES31.glUniformMatrix3fv(mNormalMatrixLocation, 1, false, mTransposedNMatrix, 0);

        GLES31.glEnableVertexAttribArray(mPositionLocation);
        GLES31.glVertexAttribPointer(
                mPositionLocation, VALUES_PER_VERTEX, GLES31.GL_FLOAT,
                false, VALUES_PER_VERTEX * 4, vertexBuffer);

        GLES31.glEnableVertexAttribArray(mColorLocation);
        GLES31.glVertexAttribPointer(
                mColorLocation, VALUES_PER_COLOR, GLES31.GL_FLOAT,
                false, VALUES_PER_COLOR * 4, colorBuffer);

        GLES31.glEnableVertexAttribArray(mNormalLocation);
        GLES31.glVertexAttribPointer(
                mNormalLocation, 3, GLES31.GL_FLOAT,
                false, VALUES_PER_NORMAL * 4, normalBuffer);

        GLES31.glDrawElements(
                GLES31.GL_TRIANGLES,
                drawOrder.length,
                GLES31.GL_UNSIGNED_SHORT,
                drawListBuffer);
    }

    public void refresh(float farClippingPlane){
        zScale = 1.0f + (float)Math.random() * 4.0f;
        zPosition = farClippingPlane + zScale / 2.0f;
    }

    public void update(float[] modelMatrix, float farClippingPlane, float ratio, float delta){
        Matrix4.identity(modelMatrix);
        if(zPosition < -zScale / 2){
            zPosition = (farClippingPlane + zScale / 2);
            refresh(farClippingPlane);
        }else{
            zPosition -= delta / 1000;
        }
        Matrix.translateM(modelMatrix, 0, 0.0f, -1.5f, zPosition);       // Scale scales translation values too
        Matrix.scaleM(modelMatrix, 0, ratio * 2f, 1.0f, zScale);
    }
}
