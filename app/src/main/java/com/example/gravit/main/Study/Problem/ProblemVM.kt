package com.example.gravit.main.Study.Problem

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProblemViewModel : ViewModel() {

    data class AnswerState(
        val selectedIndex: Int? = null,
        val submitted: Boolean = false,
        val isCorrect: Boolean? = null,
        val shortText: String = ""
    )

    data class UiState(
        val answers: Map<Long, AnswerState> = emptyMap(),
        val removedFromWrongNoteMap: Map<Long, Boolean> = emptyMap()
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    fun select(problemId: Long, index: Int?) {
        _uiState.update { state ->
            val currentAnswer = state.answers[problemId] ?: AnswerState()

            if (currentAnswer.submitted) state
            else state.copy(answers = state.answers + (problemId to currentAnswer.copy(selectedIndex = index)))
        }
    }
    fun updateText(problemId: Long, text: String) {
        _uiState.update { state ->
            val currentAnswer = state.answers[problemId] ?: AnswerState()

            if (currentAnswer.submitted) state
            else state.copy(answers = state.answers + (problemId to currentAnswer.copy(shortText = text)))
        }
    }
    fun submit(problemId: Long, isCorrect: Boolean) {
        _uiState.update { state ->
            val currentAnswer = state.answers[problemId] ?: AnswerState()
            state.copy(answers = state.answers + (problemId to currentAnswer.copy(submitted = true, isCorrect = isCorrect)))
        }
    }
    fun removeFromWrongNote(problemId: Long) {
        _uiState.update { state ->
            state.copy(
                removedFromWrongNoteMap = state.removedFromWrongNoteMap + (problemId to true)
            )
        }
    }
    fun isRemovedFromWrongNote(problemId: Long): Boolean {
        return _uiState.value.removedFromWrongNoteMap[problemId] == true
    }

    fun reset() {
        _uiState.value = UiState()
    }
}