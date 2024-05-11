package com.subbyte.subcinema.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.subbyte.subcinema.utils.StorageUtil

@Composable
fun SettingsScreen(navController: NavHostController) {
    val focusRequester = remember { FocusRequester() }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .focusRequester(focusRequester)
            .padding(12.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "GENERAL SETTINGS",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
        )
        SettingsField(
            StorageUtil.EntryBrowser_EntriesPerPage,
            StorageUtil.DEFAULT_EntryBrowser_EntriesPerPage,
            "Entries per page",
            ""
        )

        Text(
            text = "SMB SETTINGS",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
        )
        SettingsField(
            StorageUtil.EntryBrowser_SmbDomain,
            StorageUtil.DEFAULT_EntryBrowser_SmbDomain,
            "SMB domain",
            ""
        )
        SettingsField(
            StorageUtil.EntryBrowser_SmbRoot,
            StorageUtil.DEFAULT_EntryBrowser_SmbRoot,
            "SMB root share",
            ""
        )
        SettingsField(
            StorageUtil.EntryBrowser_SmbUsername,
            StorageUtil.DEFAULT_EntryBrowser_SmbUsername,
            "SMB access username",
            ""
        )
        SettingsField(
            StorageUtil.EntryBrowser_SmbPassword,
            StorageUtil.DEFAULT_EntryBrowser_SmbPassword,
            "SMB access password",
            ""
        )
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
fun SettingsField(settingName: String, settingDefault: Any, labelValue: String, placeholderValue: String) {
    val textValue = StorageUtil.getData(settingName, settingDefault).toString()
    var text by remember { mutableStateOf(textValue) }

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged {
                if (!it.isFocused) {
                    val settingValue = when (settingDefault) {
                        is String -> text
                        is Int -> text.toIntOrNull()
                        else -> null
                    }
                    if (settingValue != null) StorageUtil.saveData(settingName, settingValue)
                }
            },
        textStyle = TextStyle(color = Color.White),
        keyboardOptions = KeyboardOptions(
            keyboardType = if (settingDefault is Int) KeyboardType.Number else KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        singleLine = true,
        value = text,
        onValueChange = { newText ->
            text = newText
        },
        label = { Text(labelValue) },
        placeholder = { Text(placeholderValue) }
    )
}