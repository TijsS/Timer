package com.example.timer.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HorizontalPagerIndicator(pagerState: PagerState){
    Row(
        Modifier
            .height(50.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(2) { iteration ->
            val color = if ( (pagerState.currentPage - 1) % 2 == iteration ) Color.DarkGray else Color.LightGray
            Box(
                modifier = Modifier
                    .padding(horizontal = 6.dp)
                    .background(color, CircleShape)
                    .size(6.dp)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VerticalPagerIndicator(pagerState: PagerState){
    Column(
        Modifier
            .width(50.dp)
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        repeat(2) { iteration ->
            val color = if ( (pagerState.currentPage - 1) % 2 == iteration ) Color.DarkGray else Color.LightGray
            Box(
                modifier = Modifier
                    .padding(vertical = 6.dp)
                    .background(color, CircleShape)
                    .size(6.dp)
            )
        }
    }
}