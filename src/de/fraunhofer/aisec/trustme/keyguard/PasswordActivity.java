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
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.util.Log;

import java.io.IOException;

import de.fraunhofer.aisec.trustme.cmlcom.Communicator;
import de.fraunhofer.aisec.trustme.cmlcom.ContainerItem;
import de.fraunhofer.aisec.trustme.cmlcom.PasswordException;
import de.fraunhofer.aisec.trustme.cmlcom.LockedTillRebootException;
import de.fraunhofer.aisec.trustme.cmlcom.SmartcardException;

public class PasswordActivity extends Activity {
    private Communicator communicator;
    private int wrongPasswordAttempts;
    private static final String TAG = "PasswordActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        setContentView(R.layout.password_input);

        final String uuid = getIntent().getExtras().getString("uuid");
        final String curName = getIntent().getExtras().getString("name");
        communicator = new Communicator();

        final TextView passwordText = (TextView) findViewById(R.id.password_text);

        EditText editText = (EditText) findViewById(R.id.password);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean handled = false;
                if (i == EditorInfo.IME_ACTION_DONE) {
                    //DO SOMETHING
                    String passwd = textView.getText().toString();
                    if (passwd != null && !passwd.equals("")) {
                        try {
                            communicator.startContainer(uuid, passwd);
                            wrongPasswordAttempts = 0;
                            handled = true;
                            finish();
                        } catch (PasswordException e) {
                            wrongPasswordAttempts++;
                            passwordText.setText("Wrong password.");
                            findViewById(R.id.password_text).invalidate();
                            if (MainActivity.DEBUG) Log.w(TAG, "Couldn't start container (wrong password): " + curName);
                            handled = true;
                        } catch (LockedTillRebootException e) {
                            MainActivity.lockSoftToken();
                            passwordText.setText("Device is locked. Reboot to try again.");
                            passwordText.invalidate();
                            if (MainActivity.DEBUG) Log.w(TAG, "Couldn't start container (locked till reboot): " + curName);
                            handled = true;
                        } catch (SmartcardException e) {
                            passwordText.setText("Smartcard error.");
                            passwordText.invalidate();
                            // This was thrown even though the reason for the error was entering a wrong password.
                            // TODO better error handling?
                            if (MainActivity.DEBUG) Log.w(TAG, "Couldn't start container (smartcard lock/unlock error): " + curName);
                            handled = true;
                        } catch (IOException e) {
                            if (MainActivity.DEBUG) Log.e(TAG, "Couldn't start container: " + curName);
                            handled = true;
                        }
                    } else {
                       // password empty 
                    }
                }
                return handled;
            }
        });
    }
}
