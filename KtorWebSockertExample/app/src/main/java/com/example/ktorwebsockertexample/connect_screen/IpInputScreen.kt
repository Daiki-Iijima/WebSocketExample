package com.example.ktorwebsockertexample.connect_screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.ConnectingDialogDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle

@Destination<RootGraph>(start = true)
@Composable
fun IpInputScreen(navigator: DestinationsNavigator?, modifier: Modifier = Modifier) {

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
        modifier = modifier
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
            //  TODO : 入力のバリデートは必要

            navigator?.navigate(ConnectingDialogDestination)
        }) {
            Text(text = "接続開始")
        }

    }
}

object NonDismissibleDialog : DestinationStyle.Dialog() {
    override val properties = DialogProperties(
        dismissOnClickOutside = false,
        dismissOnBackPress = false,
    )
}

@Destination<RootGraph>(style = NonDismissibleDialog::class)
@Composable
fun ConnectingDialog(navigator: DestinationsNavigator?) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "アプリ起動中", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Text("アプリの起動中です。\nしばらくお待ち下さい")
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator() // 処理中のくるくる
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { navigator?.navigateUp() },
                modifier = Modifier.fillMaxWidth().height(50.dp).padding(horizontal = 16.dp)
            ) {
                Text(text = "キャンセル")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun InputScreenPreview() {
    IpInputScreen(null)
}

@Preview(showBackground = true)
@Composable
fun ConnectingDialogPreview() {
    ConnectingDialog(navigator = null)
}
