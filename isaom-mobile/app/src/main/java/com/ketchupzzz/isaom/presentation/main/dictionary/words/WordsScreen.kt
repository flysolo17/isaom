package com.ketchupzzz.isaom.presentation.main.dictionary.words

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ketchupzzz.isaom.R
import com.ketchupzzz.isaom.models.dictionary.Dictionary
import com.ketchupzzz.isaom.models.dictionary.Favorites
import com.ketchupzzz.isaom.presentation.main.dictionary.DictionaryEvents
import com.ketchupzzz.isaom.presentation.main.dictionary.DictionaryState
import com.ketchupzzz.isaom.ui.custom.WebViewDialog
import com.ketchupzzz.isaom.utils.generateRandomString

    @Composable
    fun WordsScreen(
        modifier: Modifier = Modifier,
        state : DictionaryState,
        events: (DictionaryEvents) -> Unit,
        dictionaryList : List<Dictionary>
    ) {
        val context = LocalContext.current
        if (state.isLoading) {
            Column(modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Text(text = "Loading.....")
            }
        } else {
            LazyColumn(
                modifier =  modifier.fillMaxSize()
            ) {
                val dictIDs = state.favorites.map { it.dictionary?.id ?:"" }
                val filteredWords = dictionaryList.filter { !dictIDs.contains(it.id) }
                items(filteredWords, key = {it.id ?: generateRandomString() }) {
                    val isEnabled = state.users  != null
                    WordCard(dictionary = it, events = events, isButtonEnable = isEnabled)
                }
            }
        }
    }



@Composable
fun WordCard(
    modifier: Modifier = Modifier,
    dictionary: Dictionary,
    events: (DictionaryEvents) -> Unit,
    isButtonEnable : Boolean = false,
) {
    var isShowDialog by remember { mutableStateOf(false) }
    if (isShowDialog) {
        WebViewDialog(dictionary = dictionary) {
            isShowDialog = !isShowDialog
        }
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp)
            .clickable {
                isShowDialog = !isShowDialog
            },
    ) {
        Column(modifier = modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = modifier
                    .fillMaxWidth()
                    .weight(1f), horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "English" , color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.labelSmall)
                    Text(text = dictionary.word ?: "No Word" , color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.titleMedium)
                }

                if (isButtonEnable) {
                    IconButton(onClick = { events(DictionaryEvents.OnAddToFavorites(dictionary)) }) {
                        Icon(painter = painterResource(id = R.drawable.baseline_star_outline_24), contentDescription = "Not Liked")

                    }
                }
            }

            Spacer(modifier = modifier.height(2.dp))
            HorizontalDivider()
            Spacer(modifier = modifier.height(2.dp))
            Text(text = "Ilocano" , color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.labelSmall)
            Text(text = dictionary.definition ?: "No Definition" , color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.titleMedium)
        }
    }
}

fun openLink(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(url)
    }
    context.startActivity(intent)
}