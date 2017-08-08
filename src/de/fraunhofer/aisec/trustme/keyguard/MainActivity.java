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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.res.Resources;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemProperties;
import android.os.CountDownTimer;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.telephony.PhoneStateListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.TextView;
import android.provider.Settings;
import android.app.WallpaperManager;
import android.view.Display;

import java.text.NumberFormat;
import java.util.ArrayList;

import java.io.IOException;

import de.fraunhofer.aisec.trustme.cmlcom.Communicator;
import de.fraunhofer.aisec.trustme.cmlcom.ContainerItem;
import de.fraunhofer.aisec.trustme.cmlcom.PasswordException;
import de.fraunhofer.aisec.trustme.cmlcom.LockedTillRebootException;
import de.fraunhofer.aisec.trustme.cmlcom.SmartcardException;

public class MainActivity extends Activity {

    private final static String TAG = "MainActivity";
    public final static boolean DEBUG = true;

    private static ArrayList<ContainerItem> containers;

    private Communicator communicator;

    private RelativeLayout.LayoutParams layoutParams;

    private int wrongPasswordAttempts = 0;
    private static boolean softtokenLockedTillReboot = false;
   
    private ContainerDotView mContainerDotView;
    private ImageView mSettingsView;

    private TextView mNotificationView;

    ContainerDotView.OnTriggerListener mOnTriggerListener = new ContainerDotView.OnTriggerListener() {
        @Override
        public void onTrigger(View view, String target) {
            // empty
        }

        @Override
        public void onReleased(View view, String target) {
            if (target == null || target.equals("none")) {
                Log.d(TAG, "onReleased: none (or null)");
                return;
            }

            if ("left".equals(target)) {
                Log.d(TAG, "onReleased: left");
                switchToContainer("a1");
            } else if ("right".equals(target)) {
                Log.d(TAG, "onReleased: right");
                switchToContainer("a2");
            }
        }
    };

    public static boolean inFlightMode() {
        String simState = SystemProperties.get("gsm.sim.state", "PIN_REQUIRED");
        String wlanState = SystemProperties.get("wlan.driver.status", "ok");
        return simState.equals("NOT_READY") && wlanState.equals("unloaded");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //String simState = SystemProperties.get("gsm.sim.state", "PIN_REQUIRED");
        //while(!simState.equals("READY")) {
        //    Log.i("TrustmeLockScreenReceiver", "gsm.sim.state: " + simState);
        //    if (inFlightMode() || simState.equals("ABSENT")) break; // Handle flight mode
        //    try {
        //        Thread.sleep(500);
        //    } catch (Exception e) {}
        //    simState = SystemProperties.get("gsm.sim.state", "PIN_REQUIRED");
        //}

        if (MainActivity.DEBUG) Log.d(TAG,"Starting MainActivtty");
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        setContentView(R.layout.glow_main);

        mContainerDotView = (ContainerDotView) findViewById(R.id.ContainerDotView);
        mContainerDotView.setOnTriggerListener(mOnTriggerListener);

        mNotificationView = (TextView) findViewById(R.id.notification);

        mSettingsView = (ImageView) findViewById(R.id.Settings);
        mSettingsView.setClickable(true);
        mSettingsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.ACTION_SETTINGS));
            }
        });

        communicator = new Communicator();

        //try {
        //        startService(new Intent(this,TrustmeKeyguardService.class));
        //} catch (Exception exp) {

        //}
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MainActivity.DEBUG) Log.d(TAG,"onResume");

        try {
            if (MainActivity.DEBUG) Log.d(TAG,"Getting containers.");
            containers = communicator.getContainers();
        } catch (IOException exp) {
            if (MainActivity.DEBUG) Log.d(TAG,"Couldn't get containers");
            containers = new ArrayList<ContainerItem>();
        }

        ContainerDotView containerDotView = (ContainerDotView) findViewById(R.id.ContainerDotView);
        for (ContainerItem curContainer : containers) {
            curContainer.setItemFromConfig(communicator,this);

            // Set the Dot-Colors to the currently configured ones.
            if ("a1".equals(curContainer.getName())) {
                containerDotView.containerDots.get(0).setOrigColor(curContainer.getColor());
            } else if ("a2".equals(curContainer.getName())) {
                containerDotView.containerDots.get(1).setOrigColor(curContainer.getColor());
            }
        }
        containerDotView.resetDotColors();

        View view = findViewById(android.R.id.content);

        //view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        //                          |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        //                          |View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        //                          |View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void onDestroy() {
        super.onDestroy();
    }

    private void switchToContainer(String name) {
        if (MainActivity.DEBUG) Log.d(TAG,"Name passed to switchToContainer: " + name);

        // Reloading container config
        reloadContainerConfig();

        // keep state of telephony container to maintain boot order
        String telephonyName = SystemProperties.get("ro.trustme.telephony.name", "a1");
        boolean telephonyRunning = false;
        for (ContainerItem curContainer : containers) {
            if (curContainer.getName().equals(telephonyName))
                telephonyRunning = curContainer.isRunning();
        }

        for (ContainerItem curContainer : containers) {

            final String curName = curContainer.getName();
            final String uuid = curContainer.getUuid();
            if (MainActivity.DEBUG) {
                Log.d(TAG, "Current container: " + curName);
                Log.d(TAG, "Current uuid: " + uuid);
                Log.d(TAG, "Container to switch to: " + name);
            }
            if (!(curName.equals(name))) {
                continue;
            }
            else {
                if (MainActivity.DEBUG) Log.d(TAG,"curName.equals(name)");
                boolean running = curContainer.isRunning();
                if (MainActivity.DEBUG) Log.d(TAG,"Container running: " + running);

                if (running) {
                    ContainerDotView containerDotView = (ContainerDotView) findViewById(R.id.ContainerDotView);
                    containerDotView.resetDotColors();
                    communicator.switchTo(uuid);
                    if (MainActivity.DEBUG) Log.d(TAG, "Switched to container: " +name);
                } else {
                    if (softtokenLockedTillReboot) {
                        if (mNotificationView.getText().equals("")) {
                            mNotificationView.setText("Too many wrong password attempts. You need to reboot in order to try again.");
                            NotificationCountdown timer = new NotificationCountdown(5000,1000);
                        }
                    } else {
                        Intent intent = new Intent(this, PasswordActivity.class);
                        intent.putExtra("uuid", uuid);
                        intent.putExtra("name", curName);
                        startActivity(intent);
                    }
                }
            }
        }

    }

    private void reloadContainerConfig() {
        try {
            if (MainActivity.DEBUG) Log.d(TAG,"Getting containers.");
            containers = communicator.getContainers();
        } catch (IOException exp) {
            if (MainActivity.DEBUG) Log.d(TAG,"Couldn't get containers, keeping old config.");
        }

        for (ContainerItem curContainer : containers) {
          curContainer.setItemFromConfig(communicator,this);
        }
    }

    public static void lockSoftToken() {
        softtokenLockedTillReboot = true;
    }

    private class NotificationCountdown extends CountDownTimer {
        long duration, interval;

        public NotificationCountdown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            start();
        }
        
        @Override
        public void onFinish() {
            mNotificationView.setText("");
        }

        @Override
        public void onTick(long duration) {
            // No action needed.
        }
    }
}
