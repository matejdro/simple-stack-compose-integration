package com.zhuinden.simplestackcomposesimpleexample

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.zhuinden.simplestack.ScopeKey
import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackcomposedogexample.CommonSharedService
import com.zhuinden.simplestackcomposeintegration.services.rememberService
import com.zhuinden.simplestackextensions.servicesktx.add
import com.zhuinden.simplestackextensions.servicesktx.rebind
import kotlinx.parcelize.Parcelize


@Immutable
@Parcelize
data object SecondKey: ComposeKey() {
    operator fun invoke() = this

    @Composable
    override fun ScreenComposable(modifier: Modifier) {
        SecondScreen(this, modifier)
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

@Composable
fun SecondScreen(scope: ScopeKey, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val commonSharedService = scope.rememberService<CommonSharedService>()
    val color = commonSharedService.color.collectAsState().value

    Column(
        modifier = modifier.fillMaxSize().background(color),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Color service: $commonSharedService")

        Button(
            onClick = {
                commonSharedService.color.value = Color.Green
            },
            content = {
                Text("Turn into Green")
            },
        )
    }
}

@Preview
@Composable
fun SecondScreenPreview() {
    MaterialTheme {
        SecondScreen(ScopeKey { "" })
    }
}
