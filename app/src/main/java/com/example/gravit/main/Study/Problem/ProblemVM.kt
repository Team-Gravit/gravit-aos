package com.example.gravit.main.Study.Problem

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProblemViewModel : ViewModel() {

    data class UiState(
        val selectedIndex: Int? = null,
        val submitted: Boolean = false,
        val isCorrect: Boolean? = null,
        val shortText: String = "",
        val removedFromWrongNoteMap: Map<Int, Boolean> = emptyMap()
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    fun select(index: Int?) {
        _uiState.update {
            if (it.submitted) it else it.copy(selectedIndex = index)
        }
    }

    fun updateText(text: String) {
        _uiState.update {
            it.copy(shortText = text)
        }
    }

    fun submit(isCorrect: Boolean) {
        _uiState.update {
            it.copy(
                submitted = true,
                isCorrect = isCorrect
            )
        }
    }

    fun removeFromWrongNote(problemId: Int) {
        _uiState.update { state ->
            state.copy(
                removedFromWrongNoteMap = state.removedFromWrongNoteMap + (problemId to true)
            )
        }
    }

    fun isRemovedFromWrongNote(problemId: Int): Boolean {
        return _uiState.value.removedFromWrongNoteMap[problemId] == true
    }

    fun reset() {
        _uiState.value = UiState()
    }
}