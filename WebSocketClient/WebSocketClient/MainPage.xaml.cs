using System;
using System.Diagnostics;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.Networking.Sockets;
using Windows.Storage.Streams;
using Windows.UI.Core;
using System.Threading.Tasks;
using System.Net.Sockets;
using Windows.UI.Xaml.Documents;

namespace WebSocketClient
{
    public sealed partial class MainPage : Page
    {
        private MessageWebSocket messageWebSocket;
        private DataWriter messageWriter;

        private UdpClient udpClient;
        private bool isWebSocketConnecting = false;

        public MainPage()
        {
            this.InitializeComponent();
            StartListening(OnReceivedWebSocketUri: (webSocketUri) =>
            {
                StartWebSocket(webSocketUri);
            });
        }

        private async void StartListening(Action<string> OnReceivedWebSocketUri)
        {
            try
            {
                udpClient = new UdpClient(8888);
                udpClient.EnableBroadcast = true;

                while (true)
                {
                    var result = await udpClient.ReceiveAsync();
                    string message = System.Text.Encoding.UTF8.GetString(result.Buffer);

                    if (message.StartsWith("ws://"))
                    {
                        if (!isWebSocketConnecting)
                        {
                            isWebSocketConnecting = true;

                            string serverIp = result.RemoteEndPoint.Address.ToString();

                            // 受信したサーバーのIPアドレスをUIに表示
                            await Dispatcher.RunAsync(CoreDispatcherPriority.Normal, () =>
                            {
                                Debug.WriteLine($"Server IP Address: {serverIp}");
                            });

                            OnReceivedWebSocketUri?.Invoke(message);
                        }
                    }

                    break;
                }
            }
            catch (Exception ex)
            {
                Debug.WriteLine($"Error receiving UDP broadcast: {ex.Message}");
            }
        }

        private async void StartWebSocket(string webSocketUri)
        {
            try
            {
                messageWebSocket = new MessageWebSocket();
                messageWebSocket.Control.MessageType = SocketMessageType.Utf8;
                messageWebSocket.MessageReceived += MessageWebSocket_MessageReceived;

                Uri serverUri = new Uri(webSocketUri);
                await messageWebSocket.ConnectAsync(serverUri);

                messageWriter = new DataWriter(messageWebSocket.OutputStream);

                // メッセージを送信
                await SendMessage("Hello, Server!");
            }
            catch (Exception ex)
            {
                Debug.WriteLine("WebSocket error: " + ex.Message);
                isWebSocketConnecting = false; // エラーが発生した場合、再接続を許可するためにフラグをリセット
            }
        }

        private async Task SendMessage(string message)
        {
            if (messageWriter != null)
            {
                messageWriter.WriteString(message);
                await messageWriter.StoreAsync();
            }
        }

        private async void MessageWebSocket_MessageReceived(MessageWebSocket sender, MessageWebSocketMessageReceivedEventArgs args)
        {
            DataReader messageReader = args.GetDataReader();
            messageReader.UnicodeEncoding = UnicodeEncoding.Utf8;
            string message = messageReader.ReadString(messageReader.UnconsumedBufferLength);

            // メッセージをUIに表示する場合
            await Dispatcher.RunAsync(CoreDispatcherPriority.Normal, () =>
            {
                Debug.WriteLine("Received from server: " + message);
            });
        }

        private void Page_Unloaded(object sender, RoutedEventArgs e)
        {
            messageWebSocket?.Dispose();
            messageWebSocket = null;
            messageWriter?.Dispose();
            messageWriter = null;
        }
    }
}
