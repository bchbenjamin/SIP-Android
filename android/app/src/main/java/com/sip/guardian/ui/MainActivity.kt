package com.sip.guardian.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import com.sip.guardian.domain.repository.AuthRepository
import com.sip.guardian.ui.navigation.SipNavDestination
import com.sip.guardian.ui.navigation.SipNavGraph
import com.sip.guardian.ui.theme.SIPGuardianTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val start = if (authRepository.currentUser() != null) {
            SipNavDestination.Dashboard.route
        } else {
            SipNavDestination.Login.route
        }

        setContent {
            SIPGuardianTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    SipNavGraph(
                        navController = rememberNavController(),
                        startDestination = start,
                    )
                }
            }
        }
    }
}
