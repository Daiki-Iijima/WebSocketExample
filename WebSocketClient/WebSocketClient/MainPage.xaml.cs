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

        public MainPage()
        {
            this.InitializeComponent();
            StartListening(OnReceivedHostInfo: (ip) => { 
                StartWebSocket(ip);
            });
        }

        private async void StartListening(Action<string> OnReceivedHostInfo)
        {
            try
            {
                udpClient = new UdpClient(8888);
                udpClient.EnableBroadcast = true;

                string serverIp = "";

                while (true)
                {
                    var result = await udpClient.ReceiveAsync();
                    string message = System.Text.Encoding.UTF8.GetString(result.Buffer);

                    if (message.Contains("CONNECT_REQUEST"))
                    {
                        serverIp = result.RemoteEndPoint.Address.ToString();

                        // 受信したサーバーのIPアドレスをUIに表示
                        await Dispatcher.RunAsync(CoreDispatcherPriority.Normal, () =>
                        {
                            Debug.WriteLine($"Server IP Address: {serverIp}");
                        });
                    }

                    if (serverIp != "")
                    {
                        OnReceivedHostInfo?.Invoke(serverIp);
                        break;
                    }
                }
            }
            catch (Exception ex)
            {
                Debug.WriteLine($"Error receiving UDP broadcast: {ex.Message}");
            }
        }

        private async void StartWebSocket(string ip)
        {
            try
            {
                messageWebSocket = new MessageWebSocket();
                messageWebSocket.Control.MessageType = SocketMessageType.Utf8;
                messageWebSocket.MessageReceived += MessageWebSocket_MessageReceived;

                Uri serverUri = new Uri($"ws://{ip}:8080/ws");
                await messageWebSocket.ConnectAsync(serverUri);

                messageWriter = new DataWriter(messageWebSocket.OutputStream);

                // メッセージを送信
                await SendMessage("Hello, Server!");

            }
            catch (Exception ex)
            {
                Debug.WriteLine("WebSocket error: " + ex.Message);
            }
        }

        private async Task SendMessage(string message)
        {
            messageWriter.WriteString(message);
            await messageWriter.StoreAsync();
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
        }
    }
}