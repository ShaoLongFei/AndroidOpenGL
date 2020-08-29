package com.example.myopengldemo;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @author shaolongfei
 */
public class RegularTriangleActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRender(new RegularTriangleRender());
    }

    private static class RegularTriangleRender implements GLSurfaceView.Renderer {

        private FloatBuffer vertexBuffer;
        private final String vertexShaderCode =
                "attribute vec4 vPosition;" +
                "uniform mat4 vMatrix;" +
                "void main(){" +
                "   gl_Position=vMatrix*vPosition;" +
                "}";
        private final String fragmentShaderCode =
                "precision mediump float;" +
                "uniform vec4 vColor;" +
                "void main(){" +
                "   gl_FragColor=vColor;" +
                "}";

        private int mProgram;

        private final int COORDS_PER_VERTEX = 3;
        private float triangleCoords[] = {
                0.5f, 0.5f, 0.0f,
                -0.5f, -0.5f, 0.0f,
                0.5f, -0.5f, 0.0f
        };

        private int mPositionHandle;
        private int mColorHandle;

        private float[] mViewMatrix = new float[16];
        private float[] mProjectMatrix = new float[16];
        private float[] mMVPMatri = new float[16];

        private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
        private final int vertexStride = COORDS_PER_VERTEX * 4;

        private int mMatrixHandler;

        private float[] color = {1.0f, 1.0f, 1.0f, 1.0f};

        @Override
        public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
            GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(triangleCoords.length * 4);
            byteBuffer.order(ByteOrder.nativeOrder());
            vertexBuffer = byteBuffer.asFloatBuffer();
            vertexBuffer.put(triangleCoords);
            vertexBuffer.position(0);
            int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
            int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
            mProgram = GLES20.glCreateProgram();
            GLES20.glAttachShader(mProgram, vertexShader);
            GLES20.glAttachShader(mProgram, fragmentShader);
            GLES20.glLinkProgram(mProgram);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(mProgram, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES20.GL_TRUE) {
                Log.e("ES20_ERROR", "Could not link program");
                Log.e("ES20_ERROR", GLES20.glGetShaderInfoLog(mProgram));
                GLES20.glDeleteProgram(mProgram);
                mProgram = 0;
            }
        }

        @Override
        public void onSurfaceChanged(GL10 gl10, int width, int height) {
            GLES20.glViewport(0, 0, width, height);
            float ratio = (float) width / height;
            Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
            Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 7.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
            Matrix.multiplyMM(mMVPMatri, 0, mProjectMatrix, 0, mViewMatrix, 0);
        }

        @Override
        public void onDrawFrame(GL10 gl10) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            GLES20.glUseProgram(mProgram);
            mMatrixHandler = GLES20.glGetUniformLocation(mProgram, "vMatrix");
            GLES20.glUniformMatrix4fv(mMatrixHandler, 1, false, mMVPMatri, 0);
            mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
            GLES20.glEnableVertexAttribArray(mPositionHandle);
            GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
            mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
            GLES20.glUniform4fv(mColorHandle, 1, color, 0);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
            GLES20.glDisableVertexAttribArray(mPositionHandle);
        }
    }
}
