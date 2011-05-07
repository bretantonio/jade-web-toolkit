#region Using directives

using System;
using System.Diagnostics;
using System.Text;
using System.Collections;

#endregion

namespace JadeSharp
{
    /// <summary>
    /// Summary description for NAgent.
    /// </summary>
    public class NAgent
    {
        #region Fields

        // random number generator
        protected Random _random = new Random();

        // Frame codec for ACL Messages
        protected FrameCodec _codec = new FrameCodec();

        protected ACLMessage _acl;
        protected ACLMessage _cleanAcl;

        // Jade connector for establishing and using connection with PC Agent
        protected JadeConnector _connector;

        // Port to be used by Jade connector
        protected int _port;

        // Agent variables
        protected bool _IsConnected;
        protected bool _IsOnline;
        protected string _ErrorReason;

        #endregion

        #region Events

        public event ConnectionEventHandler ConnectionEvent;
        public event ReceiveMessageEventHandler ReceiveMessageEvent;

        #endregion

        public NAgent()
        {
            // Create new JadeConnector
            _connector = new JadeConnector();
            _connector.ConnectionEvent += new ConnectionEventHandler(_connector_ConnectionEvent);
            _connector.ReceiveEvent += new JadeSharp.ReceiveMessageEventHandler(_connector_ReceiveEvent);

            _IsConnected = false;
            _IsOnline = false;
        }

        #region Properties

        public bool IsConnected
        {
            get
            {
                return _IsConnected;
            }
            set
            {
                _IsConnected = value;
            }
        }

        public bool IsOnline
        {
            get
            {
                return _IsOnline;
            }
        }

        public string ErrorReason
        {
            get
            {
                return _ErrorReason;
            }
        }

        public bool IsError
        {
            get
            {
                return (_ErrorReason.Length > 0);
            }
        }

        public ACLMessage ACL
        {
            get
            {
                return _acl;
            }
            set
            {
                _acl = value;
            }
        }

        public ACLMessage CleanACL
        {
            get
            {
                return _cleanAcl;
            }
            set
            {
                _cleanAcl = value;
            }
        }

        public FrameCodec Codec
        {
            get
            {
                return _codec;
            }
            set
            {
                _codec = value;
            }
        }

        public string Host
        {
            get { return _connector.Host; }
        }

        public int Port
        {
            get { return _connector.Port; }
        }

        public string PlatformName
        {
            get { return _connector.JadePlatformName; }
        }

        public Hashtable Hash
        {
            get { return _connector.Hash; }
        }

        #endregion

        #region Public Methods

        /// <summary>
        /// Connect JadeConnector to the network
        /// </summary>
        public void Connect()
        {
            Logger.LogLine("Before CONNECT", Logger.LogLevel.LOW);

            try
            {
                _connector.Connect();
            }
            catch (Exception e)
            {
                Debug.WriteLine(e.ToString());

                Logger.LogLine("CONNECT Exception caught: " + e.Message, Logger.LogLevel.LOW);

                throw; 
            }
        }

        /// <summary>
        /// Close Jade connector
        /// </summary>
        public void Disconnect()
        {
            _connector.Close();
        }

        /// <summary>
        /// Send an ACLMessage
        /// </summary>
        /// <param name="msg">ACL Message to send</param>
        public void Send(ACLMessage msg)
        {
             _connector.SendMessage(msg);
        }

        #endregion

        #region Protected Methods

        /// <summary>
        /// Generates new random conversation ID
        /// </summary>
        /// <param name="prepend">Prefix part of conversation ID to be generated</param>
        /// <returns>New conversation ID</returns>
        protected string NextConversationID(string prepend)
        {
            return prepend + _random.Next().ToString();
        }

        /// <summary>
        /// Sends a new IFrame message with a random conversation ID
        /// </summary>
        /// <param name="frame">Content to send</param>
        protected void SendFrameMessage(IFrame frame)
        {
            SendFrameMessage(frame, NextConversationID(frame.TypeName));
        }

        /// <summary>
        /// Sends a new IFrame message
        /// </summary>
        /// <param name="frame">Content to send</param>
        /// <param name="conversationId">Conversation ID</param>
        protected void SendFrameMessage(IFrame frame, string conversationId)
        {
            _acl.ConversationID = conversationId;

            byte[] data = _codec.Encode(frame);
            
            _acl.AddUserParameter(JadeConstants.PACKET_LENGTH, data.Length.ToString());
            _acl.Content = data;
            
            _connector.SendMessage(_acl);
        }

        #endregion

        #region JadeConnector Methods

        /// <summary>
        /// Handler of ConnectionEvent subscripted from JadeConnector
        /// </summary>
        /// <param name="sender">Subscripted JadeConnector</param>
        /// <param name="args">Received connection event</param>
        private void _connector_ConnectionEvent(JadeConnector sender, ConnectionEventArgs args)
        {
            switch (args.EventType)
            {
                case ConnectionEventType.connected:
                    _IsConnected = true;
                    _IsOnline = true;
                    _ErrorReason = string.Empty;
                    break;
                case ConnectionEventType.disconnected:
                    _IsConnected = false;
                    _IsOnline = false;
                    _ErrorReason = string.Empty;
                    break;
                case ConnectionEventType.error:
                    _IsConnected = false;
                    _IsOnline = false;
                    _ErrorReason = args.Reason;
                    break;
            }

            if (ConnectionEvent != null)
                ConnectionEvent(sender, args);
        }

        /// <summary>
        /// Handler of ReceiveEvent subscripted from JadeConnector
        /// </summary>
        /// <param name="sender">Subscripted JadeConnector</param>
        /// <param name="message">Received receive event</param>
        private void _connector_ReceiveEvent(JadeConnector sender, ACLMessage message)
        {
            if (ReceiveMessageEvent != null)
                ReceiveMessageEvent(sender, message);
        }

        #endregion
    }
}
