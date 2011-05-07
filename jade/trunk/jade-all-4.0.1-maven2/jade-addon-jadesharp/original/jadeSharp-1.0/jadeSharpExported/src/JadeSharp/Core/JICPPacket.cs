using System;
using System.IO;
using System.Text;
using System.Net.Sockets;

namespace JadeSharp
{
    /// <summary>
    /// Summary description for JICPPacket.
    /// </summary>
    public class JICPPacket : ICloneable
    {
        #region Fields

        /// <summary>
        /// The type of data included in the packet
        /// </summary>
        private int _Type;

        /// <summary>
        /// Bit encoded information about the content of the packet:
        /// </summary>
        private int _Info = 0;
        
        /// <summary>
        /// An optional identifier for the session this packet belongs to
        /// </summary>
        private int _SessionID = -1;

        /// <summary>
        /// An optional field indicating the actual recipient for this JICPPacket. 
        /// - A JICPServer receiving a JICPPacket from a remote container
        /// interprets this field as the ID of a local Mediator.
        /// - A Mediator receiving a JICPPacket from its mediated container
        /// interprets this field as the serialized transport address of 
        /// final destination to forward the packet.
        /// </summary>
        private string _RecipientID = null;

        /// <summary>
        /// The payload data itself, as a byte array
        /// </summary>
        private byte[] _Data;

        /// <summary>
        /// An optional identifier to check packet's state
        /// </summary>
        private bool _IsTerminated = false;

        #endregion

        #region Constructor

        public JICPPacket(int type) : this(type, (byte[])null) { }

        public JICPPacket(int type, string data) : this(type, Encoding.ASCII.GetBytes(data)) { }
        
        public JICPPacket(int type, byte[] data)
        {
            _Type = type;
            _Data = data;

            System.Diagnostics.Debug.AutoFlush = true;
        }

        #endregion

        #region Properties

        /// <summary>
        /// Get the type code of current JICPPacket
        /// </summary>
        public int TypeCode
        {
            get { return _Type; }
        }

        /// <summary>
        /// Get Error type flag of current JICPPacket
        /// </summary>
        public bool IsErrorType
        {
            get { return (_Type == JICPConstants.ERROR_TYPE); }
        }

        /// <summary>
        /// Get Response type flag of current JICPPacket
        /// </summary>
        public bool IsResponseType
        {
            get { return (_Type == JICPConstants.RESPONSE_TYPE); }
        }

        /// <summary>
        /// Get Command type flag of current JICPPacket
        /// </summary>
        public bool IsCommandType
        {
            get { return (_Type == JICPConstants.COMMAND_TYPE); }
        }

        /// <summary>
        /// Get or set the internal datas byte array of current JICPPacket
        /// </summary>
        public byte[] Data
        {
            get { return _Data; }
            set { _Data = value; }
        }

        /// <summary>
        /// Get or set the internal datas as string
        /// </summary>
        public string DataAsString
        {
            get { return ConvertData(_Data); }
            set { _Data = (value == null) ? null : Encoding.ASCII.GetBytes(value); }
        }

        /// <summary>
        /// Get or set the sessionID of this packet and adjust the info field
        /// accordingly.
        /// </summary>
        public int SessionID
        {
            get { return _SessionID; }
            set { _SessionID = value; }
        }

        /// <summary>
        /// Get or set the recipientID of this packet and adjust the info field
        /// accordingly.
        /// </summary>
        public string RecipientID
        {
            get { return _RecipientID; }
            set { _RecipientID = value; }
        }

        /// <summary>
        /// Get or set the TERMINATED_INFO flag in the info field.
        /// </summary>
        public bool Terminated
        {
            get { return _IsTerminated; }
            set { _IsTerminated = value; }
        }

        /// <summary>
        /// Get the validity of RECONNECT_INFO flag
        /// </summary>
        public bool Riconnect
        {
            get { return (_Info & JICPConstants.RECONNECT_INFO) != 0; }
        }

        #endregion

        #region Internal Methods

