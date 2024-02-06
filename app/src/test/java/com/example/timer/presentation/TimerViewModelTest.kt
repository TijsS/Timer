package com.example.timer.presentation

import com.example.timer.feature_timer.ClockTimer
import com.example.timer.feature_timer.TimerState
import com.example.timer.feature_timer.data.TimerRepository
import com.example.timer.feature_timer.presentation.TimerViewModel
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class TimerViewModelTest {

    private lateinit var timerViewModel: TimerViewModel
    private val dispatcher: TestDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(dispatcher)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {

        ClockTimer.timerState.value = TimerState.Stopped
        ClockTimer.secondsRemaining.intValue = 0

        val mockTimerRepository = mockk<TimerRepository>()

        // Capture lambda argument passed to launchIn
        every { mockTimerRepository.getTimersFlow() } returns flowOf(emptyList())

        Dispatchers.setMain(dispatcher)

        timerViewModel = TimerViewModel(
            timerRepository = mockTimerRepository,
            externalScope = testScope
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun setDismissPercentage_shouldSetCorrectPercentage() {
    // Given
        val expectedPercentage = 50f
        val inputPercentage = 50f

    // When
        timerViewModel.setDismissPercentage(inputPercentage)

    // Then
        assertEquals(expectedPercentage, timerViewModel.uiState.value.dismissPercentage)
    }

    @Test
    fun setDismissPercentage_shouldCapPercentageAt100() {
    // Given
        val expectedPercentage = 100f
        val inputPercentage = 150f

    // When
        timerViewModel.setDismissPercentage(inputPercentage)

    // Then
        assertEquals(expectedPercentage, timerViewModel.uiState.value.dismissPercentage)
    }

    @Test
    fun setDismissPercentage_shouldCapPercentageAt0() {
    // Given
        val expectedPercentage = 0f
        val inputPercentage = -50f

    // When
        timerViewModel.setDismissPercentage(inputPercentage)

    // Then
        assertEquals(expectedPercentage, timerViewModel.uiState.value.dismissPercentage)
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun addSecondsToTimer_runningTimer() {
        testScope.runTest {
        // Given
            val inputDuration = 30
            ClockTimer.timerState.value = TimerState.Running


            // Collect events emitted by eventFlow
            val collectedEvents = mutableListOf<TimerViewModel.UiEvent>()
            val collecting = launch {
                timerViewModel.eventFlow.collectLatest { event ->
                    collectedEvents.add(event)
                }
            }
            advanceUntilIdle()

        // When
            timerViewModel.addSecondsToTimer(inputDuration)
            advanceUntilIdle()

        // Then
            // Verify that secondsRemaining is updated
            assertEquals(inputDuration, ClockTimer.secondsRemaining.intValue)

            // Verify that resetMainTimeInput is toggled
            assertEquals(!timerViewModel.uiState.value.resetMainTimeInput, false)

            // Verify that TimerStart event is emitted and no other events are emitted
            assertEquals(collectedEvents.single(), TimerViewModel.UiEvent.StartTimer)
            assertEquals(collectedEvents.size, 1)

            collecting.cancelAndJoin()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun addSecondsToTimer_nonRunningTimer() {
        testScope.runTest {
            // Given
            val inputDuration = 30
            ClockTimer.timerState.value = TimerState.Stopped

            // Collect events emitted by eventFlow
            val collectedEvents = mutableListOf<TimerViewModel.UiEvent>()
            val collecting = launch {
                timerViewModel.eventFlow.collectLatest { event ->
                    collectedEvents.add(event)
                }
            }
            advanceUntilIdle()

            // When
            timerViewModel.addSecondsToTimer(inputDuration)
            advanceUntilIdle()

            // Then
            // Verify that secondsRemaining is updated
            assertEquals(inputDuration, ClockTimer.secondsRemaining.intValue)

            // Verify that resetMainTimeInput is toggled
            assertEquals(!timerViewModel.uiState.value.resetMainTimeInput, false)

            // Verify that no events are emitted
            assertEquals(collectedEvents.size, 0)

            collecting.cancelAndJoin()
        }
    }
}