package com.example.myopengldemo.shape;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.myopengldemo.base.BaseActivity;
import com.example.myopengldemo.utils.BufferUtils;
import com.example.myopengldemo.utils.OpenGLUtils;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @创建者 邵龙飞
 * @创建时间 2020/9/5 9:26 PM
 * @描述
 */
public class BallWithLightActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRender(new BallWithLightRender());
    }

    private class BallWithLightRender implements GLSurfaceView.Renderer {

        private float step = 1f;
        private FloatBuffer vertexBuffer;
        private int vSize;

        private int mProgram;
        private float[] mViewMatrix = new float[16];
        private float[] mProjectMatrix = new float[16];
        private float[] mMVPMatrix = new float[16];

        public BallWithLightRender() {
            float[] dataPos = createBallPos();
            vertexBuffer = BufferUtils.getFloatBuffer(dataPos);
            vSize = dataPos.length / 3;
        }

        private float[] createBallPos() {
            //球以(0,0,0)为中心，以R为半径，则球上任意一点的坐标为
            // ( R * cos(a) * sin(b),y0 = R * sin(a),R * cos(a) * cos(b))
            // 其中，a为圆心到点的线段与xz平面的夹角，b为圆心到点的线段在xz平面的投影与z轴的夹角
            ArrayList<Float> data = new ArrayList<>();
            float r1, r2;
            float h1, h2;
            float sin, cos;
            for (float i = -90; i < 90 + step; i += step) {
                r1 = (float) Math.cos(i * Math.PI / 180.0);
                r2 = (float) Math.cos((i + step) * Math.PI / 180.0);
                h1 = (float) Math.sin(i * Math.PI / 180.0);
                h2 = (float) Math.sin((i + step) * Math.PI / 180.0);
                // 固定纬度, 360 度旋转遍历一条纬线
                float step2 = step * 2;
                for (float j = 0.0f; j < 360.0f + step; j += step2) {
                    cos = (float) Math.cos(j * Math.PI / 180.0);
                    sin = -(float) Math.sin(j * Math.PI / 180.0);

                    data.add(r2 * cos);
                    data.add(h2);
                    data.add(r2 * sin);
                    data.add(r1 * cos);
                    data.add(h1);
                    data.add(r1 * sin);
                }
            }
            float[] floats = new float[data.size()];
            for (int i = 0; i < floats.length; i++) {
                floats[i] = data.get(i);
            }
            return floats;
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            mProgram = OpenGLUtils.createProgramAndLink(getResources(), "vshader/BallWithLight.sh", "fshader/BallWithLight.sh");
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLES20.glViewport(0, 0, width, height);
            // 计算宽高比
            float ratio = (float) width / height;
            // 设置透视投影
            Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 3, 20);
            // 设置相机位置
            Matrix.setLookAtM(mViewMatrix, 0, 0.0f, 0.0f, 10.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
            // 计算变换矩阵
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
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vSize);
            GLES20.glDisableVertexAttribArray(mPositionHandle);
        }
    }
}
