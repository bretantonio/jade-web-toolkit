using System;
using System.IO;
using System.Text;
using System.Collections;

namespace JadeSharp
{
    /// <summary>
    /// Summary description for FrameCodec.
    /// </summary>
    public class FrameCodec
    {
        #region Consts

        public const string NAME = "LEAP";

        #endregion

        #region Primitive types

        private const byte STRING = 6;
        private const byte BOOLEAN = 7;
        private const byte INTEGER = 8;
        private const byte FLOAT = 9;
        private const byte DATE = 10;
        private const byte BYTE_SEQUENCE = 11;

        #endregion

        #region Structured types

        private const byte AGGREGATE = 1;
        private const byte CONTENT_ELEMENT_LIST = 2;
        private const byte OBJECT = 3;

        #endregion

        #region Markers for structured types

        private const byte ELEMENT = 4;
        private const byte END = 5;

        #endregion

        #region Modifiers for string encoding

        private const byte MODIFIER = 0x10;	// Only bit five set to 1
        private const byte UNMODIFIER = 0xEF;	// Only bit five cleared to 1

        #endregion

        #region Fields

        private MemoryStream _BufferOut;
        private DataStream _StreamOut;
        private DataStream _StreamIn;
        private ArrayList _StringRefs = new ArrayList();
        private static object _Sync = new object();

        #endregion

        public FrameCodec()
        {
            _BufferOut = new MemoryStream();
            _StreamOut = new DataStream(_BufferOut);
        }

        #region Public Methods

        public byte[] Encode(IFrame frame)
        {
            if (frame == null)
                return new byte[0];

            lock (_Sync)
            {
                _StreamOut.Seek(0, SeekOrigin.Begin);
                _StreamOut.SetLength(0);

                _StringRefs.Clear();

                WriteFrame(frame);

                _StreamOut.Flush();
            }
            return _BufferOut.ToArray();
        }

        public IFrame Decode(byte[] content)
        {
            if (content == null || content.Length == 0) 
                return null;

            lock (_Sync)
            {
                _StreamIn = new DataStream(new MemoryStream(content));

                _StringRefs.Clear();

                return (IFrame)ReadFrame();
            }
        }

        #endregion

        #region Private Methods

        private void WriteFrame(object obj)
        {
            System.Diagnostics.Debug.WriteLine("Object " + obj + " cannot be encoded");
        }

        private void WriteFrame(string s)
        {
            WriteString(STRING, s);
        }

        private void WriteFrame(bool b)
        {
            _StreamOut.WriteByte(BOOLEAN);
            _StreamOut.WriteBoolean(b);
        }

        private void WriteFrame(Int16 i16)
        {
            _StreamOut.WriteByte(INTEGER);
            _StreamOut.WriteLong((long)i16);
        }

        private void WriteFrame(Int32 i32)
        {
            _StreamOut.WriteByte(INTEGER);
            _StreamOut.WriteLong((long)i32);
        }

        private void WriteFrame(Int64 i64)
        {
            _StreamOut.WriteByte(INTEGER);
            _StreamOut.WriteLong(i64);
        }

        private void WriteFrame(DateTime dt)
        {
            _StreamOut.WriteByte(DATE);
            _StreamOut.WriteLong((dt.Ticks - UnixEpoch.StartOfEpoch.Ticks) / 10000);
        }

        private void WriteFrame(byte[]  b)
        {
            _StreamOut.WriteByte(BYTE_SEQUENCE);
            _StreamOut.WriteInt(b.Length);
            _StreamOut.Write(b, 0, b.Length);
        }

        private void WriteFrame(OrderedFrame  frame)
        {
            string typeName = frame.TypeName;
            if (typeName != null)
            {
                // AGGREGATE
                WriteString(AGGREGATE, typeName);
            }
            else
            {
                // CONTENT_ELEMENT_LIST
                _StreamOut.WriteByte(CONTENT_ELEMENT_LIST);
            }

            for (int i = 0; i < frame.Count; ++i)
            {
                _StreamOut.WriteByte(ELEMENT);
                WriteFrame(frame[i]);
            }

            _StreamOut.WriteByte(END);
        }

        private void WriteFrame(QualifiedFrame frame)
        {
            WriteString(OBJECT, frame.TypeName);
            
            foreach (DictionaryEntry entry in frame)
            {
                WriteString(ELEMENT, (string)entry.Key);
                WriteFrame(entry.Value);
            }

            _StreamOut.WriteByte(END);
        }
           
        private void WriteString(byte tag, string s)
        {
            int index = _StringRefs.IndexOf(s);
            if (index >= 0)
            {
                // Write the tag modified and just put the index
                _StreamOut.WriteByte((byte)(tag | MODIFIER));
                _StreamOut.WriteByte((byte)index);

            }
            else
            {
                _StreamOut.WriteByte(tag);
                _StreamOut.WriteUTF(s);
                if (s.Length > 1 && _StringRefs.Count < 256) _StringRefs.Add(s);
            }
        }

        private object ReadFrame()
        {
            int type = _StreamIn.ReadByte();

            // PRIMITIVES
            if ((type & UNMODIFIER) == STRING)
            {
                return ReadString(type);
            }

            if (type == BOOLEAN)
            {
                return _StreamIn.ReadBoolean();
            }

            if (type == INTEGER)
            {
                return _StreamIn.ReadLong();
            }

            if (type == DATE)
            {
                return UnixEpoch.StartOfEpoch.AddMilliseconds((double)_StreamIn.ReadLong());
            }

            if (type == BYTE_SEQUENCE)
            {
                byte[] b = new byte[_StreamIn.ReadInt()];
                _StreamIn.Read(b, 0, b.Length);
                return b;
            }

            // AGGREGATE or CONTENT_ELEMENT_LIST
            if ((type == CONTENT_ELEMENT_LIST) || ((type & UNMODIFIER) == AGGREGATE))
            {
                OrderedFrame frame;
                if (type == CONTENT_ELEMENT_LIST) frame = new OrderedFrame();
                else frame = new OrderedFrame(ReadString(type));

                int marker = _StreamIn.ReadByte();
                do
                {
                    if (marker == ELEMENT)
                    {
                        frame.Add(ReadFrame());
                        marker = _StreamIn.ReadByte();
                    }
                }
                while (marker != END);
                return frame;
            }

            // QUALIFIED_FRAME
            if ((type & UNMODIFIER) == OBJECT)
            {
                QualifiedFrame frame = new QualifiedFrame(ReadString(type));

                int marker = _StreamIn.ReadByte();
                do
                {
                    if ((marker & UNMODIFIER) == ELEMENT)
                    {
                        string key = ReadString(marker);
                        object val = ReadFrame();
                        frame[key] = val;
                        marker = _StreamIn.ReadByte();
                    }
                }
                while (marker != END);
                return frame;
            }

            //throw new Exception("Unexpected tag " + type);
            System.Diagnostics.Debug.WriteLine("Unexpected tag " + type);
            return null;
        }

        private string ReadString(int tag)
        {
            if ((tag & MODIFIER) != 0)
            {
                int index = _StreamIn.ReadByte();
                return (string)_StringRefs[index];
            }

            string s = _StreamIn.ReadUTF();
            if (s.Length > 1 && _StringRefs.Count < 256) _StringRefs.Add(s);
            return s;
        }

        #endregion
    }
}
