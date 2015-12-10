package com.timkonieczny.runner;

import android.opengl.GLES31;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Cube {
    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;
    private ShortBuffer drawListBuffer;

    int VALUES_PER_VERTEX = 3;
    private float vertices[] = {   // front
            -0.5f,  0.5f,  0.5f,    // top left
            -0.5f, -0.5f,  0.5f,    // bottom left
             0.5f, -0.5f,  0.5f,    // bottom right
             0.5f,  0.5f,  0.5f,    // top right
                                    // back
            -0.5f,  0.5f, -0.5f,    // top left
            -0.5f, -0.5f, -0.5f,    // bottom left
             0.5f, -0.5f, -0.5f,    // bottom right
             0.5f,  0.5f, -0.5f     // top right
    };

    private final short drawOrder[] = {
            0, 1, 2, 0, 2, 3,       // front
            4, 6, 5, 4, 7, 6,       // back
            0, 3, 4, 3, 7, 4,       // top
            1, 5, 6, 1, 6, 2,       // bottom
            0, 4, 5, 0, 5, 1,       // left
            2, 6, 3, 3, 6, 7        // right
    };

    int VALUES_PER_COLOR = 4;
    private float colors[] = {
			1.0f, 0.0f, 0.0f, 1.0f, // red
			0.0f, 1.0f, 0.0f, 1.0f, // green
			0.0f, 0.0f, 1.0f, 1.0f, // blue
			1.0f, 1.0f, 0.0f, 1.0f, // yellow
			0.0f, 1.0f, 1.0f, 1.0f, // cyan
			0.0f, 1.0f, 1.0f, 1.0f, // cyan
			0.0f, 1.0f, 1.0f, 1.0f, // cyan
			1.0f, 0.0f, 1.0f, 1.0f  // magenta
	};

    private final int mProgram;

    public Cube() {

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

        int vertexShader = Renderer.loadShader(GLES31.GL_VERTEX_SHADER, "cube_vert.glsl");

        int fragmentShader = Renderer.loadShader(GLES31.GL_FRAGMENT_SHADER, "cube_frag.glsl");

        mProgram = GLES31.glCreateProgram();

        GLES31.glAttachShader(mProgram, vertexShader);
        GLES31.glAttachShader(mProgram, fragmentShader);
        GLES31.glLinkProgram(mProgram);

        mMVPMatrixLocation = GLES31.glGetUniformLocation(mProgram, "uMVPMatrix");

		mPositionLocation = GLES31.glGetAttribLocation(mProgram, "aPosition");
		mColorLocation = GLES31.glGetAttribLocation(mProgram, "aColor");

        GLES31.glEnable(GLES31.GL_CULL_FACE);
        GLES31.glCullFace(GLES31.GL_BACK);
	}

    private int mPositionLocation;
    private int mColorLocation;
    private int mMVPMatrixLocation;

    public void draw(float[] mvpMatrix) {
        GLES31.glUseProgram(mProgram);

        GLES31.glUniformMatrix4fv(mMVPMatrixLocation, 1, false, mvpMatrix, 0);

        GLES31.glEnableVertexAttribArray(mPositionLocation);
        GLES31.glVertexAttribPointer(
                mPositionLocation, VALUES_PER_VERTEX, GLES31.GL_FLOAT,
                false, VALUES_PER_VERTEX * 4, vertexBuffer);

        GLES31.glEnableVertexAttribArray(mColorLocation);
        GLES31.glVertexAttribPointer(
                mColorLocation, VALUES_PER_COLOR, GLES31.GL_FLOAT,
                false, VALUES_PER_COLOR * 4, colorBuffer);

        GLES31.glDrawElements(
                GLES31.GL_TRIANGLES,
                drawOrder.length,
                GLES31.GL_UNSIGNED_SHORT,
                drawListBuffer);

    }
}
