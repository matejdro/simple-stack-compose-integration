package com.zhuinden.simplestackcomposesimpleexample

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.ScopeKey
import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackcomposedogexample.CommonSharedService
import com.zhuinden.simplestackcomposeintegration.services.rememberService
import com.zhuinden.simplestackextensions.servicesktx.add
import com.zhuinden.simplestackextensions.servicesktx.rebind
import kotlinx.parcelize.Parcelize

class FirstModel(
    private val backstack: Backstack
): FirstScreen.ActionHandler {
    override fun navigateToSecond() {
        backstack.goTo(SecondKey())
    }
}

@Immutable
@Parcelize
data class FirstKey(val title: String) : ComposeKey() {
    constructor() : this("Hello First Screen!")

    @Composable
    override fun ScreenComposable(modifier: Modifier) {
        FirstScreen(this, title, modifier)
    }

    override fun bindServices(serviceBinder: ServiceBinder) {
        with(serviceBinder) {
            val firstModel = FirstModel(backstack)

            add(firstModel)
            rebind<FirstScreen.ActionHandler>(firstModel)

            val commonSharedService = CommonSharedService()
            add(commonSharedService)
            rebind<CommonSharedService>(commonSharedService)
        }
    }
}

class FirstScreen private constructor() {
    fun interface ActionHandler {
        fun navigateToSecond()
    }

    companion object {
        @Composable
        @SuppressLint("ComposableNaming")
        operator fun invoke(scope: ScopeKey, title: String, modifier: Modifier = Modifier) {
            val eventHandler = scope.rememberService<ActionHandler>()
            val commonSharedService = scope.rememberService<CommonSharedService>()
            val color = commonSharedService.color.collectAsState().value

            Column(
                modifier = modifier.fillMaxSize().background(color),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Color service: $commonSharedService")

                Button(onClick = {
                    // onClick is not a composition context, must get ambients above
                    eventHandler.navigateToSecond()
                }, content = {
                    Text(title)
                })
            }
        }
    }
}

@Preview
@Composable
fun FirstScreenPreview() {
    MaterialTheme {
        FirstScreen(ScopeKey { "" },"This is a preview")
    }
}
