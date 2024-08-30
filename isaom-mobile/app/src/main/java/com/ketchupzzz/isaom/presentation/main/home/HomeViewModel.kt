package com.ketchupzzz.isaom.presentation.main.home

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.ketchupzzz.isaom.models.SourceAndTargets
import com.ketchupzzz.isaom.utils.UiState
import com.ketchupzzz.isaom.repository.auth.AuthRepository
import com.ketchupzzz.isaom.repository.subject.SubjectRepository
import com.ketchupzzz.isaom.repository.translator.TranslatorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject


@HiltViewModel

class HomeViewModel @Inject constructor(
     private val authRepository: AuthRepository,
     private val translatorRepository: TranslatorRepository,
     private val subjectRepository: SubjectRepository
) : ViewModel() {
    var state by mutableStateOf(HomeState())
     init {
          state = state.copy(
               users = authRepository.getUsers()
          )
     }
     fun events(events: HomeEvents) {
          when(events) {
               is HomeEvents.OnTextChanged -> {
                    state = state.copy(
                         text = events.text
                    )
               }
               is HomeEvents.OnTranslateText -> translateText(events.text,events.source,events.target)
               is HomeEvents.OnGetSubjects -> getSubjects(events.sectionID)
              is HomeEvents.OnSourceChanged -> state = state.copy(
                  source = events.source
              )
              is HomeEvents.OnTargetChanged -> state = state.copy(
                  target = events.target
              )

              is HomeEvents.OnSwitchLanguage -> state = state.copy(
                  source = events.target,
                  target = events.source,
              )

              is HomeEvents.OnTransformImageToText -> transformImageToText(events.context,events.uri)
          }
     }

    private fun transformImageToText(context: Context, uri: Uri) {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val image: InputImage
        try {
            image = InputImage.fromFilePath(context, uri)
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    state = state.copy(
                        text = visionText.text
                    )
                    events(HomeEvents.OnTranslateText(
                        state.text,
                        state.source,
                        state.target
                    ))
                }
                .addOnFailureListener { e ->
                    state = state.copy(error = e.message)
                }
        } catch (e: IOException) {
            state = state.copy(error = e.message)
        }
    }

    private fun getSubjects(sectionID: String) {
          viewModelScope.launch {
               subjectRepository.getSubjectBySectionID(sectionID) {
                   state =  when(it) {
                         is UiState.Error -> state.copy(
                              isLoading = false,
                              error = it.message
                         )
                         is UiState.Loading -> state.copy(
                              isLoading = true,
                              error = null,
                         )
                         is UiState.Success -> state.copy(
                              isLoading = false,
                              error = null,
                              subjects = it.data
                         )
                    }
               }
          }
     }


     private fun translateText(text: String, source: SourceAndTargets, target: SourceAndTargets) {
          viewModelScope.launch {
               translatorRepository.translateText(text,source.code,target.code) { uiState ->
                    state = when (uiState) {
                        is UiState.Loading -> {
                            state.copy(isTranslating = true, error = null, translation = "Loading..")
                        }
                         is UiState.Error -> {
                              state.copy(isTranslating = false, error = uiState.message, translation = uiState.message)
                         }
                        is UiState.Success -> {
                            state.copy(
                                isTranslating = false,
                                translation = uiState.data.translation_text,
                                error = null
                            )
                        }
                    }
               }
          }
     }
}