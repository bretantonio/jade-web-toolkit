#region Using directives

using System;

#endregion

namespace JadeSharp
{
    public enum ConnectionEventType
    {
        connected,
        disconnected,
        error
    }

    public enum EVENT
    {
        PULSE = 1,
        RESET = 2,
        SET = 3,
    }
}
