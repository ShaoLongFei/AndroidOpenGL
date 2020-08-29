package com.example.myopengldemo;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @创建者 邵龙飞
 * @创建时间 2019-08-05 21:46
 * @描述
 */
public class ConeActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRender(new ConeRender());
    }

    private class ConeRender implements GLSurfaceView.Renderer {

        private int mProgram;

        private OvalActvity.OvalRender mOvalRender;
        private FloatBuffer vertexBuffer;

        private float[] mViewMatrix = new float[16];
        private float[] mProjectMatrix = new float[16];
        private float[] mMVPMatrix = new float[16];

        private int n = 360;
        private float height = 2.0f;
        private float radius = 1.0f;
        private float[] colors = {1.0f,0.0f,0.0f,1.0f};

        private int vSize;

        ConeRender(){
            mOvalRender = new OvalActvity.OvalRender();
            ArrayList<Float> pos = new ArrayList<>();
            pos.add(0.0f);
            pos.add(0.0f);
            pos.add(height);
            float angDegSpan = 360 / n;
            for (float i = 0; i < 360 + angDegSpan; i += angDegSpan) {
                pos.add((float) (radius * Math.sin(i * Math.PI / 180f)));
                pos.add((float) (radius * Math.cos(i * Math.PI / 180f)));
                pos.add(0.0f);
            }
            float[] d = new float[pos.size()];
            for (int i = 0; i < d.length; i++) {
                d[i] = pos.get(i);
            }
            vSize = d.length / 3;
            ByteBuffer buffer = ByteBuffer.allocateDirect(d.length * 4);
            buffer.order(ByteOrder.nativeOrder());
            vertexBuffer = buffer.asFloatBuffer();
            vertexBuffer.put(d);
            vertexBuffer.position(0);
        }

        @Override
        public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
            GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            //这块瞎写的 等会看一下
            mProgram = ShaderUtils.createProgram(getResources(), "vshader/Cone.sh", "fshader/Cone.sh");
            mOvalRender.onSurfaceCreated(gl10, eglConfig);
        }

        @Override
        public void onSurfaceChanged(GL10 gl10, int width, int height) {
            GLES20.glViewport(0, 0, width, height);
            // 计算宽高比
            float ratio = (float) width / height;
            //设置透视投影
            Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 3, 20);
            //设置相机位置
            Matrix.setLookAtM(mViewMatrix, 0, 1.0f, -10.0f, -4.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
            //计算变换矩阵
            Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0);
        }

        @Override
        public void onDrawFrame(GL10 gl10) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            GLES20.glUseProgram(mProgram);
            Log.e("wuwang","mProgram:"+mProgram);
            int mMatrix = GLES20.glGetUniformLocation(mProgram, "vMatrix");
            GLES20.glUniformMatrix4fv(mMatrix, 1, false, mMVPMatrix, 0);
            int mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
            Log.e("wuwang","Get Position:"+mPositionHandle);
            GLES20.glEnableVertexAttribArray(mPositionHandle);
            GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
            int mColorHandle=GLES20.glGetUniformLocation(mProgram,"vColor");
            GLES20.glEnableVertexAttribArray(mColorHandle);
            GLES20.glUniform4fv(mColorHandle,1,colors,0);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vSize);
            GLES20.glDisableVertexAttribArray(mPositionHandle);
            mOvalRender.setMatrix(mMVPMatrix);
            mOvalRender.onDrawFrame(gl10);
        }
    }
}
