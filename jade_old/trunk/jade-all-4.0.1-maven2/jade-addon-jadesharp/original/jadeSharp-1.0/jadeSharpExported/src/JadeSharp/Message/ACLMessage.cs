using System;
using System.Collections;
using System.IO;
using System.Text;

namespace JadeSharp
{
    /// <summary>
    /// Summary description for ACLMessage.
    /// </summary>
    public class ACLMessage
    {
        #region Consts

        private const byte MESSAGE_OUT = 24;    // from BackEndStub
        private const byte STRING_ID = 1;       // from SerializationEngine
        private const byte ACL_ID = 2;          // from SerializationEngine

        #endregion

        #region Performative

        /** constant identifying the FIPA performative **/
        public const int ACCEPT_PROPOSAL = 0;
        /** constant identifying the FIPA performative **/
        public const int AGREE = 1;
        /** constant identifying the FIPA performative **/
        public const int CANCEL = 2;
        /** constant identifying the FIPA performative **/
        public const int CFP = 3;
        /** constant identifying the FIPA performative **/
        public const int CONFIRM = 4;
        /** constant identifying the FIPA performative **/
        public const int DISCONFIRM = 5;
        /** constant identifying the FIPA performative **/
        public const int FAILURE = 6;
        /** constant identifying the FIPA performative **/
        public const int INFORM = 7;
        /** constant identifying the FIPA performative **/
        public const int INFORM_IF = 8;
        /** constant identifying the FIPA performative **/
        public const int INFORM_REF = 9;
        /** constant identifying the FIPA performative **/
        public const int NOT_UNDERSTOOD = 10;
        /** constant identifying the FIPA performative **/
        public const int PROPOSE = 11;
        /** constant identifying the FIPA performative **/
        public const int QUERY_IF = 12;
        /** constant identifying the FIPA performative **/
        public const int QUERY_REF = 13;
        /** constant identifying the FIPA performative **/
        public const int REFUSE = 14;
        /** constant identifying the FIPA performative **/
        public const int REJECT_PROPOSAL = 15;
        /** constant identifying the FIPA performative **/
        public const int REQUEST = 16;
        /** constant identifying the FIPA performative **/
        public const int REQUEST_WHEN = 17;
        /** constant identifying the FIPA performative **/
        public const int REQUEST_WHENEVER = 18;
        /** constant identifying the FIPA performative **/
        public const int SUBSCRIBE = 19;
        /** constant identifying the FIPA performative **/
        public const int PROXY = 20;
        /** constant identifying the FIPA performative **/
        public const int PROPAGATE = 21;
        /** constant identifying an unknown performative **/
        public const int UNKNOWN = -1;

        #endregion

        #region Static Area

        private static string[] _Performatives = new string[22];

        /**
        * User defined parameter key specifying, when set to "true", that if the delivery of a 
        * message fails, no FAILURE notification has to be sent back to the sender.
        */
        public static string IGNORE_FAILURE = "JADE-ignore-failure";

        static ACLMessage()
        {
            _Performatives[ACCEPT_PROPOSAL] = "ACCEPT-PROPOSAL";
            _Performatives[AGREE] = "AGREE";
            _Performatives[CANCEL] = "CANCEL";
            _Performatives[CFP] = "CFP";
            _Performatives[CONFIRM] = "CONFIRM";
            _Performatives[DISCONFIRM] = "DISCONFIRM";
            _Performatives[FAILURE] = "FAILURE";
            _Performatives[INFORM] = "INFORM";
            _Performatives[INFORM_IF] = "INFORM-IF";
            _Performatives[INFORM_REF] = "INFORM-REF";
            _Performatives[NOT_UNDERSTOOD] = "NOT-UNDERSTOOD";
            _Performatives[PROPOSE] = "PROPOSE";
            _Performatives[QUERY_IF] = "QUERY-IF";
            _Performatives[QUERY_REF] = "QUERY-REF";
            _Performatives[REFUSE] = "REFUSE";
            _Performatives[REJECT_PROPOSAL] = "REJECT-PROPOSAL";
            _Performatives[REQUEST] = "REQUEST";
            _Performatives[REQUEST_WHEN] = "REQUEST-WHEN";
            _Performatives[REQUEST_WHENEVER] = "REQUEST-WHENEVER";
            _Performatives[SUBSCRIBE] = "SUBSCRIBE";
            _Performatives[PROXY] = "PROXY";
            _Performatives[PROPAGATE] = "PROPAGATE";
        }

