/*
 * Copyright (C) 2011 The Android Open Source Project
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

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import java.util.ArrayList;

public class OpenGLES20Activity extends Activity {

    public MyGLSurfaceView myGLView;
    private ArrayList<Button> topList = new ArrayList<>();
    private ArrayList<Button> bottomList = new ArrayList<>();

    @Override
    public void onCreate(Bundle pSavedInstanceState) {
        super.onCreate(pSavedInstanceState);

        // removes title and action bar, otherwise OpenGL ES and Android dimensions won't align:
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // obtain Display size from window manager:
        Display lDisplay = getWindowManager().getDefaultDisplay();
        Point lSize = new Point();
        lDisplay.getSize(lSize);

        // Create a GLSurfaceView instance of the required size,
        //  and set it as the ContentView for this Activity:
        myGLView = new MyGLSurfaceView(this, 600, 600);
        setContentView(myGLView);

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);

        LinearLayout toprow = new LinearLayout(this);
        toprow.setOrientation(LinearLayout.HORIZONTAL);
        toprow.setPadding(5,5,5,5);
        toprow.setGravity(Gravity.CENTER_HORIZONTAL);
        toprow.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        Button rotateButton = getButton(R.drawable.bluebutton, "Left", true, 0);
        toprow.addView(rotateButton);
        toprow.addView(getBuffer());



        Button txyButton = getButton(R.drawable.darkbluebutton, "-", true, 1);
        toprow.addView(txyButton);
        toprow.addView(getBuffer());

        Button txzButton = getButton(R.drawable.darkbluebutton, "-", true, 2);
        toprow.addView(txzButton);
        toprow.addView(getBuffer());

        Button scaleButton = getButton(R.drawable.darkbluebutton, "Right", true, 3);
        toprow.addView(scaleButton);

        LinearLayout bottomrow = new LinearLayout(this);
        bottomrow.setOrientation(LinearLayout.HORIZONTAL);
        bottomrow.setGravity(Gravity.CENTER_HORIZONTAL);
        bottomrow.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        Button obj1Button = getButton(R.drawable.bluebutton, "-", true, 4);
        bottomrow.addView(obj1Button);
        bottomrow.addView(getBuffer());

        Button obj2Button = getButton(R.drawable.darkbluebutton, "Down", true, 5);
        bottomrow.addView(obj2Button);
        bottomrow.addView(getBuffer());

        Button obj3Button = getButton(R.drawable.darkbluebutton, "Up", true, 6);
        bottomrow.addView(obj3Button);
        bottomrow.addView(getBuffer());

        Button camButton = getButton(R.drawable.darkbluebutton, "-", true, 7);
        bottomList.add(camButton);
        bottomrow.addView(camButton);

        container.addView(toprow);
        container.addView(bottomrow);
        this.addContentView(container, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    protected void onPause() {
        super.onPause();
        // The following call pauses the rendering thread.
        // If your OpenGL application is memory intensive,
        // you should consider de-allocating objects that
        // consume significant memory here.
        myGLView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // The following call resumes a paused rendering thread.
        // If you de-allocated graphic objects for onPause()
        // this is a good place to re-allocate them.
        myGLView.onResume();
    }

    private void topButtonClick(int index) {
        for(int i = 0; i<topList.size(); i++) topList.get(i).setBackgroundResource(R.drawable.darkbluebutton);
        topList.get(index).setBackgroundResource(R.drawable.bluebutton);
        myGLView.myGLRenderer.topSelectionChanged(index);
    }

    private void bottomButtonClick(int index) {
        for(int i = 0; i<bottomList.size(); i++) bottomList.get(i).setBackgroundResource(R.drawable.darkbluebutton);
        bottomList.get(index).setBackgroundResource(R.drawable.bluebutton);
        myGLView.myGLRenderer.bottomSelectionChanged(index);
    }

    private LinearLayout getBuffer() {
        LinearLayout buffer = new LinearLayout(this);
        buffer.setLayoutParams(new ViewGroup.LayoutParams(5,ViewGroup.LayoutParams.MATCH_PARENT));
        return buffer;
    }

    private Button getButton(int color, String text, boolean isTop, final int index) {
        Button b = new Button(this);
        b.setBackgroundResource(color);
        b.setTextAppearance(R.style.ButtonText);
        b.setText(text);
        if(isTop) {
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    topButtonClick(index);
                }
            });
            topList.add(b);
        }
        else {
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomButtonClick(index);
                }
            });
            bottomList.add(b);
        }
        return b;
    }

}