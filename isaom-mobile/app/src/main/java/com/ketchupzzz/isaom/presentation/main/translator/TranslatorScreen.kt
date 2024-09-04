package com.ketchupzzz.isaom.presentation.main.translator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwitchRight
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ketchupzzz.isaom.presentation.main.home.CameraButton
import com.ketchupzzz.isaom.presentation.main.home.HomeEvents
import com.ketchupzzz.isaom.presentation.main.home.HomeState

import com.ketchupzzz.isaom.ui.custom.IsaomDropdownMenu
import com.ketchupzzz.isaom.ui.custom.PrimaryButton


@Composable
fun TranslatorScreen(
    modifier: Modifier = Modifier,
    state: TranslatorState,
    events: (TranslatorEvents) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        item {
            TranslatorLayout(state =state , events = events)
        }
        item {
            Text(
                text = "History",
                modifier = modifier.padding(8.dp),
                style = MaterialTheme.typography.titleLarge
            )
        }
        items(state.history, { it.id?: "" }) {
            TranslationCard(history = it, onCopy = {  }, onShare = { })
        }

    }
}
@Composable
fun TranslatorLayout(
    modifier: Modifier = Modifier,
    state: TranslatorState,
    events: (TranslatorEvents) -> Unit
) {
    val context = LocalContext.current

    Spacer(modifier = modifier.height(8.dp))
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                IsaomDropdownMenu(
                    modifier = modifier.weight(1f),
                    selectedValue = state.source
                ) {
                    events.invoke(TranslatorEvents.OnSourceChanged(it))
                }
                IconButton(onClick = {
                    events.invoke(
                        TranslatorEvents.OnSwitchLanguage(
                            state.source,
                            state.target
                        ))
                }) {
                    Icon(imageVector = Icons.Default.SwitchRight, contentDescription = "Switch")
                }
                IsaomDropdownMenu(
                    modifier = modifier.weight(1f),
                    selectedValue = state.target
                ) {
                    events.invoke(TranslatorEvents.OnTargetChanged(it))
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val textFielfColors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent
                )
                TextField(
                    value = state.text,
                    onValueChange = {events.invoke(TranslatorEvents.OnTextChanged(it))},
                    modifier = modifier.weight(1f),
                    label = { Text("Enter text") },
                    minLines = 3,
                    colors = textFielfColors
                )
                TextField(
                    value = state.translation ?: "",
                    readOnly = true,
                    onValueChange = {},
                    label = { Text("Translation") },
                    minLines = 3,
                    modifier = modifier.weight(1f),
                    colors = textFielfColors
                )
            }
            Row(
                modifier = modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CameraButton {
                    if (it.path?.isNotEmpty() == true) {
                        events.invoke(
                            TranslatorEvents.OnTransformImageToText(
                            context = context, uri = it
                        ))
                        return@CameraButton
                    }
                }
                PrimaryButton(
                    modifier  = modifier,
                    isLoading = state.isLoading,
                    onClick = {
                        events.invoke(TranslatorEvents.OnTranslateText(state.text,state.source,state.target))
                    }
                ) {
                    Text(text = "Translate")
                }
            }

        }
    }
}