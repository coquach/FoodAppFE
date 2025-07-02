package com.example.foodapp

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.foodapp.ui.screen.notification.NotificationState
import com.example.foodapp.ui.screen.notification.NotificationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
abstract class BaseFoodAppActivity : ComponentActivity() {
    var showSplashScreen = true
    val viewModel by viewModels<MainViewModel>()
    val notificationViewModel by viewModels<NotificationViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                showSplashScreen
            }
            setOnExitAnimationListener { screen ->
                val zoomX = ObjectAnimator.ofFloat(
                    screen.iconView,
                    View.SCALE_X,
                    0.8f,
                    0f
                )
                val zoomY = ObjectAnimator.ofFloat(
                    screen.iconView,
                    View.SCALE_Y,
                    0.8f,
                    0f
                )
                zoomX.duration = 500L
                zoomY.duration = 500L
                zoomX.interpolator = OvershootInterpolator()
                zoomY.interpolator = OvershootInterpolator()
                zoomX.doOnEnd {
                    screen.remove()
                }
                zoomY.doOnEnd {
                    screen.remove()
                }
                zoomY.start()
                zoomX.start()
            }

        }
        super.onCreate(savedInstanceState)
        hideSystemUI(window)
        CoroutineScope(Dispatchers.IO).launch {
            delay(1500L)
            showSplashScreen = false
        }


        processIntent(intent, viewModel)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        notificationViewModel.getNotifications()
        Log.d("INTENT_CHECK", "Intent extras: ${intent.extras}")
        processIntent(intent, viewModel)
    }

    private fun processIntent(intent: Intent, viewModel: MainViewModel) {
        if (intent.hasExtra("type")) {
            viewModel.navigateToNotification()
            intent.removeExtra("type")
        }

    }
}
@Suppress("DEPRECATION")
fun hideSystemUI(window: Window) {
    window.decorView.systemUiVisibility =
        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_FULLSCREEN
}