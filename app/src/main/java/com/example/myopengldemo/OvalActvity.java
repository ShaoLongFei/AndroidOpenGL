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
 * @创建时间 2019-07-27 17:34
 * @描述
 */
public class OvalActvity extends BaseActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRender(new OvalRender());
    }

    public static class OvalRender implements GLSurfaceView.Renderer {

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

        private int mPostionHandle;
        private int mColorHandle;

        private float[] mViewMatrix = new float[16];
        private float[] mProjectMatrix = new float[16];
        private float[] mMVPMatrix = new float[16];

        private final int vertexStride = 0;

        private int mMatrixHandler;

        private float radius = 1.0f;
        private int n = 360; // 切割份数

        private float[] shapePos;
        private float height = 0.0f;

        // 设置颜色，依次为红绿蓝和透明通道
        private float[] color = {1.0f, 1.0f, 1.0f, 1.0f};

        OvalRender() {
            this(0.0f);
        }

        OvalRender(float height) {
            this.height = height;
            createPositions();
        }

        @Override
        public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
            vertexBuffer = getFloatBuffer(shapePos);

            int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
            int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

            mProgram = creatProgramAndLink(vertexShader, fragmentShader);
            checkLinkState(mProgram);
        }

        @Override
        public void onSurfaceChanged(GL10 gl10, int width, int height) {
            // 计算宽高比
            float ratio = (float) width / height;
            // 设置透视投影
            Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
            // 设置相机位置
            Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 7.0f, 0f,
                    0f, 0f, 0f, 1.0f, 0.0f);
            // 计算变换矩阵
            Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0);
        }

        @Override
        public void onDrawFrame(GL10 gl10) {
            // 将程序加入到 OpenGLES 2.0 的环境中
            GLES20.glUseProgram(mProgram);
            // 获取变换矩阵 vMatrix 的句柄
            mMatrixHandler = GLES20.glGetUniformLocation(mProgram, "vMatrix");
            // 指定 vMatrix 的值
            GLES20.glUniformMatrix4fv(mMatrixHandler, 1, false, mMVPMatrix, 0);
            // 获取顶点着色器 vPosition 的句柄
            mPostionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
            // 启动三角形顶点的句柄
            GLES20.glEnableVertexAttribArray(mPostionHandle);
            // 准备三角形的坐标数据
            GLES20.glVertexAttribPointer(mPostionHandle, COORDS_PER_VERTEX,
                    GLES20.GL_FLOAT, false,
                    vertexStride, vertexBuffer);
            // 获取片元着色器 vColor 的句柄
            mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
            // 设置绘制三角形的颜色
            GLES20.glUniform4fv(mColorHandle, 1, color, 0);
            // 绘制三角形
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, shapePos.length / 3);
            // 禁止顶点数组的句柄
            GLES20.glDisableVertexAttribArray(mPostionHandle);
        }

        private void createPositions() {
            ArrayList<Float> data = new ArrayList<>();
            // 设置圆心坐标
            data.add(0.0f);
            data.add(0.0f);
            data.add(height);
            float angDegSpan = 360 / n;
            for (float i = 0; i < 360; i += angDegSpan) {
                data.add((float) (radius * Math.sin(i * Math.PI / 180f)));
                data.add((float) (radius * Math.cos(i * Math.PI / 180f)));
                data.add(height);
            }
            shapePos = new float[data.size()];
            for (int i = 0; i < shapePos.length; i++) {
                shapePos[i] = data.get(i);
            }
        }

        public void setMatrix(float[] matrix){
            this.mMVPMatrix=matrix;
        }
    }
}
