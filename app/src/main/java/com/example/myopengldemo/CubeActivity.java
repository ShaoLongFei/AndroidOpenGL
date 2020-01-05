package com.example.myopengldemo;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;

import androidx.annotation.Nullable;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @创建者 邵龙飞
 * @创建时间 2019-08-05 19:48
 * @描述
 */
public class CubeActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRender(new CubeRender());
    }

    private class CubeRender implements GLSurfaceView.Renderer {

        private FloatBuffer vertexBuffer, colorBuffer;
        private ShortBuffer indexBuffer;
        private final String vertexShaderCode =
                "attribute vec4 vPosition;" +
                        "uniform mat4 vMatrix;"+
                        "varying  vec4 vColor;"+
                        "attribute vec4 aColor;"+
                        "void main() {" +
                        "  gl_Position = vMatrix*vPosition;" +
                        "  vColor=aColor;"+
                        "}";

        private final String fragmentShaderCode =
                "precision mediump float;" +
                        "varying vec4 vColor;" +
                        "void main() {" +
                        "  gl_FragColor = vColor;" +
                        "}";

        private int mProgram;

        final float cubePositions[] = {
                -1.0f,1.0f,1.0f,
                -1.0f,-1.0f,1.0f,
                1.0f,-1.0f,1.0f,
                1.0f,1.0f,1.0f,
                -1.0f,1.0f,-1.0f,
                -1.0f,-1.0f,-1.0f,
                1.0f,-1.0f,-1.0f,
                1.0f,1.0f,-1.0f,
        };
        final short index[]={
                6,7,4,6,4,5,    //后面
                6,3,7,6,2,3,    //右面
                6,5,1,6,1,2,    //下面
                0,3,2,0,2,1,    //正面
                0,1,5,0,5,4,    //左面
                0,7,3,0,4,7,    //上面
        };

        float color[] = {
                0f,1f,0f,1f,
                0f,1f,0f,1f,
                0f,1f,0f,1f,
                0f,1f,0f,1f,
                1f,0f,0f,1f,
                1f,0f,0f,1f,
                1f,0f,0f,1f,
                1f,0f,0f,1f,
        };
        private int mPositionHandle;
        private int mColorHandle;

        private float[] mViewMatrix = new float[16];
        private float[] mProjectMatrix = new float[16];
        private float[] mMVPMatrix = new float[16];

        private int mMatrixHandler;


        @Override
        public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
            GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
            vertexBuffer = getFloatBuffer(cubePositions);
            colorBuffer = getFloatBuffer(color);
            indexBuffer = getShortBuffer(index);
            int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
            int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
            mProgram = creatProgramAndLink(mProgram, vertexShader, fragmentShader);
            checkLinkState(mProgram);
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        }

        @Override
        public void onSurfaceChanged(GL10 gl10, int width, int height) {
            GLES20.glViewport(0, 0, width, height);
            float ratio = (float) width / height;
            Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 3, 20);
            Matrix.setLookAtM(mViewMatrix, 0, 5.0f, 5.0f, 10.0f, 0f, 0f, 0f, 0f, 1.0f, 0f);
            Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0);
        }

        @Override
        public void onDrawFrame(GL10 gl10) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            GLES20.glUseProgram(mProgram);
            mMatrixHandler = GLES20.glGetUniformLocation(mProgram, "vMatrix");
            GLES20.glUniformMatrix4fv(mMatrixHandler, 1, false, mMVPMatrix, 0);
            mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
            GLES20.glEnableVertexAttribArray(mPositionHandle);
            GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
            mColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
            GLES20.glEnableVertexAttribArray(mColorHandle);
            GLES20.glVertexAttribPointer(mColorHandle, 4, GLES20.GL_FLOAT, false, 0, colorBuffer);
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, index.length, GLES20.GL_UNSIGNED_SHORT, indexBuffer);
            GLES20.glDisableVertexAttribArray(mPositionHandle);
        }
    }
}