        #endregion

        #region Fields

        // keeps the performative type of this object
        private int _Performative;

        private string _Encoding;
        private string _Ontology;
        private string _Language;
        private string _Protocol;
        private string _ConversationId;
        private string _InReplyTo;
        private string _ReplyWith;
        private string _SenderAID;
        private string _ReceiverAID;
        private string _StringContent = null;
        private byte[] _Content;
        private bool _IsStringContent;
        private Hashtable _Props = new Hashtable();

        #endregion

        #region Constructor

        internal ACLMessage() : this(UNKNOWN) { }

        public ACLMessage(int performative)
        {
            _Performative = performative;
            _ConversationId = (new Random().Next(int.MaxValue - 1) * new Random().Next(int.MaxValue - 1)) + "";
        }

        #endregion

        #region Properties

        /// <summary>
        /// Get all the performatives defined for ACL Messages
        /// </summary>
        public static string[] Performatives
        {
            get { return _Performatives; }
        }

        /// <summary>
        /// Get all the performatives defined for ACL Messages returned as a string
        /// </summary>
        public string PerformativeAsString
        {
            get { return _Performatives[_Performative]; }
        }

        /// <summary>
        /// Get or set the String Encoding field of ACL Message
        /// </summary>
        public string StringEncoding
        {
            get { return _Encoding; }
            set { _Encoding = value; }
        }

        /// <summary>
        ///  Get or set the Ontology field of ACL Message
        /// </summary>
        public string Ontology
        {
            get { return _Ontology; }
            set { _Ontology = value; }
        }

        /// <summary>
        ///  Get or set the Language field of ACL Message
        /// </summary>
        public string Language
        {
            get { return _Language; }
            set { _Language = value; }
        }

        /// <summary>
        ///  Get or set the Protocol field of ACL Message
        /// </summary>
        public string Protocol
        {
            get { return _Protocol; }
            set { _Protocol = value; }
        }

        /// <summary>
        ///  Get or set the ConversationID field of ACL Message
        /// </summary>
        public string ConversationID
        {
            get { return _ConversationId; }
            set { _ConversationId = value; }
        }

        /// <summary>
        ///  Get or set the InReplyTo field of ACL Message
        /// </summary>
        public string InReplyTo
        {
            get { return _InReplyTo; }
            set { _InReplyTo = value; }
        }

        /// <summary>
        ///  Get or set the ReplyWith field of ACL Message
        /// </summary>
        public string ReplyWith
        {
            get { return _ReplyWith; }
            set { _ReplyWith = value; }
        }

        /// <summary>
        ///  Get or set the Sender field of ACL Message
        /// </summary>
        public string Sender
        {
            get { return _SenderAID; }
            set { _SenderAID = value; }
        }

        /// <summary>
        ///  Get or set the Receiver field of ACL Message
        /// </summary>
        public string Receiver
        {
            get { return _ReceiverAID; }
            set { _ReceiverAID = value; }
        }

        /// <summary>
        ///  Get or set the Performative field of ACL Message
        /// </summary>
        public int Performative
        {
            get { return _Performative; }
            set { _Performative = value; }
        }

        /// <summary>
        ///  Get or set the Content field of ACL Message
        /// </summary>
        public byte[] Content
        {
            get { return _Content; }
            set
            {
                _Content = value;
                _IsStringContent = false;
            }
        }

        /// <summary>
        /// This method allows to check if the content of this ACLMessage is a byteSequence or a String
        /// </summary>
        public bool HasByteSequenceCOntent
        {
            get { return !_IsStringContent; }
        }

