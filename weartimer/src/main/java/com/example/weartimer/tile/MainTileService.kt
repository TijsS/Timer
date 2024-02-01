package com.example.weartimer.tile

//
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.protolayout.ActionBuilders
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.ModifiersBuilders
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.TimelineBuilders
import androidx.wear.protolayout.material.Text
import androidx.wear.protolayout.material.layouts.PrimaryLayout
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.TileBuilders
import com.example.weartimer.ClockTimer
import com.example.weartimer.timeRemainingToClockFormatWithoutSeconds
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.tools.LayoutRootPreview
import com.google.android.horologist.compose.tools.buildDeviceParameters
import com.google.android.horologist.tiles.SuspendingTileService


private const val RESOURCES_VERSION = "0"

@OptIn(ExperimentalHorologistApi::class)
class TimerTileService : SuspendingTileService() {

    override suspend fun resourcesRequest(
        requestParams: RequestBuilders.ResourcesRequest
    ): ResourceBuilders.Resources {
        return ResourceBuilders.Resources.Builder()
            .build()
    }

    override suspend fun tileRequest(
        requestParams: RequestBuilders.TileRequest
    ): TileBuilders.Tile {
        val singleTileTimeline: TimelineBuilders.Timeline = TimelineBuilders.Timeline.Builder()
            .addTimelineEntry(
                TimelineBuilders.TimelineEntry.Builder()
                    .setLayout(
                        LayoutElementBuilders.Layout.Builder()
                            .setRoot(tileLayout(this))
                            .build()
                    )
                    .build()
            )
            .build()

        return TileBuilders.Tile.Builder()
            .setTileTimeline(singleTileTimeline)
            .setFreshnessIntervalMillis(30 * 1000)
            .build()
    }
}
    private fun tileLayout(context: Context): PrimaryLayout {

        return PrimaryLayout.Builder(buildDeviceParameters(context.resources))
            .setContent(
                Text.Builder(context, ClockTimer.secondsRemaining.value.timeRemainingToClockFormatWithoutSeconds() )
                    .setModifiers(ModifiersBuilders.Modifiers.Builder().setClickable(launchAppClickable(openApp())).build())
                    .build()
            )
            .build()
    }

    internal fun launchAppClickable(
        androidActivity: ActionBuilders.AndroidActivity
    ) = ModifiersBuilders.Clickable.Builder()
        .setOnClick(
            ActionBuilders.LaunchAction.Builder()
                .setAndroidActivity(androidActivity)
                .build()
        )
        .build()

    internal fun openApp() = ActionBuilders.AndroidActivity.Builder()
        .setPackageName("com.example.weartimer")
        .setClassName("com.example.weartimer.presentation.MainActivity")
        .build()


@Preview(
    device = Devices.WEAR_OS_SMALL_ROUND,
    showSystemUi = true,
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun TilePreview() {
    LayoutRootPreview(root = tileLayout(LocalContext.current))
}

@Preview(
    device = Devices.WEAR_OS_SMALL_ROUND,
    showSystemUi = true,
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun TilePreview2() {
    LayoutRootPreview(root = tileLayout(LocalContext.current))
}