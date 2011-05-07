using System;
using System.IO;
using System.Runtime.InteropServices;
using System.Text;

namespace JadeSharp
{
    /// <summary>
    /// This class is the C# equivalent of the Java's DataInputStream 
    /// and DataOutputStream to implement serialization of most basic 
    /// types and interoperate with Java.
    /// </summary>
    public class DataStream
    {
        #region Fields

        private Stream _Stream;
        private byte[] _Int64Buffer = new byte[8];

        #endregion

        public DataStream(Stream stream)
        {
            _Stream = stream;
        }

        #region Properties

        public bool CanRead
        {
            get { return _Stream.CanRead; }
        }

        public bool CanWrite
        {
            get { return _Stream.CanWrite; }
        }

        public bool CanSeek
        {
            get { return _Stream.CanSeek; }
        }

        public long Length
        {
            get { return _Stream.Length; }
        }

        public long Position
        {
            get { return _Stream.Position; }
            set { _Stream.Position = value; }
        }

        #endregion

        #region Public Methods

        public long Seek(long offset, SeekOrigin origin)
        {
            return _Stream.Seek(offset, origin);
        }

        public void SetLength(long val)
        {
            _Stream.SetLength(val);
        }

        public virtual void Close()
        {
            _Stream.Close();
        }

        public void Flush()
        {
            _Stream.Flush();
        }


        public void Read([In, Out] byte[] buffer, int offset, int count)
        {
            _Stream.Read(buffer, offset, count);
        }

        public virtual int ReadByte()
        {
            return _Stream.ReadByte();
        }

        public bool ReadBoolean()
        {
            return _Stream.ReadByte() == 0 ? false : true;
        }

        public short ReadShort()
        {
            int i = _Stream.ReadByte() << 8;
            i |= _Stream.ReadByte();
            return (short)i;
        }

        public int ReadInt()
        {
            int i = _Stream.ReadByte() << 24;
            i |= _Stream.ReadByte() << 16;
            i |= _Stream.ReadByte() << 8;
            i |= _Stream.ReadByte();
            return i;
        }

        public long ReadLong()
        {
            _Stream.Read(_Int64Buffer, 0, 8);
            long l = (long)((_Int64Buffer[0] & 0xFF) << 56);
            l |= (long)(_Int64Buffer[1] & 0xFF) << 48;
            l |= (long)(_Int64Buffer[2] & 0xFF) << 40;
            l |= (long)(_Int64Buffer[3] & 0xFF) << 32;
            l |= (long)(_Int64Buffer[4] & 0xFF) << 24;
            l |= (long)(_Int64Buffer[5] & 0xFF) << 16;
            l |= (long)(_Int64Buffer[6] & 0xFF) << 8;
            l |= (long)(_Int64Buffer[7] & 0xFF);
            return l;
        }

        /// <summary>
        /// Reads from the
        /// stream <code>in</code> a representation
        /// of a Unicode  character string encoded in
        /// <a href="DataInput.html#modified-utf-8">modified UTF-8</a> format;
        /// this string of characters is then returned as a <code>String</code>.
        /// The details of the modified UTF-8 representation
        /// are  exactly the same as for the <code>readUTF</code>
        /// method of <code>DataInput</code>.
        /// </summary>
        /// <returns>Result string</returns>
        /// <remarks>These method is copied from DataInputStream Java Class</remarks>
        public string ReadUTF()
        {
            int len = _Stream.ReadByte() << 8;

            len |= _Stream.ReadByte();
            
            byte[] data = new byte[len];
            _Stream.Read(data, 0, len);
            
            return Encoding.UTF8.GetString(data, 0, len);
        }

        public void Write(byte[] buffer, int offset, int count)
        {
            _Stream.Write(buffer, offset, count);
        }

        public void WriteByte(byte b)
        {
            _Stream.WriteByte(b);
        }

        public void WriteBoolean(bool b)
        {
            _Stream.WriteByte(b ? (byte)1 : (byte)0);
        }

        public void WriteShort(short s)
        {
            _Stream.WriteByte((byte)((s >> 8) & 0xFF));
            _Stream.WriteByte((byte)(s & 0xFF));
        }

        public void WriteInt(int i)
        {
            _Stream.WriteByte((byte)((i >> 24) & 0xFF));
            _Stream.WriteByte((byte)((i >> 16) & 0xFF));
            _Stream.WriteByte((byte)((i >> 8) & 0xFF));
            _Stream.WriteByte((byte)((i >> 0) & 0xFF));
        }

        public void WriteLong(long l)
        {
            _Int64Buffer[0] = (byte)(l >> 56);
            _Int64Buffer[1] = (byte)(l >> 48);
            _Int64Buffer[2] = (byte)(l >> 40);
            _Int64Buffer[3] = (byte)(l >> 32);
            _Int64Buffer[4] = (byte)(l >> 24);
            _Int64Buffer[5] = (byte)(l >> 16);
            _Int64Buffer[6] = (byte)(l >> 8);
            _Int64Buffer[7] = (byte)(l >> 0);
            _Stream.Write(_Int64Buffer, 0, 8);
        }

        /// Writes a string to the specified DataOutput using
        /// <a href="DataInput.html#modified-utf-8">modified UTF-8</a>
        /// encoding in a machine-independent manner. 
        /// <p>First, two bytes are written to out as if by the <code>writeShort</code>
        /// method giving the number of bytes to follow. This value is the number of
        /// bytes actually written out, not the length of the string. Following the
        /// length, each character of the string is output, in sequence, using the
        /// modified UTF-8 encoding for the character
        /// <remarks>These method is copied from DataOutputStream Java Class</remarks>
        public void WriteUTF(string s)
        {
            byte[] data = Encoding.UTF8.GetBytes(s);

            _Stream.WriteByte((byte)((data.Length >> 8) & 0xFF));
            _Stream.WriteByte((byte)(data.Length & 0xFF));
            _Stream.Write(data, 0, data.Length);
        }

        #endregion
    }
}
