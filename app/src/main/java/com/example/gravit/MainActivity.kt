package com.example.gravit

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import com.auth0.android.provider.WebAuthProvider
import com.example.gravit.navigation.AppNavigation
import com.example.gravit.ui.theme.GravitTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WebAuthProvider.resume(intent)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            GravitTheme {
                AppNavigation()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        WebAuthProvider.resume(intent)
    }
}

@Preview(showBackground = true)
@Composable
fun AppNavigationPreview() {
    AppNavigation()
}