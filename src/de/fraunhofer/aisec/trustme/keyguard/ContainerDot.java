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

import android.graphics.RectF;
import android.graphics.Paint;
import android.graphics.Color;

public class ContainerDot {

    private int width;
    private Paint origColor;
    private Paint curColor;
    private RectF position;

    public ContainerDot(int width) {
        this.width = width;
	this.setColor(Color.GRAY);
    }

    public ContainerDot(Paint color, int width) {
        this.curColor = color;
        this.origColor = new Paint(color);
        this.width = width;
    }

    public int getWidth() {
        return width;
    }

    public void setPosition(RectF position) {
        this.position = position;
    }

    public RectF getPosition() {
        return position;
    }

    public Paint getColor() {
        return curColor;
    }

    public void setColor(int color) {
        Paint colorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        colorPaint.setColor(color);
        this.curColor = colorPaint;
        if (origColor == null) {
            this.origColor = colorPaint;
        }
    }
    
    public void setOrigColor(int origColor) {
	Paint colorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	colorPaint.setColor(origColor);
	this.origColor = colorPaint;
    }

    public void resetColor() {
        curColor = new Paint(origColor);
    }
}
