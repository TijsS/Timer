package com.example.timer.components

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.timer.ui.theme.TimerTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InfiniteCircularList(
    width: Dp,
    itemHeight: Dp,
    numberOfDisplayedItems: Int = 3,
    items: List<Int>,
    initialItem: Int,
    itemScaleFact: Float = 1.5f,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    selectedTextColor: Color = MaterialTheme.colorScheme.onSurface,
    resetInput: Boolean,
    onItemSelected: (index: Int, item: Int) -> Unit = { _, _ -> }
) {

    //To hide the switch from index 0 to the halfway index on startup
    val reshuffledItems = mutableListOf<Int>().apply {
        addAll(items)
        add(0, items.last())
        removeLast()
    }

    val itemHalfHeight = LocalDensity.current.run { itemHeight.toPx() / 2f }
    val scrollState = rememberLazyListState(0)
    val scrollShadow = MaterialTheme.colorScheme.surface

    var manualResetInput by remember {
        mutableStateOf(false)
    }

    var lastSelectedIndex by remember {
        mutableIntStateOf(0)
    }

    val itemsState by remember {
        mutableStateOf(reshuffledItems)
    }

    LaunchedEffect(resetInput, manualResetInput) {
        var targetIndex = reshuffledItems.indexOf(initialItem) - 1
        targetIndex += ((Int.MAX_VALUE / 2) / reshuffledItems.size) * reshuffledItems.size
        lastSelectedIndex = targetIndex
        scrollState.scrollToItem(targetIndex)
    }

    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(width)
            .height(itemHeight * numberOfDisplayedItems)
            .clickable {
                manualResetInput = !manualResetInput
            }
            .drawWithContent {
                drawContent()
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            scrollShadow,
                            Color.Transparent,
                            scrollShadow,
                        ),
                        startY = 25f,
                        endY = size.height - 25f
                    ),
                )
            },
        state = scrollState,
        flingBehavior = rememberSnapFlingBehavior(
            lazyListState = scrollState
        )
    ) {
        items(
            count = Int.MAX_VALUE,
            itemContent = { i ->
                val item: Int = itemsState[(i % itemsState.size)] as Int
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .height(itemHeight)
                        .fillMaxWidth()
                        .onGloballyPositioned { coordinates ->
                            val y = coordinates.positionInParent().y - itemHalfHeight
                            val parentHalfHeight =
                                (coordinates.parentCoordinates?.size?.height ?: 0) / 2f
                            val isSelected =
                                (y > parentHalfHeight - itemHalfHeight && y < parentHalfHeight + itemHalfHeight)
                            if (isSelected && lastSelectedIndex != i) {
                                onItemSelected(i % itemsState.size, item)
                                lastSelectedIndex = i
                            }
                        }

                ) {
                    Text(
                        text = if(item.toString().length == 1) "0$item" else "$item",
                        style = MaterialTheme.typography.titleLarge,
                        color = if (lastSelectedIndex == i) selectedTextColor else textColor,
                        //to hide the initial switch to the half size index
                        fontSize = if (lastSelectedIndex == i && lastSelectedIndex != 0 || i == 1) {
                            MaterialTheme.typography.titleLarge.fontSize * itemScaleFact
                        } else {
                            MaterialTheme.typography.titleLarge.fontSize
                        }
                    )
                }
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Preview(showBackground = true)
@Composable
fun ScrollPickerPreview() {
    TimerTheme {
        val x = (0..59).toMutableList()
        x.add(0, 59)
        InfiniteCircularList(
            width = 50.dp,
            itemHeight = 40.dp,
            items = x,
            initialItem = 0,
            textColor = MaterialTheme.colorScheme.onSurface,
            selectedTextColor = MaterialTheme.colorScheme.onSurface,
            resetInput = false,
            onItemSelected = { _, _ -> }
        )
    }
}