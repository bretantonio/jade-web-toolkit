#region Using directives

using System;

#endregion

namespace JadeSharp
{
    /// <summary>
    /// Class to emulate Unix Time System.
    /// In Unix, the zero-day begin at 1st January, 1970
    /// </summary>
    public class UnixEpoch
    {
        #region Static Properties

        /// <summary>
        /// Returns initial Date and Time of Unix Epoch (1st January, 1970) tranformed
        /// in a .Net DateTime object
        /// </summary>
        public static DateTime StartOfEpoch
        {
            get
            {
                return new DateTime(1970, 1, 1);
            }
        }

        /// <summary>
        /// Returns Unix Epoch style of current time expressed as UTC
        /// </summary>
        public static long TimeElapsed
        {
            get
            {
                TimeSpan timestamp = (DateTime.UtcNow - StartOfEpoch);
                return (long) timestamp.TotalMilliseconds;

                // To use ONLY in case of little time difference between client and server

                // aggiungo 5 giorni al risultato precedente poichè ci
                // sono delle discrepanze (nei millisecondi ritornati) rispetto
                // al server dove gira il mediator.
                //long span = 2600000000;
                //return (long)timestamp.TotalMilliseconds + span;
            }
        }

        /// <summary>
        /// Returns current time expressed as milliseconds
        /// </summary>
        public static long TicksElapsed
        {
            get
            {
                return TimeElapsed / TimeSpan.TicksPerMillisecond;
            }
        }

        #endregion
    }
}
