using System;
using System.IO;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Diagnostics;
using System.Threading;
using System.Collections;

namespace JadeSharp
{
    #region EventArgs

    public class ConnectionEventArgs : EventArgs
    {
        #region Fields

        private ConnectionEventType _EventType;
        private string _Reason;
        private ConnectionEvents _Type;

        #endregion

        #region Enums

        public enum ConnectionEvents
        {
            GENERIC = 0,
            PLATFORM_SHUTDOWN
        }

        #endregion

        #region Constructors

        public ConnectionEventArgs(ConnectionEventType et) : this(et, null, ConnectionEvents.GENERIC) { }

        public ConnectionEventArgs(ConnectionEventType et, string reason) : this(et, reason, ConnectionEvents.GENERIC) {}

        public ConnectionEventArgs(ConnectionEventType et, string reason, ConnectionEvents type)
        {
            _EventType = et;
            _Reason = reason;
            _Type = type;
        }

        #endregion

        #region Properties

        public ConnectionEventType EventType
        {
            get { return _EventType; }
        }

        public string Reason
        {
            get { return _Reason; }
        }

        public ConnectionEvents Type
        {
            get { return _Type; }
        }

        #endregion

        #region Override Methods

        public override string ToString()
        {
            StringBuilder sb = new StringBuilder(_EventType.ToString());

            if (_Reason != null)
            {
                sb.Append(Consts.NEWLINE);
                sb.Append("Reason: " + _Reason);
            }

            sb.Append(Consts.NEWLINE);
            sb.Append("Type: " + _Type);

            return sb.ToString();
        }

        #endregion
    }

    #endregion

    #region Delegate

    public delegate void ConnectionEventHandler(JadeConnector sender, ConnectionEventArgs args);
    public delegate void ReceiveMessageEventHandler(JadeConnector sender, ACLMessage message);

    #endregion

    /// <summary>
    /// Summary description for JadeConnector.
    /// </summary>
    public class JadeConnector
    {
        #region Consts

        private const int SLEEP_TIME = 5000;

        private const byte MESSAGE_IN = 14;     // from FrontEndSkel
        private const byte EXIT = 15;           // from FrontEndSkel

        #endregion

        #region Fields

        private StringBuilder _Props;
        private int _Port;
        private string _PlatformName;
        private string _Host;
        private bool _FirstTime;

        private string _Agent = null;
        private string _Platform = null;
        private string _Mediator = null;
        private string _Owner = null;
        
        private Hashtable _Hash;

        private TcpClient _Connection;
        private NetworkStream _Stream;
        private MemoryStream _Buffer = new MemoryStream();
        private int _SessionID = 0;
        private bool _IsConnected = false;
        private bool _DoConnect = false;
        private bool _IsRunning = false;

        private Thread _ReadThread;

        private static object _SyncLock = new object();

        private JICPPacket _ResponsePacket;
        private JICPPacket _ErrorPacket;
        private JICPPacket _Packet;

        #endregion 

        #region Events

        public event ConnectionEventHandler ConnectionEvent;
        public event ReceiveMessageEventHandler ReceiveEvent;

        #endregion

        public JadeConnector()
        {
            // default properties
            _Hash = new Hashtable();
            _Hash.Add("mediator-class", "jade.imtp.leap.nio.BackEndDispatche");
            _Hash.Add("max-disconnection-time", "600000");
            _Hash.Add("keep-alive-time", "-1");
            _Hash.Add("agents", "%C:JadeSharp.NJade.JadeConnector");
            _Hash.Add("owner", "4613254132764132612");
            _Hash.Add("port", "2009");

            _FirstTime = true;

            // Load configuration file values
            _Props = new StringBuilder();

            Hashtable hash2 = ConfigurationSettings.Instance.LoadValues();
            IDictionaryEnumerator idEn = hash2.GetEnumerator();
            while (idEn.MoveNext())
            {
                if (_Hash.ContainsKey(idEn.Key))
                    _Hash.Remove(idEn.Key);
                _Hash.Add(idEn.Key, idEn.Value);

                switch (idEn.Key.ToString())
                {
                    case "host":
                        _Host = idEn.Value.ToString();
                        break;
                    case "port":
                        {
                            try
                            {
                                _Port = int.Parse(idEn.Value.ToString());
                            }catch(Exception e)
                            {
                                Logger.LogLine(e.Message, Logger.LogLevel.LOW);
                                _Port = 2099;
                            }
                        }
                        break;
                    case "platform-name":
                        _PlatformName = idEn.Value.ToString();
                        break;
                    default:
                        _Props.Append(idEn.Key.ToString());
                        _Props.Append("=");
                        _Props.Append(idEn.Value.ToString());
                        _Props.Append("#");
                        break;
                }
            }

            // rimuovo il # finale decrementando di uno 
            // la dimensione dello StringBuilder
            _Props.Length = _Props.Length - 1;

            // the response command has a body with 2 bytes:
            // 1 == Command.OK (see FrontEndSkel.executeCommand)
            // 0 == no parameters in the command
            _ResponsePacket = new JICPPacket(JICPConstants.RESPONSE_TYPE, new byte[] { 1, 0 });
            _ErrorPacket = new JICPPacket(JICPConstants.ERROR_TYPE);
            _Packet = new JICPPacket(JICPConstants.COMMAND_TYPE);
        }