        /// <summary>
        /// Get or set content as a string
        /// </summary>
        public string ContentAsString
        {
            get
            {
                if (_StringContent != null)
                    return _StringContent;

                if (_Content != null)
                    _StringContent = ConvertData(_Content);

                return _StringContent;
            }
            set
            {
                _IsStringContent = true;
                _StringContent = value;
                _Content = (value == null) ? null : Encoding.ASCII.GetBytes(value);
            }
        }

        #endregion

        #region Public Methods

        public void AddUserParameter(string key, string val)
        {
            _Props[key] = val;
        }

        public void RemoveUserParameter(string key)
        {
            _Props.Remove(key);
        }

        public string GetUserParameter(string key)
        {
            return (string)_Props[key];
        }

        public void ClearUserParameters()
        {
            _Props.Clear();
        }

        public Hashtable UserParameters
        {
            get { return _Props; }
        }

        /**
        * create a new ACLMessage that is a reply to this message.
        * In particular, it sets the following parameters of the new message:
        * receiver, language, ontology, protocol, conversation-id,
        * in-reply-to, reply-with.
        * The programmer needs to set the communicative-act and the content.
        * Of course, if he wishes to do that, he can reset any of the fields.
        */
        public ACLMessage CreateReply(int performative)
        {
            ACLMessage acl = new ACLMessage(performative);

            acl.Language = _Language;
            acl.Ontology = _Ontology;
            acl.Protocol = _Protocol;
            acl.ConversationID = _ConversationId;
            acl.InReplyTo = _ReplyWith;
            acl.Receiver = _InReplyTo != null ? _InReplyTo : _SenderAID;
            acl.Sender = _ReceiverAID;

            return acl;
        }

        #endregion

        #region Internal Methods

        /// <summary>
        /// Write ACLMessage byte array in specified network stream
        /// </summary>
        /// <param name="stream">Available network stream</param>
        internal void WriteTo(DataStream stream)
        {
            // this actually serialize into a Command that contain this ACLMessage
            // and the sender (so 2 parameters in the Command)

            // start Command header from SerializationEngine
            stream.WriteByte(MESSAGE_OUT);
            stream.WriteByte(2);  // Command parameter count
            stream.WriteByte(ACL_ID);
            // end Command header

            // start ACL serialization
            stream.WriteByte((byte)_Performative);
            byte presence1 = 0;
            byte presence2 = 0;

            //if (_senderAID != null) {presence1 |= 0x80;}
            if (_Language != null)
            { 
                presence1 |= 0x40; 
            }
            
            if (_Ontology != null)
            { 
                presence1 |= 0x20; 
            }
            
            if (_Encoding != null) 
            { 
                presence1 |= 0x10; 
            }
            
            if (_Protocol != null) 
            { 
                presence1 |= 0x08; 
            }
            
            if (_ConversationId != null) 
            { 
                presence1 |= 0x04; 
            }
            
            if (_InReplyTo != null) 
            { 
                presence1 |= 0x02; 
            }
            
            if (_ReplyWith != null) 
            { 
                presence1 |= 0x01; 
            }
            
            //if (_replyBy != null) {presence2 |= 0x80;}
            
            presence2 |= (byte)(_Props.Count & 0x3F);

            stream.WriteByte(presence1);
            stream.WriteByte(presence2);

            // the sender is not used in the ACL message the Command 
            // has the sender already (see later)
            
            /*
            if (_senderAID != null) {
                stream.WriteByte(0x80); // serialize AID (only name is present)
                WriteUTF(_senderAID, stream);
            }
            */

            if (_Language != null) 
                stream.WriteUTF(_Language);

            if (_Ontology != null) 
                stream.WriteUTF(_Ontology);

            if (_Encoding != null) 
                stream.WriteUTF(_Encoding);

            if (_Protocol != null) 
                stream.WriteUTF(_Protocol);

            if (_ConversationId != null)
                stream.WriteUTF(_ConversationId);

            if (_InReplyTo != null) 
                stream.WriteUTF(_InReplyTo);

            if (_ReplyWith != null) 
                stream.WriteUTF(_ReplyWith);

            // if (_replyBy != null) { dos.writeLong(replyBy.getTime()); }

            // serialize user properties
            foreach (DictionaryEntry entry in _Props)
            {
                stream.WriteUTF((string)entry.Key);
                stream.WriteUTF((string)entry.Value);
            }

            // serialize receivers (there's only one)
            if (_ReceiverAID != null)
            {
                stream.WriteBoolean(true);
                stream.WriteByte(0x80); // serialize AID (only name is present)
                stream.WriteUTF(_ReceiverAID);
            }
            stream.WriteBoolean(false);

            // serialize reply-to...
            stream.WriteBoolean(false); // ...no reply-to is present 

            // serialzie content
            if (_Content != null)
            {
                if (_IsStringContent)
                {
                    // content present in string form
                    stream.WriteByte(1);
                }
                else
                {
                    // content present in binary form
                    stream.WriteByte(2);
                }

                // don't use writeUTF to avoid the 2 bytes length limitation
                stream.WriteInt(_Content.Length);
                stream.Write(_Content, 0, _Content.Length);
            }
            else
            {
                // no content present
                stream.WriteByte(0);
            }
            // end ACL

            // serialize the sender in the Command as from SerializationEngine
            stream.WriteByte(STRING_ID);
            stream.WriteUTF(_SenderAID);
        }

