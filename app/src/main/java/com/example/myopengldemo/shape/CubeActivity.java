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
                "varying vec4 vColor;"+
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
                -1.0f, 1.0f, 1.0f, // 正面左上0
                -1.0f, -1.0f, 1.0f, // 正面左下1
                1.0f, -1.0f, 1.0f, // 正面右下2
                1.0f, 1.0f, 1.0f, // 正面右上3
                -1.0f, 1.0f, -1.0f, // 反面左上4
                -1.0f, -1.0f, -1.0f, // 反面左下5
                1.0f, -1.0f, -1.0f, // 反面右下6
                1.0f, 1.0f, -1.0f, // 反面右上7
        };
        final short index[] = {
                6, 7, 4, 6, 4, 5, // 后面
                6, 3, 7, 6, 2, 3, // 右面
                6, 5, 1, 6, 1, 2, // 下面
                0, 3, 2, 0, 2, 1, // 正面
                0, 1, 5, 0, 5, 4, // 左面
                0, 7, 3, 0, 4, 7, // 上面
        };

        float color[] = {
                0f, 1f, 0f, 1f,
                0f, 1f, 0f, 1f,
                0f, 1f, 0f, 1f,
                0f, 1f, 0f, 1f,
                1f, 0f, 0f, 1f,
                1f, 0f, 0f, 1f,
                1f, 0f, 0f, 1f,
                1f, 0f, 0f, 1f,
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
            vertexBuffer = BufferUtils.getFloatBuffer(cubePositions);
            colorBuffer = BufferUtils.getFloatBuffer(color);
            indexBuffer = BufferUtils.getShortBuffer(index);
            mProgram = OpenGLUtils.createProgramAndLink(vertexShaderCode, fragmentShaderCode);
            // 开启深度测试
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        }

        @Override
        public void onSurfaceChanged(GL10 gl10, int width, int height) {
            GLES20.glViewport(0, 0, width, height);
            // 计算宽高比
            float ratio = (float) width / height;
            // 设置透视投影
            Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 3, 20);
            // 设置相机位置
            Matrix.setLookAtM(mViewMatrix, 0, 5.0f, 5.0f, 10.0f, 0f, 0f, 0f, 0f, 1.0f, 0f);
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
            GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
            // 获取片元着色器的 vColor 成员的句柄
            mColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
            // 启用片元着色器的句柄
            GLES20.glEnableVertexAttribArray(mColorHandle);
            // 为片元着色器填充数据
            GLES20.glVertexAttribPointer(mColorHandle, 4, GLES20.GL_FLOAT, false, 0, colorBuffer);
            // 索引法绘制正方体
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, index.length, GLES20.GL_UNSIGNED_SHORT, indexBuffer);
            // 禁用顶点数组的句柄
            GLES20.glDisableVertexAttribArray(mPositionHandle);
        }
    }
}
