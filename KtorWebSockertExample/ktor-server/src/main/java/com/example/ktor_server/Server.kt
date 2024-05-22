package com.example.ktor_server

import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.NetworkInterface

fun main() {
    Server().start(){

    }
}

class Server {
    private var server: ApplicationEngine? = null

    private var broadcastJob: Job? = null

    fun start(onReceiveMessage: (String)->Unit) {
        val ipAddress = getLocalIpAddress()

        server = embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
            install(WebSockets)
            routing {
                get("/") {
                    call.respondText("Hello, world!")
                }
                webSocket("/ws") {
                    for (frame in incoming) {
                        if (frame is Frame.Text) {
                            val receivedText = frame.readText()

                            onReceiveMessage.invoke(receivedText)

                            outgoing.send(Frame.Text("ACK: OK"))
                        }
                    }
                }
            }
        }.start(wait = false)    //  メインスレッドをブロックしないようにする

        startBroadcasting(ipAddress, 2000)
    }

    fun stop() {
        server?.stop(gracePeriodMillis = 1000, timeoutMillis = 5000)
        broadcastJob?.cancel()
    }

    fun getLocalIpAddress(): String {
        return try {
            NetworkInterface.getNetworkInterfaces().toList().flatMap { it.inetAddresses.toList() }
                .first { !it.isLoopbackAddress && it is InetAddress && it.hostAddress.indexOf(':') < 0 }
                .hostAddress
        } catch (ex: Exception) {
            "127.0.0.1" // デフォルトのループバックアドレス
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun startBroadcasting(ipAddress: String, sendDelay: Long) {
        val broadcastAddress = "255.255.255.255"
        val port = 8888
        val message = "SERVER_IP: $ipAddress"

        broadcastJob = GlobalScope.launch(Dispatchers.IO) {
            DatagramSocket().use { socket ->
                val buffer = message.toByteArray()
                val packet = DatagramPacket(
                    buffer,
                    buffer.size,
                    InetAddress.getByName(broadcastAddress),
                    port
                )
                while (isActive) {
                    socket.send(packet)
                    delay(sendDelay)
                }
            }

        }
    }
}