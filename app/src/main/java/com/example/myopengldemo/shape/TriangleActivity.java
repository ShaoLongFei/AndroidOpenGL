package com.example.myopengldemo.shape;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
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
public class TriangleActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRender(new TriangleRender());
    }

    public static class TriangleRender implements GLSurfaceView.Renderer {

        private FloatBuffer vertexBuffer;
        private final String vertexShaderCode =
                "attribute vec4 vPosition;" +
                "void main(){" +
                "   gl_Position=vPosition;" +
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
                0.5f, -0.5f, 0.0f};

        private int mPositionHandle;
        private int mColorHandle;

        private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
        private final int vertexStride = COORDS_PER_VERTEX * 4;

        private float[] color = {1.0f, 1.0f, 1.0f, 1.0f};

        @Override
        public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
            GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
            vertexBuffer = BufferUtils.getFloatBuffer(triangleCoords);
            mProgram = OpenGLUtils.createProgramAndLink(vertexShaderCode,fragmentShaderCode);
        }

        @Override
        public void onSurfaceChanged(GL10 gl10, int width, int height) {
            GLES20.glViewport(0, 0, width, height);
        }

        @Override
        public void onDrawFrame(GL10 gl10) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            // 将程序加入到 OpenGLES2.0 环境
            GLES20.glUseProgram(mProgram);
            // 获取顶点着色器的 vPosition 成员句柄
            mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
            // 启用三角形顶点的句柄
            GLES20.glEnableVertexAttribArray(mPositionHandle);
            // 准备三角形的坐标数据
            GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
            // 获取片元着色器的 vColor 成员的句柄
            mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
            // 设置绘制三角形的颜色
            GLES20.glUniform4fv(mColorHandle, 1, color, 0);
            // 绘制三角形
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
            // 禁用顶点数组的句柄
            GLES20.glDisableVertexAttribArray(mPositionHandle);
        }
    }
}