        #region Properties

        public string AgentName
        {
            get { return _Agent; }
        }

        public string PlatformName
        {
            get { return _Platform; }
        }

        public string Host
        {
            get { return _Host; }
        }

        public int Port
        {
            get { return _Port; }
        }

        public string JadePlatformName
        {
            get { return _PlatformName; }
        }

        public string Owner
        {
            get { return _Owner; }
            set { _Owner = value; }
        }

        public Hashtable Hash
        {
            get { return _Hash; }
        }

        #endregion

        #region Public Methods

        public void Connect()
        {
            lock (_SyncLock)
            {
                if (_IsConnected)
                {
                    System.Diagnostics.Debug.WriteLine("Already connected to " + _Host);
                    return;
                }

                if (_DoConnect)
                {
                    System.Diagnostics.Debug.WriteLine("Already connecting to " + _Host);
                    return;
                }

                _DoConnect = true;

                _ReadThread = new Thread(new ThreadStart(this.ReadPacket));
                _ReadThread.Start();
            }
        }

        public void Close()
        {
            _DoConnect = false;
            lock (_SyncLock)
            {
                if (_IsConnected)
                {
                    Logger.LogLine("[JadeConnector] - Close: closing streams...", Logger.LogLevel.HIGH);

                    JICPPacket packet = new JICPPacket(JICPConstants.COMMAND_TYPE);
                    packet.Terminated = true;
                    packet.SessionID = _SessionID;
                    WritePacket(packet);

                    Logger.LogLine("[JadeConnector] - Close: After WritePacket", Logger.LogLevel.LOW);
                    
                    Disconnect();
                }
            }
        }

        public void SendMessage(ACLMessage message)
        {
            //lock (this)
            //{
                if (!_IsConnected) 
                    System.Diagnostics.Debug.WriteLine("Not connected");

                if (message.Sender == null)
                    message.Sender = AgentName;

                MemoryStream buffer = new MemoryStream();
                DataStream stream = new DataStream(buffer);
                message.WriteTo(stream);
                stream.Flush();

                JICPPacket packet = new JICPPacket(JICPConstants.COMMAND_TYPE, buffer.ToArray());
                packet.SessionID = _SessionID;
                WritePacket(packet);
                
                ++_SessionID;
                _SessionID &= 0x0F;
            //} // end of LOCK
        }

        #endregion

        #region Private Methods

        private void ReadPacket()
        {
            Logger.LogLine("ReadPacket STARTED", Logger.LogLevel.HIGH);
                        
            while (_DoConnect)
            {
                try
                {
                    ConnectMediator();

                    // read and dispatch COMMAND
                    while (_IsConnected)
                    {
                        _Packet.ReadFrom(_Stream);
                        OnReadPacketCallback(_Packet);
                    }
                }
                catch (System.Net.Sockets.SocketException se)
                {
                    Logger.LogLine("[JadeConnector] - ReadPacket: SocketException caught -> " + se.ToString(), Logger.LogLevel.HIGH);
                    Disconnect();

                    _DoConnect = false;
                }
                catch (Exception e)
                {
                    Logger.LogLine("[JadeConnector] - ReadPacket: Exception caught -> " + e.ToString(), Logger.LogLevel.HIGH);
                    Disconnect();

                    _DoConnect = false;
                }
            }

            Logger.LogLine("[JadeConnector] - ReadPacket: exiting now", Logger.LogLevel.HIGH);
            Logger.LogLine("ReadPacket STARTED", Logger.LogLevel.HIGH);

            // force _firstTime to be TRUE to send CREATE_MEDIATOR command
            _FirstTime = true;

            Logger.LogLine("[JadeConnector] - FirstTime set to TRUE", Logger.LogLevel.HIGH);
            OnConnectionEvent(new ConnectionEventArgs(ConnectionEventType.disconnected));
        }

