package com.example.myopengldemo;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

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

        private int vSize;

        @Override
        public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
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
        public void onSurfaceChanged(GL10 gl10, int i, int i1) {
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            //这块瞎写的 等会看一下
            mProgram = ShaderUtils.createProgram(Resources.getSystem(), "vshader/Cone.sh", "fshader/Cone.sh");
        }

        @Override
        public void onDrawFrame(GL10 gl10) {

        }
    }
}
