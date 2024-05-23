package com.example.ktorwebsockertexample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ktor_server.Server
import com.example.ktorwebsockertexample.ui.theme.KtorWebSockertExampleTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.NavGraphs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.InetAddress
import java.net.NetworkInterface
import kotlin.concurrent.thread

class MainActivity : ComponentActivity() {

    private val server = Server()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val ip = server.getLocalIpAddress()

        setContent {
            KtorWebSockertExampleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DestinationsNavHost(navGraph = NavGraphs.root)
                }
            }
        }
    }
}

@Composable
fun ServerControlScreen(server: Server, ip: String) {
    var isServerRunning by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    var message by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                if (isServerRunning) {
                    coroutineScope.launch(Dispatchers.IO) {
                        server.stop()
                        isServerRunning = false
                    }
                } else {
                    coroutineScope.launch(Dispatchers.IO) {
                        server.start() {
                            message = it
                        }
                        isServerRunning = true
                    }
                }
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(if (isServerRunning) "サーバー停止" else "サーバー開始")
        }

        Text(text = if (isServerRunning) "サーバー起動中" else "サーバー停止中")
        Text(text = ip)

        Text(text = message)
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KtorWebSockertExampleTheme {
        Greeting("Android")
    }
}