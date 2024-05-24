package com.example.ktor_server

import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

object NetworkModule {
    private val logger = LoggerFactory.getLogger("HololensApp")

    private val client = HttpClient(CIO) {
        install(Auth) {
            basic {
                credentials {
                    BasicAuthCredentials(username = "", password = "")
                }
                realm = "Hololens"
            }
        }
        install(ContentNegotiation) {
            json()
        }
    }

    fun handleStartAppRequest(
        ip: String,
        username: String,
        password: String,
        appName: String,
        sendProgress: suspend (String) -> Unit
    ): ApiResponse = runBlocking {
        logger.info("処理開始: Hololensに接続を試みます")
        sendProgress("Connecting to Hololens at $ip")

        // デバイスの到達可能性をチェック
        if (!isDeviceReachable(ip, username, password, sendProgress)) {
            return@runBlocking ApiResponse(false, "デバイスに接続できません")
        }

        // アプリ一覧の取得
        val appsResponse = getInstalledApps(ip, username, password, sendProgress)
        if (appsResponse == null) {
            return@runBlocking ApiResponse(false, "アプリ一覧の取得に失敗しました")
        }

        // 指定したアプリ名の検索
        val appInfo = findAppByName(appsResponse, appName, sendProgress)
        if (appInfo == null) {
            return@runBlocking ApiResponse(false, "指定したアプリが見つかりません: $appName")
        }

        // アプリの起動
        return@runBlocking startApp(ip, username, password, appInfo.packageFamilyName, sendProgress)
    }

    // デバイスの到達可能性をチェック
    private suspend fun isDeviceReachable(ip: String, username: String, password: String, sendProgress: suspend (String) -> Unit): Boolean {
        return try {
            val response: String = client.get("http://$ip/api/os/info").toString()
            if (response.contains("Windows")) {
                logger.info("デバイスに到達可能: $ip")
                sendProgress("Device reachable at $ip")
                true
            } else {
                logger.error("デバイスに到達できません: $ip")
                sendProgress("Device not reachable at $ip")
                false
            }
        } catch (e: Exception) {
            logger.error("デバイスに到達できません: $e")
            sendProgress("Failed to reach device: ${e.message}")
            false
        }
    }

    // アプリ一覧の取得
    private suspend fun getInstalledApps(
        ip: String,
        username: String,
        password: String,
        sendProgress: suspend (String) -> Unit
    ): List<AppInfo>? {
        return try {
            val appsResponse: List<AppInfo> = client.get("http://$ip/api/app/packagemanager/packages") {
                contentType(ContentType.Application.Json)
            }.body()
            logger.info("アプリ一覧を取得しました: ${appsResponse.size}件")
            sendProgress("Retrieved ${appsResponse.size} apps from device")
            appsResponse
        } catch (e: Exception) {
            logger.error("アプリ一覧の取得に失敗しました: $e")
            sendProgress("Failed to retrieve app list: ${e.message}")
            null
        }
    }

    // 指定したアプリ名の検索
    private suspend fun findAppByName(appsResponse: List<AppInfo>, appName: String, sendProgress: suspend (String) -> Unit): AppInfo? {
        val appInfo = appsResponse.find { it.name == appName }
        if (appInfo != null) {
            logger.info("指定したアプリが見つかりました: $appName")
            sendProgress("App found: $appName")
        } else {
            logger.warn("指定したアプリが見つかりませんでした: $appName")
            sendProgress("App not found: $appName")
        }
        return appInfo
    }

    // アプリの起動
    private suspend fun startApp(
        ip: String,
        username: String,
        password: String,
        packageFamilyName: String,
        sendProgress: suspend (String) -> Unit
    ): ApiResponse {
        return try {
            val startResponse: String = client.post("http://$ip/api/taskmanager/app") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("package" to packageFamilyName))
            }.toString()
            logger.info("アプリを起動しました: $packageFamilyName")
            sendProgress("Started app: $packageFamilyName")
            ApiResponse(true, "アプリを起動しました: $packageFamilyName")
        } catch (e: Exception) {
            logger.error("アプリの起動に失敗しました: $e")
            sendProgress("Failed to start app: ${e.message}")
            ApiResponse(false, "アプリの起動に失敗しました: ${e.message}")
        }
    }
}