        /// <summary>
        /// Write JICPPacket data into network stream
        /// </summary>
        /// <param name="stream">Current available network stream</param>
        internal void WriteTo(Stream stream)
        {
            // Write the packet type
            stream.WriteByte((byte)_Type);

            int info = 0;
            if (_SessionID != -1) 
                info |= JICPConstants.SESSION_ID_PRESENT_INFO;

            if (_RecipientID != null) 
                info |= JICPConstants.RECIPIENT_ID_PRESENT_INFO;

            if (_Data != null) 
                info |= JICPConstants.DATA_PRESENT_INFO;

            if (_IsTerminated) 
                info |= JICPConstants.TERMINATED_INFO;

            stream.WriteByte((byte)info);

            // Write the session ID if present
            if (_SessionID != -1)
            {
                stream.WriteByte((byte)_SessionID);
            }

            // Write recipient ID if != null
            if (_RecipientID != null)
            {
                byte[] b = Encoding.ASCII.GetBytes(_RecipientID);

                stream.WriteByte((byte)b.Length);
                stream.Write(b, 0, b.Length);
            }

            // Write data if != null
            if (_Data != null)
            {
                // Size
                int size = _Data.Length;

                stream.WriteByte((byte)size);
                stream.WriteByte((byte)(size >> 8));
                stream.WriteByte((byte)(size >> 16));
                stream.WriteByte((byte)(size >> 24));
                
                // Payload
                if (_Data.Length > 0)
                {
                    stream.Write(_Data, 0, _Data.Length);
                }
            }
        }

        /// <summary>
        /// Reads from available network stream a valid JICPPacket.
        /// In case of error a special Error JICPPacket (with type code 100)
        /// will be returned.
        /// </summary>
        /// <param name="stream">Current available network stream</param>
        internal void ReadFrom(NetworkStream stream)
        {
            try
            {
                InternalReadFrom(stream);
            }
            catch (OverflowException oe)
            {
                Logger.LogLine("[JICPPacket] - ReadFrom: OverflowException caught -> " + oe.ToString(), Logger.LogLevel.HIGH);
                string errMsg = oe.Message;
                GenerateErrorPacket(null);

                throw oe;
            }
            catch (IOException ioe)
            {
                Logger.LogLine("[JICPPacket] - ReadFrom: IOException -> " + ioe.ToString(), Logger.LogLevel.HIGH);
                GenerateErrorPacket(null);

                throw ioe;
            }
            catch (Exception sre)
            {
                Logger.LogLine("[JICPPacket] - StreamReadingException -> " + sre.Message, Logger.LogLevel.MEDIUM);
                GenerateErrorPacket(null);

                throw sre;
            }
        }

        /// <summary>
        /// Reads from available network stream a valid JICPPacket.
        /// </summary>
        /// <param name="stream">Current available network stream</param>
        internal void InternalReadFrom(NetworkStream stream)
        {
            // reset values;
            _Info = -1;
            _Type = -1;
            _SessionID = -1;
            _RecipientID = null;
            _Data = null;

            try
            {
                _Type = stream.ReadByte();
                _Info = stream.ReadByte();
            }
            catch (ObjectDisposedException ode)
            {
                string msg = ode.Message;
                Logger.LogLine("ObjectDisposedException: " + msg, Logger.LogLevel.MEDIUM);

                throw new Exception(ode.Message);
            }
            catch (OverflowException oe)
            {
                string msg = oe.Message;
                Logger.LogLine("OverflowException Type & Info: " + msg, Logger.LogLevel.MEDIUM);

                throw new Exception(oe.Message);
            }

            // Read session ID if present
            if ((_Info & JICPConstants.SESSION_ID_PRESENT_INFO) != 0)
            {
                _SessionID = stream.ReadByte();
            }

            // Read recipient ID if present
            if ((_Info & JICPConstants.RECIPIENT_ID_PRESENT_INFO) != 0)
            {
                int qt = stream.ReadByte();
                Logger.LogLine("Quantity: " + qt);

                if (qt == -1)
                {
                    throw new Exception("STREAM_END_ERROR");
                }

                byte[] asciiBytes = ReadFully(stream, 0, qt);
                _RecipientID = ConvertData(asciiBytes);
            }

            // Read data if present
            if ((_Info & JICPConstants.DATA_PRESENT_INFO) != 0)
            {
                int originalSize;
                int size = stream.ReadByte();
                originalSize = size;

                size |= (stream.ReadByte() & 0xFF) << 8;
                size |= (stream.ReadByte() & 0xFF) << 16;
                size |= (stream.ReadByte() & 0xFF) << 24;

                try
                {
                    _Data = new byte[size];
                }
                catch (OutOfMemoryException oome)
                {
                    System.Diagnostics.Debug.WriteLine(oome.Message);
                    Logger.LogLine("OutOfMemoryException: " + oome.Message, Logger.LogLevel.MEDIUM);

                    size = 1;
                    _Data = new byte[size];
                }
                catch (OverflowException oe)
                {
                    System.Diagnostics.Debug.WriteLine(oe.Message);
                    Logger.LogLine("OverflowException: " + oe.Message, Logger.LogLevel.MEDIUM);

                    size = 1;
                    _Data = new byte[size];
                }
                finally
                {
                    try
                    {
                        _Data = ReadFully(stream, 0, size);
                    }
                    catch (Exception e)
                    {
                        Logger.LogLine("InternalReadFrom: Message -> " + e.Message);
                        Logger.LogLine("InternalReadFrom: Size -> " + size);
                        Logger.LogLine("InternalReadFrom: InnerException -> " + e.InnerException);
                    }
                }
            }
        }

