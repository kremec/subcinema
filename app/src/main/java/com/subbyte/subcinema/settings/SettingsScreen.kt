package com.subbyte.subcinema.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.key.NativeKeyEvent
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.subbyte.subcinema.models.Setting
import com.subbyte.subcinema.utils.SettingsUtil

@Composable
fun SettingsScreen(navController: NavHostController, settingsMenuItemFocusRequester: FocusRequester?) {
    val focusRequester = remember { FocusRequester() }

    val focusedSettingIndex = remember { mutableIntStateOf(0) }
    val showFocusedSettingAlert = remember { mutableStateOf(false) }

    fun moveDown() {
        focusedSettingIndex.intValue++
        if (focusedSettingIndex.intValue == 5) focusedSettingIndex.intValue = 0
    }
    fun moveUp() {
        focusedSettingIndex.intValue--
        if (focusedSettingIndex.intValue == -1) focusedSettingIndex.intValue = 4
    }

    fun select() {
        showFocusedSettingAlert.value = true
    }
    fun saveSetting(setting: Setting, value: String) {
        showFocusedSettingAlert.value = false
        val settingValue = when (setting.defaultValue) {
            is String -> value
            is Int -> value.toIntOrNull()
            else -> null
        }
        if (settingValue != null) SettingsUtil.saveData(setting, settingValue)
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .focusRequester(focusRequester)
            .focusable()
            .onKeyEvent {
                if (it.nativeKeyEvent.action == NativeKeyEvent.ACTION_DOWN) {
                    when (it.nativeKeyEvent.keyCode) {
                        NativeKeyEvent.KEYCODE_DPAD_DOWN -> moveDown()
                        NativeKeyEvent.KEYCODE_DPAD_UP -> moveUp()
                        NativeKeyEvent.KEYCODE_DPAD_CENTER -> select()
                        NativeKeyEvent.KEYCODE_DPAD_LEFT -> settingsMenuItemFocusRequester?.requestFocus()

                        NativeKeyEvent.KEYCODE_ENTER -> select()
                    }
                }
                false
            }
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
            SettingsUtil.EntryBrowser_EntriesPerPage,
            focusedSettingIndex,
            showFocusedSettingAlert,
            ::saveSetting
        )

        Text(
            text = "SMB SETTINGS",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
        )
        SettingsField(
            SettingsUtil.EntryBrowser_SmbDomain,
            focusedSettingIndex,
            showFocusedSettingAlert,
            ::saveSetting
        )
        SettingsField(
            SettingsUtil.EntryBrowser_SmbRoot,
            focusedSettingIndex,
            showFocusedSettingAlert,
            ::saveSetting
        )
        SettingsField(
            SettingsUtil.EntryBrowser_SmbUsername,
            focusedSettingIndex,
            showFocusedSettingAlert,
            ::saveSetting
        )
        SettingsField(
            SettingsUtil.EntryBrowser_SmbPassword,
            focusedSettingIndex,
            showFocusedSettingAlert,
            ::saveSetting,
        )
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
fun SettingsField(
    setting: Setting,
    focusedEntryIndex: MutableIntState,
    showFocusedSettingAlert: MutableState<Boolean>,
    saveSetting: (Setting, String) -> Unit
) {
    val value by remember { mutableStateOf(SettingsUtil.getData(setting.key, setting.defaultValue).toString()) }

    TextButton(
        modifier = Modifier
            .fillMaxWidth()
            .focusable(false),
        shape = RectangleShape,
        onClick = { },
        border = if (setting.index == focusedEntryIndex.intValue) BorderStroke(1.dp, Color.White) else null
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "${setting.name}    :    $value",
            textAlign = TextAlign.Left
        )
    }

    if (showFocusedSettingAlert.value && setting.index == focusedEntryIndex.intValue) {
        SettingDialog(
            setting = setting,
            saveSetting
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingDialog(setting: Setting, saveSetting: (Setting, String) -> Unit, ) {

    val textValue = SettingsUtil.getData(setting.key, setting.defaultValue).toString()
    var text by remember { mutableStateOf(textValue) }

    AlertDialog(
        onDismissRequest = { saveSetting(setting, text) },
        containerColor = Color.Black,
        title = {
            Text(
                text = setting.name,
                color = Color.White
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .padding(16.dp),
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    textStyle = TextStyle(color = Color.White),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = Color.Black,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = if (setting.defaultValue is Int) KeyboardType.Number else KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true,
                    value = text,
                    onValueChange = { newText ->
                        text = newText
                    }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    saveSetting(setting, text)
                }
            ) {
                Text("Confirm")
            }
        }
    )
}