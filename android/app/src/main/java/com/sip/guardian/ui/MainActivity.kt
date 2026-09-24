package com.sip.guardian.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.sip.guardian.ui.theme.SIPGuardianTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SIPGuardianTheme {
                // TODO: Replace with SipNavGraph once navigation is implemented.
            }
        }
    }
}