        /// <summary>
        /// Read an ACLMessage form network stream
        /// </summary>
        /// <param name="stream">Available Network Stream</param>
        internal void ReadFrom(DataStream stream)
        {
            // this actually deserialize a a Command that contain an ACLMessage
            // and the sender (so 2 parameters in the Command)

            // start command header
            // skip first byte because is always MESSAGE_IN
            int i = stream.ReadByte();

            i = stream.ReadByte();
            if (i != 2)
            {
                Logger.LogLine("Unexpected number of Command parameters " + i, Logger.LogLevel.LOW);
                throw new Exception("Unexpected number of Command parameters " + i);
            }

            i = stream.ReadByte();
            if (i != ACL_ID)
            {
                Logger.LogLine("Unexpected ACL type " + i, Logger.LogLevel.LOW);
                throw new Exception("Unexpected ACL type " + i);
            }
            // end Command header

            _Performative = stream.ReadByte();
            int presence1 = stream.ReadByte();
            int presence2 = stream.ReadByte();

            if ((presence1 & 0x80) != 0)
                _SenderAID = DeserializeAID(stream);

            if ((presence1 & 0x40) != 0)
                _Language = stream.ReadUTF();

            if ((presence1 & 0x20) != 0)
                _Ontology = stream.ReadUTF();

            if ((presence1 & 0x10) != 0)
                _Encoding = stream.ReadUTF();

            if ((presence1 & 0x08) != 0)
                _Protocol = stream.ReadUTF();

            if ((presence1 & 0x04) != 0)
                _ConversationId = stream.ReadUTF();

            if ((presence1 & 0x02) != 0)
                _InReplyTo = stream.ReadUTF();

            if ((presence1 & 0x01) != 0)
                _ReplyWith = stream.ReadUTF();

            if ((presence2 & 0x80) != 0)
                stream.ReadInt(); // DATE

            // User defined properties
            int propsSize = presence2 & 0x3F;
            for (i = 0; i < propsSize; ++i)
            {
                string key = stream.ReadUTF();
                string val = stream.ReadUTF();
                _Props[key] = val;
            }

            // Receivers
            while (stream.ReadBoolean())
                _ReceiverAID = DeserializeAID(stream);

            // Reply-to
            while (stream.ReadBoolean())
                DeserializeAID(stream);

            // Content
            i = stream.ReadByte();
            if (i > 0)
            {
                _IsStringContent = i == 1;
                _Content = new byte[stream.ReadInt()];
                stream.Read(_Content, 0, _Content.Length);
            }

            // deserialize the sender in the Command as from SerializationEngine
            i = stream.ReadByte();
            if (i != STRING_ID)
            {
                Logger.LogLine("Unexpected STRING type " + i, Logger.LogLevel.LOW);
                throw new Exception("Unexpected STRING type " + i);
            }

            stream.ReadUTF();
        }

