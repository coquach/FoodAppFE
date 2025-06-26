package com.se114.foodapp.ui.screen.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.dto.ApiResponse
import com.se114.foodapp.data.model.DailyReport
import com.se114.foodapp.data.model.MenuReport
import com.se114.foodapp.data.model.MonthlyReport
import com.se114.foodapp.domain.use_case.report.GetDailyReportUseCase
import com.se114.foodapp.domain.use_case.report.GetMenuReportUseCase
import com.se114.foodapp.domain.use_case.report.GetMonthlyReportUseCase
import com.se114.foodapp.ui.screen.statistics.StaticsState.DailyReportState
import com.se114.foodapp.ui.screen.statistics.StaticsState.Event.ShowErrorToast
import com.se114.foodapp.ui.screen.statistics.StaticsState.MenuReportState
import com.se114.foodapp.ui.screen.statistics.StaticsState.MonthlyReportState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val getMonthlyReportUseCase: GetMonthlyReportUseCase,
    private val getDailyReportUseCase: GetDailyReportUseCase,
    private val getMenuReportUseCase: GetMenuReportUseCase,
) : ViewModel() {

    private val toMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
    private val toYear = Calendar.getInstance().get(Calendar.YEAR)
    private val fromMonthYear = adjustMonthYear(toMonth -1, toYear)
    private val fromMonth = fromMonthYear.first
    private val fromYear = fromMonthYear.second


    private val _uiState = MutableStateFlow(
        StaticsState.UiState(
            fromMonth = fromMonth,
            fromYear = fromYear,
            toMonth = toMonth,
            toYear = toYear
        )
    )
    val uiState get() = _uiState.asStateFlow()

    private val _event = Channel<StaticsState.Event>()
    val event get() = _event.receiveAsFlow()




    fun getMonthlyReports() {
        viewModelScope.launch {
            getMonthlyReportUseCase.invoke(
                fromYear =_uiState.value.fromYear,
                fromMonth = _uiState.value.fromMonth,
               toYear = _uiState.value.toYear,
                toMonth = _uiState.value.toMonth
            ).collect { result ->
                when (result) {
                    is ApiResponse.Loading -> {
                        _uiState.update { it.copy(monthlyReportState = MonthlyReportState.Loading) }
                    }

                    is ApiResponse.Success -> {
                        _uiState.update {
                            it.copy(
                                monthlyReportState = MonthlyReportState.Success,
                                monthlyReports = result.data
                            )
                        }
                    }

                    is ApiResponse.Failure -> {
                        _uiState.update {
                            it.copy(
                                monthlyReportState = MonthlyReportState.Error(
                                    result.errorMessage
                                )
                            )
                        }
                    }

                }
            }
        }
    }

    fun getDailyReports() {
        viewModelScope.launch {
            getDailyReportUseCase.invoke(year = _uiState.value.selectedYear, month =  _uiState.value.selectedMonth)
                .collect { result ->
                    when (result) {
                        is ApiResponse.Loading -> {
                            _uiState.update { it.copy(dailyReportState = DailyReportState.Loading) }
                        }

                        is ApiResponse.Success -> {
                            _uiState.update {
                                it.copy(
                                    dailyReportState = DailyReportState.Success,
                                    dailyReports = result.data
                                )
                            }
                        }

                        is ApiResponse.Failure -> {
                            _uiState.update {
                                it.copy(
                                    dailyReportState = DailyReportState.Error(
                                        result.errorMessage
                                    )
                                )
                            }
                        }
                    }
                }
        }
    }

    fun getMenuReports() {
        viewModelScope.launch {
            getMenuReportUseCase.invoke(_uiState.value.selectedYear, _uiState.value.selectedMonth)
                .collect { result ->
                    when (result) {
                        is ApiResponse.Loading -> {
                            _uiState.update { it.copy(menuReportState = MenuReportState.Loading) }
                        }

                        is ApiResponse.Success -> {
                            _uiState.update {
                                it.copy(
                                    menuReportState = MenuReportState.Success,
                                    menuReports = result.data
                                )
                            }
                        }

                        is ApiResponse.Failure -> {
                            _uiState.update {
                                it.copy(
                                    menuReportState = MenuReportState.Error(
                                        result.errorMessage
                                    )
                                )
                            }
                        }
                    }
                }
        }
    }

    fun onAction(action: StaticsState.Action) {
        when (action) {
            is StaticsState.Action.OnChangeFromMonthYear -> {
                if (isMonthYearBeforeOrEqual(action.year, action.month, _uiState.value.toYear, _uiState.value.toMonth)) {
                    _uiState.update { it.copy(fromMonth = action.month, fromYear = action.year) }
                    getMonthlyReports()
                } else {
                    viewModelScope.launch {
                        _event.send(ShowErrorToast("Thời gian không hợp lệ"))
                    }
                }

            }

            is StaticsState.Action.OnChangeToMonthYear -> {
                val now = Calendar.getInstance()
                val currentYear = now.get(Calendar.YEAR)
                val currentMonth = now.get(Calendar.MONTH) + 1 // vì Calendar.MONTH bắt đầu từ 0

                if (
                    isMonthYearAfterOrEqual(action.year, action.month, _uiState.value.fromYear, _uiState.value.fromMonth) &&
                    isMonthYearBeforeOrEqual(action.year, action.month, currentYear, currentMonth)
                ) {
                    _uiState.update { it.copy(toMonth = action.month, toYear = action.year) }
                    getMonthlyReports()
                } else {
                    viewModelScope.launch {
                        _event.send(ShowErrorToast("Thời gian không hợp lệ"))
                    }
                }
            }

            is StaticsState.Action.OnChangeSelectedMonthYear -> {
                val (newMonth, newYear) = adjustMonthYear(action.month, action.year)
                val now = Calendar.getInstance()
                val nowMonth = now.get(Calendar.MONTH) + 1
                val nowYear = now.get(Calendar.YEAR)

                if (isMonthYearBeforeOrEqual(newYear, newMonth, nowYear, nowMonth)) {
                    _uiState.update {
                        it.copy(
                            selectedMonth = newMonth,
                            selectedYear = newYear
                        )
                    }
                    getDailyReports()
                    getMenuReports()
                } else {
                    viewModelScope.launch {
                        _event.send(ShowErrorToast("Thời gian vượt hiện tại"))
                    }
                }
            }

            StaticsState.Action.GoToNotification -> {
                viewModelScope.launch {
                    _event.send(StaticsState.Event.GoToNotification)
                }
            }
            StaticsState.Action.GetDailyReport -> {
                getDailyReports()
            }
            StaticsState.Action.GetMenuReport -> {
                getMenuReports()
            }
            StaticsState.Action.GetMonthlyReport -> {
                getMonthlyReports()
            }

        }
    }


}