        private void Disconnect()
        {
            Logger.LogLine("[JadeConnector] - Disconnect: _isConnected is " + _IsConnected, Logger.LogLevel.LOW);

            if (_IsConnected)
            {
                _IsConnected = false;

                try
                {
                    // scrivo un Byte sullo stream, così da sbloccare l'eventuale 
                    // attesa su una ReadByte()
                    if (_Stream.CanWrite)
                    {
                        _Stream.WriteByte(byte.MinValue);
                        _Stream.WriteByte(byte.MinValue);
                        _Stream.WriteByte(byte.MinValue);
                    }

                    _Stream.Flush();
                    _Stream.Close();
                    _Stream = null;

                    _Connection.Close();
                }
                catch (Exception e)
                {
                    Logger.LogLine("Exception thrown during Connection closing: " + e.Message, Logger.LogLevel.MEDIUM);
                }

                Logger.LogLine("[JadeConnector] - Disconnect OK", Logger.LogLevel.LOW);
                OnConnectionEvent(new ConnectionEventArgs(ConnectionEventType.disconnected));
            }
        }

        private void ConnectMediator()
        {
            Logger.LogLine("Before CONNECT MEDIATOR", Logger.LogLevel.LOW);

            try
            {
                _Connection = new TcpClient();
                
                int bSize = _Connection.ReceiveBufferSize;
                Logger.LogLine("ReceiveBufferSize is " + bSize, Logger.LogLevel.LOW);

                _Connection.Connect(new IPEndPoint(IPAddress.Parse(_Host), _Port));
            }
            catch (SocketException e)
            {
                Logger.LogLine("SocketException caught: " + e.Message, Logger.LogLevel.LOW);

                if (!_IsRunning)
                {
                    _IsRunning = true;
                    new Thread(new ThreadStart(this.HttpConnect)).Start();
                }
                throw e;
            }

            JICPPacket packet;
            Logger.LogLine("[JadeConnector] - ConnectMediator: FirstTime is " + _FirstTime, Logger.LogLevel.HIGH);

            if (_FirstTime)
            {
                Logger.LogLine("[JadeConnector] - Create Mediator", Logger.LogLevel.HIGH);
                packet = new JICPPacket(JICPConstants.CREATE_MEDIATOR_TYPE, _Props.ToString());
                _FirstTime = false;
            }
            else
            {
                Logger.LogLine("[JadeConnector] - Connect Mediator", Logger.LogLevel.HIGH);
                packet = new JICPPacket(JICPConstants.CONNECT_MEDIATOR_TYPE);
                packet.RecipientID = _Mediator;
            }

            _Stream = _Connection.GetStream();
            WritePacket(packet);

            try
            {
                packet.ReadFrom(_Stream);
            }
            catch (Exception e)
            {
                Logger.LogLine("Exception is " + e.Message, Logger.LogLevel.MEDIUM);
                packet = null;
            }

            if (packet == null)
            {
                // No Connection try permitted...
                _DoConnect = false;
                return;
            }

            string data = packet.DataAsString;

            if (packet.IsResponseType)
            {
                _IsConnected = true;
                string val = GetProperty(JICPConstants.MEDIATOR_ID_KEY, data);
                if (val != null) 
                    _Mediator = val;

                val = GetProperty(JICPConstants.PLATFORM_ID_KEY, data);
                if (val != null)
                    _Platform = val;

                val = GetProperty(JICPConstants.AGENTS_KEY, data);
                if (val != null)
                {
                    _Agent = val;
                    int i = val.IndexOf(':');
                    if (i != -1) 
                        _Agent = _Agent.Substring(i + 1);

                    i = _Agent.IndexOf('(');
                    if (i != -1) 
                        _Agent = _Agent.Substring(0, i);
                }

                OnConnectionEvent(new ConnectionEventArgs(ConnectionEventType.connected));

            }
            else
            {
                _DoConnect = false;
                _Mediator = null;
                _Connection.Close();

                Logger.LogLine("[JadeConnector] - ConnectMediator : error in connecting phase", Logger.LogLevel.MEDIUM);

                OnConnectionEvent(new ConnectionEventArgs(ConnectionEventType.error, data));
            }
        }

        // this method, called by a worker-thread, is a trick to setup the GPRS connection.
        // Apparently there's no way to force a GPRS connection from TCP in the CF.
        private void HttpConnect()
        {
            try
            {
                WebRequest webReq = HttpWebRequest.Create("http://fakehost.nonexist.not");
                webReq.Proxy = new WebProxy("proxy-rr.cselt.it", 8080);
                webReq.Proxy.Credentials = new NetworkCredential("ue_pieri", "60AB9D20352EDE");

                webReq.GetResponse();
            }
            catch (Exception e) 
            {
                Debug.WriteLine(e.Message.ToString());
            }
            _IsRunning = false;
        }

