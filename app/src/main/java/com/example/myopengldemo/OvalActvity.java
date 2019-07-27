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
public class OvalActvity extends BaseActivity{


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRender(new OvalRender());
    }

    private class OvalRender implements GLSurfaceView.Renderer{

        private FloatBuffer vertexBuffer;
        private final String vertexShaderCode=
                "attribute vec4 vPosition;"+
                        "uniform mat4 vMatrix;"+
                        "void main(){"+
                        "   gl_Position=vMatrix*vPosition;"+
                        "}";
        private final String fragmentShaderCode=
                "precision mediump float;"+
                        "uniform vec4 vColor;"+
                        "void main(){"+
                        "   gl_FragColor=vColor;"+
                        "}";

        private int mProgram;

        private final int COORDS_PER_VERTEX=3;

        private int mPostionHandle;
        private int mColorHandle;

        private float[] mViewMatrix=new float[16];
        private float[] mProjectMatrix=new float[16];
        private float[] mMVPMatrix=new float[16];

        private final int vertexStride=0;

        private int mMatrixHandler;

        private float radius=1.0f;
        private int n=360;

        private float[] shapePos;
        private float height=0.0f;

        private float[] color={1.0f,1.0f,1.0f,1.0f};


        @Override
        public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
            createPositions();
            vertexBuffer=getFloatBuffer(shapePos);
            int vertexShader=loadShader(GLES20.GL_VERTEX_SHADER,vertexShaderCode);
            int fragmentShader=loadShader(GLES20.GL_FRAGMENT_SHADER,fragmentShaderCode);
            mProgram=creatProgramAndLink(mProgram,vertexShader,fragmentShader);
            if (!checkLinkState(mProgram)){
                Log.e("ES20_ERROR", "Could not link program: ");
                Log.e("ES20_ERROR", GLES20.glGetProgramInfoLog(mProgram));
            }
        }

        @Override
        public void onSurfaceChanged(GL10 gl10, int width, int height) {
            float ratio=(float)width/height;
            Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
            Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 7.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
            Matrix.multiplyMM(mMVPMatrix,0,mProjectMatrix,0,mViewMatrix,0);
        }

        @Override
        public void onDrawFrame(GL10 gl10) {
            GLES20.glUseProgram(mProgram);
            mMatrixHandler=GLES20.glGetUniformLocation(mProgram,"vMatrix");
            GLES20.glUniformMatrix4fv(mMatrixHandler,1,false,mMVPMatrix,0);
            mPostionHandle=GLES20.glGetAttribLocation(mProgram,"vPosition");
            GLES20.glEnableVertexAttribArray(mPostionHandle);
            GLES20.glVertexAttribPointer(mPostionHandle,COORDS_PER_VERTEX,
                    GLES20.GL_FLOAT,false,
                    vertexStride,vertexBuffer);
            mColorHandle=GLES20.glGetUniformLocation(mProgram,"vColor");
            GLES20.glUniform4fv(mColorHandle,1,color,0);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN,0,shapePos.length/3);
            GLES20.glDisableVertexAttribArray(mPostionHandle);
        }

        private void createPositions(){
            ArrayList<Float> data=new ArrayList<>();
            float angDegSpan=360/n;
            for (float i=0;i<360;i+=angDegSpan){
                data.add((float)(radius*Math.sin(i*Math.PI/180f)));
                data.add((float)(radius*Math.cos(i*Math.PI/180f)));
                data.add(height);
            }
            shapePos=new float[data.size()];
            for (int i=0;i<shapePos.length;i++){
                shapePos[i]=data.get(i);
            }
        }
    }

}
