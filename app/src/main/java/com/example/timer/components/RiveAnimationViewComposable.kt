package com.example.timer.components

import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import app.rive.runtime.kotlin.RiveAnimationView
import app.rive.runtime.kotlin.core.Fit

@Composable
fun ComposableRiveAnimationView(
    modifier: Modifier = Modifier,
    @RawRes animation: Int,
    stateMachineName: String? = null,
    alignment: app.rive.runtime.kotlin.core.Alignment = app.rive.runtime.kotlin.core.Alignment.CENTER,
    fit: Fit = Fit.CONTAIN,
    onInit: (RiveAnimationView) -> Unit
) {
    AndroidView(
        factory = { context ->
            RiveAnimationView(context).also {
                it.setRiveResource(
                    resId = animation,
                    stateMachineName = stateMachineName,
                    autoplay = true,
                    alignment = alignment,
                    fit = fit,
                )
            }
        },
        update = { view -> onInit(view) },
        modifier = modifier
    )
}