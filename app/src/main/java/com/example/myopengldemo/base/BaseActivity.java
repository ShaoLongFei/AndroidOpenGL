package com.example.myopengldemo.base;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