        /// <summary>
        /// Get AID of Sender/Receiver if current ACLMessage from Network Stream
        /// </summary>
        /// <param name="stream">Available Network Stream</param>
        /// <returns>String representation of AID extracted</returns>
        private string DeserializeAID(DataStream stream)
        {
            string name = null;
            int presence = stream.ReadByte();
            if ((presence & 0x80) != 0)
            {
                // deserialize name
                name = stream.ReadUTF();
            }

            if ((presence & 0x40) != 0)
            { // Addresses
                do
                {
                    stream.ReadUTF();
                } while (stream.ReadBoolean());
            }

            if ((presence & 0x20) != 0)
            { // Resolvers
                do
                {
                    DeserializeAID(stream);
                } while (stream.ReadBoolean());
            }

            int propsSize = presence & 0x1F; // User defined slots
            for (int i = 0; i < propsSize; ++i)
            {
                stream.ReadUTF(); // key
                stream.ReadUTF(); // value
            }
            return name;
        }

        #endregion

        #region Private Methods

        /// <summary>
        /// Convert byte array into string
        /// </summary>
        /// <param name="asciiBytes">Data to be converted</param>
        /// <returns>String representation of input byte array</returns>
        private string ConvertData(byte[] asciiBytes)
        {
            char[] asciiChars = new char[Encoding.ASCII.GetCharCount(asciiBytes, 0, asciiBytes.Length)];
            Encoding.ASCII.GetChars(asciiBytes, 0, asciiBytes.Length, asciiChars, 0);
            return new string(asciiChars);
        }

        #endregion

        #region Override Methods

        public override string ToString()
        {
            StringBuilder sb = new StringBuilder("performative: ");
            sb.Append(_Performatives[_Performative]);
            if (_Encoding != null)
            {
                sb.Append(Consts.NEWLINE);
                sb.Append("Encoding ");
                sb.Append(_Encoding);
            }
            if (_Ontology != null)
            {
                sb.Append(Consts.NEWLINE);
                sb.Append("Ontology ");
                sb.Append(_Ontology);
            }
            if (_Protocol != null)
            {
                sb.Append(Consts.NEWLINE);
                sb.Append("Protocol ");
                sb.Append(_Protocol);
            }
            if (_Language != null)
            {
                sb.Append(Consts.NEWLINE);
                sb.Append("Language ");
                sb.Append(_Language);
            }
            if (_ConversationId != null)
            {
                sb.Append(Consts.NEWLINE);
                sb.Append("ConversationID ");
                sb.Append(_ConversationId);
            }
            if (_InReplyTo != null)
            {
                sb.Append(Consts.NEWLINE);
                sb.Append("InReplyTo ");
                sb.Append(_InReplyTo);
            }
            if (_ReplyWith != null)
            {
                sb.Append(Consts.NEWLINE);
                sb.Append("ReplyWith ");
                sb.Append(_ReplyWith);
            }
            if (_SenderAID != null)
            {
                sb.Append(Consts.NEWLINE);
                sb.Append("Sender ");
                sb.Append(_SenderAID);
            }
            if (_ReceiverAID != null)
            {
                sb.Append(Consts.NEWLINE);
                sb.Append("Receiver ");
                sb.Append(_ReceiverAID);
            }
            if (_Content != null)
            {
                sb.Append(Consts.NEWLINE);
                sb.Append("Content ");
                if (_IsStringContent) sb.Append(ContentAsString);
                else
                {
                    sb.Append("is binary, length: ");
                    sb.Append(_Content.Length);
                }
            }
            if (_Props.Count > 0)
            {
                sb.Append(Consts.NEWLINE);
                sb.Append("User Properties:");
                foreach (DictionaryEntry entry in _Props)
                {
                    sb.Append(Consts.NEWLINE);
                    sb.Append("[");
                    sb.Append(entry.Key);
                    sb.Append("] = ");
                    sb.Append(entry.Value);
                }
            }
            return sb.ToString();
        }

        #endregion
    }
}
