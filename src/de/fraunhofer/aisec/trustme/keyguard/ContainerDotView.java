/*
 * This file is part of trust|me
 * Copyright(c) 2013 - 2017 Fraunhofer AISEC
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V.
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms and conditions of the GNU General Public License,
 * version 2 (GPL 2), as published by the Free Software Foundation.
 *
 * This program is distributed in the hope it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GPL 2 license for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, see <http://www.gnu.org/licenses/>
 *
 * The full GNU General Public License is included in this distribution in
 * the file called "COPYING".
 *
 * Contact Information:
 * Fraunhofer AISEC <trustme@aisec.fraunhofer.de>
 */

package de.fraunhofer.aisec.trustme.keyguard;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class ContainerDotView extends View {

    public interface OnTriggerListener {
        public void onTrigger(View view, String target);
        public void onReleased(View view, String target);
    }

    ArrayList<ContainerDot> containerDots;

    ContainerDot curTarget;
    ContainerDot prevTarget;

    RectF leftDot;
    RectF rightDot;

    int dotWidth = 150;

    String TAG = "ContainerDotView";

    private OnTriggerListener mOnTriggerListener;

    public ContainerDotView(Context context) {
        this(context,null);
        Log.d(TAG, "Constructor.");

    }

    public ContainerDotView(Context context, AttributeSet attrs){
        super(context,attrs);
        Log.d(TAG, "attrs Constructor.");
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setContainerDots();
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        setDotPositions(canvas);
        drawDots(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        Log.d(TAG, "Event: " + event.toString());
        prevTarget = curTarget;
        String type = checkTouchEvent(event); // will set curTarget
        Log.d(TAG,"type: " + type);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // Make the dot white while pressing.
            if (curTarget != null) {
                curTarget.setColor(Color.rgb(255,255,255));
                invalidate();
            }

            // Call the functionality defined in MainActivity.
            mOnTriggerListener.onTrigger(this, type);
        } else if (event.getAction() == MotionEvent.ACTION_UP && curTarget != null) {
            // Reset the color of the dot when releasing.
            curTarget.resetColor();

            invalidate();
            // Call the functionality defined in MainActivity.
            mOnTriggerListener.onReleased(this, type);
        } else if (type.equals("left") || type.equals("right")) {
            // Make the dot white while hovering over it.
            if (curTarget != null) {
                curTarget.setColor(Color.rgb(255,255,255));
                invalidate();
            }
        }
        if (type.equals("none") && curTarget == null && prevTarget != null) { // make sure this fires only once
            // Reset the color of the dot. This case is important if the
            // user moves away from the dot while still pressing down.
            prevTarget.resetColor();
            invalidate();
        }

        return true;
    }

    private void drawDots(Canvas canvas){

        canvas.drawOval(containerDots.get(0).getPosition(),containerDots.get(0).getColor());
        canvas.drawOval(containerDots.get(1).getPosition(),containerDots.get(1).getColor());

    }

    private String checkTouchEvent(MotionEvent event) {
        float evX = event.getX();
        float evY = event.getY();

        Log.d(TAG,"ContainerDots: " + containerDots.toString());
        Log.d(TAG,"pos 0: " + containerDots.get(0).toString());
        if (containerDots.get(0).getPosition().contains(evX,evY)) {
            curTarget = containerDots.get(0);
            return "left";
        } else if (containerDots.get(1).getPosition().contains(evX,evY)) {
            curTarget = containerDots.get(1);
            return "right";
        } else {
            curTarget = null;
            return "none";
        }

    }

    public void setOnTriggerListener(OnTriggerListener listener) {
        mOnTriggerListener = listener;
    }

    public void setDotPositions(Canvas canvas) {
        int cWidth = canvas.getWidth();
        int cHeight = canvas.getHeight();
        int cMiddleWidth = cWidth / 2;
        int cMiddleHeight = cHeight * 2 / 3;
        int cMiddleDistance = cWidth / 6;

        leftDot.set(cMiddleWidth-cMiddleDistance-containerDots.get(0).getWidth()/2,
                cMiddleHeight-containerDots.get(0).getWidth()/2,
                cMiddleWidth-cMiddleDistance+containerDots.get(0).getWidth()/2,
                cMiddleHeight+containerDots.get(0).getWidth()/2);
        containerDots.get(0).setPosition(leftDot);

        rightDot.set(cMiddleWidth+cMiddleDistance-containerDots.get(0).getWidth()/2,
                cMiddleHeight-containerDots.get(0).getWidth()/2,
                cMiddleWidth+cMiddleDistance+containerDots.get(0).getWidth()/2,
                cMiddleHeight+containerDots.get(0).getWidth()/2);
        containerDots.get(1).setPosition(rightDot);
    }

    public void setContainerDots() {
        leftDot = new RectF(0,0,50,50);
        rightDot = new RectF(0,0,50,50);

        containerDots = new ArrayList<ContainerDot>();
        containerDots.add(new ContainerDot(dotWidth));
        containerDots.add(new ContainerDot(dotWidth));
    }

    public void resetDotColors() {
        containerDots.get(0).resetColor();
        containerDots.get(1).resetColor();
    }

    public void addTarget(ContainerDot dot) {
        containerDots.add(dot);
    }
}