        #endregion

        #region Private Methods

        /// <summary>
        /// Create a special JICPPacket to propagate error information on
        /// above layers during JICPPacket reading/writing.
        /// </summary>
        /// <param name="error">Error message to store inside created JICPPacket</param>
        private void GenerateErrorPacket(string error)
        {
            // Error value for Type Code is 100
            _Type = 100;

            // Sets other variable with invalid values
            _SessionID = -1;
            _Info = -1;
            _RecipientID = null;

            if (error != null)
                _Data = System.Text.Encoding.Unicode.GetBytes(error);
            else
                _Data = null;
        }

        /// <summary>
        /// Reads specified number of bytes form the network stream and
        /// returns read data in a byte array.
        /// </summary>
        /// <param name="stream"></param>
        /// <param name="start"></param>
        /// <param name="length"></param>
        /// <returns></returns>
        private byte[] ReadFully(Stream stream, int start, int length)
        {
            byte[] result = new byte[length];

            // MAX connection try possible
            int maxAttemps = 5;

            // Waiting time vetween two tries
            int msWait = 50;

            // Try counter
            int attemp = 0;

            // Total counter
            int total = 0;

            do
            {
                int n = stream.Read(result, start, length - total);
                if (n == 0 && attemp == maxAttemps)
                {
                    Logger.LogLine("STREAM ERROR", Logger.LogLevel.MEDIUM);
                    throw new Exception("GENERIC_ERROR");
                }
                else if (n == 0)
                {
                    attemp++;
                    System.Threading.Thread.Sleep(msWait);
                }
                else
                {
                    total += n;
                    start += n;
                }
            } 
            while (total < length);

            return result;
        } 

        /// <summary>
        /// Convert a data byte array into a string
        /// </summary>
        /// <param name="asciiBytes"></param>
        /// <returns></returns>
        private string ConvertData(byte[] asciiBytes)
        {
            int charNum = Encoding.ASCII.GetCharCount(asciiBytes, 0, asciiBytes.Length);
            char[] asciiChars = new char[charNum];
            Encoding.ASCII.GetChars(asciiBytes, 0, asciiBytes.Length, asciiChars, 0);

            return new string(asciiChars);
        }

        #endregion

        #region Override Methods

        public override string ToString()
        {
            StringBuilder sb = new StringBuilder();
            sb.Append("type=");
            sb.Append(_Type);
            if (_SessionID >= 0)
            {
                sb.Append(Consts.NEWLINE);
                sb.Append("session=");
                sb.Append(_SessionID);
            }
            if (_RecipientID != null)
            {
                sb.Append(Consts.NEWLINE);
                sb.Append("recipient=");
                sb.Append(_RecipientID);
            }
            if (_Data != null)
            {
                sb.Append(Consts.NEWLINE);
                sb.Append("data length ");
                sb.Append(_Data.Length);
            }
            return sb.ToString();
        }

        // ICloneable
        public object Clone()
        {
            JICPPacket clone = new JICPPacket(_Type);
            clone._Info = _Info;
            clone._IsTerminated = _IsTerminated;
            clone._RecipientID = _RecipientID;
            clone._Data = _Data;

            return clone;
        }

        #endregion
                
    }
}
