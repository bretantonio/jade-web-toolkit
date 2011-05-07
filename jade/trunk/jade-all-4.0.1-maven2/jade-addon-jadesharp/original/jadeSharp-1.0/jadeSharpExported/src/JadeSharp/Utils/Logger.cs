#region Using directives

using System;
using System.IO;

#endregion

namespace JadeSharp
{
    /// <summary>
    /// Generic Logger based on text file
    /// </summary>
    public class Logger
    {
        #region Static Fields

        // Name of logger file
        static private string LogFileName;
        // Streams needed by logger
        static private FileStream LogFD = null;
        static private StreamWriter LogSD = null;
        // Level to be assigned to each entry
        static private LogLevel level;

        #endregion

        #region Enums

        // To assign a priority for each entry in the logger
        public enum LogLevel
        {
            ALL,
            LOW,
            MEDIUM,
            HIGH
        }

        #endregion

        #region Public Static Methods

        /// <summary>
        /// Initialize the Logger.
        /// Logger file will be stored ALWAYS on My Documents folder
        /// </summary>
        /// <param name="filename">File name to be stored logger entries</param>
        /// <returns></returns> 
        static public int StartLog(string filename)
        {
            return StartLog(filename, LogLevel.HIGH);
        }

        /// <summary>
        /// Initialize the Logger with specified level
        /// Logger file will be stored ALWAYS on My Documents folder
        /// </summary>
        /// <param name="filename">File name to be stored logger entries</param>
        /// <param name="l">Start level for storing inserted entries</param>
        /// <returns></returns>
        static public int StartLog(string filename, LogLevel l)
        {
            int ret = -1;
            level = l;

            if (LogFD == null)
            {
                LogFileName = "\\My Documents\\" + filename;
                LogFD = new FileStream(LogFileName, FileMode.Create, FileAccess.Write);
                LogSD = new StreamWriter(LogFD);

                ret = 0;
            }

            return ret;
        }

        /// <summary>
        /// Insert a new entry in the Logger
        /// </summary>
        /// <param name="line">Entry to insert</param>
        /// <returns>Result of the insertion: 
        /// if 0 the entry has been inserted, 
        /// if -1 hasn't been inserted, 
        /// if 1 Log level was lower than the setted one</returns>
        static public int LogLine(string line)
        {
            return LogLine(line, LogLevel.LOW);
        }

        /// <summary>
        /// Insert a new entry in the Logger with Log level specification
        /// </summary>
        /// <param name="line">Entry to insert</param>
        /// <param name="lev">Log level to use</param>
        /// <returns>Result of the insertion: 
        /// if 0 the entry has been inserted, 
        /// if -1 hasn't been inserted, 
        /// if 1 Log level was lower than the setted one</returns>
        static public int LogLine(string line, LogLevel lev)
        {
            int ret = -1;
            if ((int)lev < (int)level)
                return 1;

            try
            {
                DateTime now = DateTime.Now;
                LogSD.WriteLine(now.ToString("dd/MM/yyyy - hh:mm:ss") + line);
                LogSD.Flush();

                System.Diagnostics.Debug.WriteLine(now.ToString("dd/MM/yyyy - hh:mm:ss") + line);

                ret = 0;
            }
            catch (FileNotFoundException ex)
            {
                string msg = ex.Message;
                ret = -1;
            }
            catch (Exception ex)
            {
                string msg = ex.Message;
                ret = -2;
            }

            return ret;
        }

        /// <summary>
        /// Stop and close the Logger.
        /// After the call to this method, the Logger won't be used.
        /// </summary>
        /// <returns></returns>
        static public int StopLog()
        {
            int ret = -1;

            if (LogFD != null)
            {
                LogSD.Close();
                LogSD = null;
                LogFD.Close();
                LogFD = null;


                ret = 0;
            }

            return ret;
        }

        /// <summary>
        /// Returns Setted Log Level of the Logger
        /// </summary>
        public static LogLevel Level
        {
            get
            {
                return level;
            }
            set
            {
                level = value;
            }
        }

        #endregion
    }
}

