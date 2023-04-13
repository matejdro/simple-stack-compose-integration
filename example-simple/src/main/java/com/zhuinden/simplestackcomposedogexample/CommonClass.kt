package com.zhuinden.simplestackcomposedogexample

import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Scoped service that is injected into every screen, but it should be a different instance for
 * every screen
 */
class CommonSharedService {
    var color = MutableStateFlow(Color.Red)
}
