package com.example.myopengldemo;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @author shaolongfei
 */
public class RotateTriangleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RotateTriangleSurfaceView rotateTriangleSurfaceView = new RotateTriangleSurfaceView(this);
        setContentView(rotateTriangleSurfaceView);
    }

    protected int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        int[] compiled = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            Log.e("ES20_ERROR", "Could not compile shader " + type + " : " + shaderCode);
            Log.e("ES20_ERROR", GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            shader = 0;
        }
        return shader;
    }

    private class RotateTriangleSurfaceView extends GLSurfaceView {

        private RotateTriangleRender rotateTriangleRender;

        // 旋转的灵敏度
        private final float TOUCH_SCALE_FACTOR = 0.5f;
        private float mPreviousX;
        private float mPreviousY;

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    float dx = x - mPreviousX;
                    float dy = y - mPreviousY;
                    // 调整四个象限x轴和y轴的正方向
                    if (y < getHeight() / 2) {
                        dx = dx * -1;
                    }

                    if (x > getWidth() / 2) {
                        dy = dy * -1;
                    }

                    rotateTriangleRender.setAngle(rotateTriangleRender.getAngle() + ((dx + dy) * TOUCH_SCALE_FACTOR));
                    requestRender();
                    break;
                default:
                    break;
            }

            mPreviousX = x;
            mPreviousY = y;
            return true;
        }

        public RotateTriangleSurfaceView(Context context) {
            this(context, null);
        }

        public RotateTriangleSurfaceView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        private void init() {
            setEGLContextClientVersion(2);
            rotateTriangleRender = new RotateTriangleRender();
            setRenderer(rotateTriangleRender);
        }

    }

    private class RotateTriangleRender implements GLSurfaceView.Renderer {

        private Triangle mTriangle;

        private float[] mRotationMatrix = new float[16];

        public volatile float mAngle;

        public void setAngle(float angle) {
            mAngle = angle;
        }

        public float getAngle() {
            return mAngle;
        }

        private final float[] mMVPMatrix = new float[16];
        private final float[] mProjectionMatrix = new float[16];
        private final float[] mViewMatrix = new float[16];

        @Override
        public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
            GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
            mTriangle = new Triangle();
        }

        @Override
        public void onSurfaceChanged(GL10 gl10, int width, int height) {
            GLES20.glViewport(0, 0, width, height);
            float ratio = (float) width / height;
            Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
        }

        @Override
        public void onDrawFrame(GL10 gl10) {
            Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
            Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
            float[] scratch = new float[16];
            Matrix.setRotateM(mRotationMatrix, 0, mAngle, 0, 0, -1.0f);
            Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
            mTriangle.draw(scratch);
        }

    }

    private class Triangle {

        private final String vertexShaderCode =
                "attribute vec4 vPosition;" +
                "uniform mat4 uMVPMatrix;" +
                "varying vec4 vColor;" +
                "attribute vec4 aColor;" +
                "void main(){" +
                "   gl_Position=uMVPMatrix*vPosition;" +
                "   vColor=aColor;" +
                "}";
        private final String fragmentShaderCode =
                "precision mediump float;" +
                "varying vec4 vColor;" +
                "void main(){" +
                "   gl_FragColor=vColor;" +
                "}";
        private int mProgram;
        private FloatBuffer vertexBuffer, colorBuffer;
        private int mMVPMatrixHandle;

        private final int COORDS_PER_VERTEX = 3;
        private float triangleCoords[] = {
                0.0f, 0.577f, 0.0f,
                -0.5f, -0.288f, 0.0f,
                0.5f, -0.288f, 0.0f};

        private float color[] = {
                1.0f, 0f, 0f, 1.0f,
                0f, 1.0f, 0f, 1.0f,
                0f, 0f, 1.0f, 1.0f};

        private int mPositionHandle;
        private int mColorHandle;

        private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
        private final int vertexStride = COORDS_PER_VERTEX * 4;

        public Triangle() {
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(triangleCoords.length * 4);
            byteBuffer.order(ByteOrder.nativeOrder());
            vertexBuffer = byteBuffer.asFloatBuffer();
            vertexBuffer.put(triangleCoords);
            vertexBuffer.position(0);

            ByteBuffer byteBuffer1 = ByteBuffer.allocateDirect(color.length * 4);
            byteBuffer1.order(ByteOrder.nativeOrder());
            colorBuffer = byteBuffer1.asFloatBuffer();
            colorBuffer.put(color);
            colorBuffer.position(0);

            int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
            int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

            mProgram = GLES20.glCreateProgram();
            GLES20.glAttachShader(mProgram, vertexShader);
            GLES20.glAttachShader(mProgram, fragmentShader);
            GLES20.glLinkProgram(mProgram);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(mProgram, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES20.GL_TRUE) {
                Log.e("ES20_ERROR", "Could not link program: ");
                Log.e("ES20_ERROR", GLES20.glGetProgramInfoLog(mProgram));
                GLES20.glDeleteProgram(mProgram);
                mProgram = 0;
            }
        }

        public void draw(float[] mvpMatrix) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            GLES20.glUseProgram(mProgram);
            mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
            GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
            mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
            GLES20.glEnableVertexAttribArray(mPositionHandle);
            GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
            mColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
            GLES20.glEnableVertexAttribArray(mColorHandle);
            GLES20.glVertexAttribPointer(mColorHandle, 4, GLES20.GL_FLOAT, false, 0, colorBuffer);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
            GLES20.glDisableVertexAttribArray(mPositionHandle);
        }
    }
}
