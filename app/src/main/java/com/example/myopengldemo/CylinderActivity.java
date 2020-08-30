package com.example.myopengldemo;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @创建者 邵龙飞
 * @创建时间 2020/8/30 8:41 PM
 * @描述
 */
public class CylinderActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRender(new CylinderRender());
    }

    private class CylinderRender implements GLSurfaceView.Renderer{

        private int mProgram;
        private OvalActvity.OvalRender mBottomOval,mTopOval;
        private FloatBuffer vertexBuffer;

        private float[] mViewMatrix = new float[16];
        private float[] mProjectMatrix = new float[16];
        private float[] mMVPMatrix = new float[16];

        private int n = 360;  // 切割份数
        private float height = 2.0f;  // 圆锥高度
        private float radius = 1.0f;  // 圆锥底面半径

        private int vSize;

        CylinderRender(){
            mBottomOval = new OvalActvity.OvalRender();
            mTopOval = new OvalActvity.OvalRender(height);

            ArrayList<Float> pos = new ArrayList<>();
            float angDegSpan = 360f / n;
            for (float i = 0; i < 360 + angDegSpan; i += angDegSpan) {
                pos.add((float) (radius * Math.sin(i * Math.PI / 180f)));
                pos.add((float) (radius * Math.cos(i * Math.PI / 180f)));
                pos.add(height);
                pos.add((float) (radius * Math.sin(i * Math.PI / 180f)));
                pos.add((float) (radius * Math.cos(i * Math.PI / 180f)));
                pos.add(0.0f);
            }

            float[] d = new float[pos.size()];
            for (int i = 0; i < d.length; i++) {
                d[i] = pos.get(i);
            }
            vSize = d.length / 3;

            vertexBuffer = getFloatBuffer(d);
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            mProgram = ShaderUtils.createProgram(getResources(), "vshader/Cone.sh", "fshader/Cone.sh");
            mBottomOval.onSurfaceCreated(gl, config);
            mTopOval.onSurfaceCreated(gl, config);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLES20.glViewport(0, 0, width, height);
            //计算宽高比
            float ratio = (float) width / height;
            //设置透视投影
            Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 3, 20);
            //设置相机位置
            Matrix.setLookAtM(mViewMatrix, 0, 1.0f, -10.0f, -4.0f, 0f,
                    0f, 0f, 0f, 1.0f, 0.0f);
            //计算变换矩阵
            Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            GLES20.glUseProgram(mProgram);
            int mMatrix = GLES20.glGetUniformLocation(mProgram, "vMatrix");
            GLES20.glUniformMatrix4fv(mMatrix, 1, false, mMVPMatrix, 0);
            int mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
            GLES20.glEnableVertexAttribArray(mPositionHandle);
            GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
            //        int mColorHandle=GLES20.glGetUniformLocation(mProgram,"vColor");
            //        GLES20.glEnableVertexAttribArray(mColorHandle);
            //        GLES20.glUniform4fv(mColorHandle,1,colors,0);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vSize);
            GLES20.glDisableVertexAttribArray(mPositionHandle);
            mBottomOval.setMatrix(mMVPMatrix);
            mBottomOval.onDrawFrame(gl);
            mTopOval.setMatrix(mMVPMatrix);
            mTopOval.onDrawFrame(gl);
        }
    }
}
