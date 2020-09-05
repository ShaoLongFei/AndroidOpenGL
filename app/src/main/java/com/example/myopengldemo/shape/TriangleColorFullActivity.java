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

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @author shaolongfei
 */
public class TriangleColorFullActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRender(new TriangleColorFullRender());
    }

    private static class TriangleColorFullRender implements GLSurfaceView.Renderer {

        private FloatBuffer vertexBuffer, colorBuffer;
        private final String vertexShaderCode =
                "attribute vec4 vPosition;" +
                "uniform mat4 vMatrix;" +
                "varying vec4 vColor;" +
                "attribute vec4 aColor;" +
                "void main(){" +
                "   gl_Position=vMatrix*vPosition;" +
                "   vColor=aColor;" +
                "}";
        private final String fragmentShaderCode =
                "precision mediump float;" +
                "varying vec4 vColor;" +
                "void main(){" +
                "   gl_FragColor=vColor;" +
                "}";

        private int mProgram;

        private final int COORDS_PER_VERTEX = 3;
        private float[] trigleCoords = {
                0.5f, 0.5f, 0.0f,
                -0.5f, -0.5f, 0.0f,
                0.5f, -0.5f, 0.0f
        };

        private int mPositionHandle;
        private int mColorHandle;

        private float[] mViewMatrix = new float[16];
        private float[] mProjectMatrix = new float[16];
        private float[] mMVPMatrix = new float[16];

        private final int vertexCount = trigleCoords.length / COORDS_PER_VERTEX;
        private final int vertexStride = COORDS_PER_VERTEX * 4;

        private int mMatrixHandler;

        private float[] colors = {
                0.0f, 1.0f, 0.0f, 1.0f,
                1.0f, 0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f, 1.0f
        };

        @Override
        public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
            vertexBuffer = BufferUtils.getFloatBuffer(trigleCoords);
            colorBuffer = BufferUtils.getFloatBuffer(colors);

            mProgram = OpenGLUtils.createProgramAndLink(vertexShaderCode, fragmentShaderCode);
        }

        @Override
        public void onSurfaceChanged(GL10 gl10, int width, int height) {
            // 计算宽高比
            float ratio = (float) width / height;
            // 设置透视投影
            Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
            // 设置相机位置
            Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 7.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
            // 计算变换矩阵
            Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0);
        }

        @Override
        public void onDrawFrame(GL10 gl10) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            // 将程序加入到 OpenGLES2.0 环境
            GLES20.glUseProgram(mProgram);
            // 获取变换矩阵 vMatrix 成员句柄
            mMatrixHandler = GLES20.glGetUniformLocation(mProgram, "vMatrix");
            // 指定 vMatrix 的值
            GLES20.glUniformMatrix4fv(mMatrixHandler, 1, false, mMVPMatrix, 0);
            // 获取顶点着色器的 vPosition 成员句柄
            mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
            // 启用三角形顶点的句柄
            GLES20.glEnableVertexAttribArray(mPositionHandle);
            // 准备三角形的坐标数据
            GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
            // 获取片元着色器的 vColor 成员的句柄
            mColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
            // 启用片元着色器的句柄
            GLES20.glEnableVertexAttribArray(mColorHandle);
            // 为片元着色器填充颜色数据
            GLES20.glVertexAttribPointer(mColorHandle, 4, GLES20.GL_FLOAT, false, 0, colorBuffer);
            // 绘制三角形
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
            // 禁止顶点数组的句柄
            GLES20.glDisableVertexAttribArray(mPositionHandle);
        }
    }
}
