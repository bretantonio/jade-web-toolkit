using System;
using System.Collections;
using System.Text;

namespace JadeSharp
{
    /// <summary>
    /// Summary description for QualifiedFrame.
    /// </summary>
    public class QualifiedFrame : Hashtable, IFrame
    {
        #region Fields

        private string _TypeName;

        #endregion

        #region Constructors

        /// <summary>
        /// Create a QualifiedFrame with a given type-name.
        /// </summary>
        /// <param name="typeName">Type-name of the QualifiedFrame to be created.</param>
        public QualifiedFrame(string typeName) : base()
        {
            if (typeName == null)
            {
                //throw new Exception("Type name cannot be null");
                System.Diagnostics.Debug.WriteLine("Type name cannot be null");
                typeName = "FakeName";
            }

            _TypeName = typeName;
        }

        #endregion

        #region Properties

        /// <summary>
        /// Retrieve the type-name of this QualifiedFrame.
        /// </summary>
        /// <returns>Type-name of this QualifiedFrame</returns>
        public string TypeName
        {
            get { return _TypeName; }
        }

        #endregion

        #region Override Methods

        public override string ToString()
        {
            StringBuilder sb = new StringBuilder("QUALIFIED frame ");
            sb.Append(_TypeName);
            sb.Append(Consts.NEWLINE);

            foreach (DictionaryEntry entry in this)
            {
                sb.Append(entry.Key);
                sb.Append(" = ");
                sb.Append(entry.Value);
                sb.Append(Consts.NEWLINE);
            }

            return sb.ToString();
        }

        #endregion
    }
}
