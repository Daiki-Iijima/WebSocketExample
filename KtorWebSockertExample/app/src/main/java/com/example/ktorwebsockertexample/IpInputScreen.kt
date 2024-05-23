package com.example.ktorwebsockertexample

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun IpInputScreen() {

    var ipStr by remember {
        mutableStateOf("")
    }

    var userStr by remember {
        mutableStateOf("")
    }

    var passwordStr by remember {
        mutableStateOf("")
    }

    var appNameStr by remember {
        mutableStateOf("Default App Name")
    }

    var enableManualSettingAppName by remember {
        mutableStateOf(false)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 10.dp),
    ) {
        OutlinedTextField(
            placeholder = {
                Text(text = "192.168.0.10")
            },
            label = {
                Text(text = "IPアドレス")
            },
            value = ipStr,
            onValueChange = {
                ipStr = it
            }
        )
        OutlinedTextField(
            label = {
                Text(text = "ユーザー名")
            },
            value = userStr,
            onValueChange = {
                userStr = it
            }
        )
        OutlinedTextField(
            label = {
                Text(text = "パスワード")
            },
            value = passwordStr,
            onValueChange = {
                passwordStr = it
            }
        )

        Column(
            modifier = Modifier.padding(top = 20.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Switch(
                    checked = enableManualSettingAppName,
                    onCheckedChange = {
                        enableManualSettingAppName = it
                    }
                )
                Text(text = "起動アプリを指定する")
            }

            OutlinedTextField(
                label = {
                    Text(text = "アプリ名")
                },
                enabled = enableManualSettingAppName,
                value = appNameStr,
                onValueChange = {
                    appNameStr = it
                }
            )
        }

        Button(onClick = {
            //  ポップアップ表示
        }) {
            Text(text = "接続開始")
        }
    }
}

@Composable
fun ConnectingPopup() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(
                vertical = 10.dp
            )
    ) {
        Text(text = "接続中")
    }
}

@Preview(showBackground = true)
@Composable
fun InputScreenPreview() {
    IpInputScreen()
}

@Preview(showBackground = true)
@Composable
fun ConnectingPopupPreview() {
    ConnectingPopup()
}