object StaticsState {
    data class UiState(
        val monthlyReports: List<MonthlyReport> = emptyList(),
        val monthlyReportState: MonthlyReportState = MonthlyReportState.Loading,
        val dailyReports: List<DailyReport> = emptyList(),
        val dailyReportState: DailyReportState = DailyReportState.Loading,
        val menuReports: List<MenuReport> = emptyList(),
        val menuReportState: MenuReportState = MenuReportState.Loading,
        val fromMonth: Int,
        val fromYear: Int,
        val toMonth: Int,
        val toYear: Int,
        val selectedMonth: Int = Calendar.getInstance().get(Calendar.MONTH) + 1,
        val selectedYear: Int = Calendar.getInstance().get(Calendar.YEAR),


        )

    sealed interface MonthlyReportState {
        data object Loading : MonthlyReportState
        data class Error(val message: String) : MonthlyReportState
        data object Success : MonthlyReportState

    }

    sealed interface DailyReportState {
        data object Loading : DailyReportState
        data class Error(val message: String) : DailyReportState
        data object Success : DailyReportState
    }

    sealed interface MenuReportState {
        data object Loading : MenuReportState
        data class Error(val message: String) : MenuReportState
        data object Success : MenuReportState
    }

    sealed interface Event {
        data object GoToNotification : Event
        data class ShowErrorToast(val message: String) : Event

    }

    sealed interface Action {
        data object GoToNotification : Action
        data class OnChangeFromMonthYear(val year: Int, val month: Int) : Action
        data class OnChangeToMonthYear(val year: Int, val month: Int) : Action
        data class OnChangeSelectedMonthYear(val year: Int, val month: Int) : Action
        data object GetMonthlyReport : Action
        data object GetDailyReport : Action
        data object GetMenuReport : Action


    }
}


fun adjustMonthYear(currentMonth: Int, currentYear: Int): Pair<Int, Int> {
    var newMonth = currentMonth
    var newYear = currentYear

    // Lùi hoặc tiến nhiều tháng
    while (newMonth < 1) {
        newMonth += 12
        newYear -= 1
    }

    while (newMonth > 12) {
        newMonth -= 12
        newYear += 1
    }

    return newMonth to newYear
}

fun isMonthYearBeforeOrEqual(y1: Int, m1: Int, y2: Int, m2: Int): Boolean {
    return y1 < y2 || (y1 == y2 && m1 <= m2)
}

fun isMonthYearAfterOrEqual(y1: Int, m1: Int, y2: Int, m2: Int): Boolean {
    return y1 > y2 || (y1 == y2 && m1 >= m2)
}