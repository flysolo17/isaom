package com.ketchupzzz.isaom.presentation.teacher.subject.add_subject

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.ketchupzzz.isaom.presentation.auth.register.RegisterEvents
import com.ketchupzzz.isaom.ui.custom.PrimaryButton
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSubjectScreen(
    modifier: Modifier = Modifier,
    state: AddSubjectState,
    events: (AddSubjectEvents) -> Unit,
    navHostController: NavHostController
) {
    val context = LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            events(AddSubjectEvents.OnCoverSelected(it))
        }
    }
    LaunchedEffect(state) {
        if (state.error !== null) {
            Toast.makeText(context,state.error,Toast.LENGTH_SHORT).show()
        }
        if (state.success !== null) {
            Toast.makeText(context,state.success,Toast.LENGTH_SHORT).show()
            delay(1000)
            navHostController.popBackStack()
        }
    }
    Scaffold {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(it)
            .padding(16.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                    .clickable {
                        imagePickerLauncher.launch("image/*")
                    },
                contentAlignment = Alignment.Center
            ) {
                if (state.cover != null) {
                    Image(
                        painter = rememberAsyncImagePainter(state.cover),
                        contentDescription = "Subject Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = "Pick Image",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = modifier.fillMaxSize()
                    )
                }
            }
            Spacer(modifier = modifier.height(8.dp))
            OutlinedTextField(
                value = state.name,
                onValueChange = {events(AddSubjectEvents.OnNameChanged(it))},
                label = { Text(text = "Enter subject name")},
                modifier = modifier.fillMaxWidth()
            )
            Spacer(modifier = modifier.height(8.dp))

            val sections = state.sections
            var sectionExpanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(expanded = sectionExpanded, onExpandedChange =  {
                sectionExpanded = !sectionExpanded
            }) {
                OutlinedTextField(
                    readOnly = true,
                    value = state.selectedSection?.name ?: "No Section Selected",
                    onValueChange = { events(AddSubjectEvents.OnSectionSelected(state.selectedSection)) },
                    label = { Text("Select Section") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sectionExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    isError = state.selectedSection == null,
                    supportingText = {
                        Text(
                            text = if( state.selectedSection == null ) "No section selected" else "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Start
                        )
                    },
                )

                ExposedDropdownMenu(
                    expanded = sectionExpanded,
                    onDismissRequest = { sectionExpanded = false },
                    modifier = Modifier.exposedDropdownSize()
                ) {
                    sections.forEach { section ->
                        DropdownMenuItem(
                            text = { Text(section.name?: "no section") },
                            onClick = {
                                events(AddSubjectEvents.OnSectionSelected(section))
                                sectionExpanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = modifier.weight(1f))
            PrimaryButton(onClick = {
                if(state.selectedSection !== null && !state.isLoading) {
                    events.invoke(AddSubjectEvents.OnAddingSubject(state.selectedSection.id ?: ""))
                } else {
                    Toast.makeText(context,"No Section Selected",Toast.LENGTH_SHORT).show()
                }
            }, isLoading = state.isLoading) {
                Text(text = "Create Subject")
            }
        }
    }

}