        private void WritePacket(JICPPacket packet)
        {
            try
            {
                _Buffer.Seek(0, SeekOrigin.Begin);
                _Buffer.SetLength(0);
                
                packet.WriteTo(_Buffer);

                byte[] internalBuffer = _Buffer.GetBuffer();
                int len = (int) _Buffer.Length;

                if (_Stream.CanWrite)
                {
                    _Stream.Write(internalBuffer, 0, len);
                }
                else
                    throw new IOException("Il NetworkStream non è pronto per la scrittura!");

                _Stream.Flush();
            }
            catch (ObjectDisposedException ode)
            {
                string msg = ode.Message;
            }
            catch (IOException ioe)
            {
                string msg = ioe.Message;
            }
        }

        private string GetProperty(string key, string data)
        {
            int i = data.IndexOf(key);
            if (i == -1) 
                return null;

            int j = data.IndexOf('=', i);
            int k = data.IndexOf('#', i);

            if (k != -1) 
                return data.Substring(j + 1, k - j - 1);

            return data.Substring(j + 1);
        }

        protected virtual void OnConnectionEvent(ConnectionEventArgs args)
        {
            try
            {
                if (ConnectionEvent != null) 
                    ConnectionEvent(this, args);
            }
            catch (Exception e)
            {
                Debug.WriteLine(e.ToString());
                Logger.LogLine("JadeConnector - [OnConnectionEvent] Exception caught: " + e.Message, Logger.LogLevel.LOW);
            }
        }

        private void OnReadPacketCallback(object state)
        {
            JICPPacket packet = state as JICPPacket;
            if (packet == null)
                return;

            switch (packet.TypeCode)
            {
                case JICPConstants.ERROR_TYPE:
                    if (packet.Data != null && packet.Data.Length > 0)
                        ThreadPool.QueueUserWorkItem(new WaitCallback(OnConnectionErrorCallback), packet.DataAsString);
                    break;

                case JICPConstants.RESPONSE_TYPE:
                    // do nothing
                    break;

                case JICPConstants.COMMAND_TYPE:
                    {
                        // start Command header from SerializationEngine
                        int i = packet.Data[0];

                        if (i == EXIT)
                        {
                            JICPPacket pck = new JICPPacket(JICPConstants.RESPONSE_TYPE);
                            pck.Terminated = true;
                            pck.SessionID = _SessionID;
                            WritePacket(pck);

                            Disconnect();
                        }
                        else if (i == MESSAGE_IN)
                        {
                            try
                            {
                                Logger.LogLine("BEFORE WritePacket for COMMAND_TYPE", Logger.LogLevel.LOW);

                                ACLMessage message = new ACLMessage();
                                message.ReadFrom(new DataStream(new MemoryStream(packet.Data)));
                                _ResponsePacket.SessionID = packet.SessionID;

                                WritePacket(_ResponsePacket);
                                ThreadPool.QueueUserWorkItem(new WaitCallback(OnReceiveMessageCallback), message);
                            }
                            catch (Exception e)
                            {
                                Logger.LogLine("Exception in WritePacket is " + e.Message, Logger.LogLevel.LOW);

                                Debug.WriteLine(e.ToString());

                                _ErrorPacket.DataAsString = e.ToString();
                                _ErrorPacket.SessionID = packet.SessionID;

                                WritePacket(_ErrorPacket);
                            }
                        }
                        else
                        {
                            Logger.LogLine("Unexpected Command type " + i, Logger.LogLevel.LOW);
                            throw new Exception("Unexpected Command type " + i);
                        }
                    }
                    break;
                default:
                    Logger.LogLine("[JadeConnector] - ReadPacket: Unexpected JICP packet " + packet.TypeCode, Logger.LogLevel.HIGH);
                    break;
            }
        }

        private void OnConnectionErrorCallback(object error)
        {
            OnConnectionEvent(new ConnectionEventArgs(ConnectionEventType.error, error.ToString()));
        }

        private void OnReceiveMessageCallback(object state)
        {
            ACLMessage msg = state as ACLMessage;
            if (msg != null)
                OnReceiveMessageEvent(msg);
        }

        protected virtual void OnReceiveMessageEvent(ACLMessage message)
        {
            try
            {
                if (ReceiveEvent != null) 
                    ReceiveEvent(this, message);
            }
            catch (Exception e)
            {
                Debug.WriteLine(e.ToString());
            }
        }

        #endregion
    }
}
