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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.util.Log;
import android.os.SystemProperties;

/**
 * Created by brosche on 30.09.14.
 */
public class LockScreenReceiver extends BroadcastReceiver{

    public static boolean wasScreenOn = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        String simState = SystemProperties.get("gsm.sim.state", "PIN_REQUIRED");
        /*
        if (!MainActivity.inFlightMode() && !simState.equals("READY") && !simState.equals("ABSENT")) {
            // stop MainActivity when flight mode changes
            Intent intent1 = new Intent(context, MainActivity.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);
        }

        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            wasScreenOn = false;
            Intent intent1 = new Intent(context,MainActivity.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.i("TrustmeLockScreenReceiver", "Action: ACTION_SCREEN_OFF");
            context.startActivity(intent1);
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            wasScreenOn = true;
            Intent intent1 = new Intent(context,MainActivity.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.i("TrustmeLockScreenReceiver", "Action: ACTION_SCREEN_ON");
            context.startActivity(intent1);
        } else if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
        */
        boolean waitForPin = !MainActivity.inFlightMode() && !simState.equals("READY") && !simState.equals("ABSENT");

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) && !waitForPin) {
            Intent intent1 = new Intent(context, MainActivity.class);
            // Clears any existing task associated with the activity
            // and starts the activity as the root of a new task:
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            Log.i("TrustmeLockScreenReceiver", "Action: ACTION_BOOT_COMPLETED");
            context.startActivity(intent1);
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF) && !waitForPin) {
            Intent intent1 = new Intent(context, MainActivity.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.i("TrustmeLockScreenReceiver", "Action: ACTION_SCREEN_OFF");
            context.startActivity(intent1);
        }
    }

}
