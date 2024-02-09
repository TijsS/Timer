package com.example.timer.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.timer.ui.theme.TimerTheme
import com.example.timer.ui.theme.Values.SMALL_PADDING

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HorizontalPagerIndicator(pagerState: PagerState) {
    Row(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        repeat(2) { iteration ->
            val color = if (pagerState.currentPage == iteration) Color.LightGray else Color.DarkGray
            Box(
                modifier = Modifier
                    .padding(horizontal = SMALL_PADDING)
                    .background(color, CircleShape)
                    .size(6.dp)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VerticalPagerIndicator(pagerState: PagerState) {
    Column(
        Modifier
            .width(50.dp)
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        (1 downTo 0).forEach { iteration ->
            val color = if (pagerState.currentPage == iteration) Color.LightGray else Color.DarkGray
            Box(
                modifier = Modifier
                    .padding(vertical = SMALL_PADDING)
                    .background(color, CircleShape)
                    .size(6.dp)
            )
        }
    }
}



@OptIn(ExperimentalFoundationApi::class)
@Composable
@Preview(showBackground = true)
fun HorizontalPagerIndicatorPreview() {
    TimerTheme {
        HorizontalPagerIndicator(pagerState = rememberPagerState(pageCount = {1}))
    }
}
@OptIn(ExperimentalFoundationApi::class)
@Composable
@Preview(showBackground = true, device = Devices.AUTOMOTIVE_1024p)
fun VerticalPagerIndicatorPreview() {
    TimerTheme {
        VerticalPagerIndicator(pagerState = rememberPagerState(pageCount = {1}))
    }
}