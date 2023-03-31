package com.zhuinden.simplestackftuecomposesample.app

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.zhuinden.simplestack.*
import com.zhuinden.simplestack.navigator.Navigator
import com.zhuinden.simplestackcomposeintegration.core.BackstackProvider
import com.zhuinden.simplestackcomposeintegration.core.ComposeStateChanger
import com.zhuinden.simplestackextensions.lifecyclektx.observeAheadOfTimeWillHandleBackChanged
import com.zhuinden.simplestackextensions.navigatorktx.androidContentFrame
import com.zhuinden.simplestackextensions.servicesktx.get
import com.zhuinden.simplestackftuecomposesample.features.login.LoginKey
import com.zhuinden.simplestackftuecomposesample.features.profile.ProfileKey

class MainActivity : AppCompatActivity(), AsyncStateChanger.NavigationHandler {

    private lateinit var composeStateChanger: ComposeStateChanger
    private lateinit var authenticationManager: AuthenticationManager

    private lateinit var backstack: Backstack

    private val backPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            backstack.goBack()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onBackPressedDispatcher.addCallback(backPressedCallback) // this is the only way to make Compose BackHandler work reliably for now

        composeStateChanger = ComposeStateChanger()

        val app = application as CustomApplication
        val globalServices = app.globalServices

        authenticationManager = globalServices.get()

        backstack = Navigator.configure()
            .setBackHandlingModel(BackHandlingModel.AHEAD_OF_TIME)
            .setStateChanger(AsyncStateChanger(this))
            .setScopedServices(ServiceProvider())
            .setGlobalServices(globalServices)
            .install(
                this, androidContentFrame, History.of(
                    when {
                        authenticationManager.isAuthenticated() -> ProfileKey(authenticationManager.getAuthenticatedUser())
                        else -> LoginKey()
                    }
                )
            )

        backPressedCallback.isEnabled = backstack.willHandleAheadOfTimeBack()

        backstack.observeAheadOfTimeWillHandleBackChanged(this) {
            backPressedCallback.isEnabled = it
        }

        setContent {
            BackstackProvider(backstack) {
                MaterialTheme {
                    Surface(color = MaterialTheme.colors.background) {
                        Box(Modifier.fillMaxSize()) {
                            composeStateChanger.RenderScreen()
                        }
                    }
                }
            }
        }
    }

    override fun onNavigationEvent(stateChange: StateChange, completionCallback: StateChanger.Callback) {
        composeStateChanger.onNavigationEvent(stateChange, completionCallback)
    }

    override fun onDestroy() {
        if (isFinishing) {
            authenticationManager.clearRegistration() // FIXME this actually no longer happens since Android 12 o-o
        }

        super.onDestroy()
    }
}