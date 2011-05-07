#region Using directives

using System;
using System.Xml;
using System.IO;
using System.Reflection;
using System.Collections;

#endregion

namespace JadeSharp
{
    public class ConfigurationSettings
    {
        #region Fields

        private StreamReader _StreamReader;
        private FileStream _FileStream;
        private string _ConfigName = "app.properties";

        private static ConfigurationSettings _Instance;
        private static readonly object _Sync = new object();

        #endregion

        private ConfigurationSettings()
        {
            String dir = Path.GetDirectoryName(System.Reflection.Assembly.GetExecutingAssembly().GetName().CodeBase);

            _FileStream = new FileStream(dir + "\\" + _ConfigName, FileMode.Open, FileAccess.Read);
            _StreamReader = new StreamReader(_FileStream);
        }

        public static ConfigurationSettings Instance
        {
            get
            {
                lock (_Sync)
                {
                    if (_Instance == null)
                        _Instance = new ConfigurationSettings();
                }

                return _Instance;
            }
        }

        public Hashtable LoadValues()
        {
            Hashtable myHash = new Hashtable();

            string line = _StreamReader.ReadLine();

            while (line != null)
            {
                int idx = line.IndexOf('=');
                if (idx > 0)
                    myHash.Add(line.Substring(0, idx), line.Substring(idx + 1));

                line = _StreamReader.ReadLine();
            }

            return myHash;
        }
    }
}