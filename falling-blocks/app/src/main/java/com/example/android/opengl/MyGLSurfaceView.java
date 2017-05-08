/*
 * initial code Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.opengl;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Logger;

import android.util.Log;

/**
 * A view container where OpenGL ES graphics can be drawn on screen.
 * This view can also be used to capture touch events, such as a user
 * interacting with drawn objects.
 */
public class MyGLSurfaceView extends GLSurfaceView {

    public MyGLRenderer myGLRenderer;
    private float mPreviousX;
    private float mPreviousY;
    private float mDensity;
    // -----------------------------------

    public MyGLSurfaceView(Context context)
    {
        super(context);
    }

    public MyGLSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public MyGLSurfaceView(Context pContext, int pScreenHeight, int pScreenWidth) {

        super(pContext);

        // Create an OpenGL ES 2.0 context:
        setEGLContextClientVersion(2);

        // void setEGLConfigChooser (int redSize, int greenSize, int blueSize, int alphaSize, int depthSize, int stencilSize)
        // Install a config chooser which will choose a config with at least the specified depthSize and stencilSize,
        // and exactly the specified redSize, greenSize, blueSize and alphaSize.
        super.setEGLConfigChooser(8, 8, 8, 8, 16, 0);

        // Set the Renderer for drawing on the GLSurfaceView
        myGLRenderer = new MyGLRenderer(pContext);

        // set the surface's renderer to be our OpenGL ES renderer:
        setRenderer(myGLRenderer);

        // Render the view only when there is a change in the drawing data:
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (event != null)
        {
            float x = event.getX();
            float y = event.getY();

            if (event.getAction() == MotionEvent.ACTION_MOVE)
            {
                if (myGLRenderer != null)
                {
                    float deltaX = (x - mPreviousX) / 1 / 2f;
                    float deltaY = (y - mPreviousY) / 1 / 2f;

                    System.out.println(mPreviousX);
                    System.out.println(mPreviousY);
                    System.out.println(mDensity);

                    myGLRenderer.mDeltaX += deltaX;
                    myGLRenderer.mDeltaY += deltaY;

                }
            }

            mPreviousX = x;
            mPreviousY = y;

            return true;
        }
        else
        {
            return super.onTouchEvent(event);
        }
    }

    // Hides superclass method.
    public void setRenderer(MyGLRenderer renderer, float density)
    {
        myGLRenderer = renderer;
        mDensity = density;

        super.setRenderer(renderer);
    }

   
}
