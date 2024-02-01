//package com.example.timer.presentation
//
//import com.example.timer.MainDispatcherRule
//import com.example.timer.feature_timer.ClockTimer
//import com.example.timer.feature_timer.TimerState
//import com.example.timer.feature_timer.data.TimerRepository
//import com.example.timer.feature_timer.presentation.TimerViewModel
//import io.mockk.every
//import io.mockk.junit5.MockKExtension
//import io.mockk.mockk
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.flow.collectLatest
//import kotlinx.coroutines.flow.flowOf
//import kotlinx.coroutines.test.StandardTestDispatcher
//import kotlinx.coroutines.test.TestDispatcher
//import kotlinx.coroutines.test.advanceUntilIdle
//import kotlinx.coroutines.test.runTest
//import kotlinx.coroutines.test.setMain
//import org.junit.Assert.assertEquals
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//import org.junit.jupiter.api.extension.ExtendWith
//
//@ExtendWith(MockKExtension::class)
//class TimerViewModelTest {
//
//    lateinit var timerViewModel: TimerViewModel
//
//    private val testDispatcher: TestDispatcher = StandardTestDispatcher()
//
//    @get:Rule
//    val mainDispatcherRule = MainDispatcherRule()
//
//    @OptIn(ExperimentalCoroutinesApi::class)
//    @Before
//    fun setup() {
//
//        val mockTimerRepository = mockk<TimerRepository>()
//
//        // Capture lambda argument passed to launchIn
//        every { mockTimerRepository.getTimersFlow() } returns flowOf(emptyList())
//
//        Dispatchers.setMain(testDispatcher)
//
//        timerViewModel = TimerViewModel(
//            timerRepository = mockTimerRepository,
//            defaultDispatcher = testDispatcher
//        )
//    }
//
//    @Test
//    fun setDismissPercentage_shouldSetCorrectPercentage() {
//    // Given
//        val expectedPercentage = 50f
//        val inputPercentage = 50f
//
//    // When
//        timerViewModel.setDismissPercentage(inputPercentage)
//
//    // Then
//        assertEquals(expectedPercentage, timerViewModel.uiState.value.dismissPercentage)
//    }
//
//    @Test
//    fun setDismissPercentage_shouldCapPercentageAt100() {
//    // Given
//        val expectedPercentage = 100f
//        val inputPercentage = 150f
//
//    // When
//        timerViewModel.setDismissPercentage(inputPercentage)
//
//    // Then
//        assertEquals(expectedPercentage, timerViewModel.uiState.value.dismissPercentage)
//    }
//
//    @Test
//    fun setDismissPercentage_shouldCapPercentageAt0() {
//    // Given
//        val expectedPercentage = 0f
//        val inputPercentage = -50f
//
//    // When
//        timerViewModel.setDismissPercentage(inputPercentage)
//
//    // Then
//        assertEquals(expectedPercentage, timerViewModel.uiState.value.dismissPercentage)
//    }
//
//    @Test
//    fun addSecondsToTimer_runningTimer() {
//        runTest {
//
//        // Given
//            val inputDuration = 30
//            ClockTimer.timerState.value = TimerState.Running
//
//        // When
//            timerViewModel.addSecondsToTimer(inputDuration)
//            advanceUntilIdle()
//        // Then
//            // Verify that secondsRemaining is updated
//            assertEquals(inputDuration, ClockTimer.secondsRemaining.intValue)
//
//            // Verify that resetMainTimeInput is toggled
//            assertEquals(!timerViewModel.uiState.value.resetMainTimeInput, false)
//
//            // Verify that TimerStart event is emitted
//            timerViewModel.eventFlow.collectLatest { event ->
//                assertEquals(event, TimerViewModel.UiEvent.StartTimer)
//            }
//        }
//    }
//    @Test
//    fun addSecondsToTimer_nonRunningTimer() {
//        runTest {
//        // Given
//            val inputDuration = 30
//
//        // When
//            timerViewModel.addSecondsToTimer(inputDuration)
//
//        // Then
//            assertEquals(inputDuration, ClockTimer.secondsRemaining.intValue)
//
//            // Verify that resetMainTimeInput is toggled
//            assertEquals(!timerViewModel.uiState.value.resetMainTimeInput, false)
//
//            // Verify that TimerStart event is emitted
//            timerViewModel.eventFlow.collectLatest{event ->
//                assertEquals(event, TimerViewModel.UiEvent.StartTimer)
//            }
//        }
//    }
//}