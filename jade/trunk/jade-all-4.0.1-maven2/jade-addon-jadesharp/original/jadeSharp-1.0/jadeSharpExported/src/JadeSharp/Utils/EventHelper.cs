#region Using directives

using System;
using System.Runtime.InteropServices;

using EVENT = JadeSharp.EVENT;

#endregion

namespace JadeSharp
{
    /// <summary>
    /// An helper class to manage P/Invoke calls to native functions useful in handling 
    /// events in Windows Mobile.
    /// </summary>
    public class EventHelper
    {
        #region P/Invoke

        [DllImport("coredll.dll")]
        public extern static int WaitForSingleObject(IntPtr Handle, uint Wait);

        [DllImport("coredll.dll", EntryPoint = "CreateEvent", SetLastError = true)]
        public static extern IntPtr CreateEvent(IntPtr lpEventAttributes,
               bool bManualReset, bool bInitialState, string lpName);

        [DllImport("coredll.dll", SetLastError = true)]
        private static extern bool EventModify(IntPtr hEvent, EVENT ef);

        public static bool SetEvent(IntPtr hEvent) { return EventModify(hEvent, EVENT.SET); }
        public static bool ResetEvent(IntPtr hEvent) { return EventModify(hEvent, EVENT.RESET); }

        #endregion

        #region P/Invoke Consts

        public const uint INFINITE = 0xFFFFFFFF;

        public const uint WAIT_OBJECT_0 = 0;
        public const uint WAIT_TIMEOUT = 0x00000102;

        #endregion
    }
}
