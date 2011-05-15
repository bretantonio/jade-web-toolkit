using System;
using System.Collections;
using System.Text;

namespace JadeSharp
{
    /// <summary>
    /// Summary description for OrederedFrame.
    /// </summary>
    public class OrderedFrame : ArrayList, IFrame
    {
        #region Fields

        private string _TypeName;

        #endregion

        #region Constructors

        /// <summary>
        /// Create an OrderedFrame without an unknown type-name.
        /// </summary>
        public OrderedFrame() : this(null) { }

        /// <summary>
        /// Create an OrderedFrame with a given type-name.
        /// </summary>
        /// <param name="typeName">Type-name of the OrderedFrame to be created.</param>
        public OrderedFrame(string typeName)
            : base()
        {
            _TypeName = typeName;
        }

        #endregion

        #region Properties

        /// <summary>
        /// Retrieve the type-name of this OrderedFrame.
        /// </summary>
        /// <returns>Type-name of this OrderedFrame</returns>
        public string TypeName
        {
            get { return _TypeName; }
        }

        #endregion

        #region Override Methods

        public override string ToString()
        {
            StringBuilder sb = new StringBuilder("ORDERED frame ");
            sb.Append(_TypeName);
            sb.Append(Consts.NEWLINE);
            
            for (int i = 0; i < this.Count; ++i)
            {
                sb.Append(this[i]);
                sb.Append(Consts.NEWLINE);
            }

            return sb.ToString();
        }

        #endregion
    }
}
