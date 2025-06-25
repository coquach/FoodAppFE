package com.example.foodapp

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
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
                zoomX.duration = 500
                zoomY.duration = 500
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
        CoroutineScope(Dispatchers.IO).launch {
            delay(2000L)
            showSplashScreen = false
        }
        super.onCreate(savedInstanceState)

        processIntent(intent, viewModel)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        notificationViewModel.onAction(NotificationState.Action.Retry)
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