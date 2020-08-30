package com.example.myopengldemo;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class BaseActivity extends AppCompatActivity {

    public MyGlSurfaceView myGlSurfaceView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myGlSurfaceView = new MyGlSurfaceView(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        myGlSurfaceView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        myGlSurfaceView.onResume();
    }


    public void setRender(GLSurfaceView.Renderer renderer) {
        myGlSurfaceView.setRenderer(renderer);
        myGlSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        setContentView(myGlSurfaceView);
    }

    protected static int loadShader(int type, String shaderCode) {
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

    protected IntBuffer getIntBuffer(int[] arr) {
        IntBuffer intBuffer;
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(arr.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        intBuffer = byteBuffer.asIntBuffer();
        intBuffer.put(arr);
        intBuffer.position(0);
        return intBuffer;
    }

    protected static FloatBuffer getFloatBuffer(float[] arr) {
        FloatBuffer floatBuffer;
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(arr.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        floatBuffer = byteBuffer.asFloatBuffer();
        floatBuffer.put(arr);
        floatBuffer.position(0);
        return floatBuffer;
    }

    protected ShortBuffer getShortBuffer(short[] arr) {
        ShortBuffer shortBuffer;
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(arr.length * 2);
        byteBuffer.order(ByteOrder.nativeOrder());
        shortBuffer = byteBuffer.asShortBuffer();
        shortBuffer.put(arr);
        shortBuffer.position(0);
        return shortBuffer;
    }

    protected static int creatProgramAndLink(int vertexShader, int fragmentShader) {
        int program;
        // 创建一个空的OpenGLES程序
        program = GLES20.glCreateProgram();
        // 将顶点着色器加入到程序
        GLES20.glAttachShader(program, vertexShader);
        // 将片元着色器加入到程序中
        GLES20.glAttachShader(program, fragmentShader);
        // 连接到着色器程序
        GLES20.glLinkProgram(program);
        return program;
    }

    protected static boolean checkLinkState(int mProgram) {
        int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(mProgram, GLES20.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] != GLES20.GL_TRUE) {
            GLES20.glDeleteProgram(mProgram);
            Log.e("ES20_ERROR", "Could not link program: \n" + GLES20.glGetProgramInfoLog(mProgram));
            return false;
        }
        return true;
    }

    private class MyGlSurfaceView extends GLSurfaceView {
        public MyGlSurfaceView(Context context) {
            this(context, null);
        }

        public MyGlSurfaceView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        private void init() {
            setEGLContextClientVersion(2);
        }
    }
}
