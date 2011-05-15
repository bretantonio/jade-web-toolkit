using System;

namespace JadeSharp
{
    /// <summary>
    /// Summary description for JICPConstants.
    /// </summary>
    /// 
    public class JICPConstants
    {
        #region JICP Packet types

        /** ID code for packets carrying IMTP commands */
        public const int COMMAND_TYPE = 0;
        /** ID code for packets carrying IMTP responses */
        public const int RESPONSE_TYPE = 1;
        /** ID code for packets carrying keep-alive  */
        public const int KEEP_ALIVE_TYPE = 2;
        /** ID code for packets carrying requests to get the local address */
        public const int GET_ADDRESS_TYPE = 21;
        /** ID code for packets carrying requests to create a Mediator */
        public const int CREATE_MEDIATOR_TYPE = 22;
        /** ID code for packets carrying requests to connect to a Mediator */
        public const int CONNECT_MEDIATOR_TYPE = 23;
        /** ID code for packets carrying requests to drop-down the connection with the mediator */
        public const int DROP_DOWN_TYPE = 30;
        /** ID code for packets carrying JICP protocol errors */
        public const int ERROR_TYPE = 100;

        #endregion

        #region Bit encoded data info constants
         
        public const int DEFAULT_INFO = 0;                  // All bits = 0
        public const int COMPRESSED_INFO = 1;               // bit 1 == 1
        public const int RECIPIENT_ID_PRESENT_INFO = 2;     // bit 2 == 1
        public const int SESSION_ID_PRESENT_INFO = 4;       // bit 3 == 1
        public const int DATA_PRESENT_INFO = 8;             // bit 4 == 1
        public const int RECONNECT_INFO = 16;               // bit 5 == 1 
        public const int OK_INFO = 32;                      // bit 6 == 1
        public const int TERMINATED_INFO = 64;              // bit 7 == 1	

        #endregion

        #region Configuration keys
        
        public const string LOCAL_PORT_KEY = "local-port";
        public const string LOCAL_HOST_KEY = "local-host";
        public const string REMOTE_URL_KEY = "remote-url";
        public const string UNREACHABLE_KEY = "unreachable";
        public const string RECONNECTION_RETRY_TIME_KEY = "reconnection-retry-time";
        public const string MAX_DISCONNECTION_TIME_KEY = "max-disconnection-time";
        public const string KEEP_ALIVE_TIME_KEY = "keep-alive-time";
        public const string DROP_DOWN_TIME_KEY = "drop-down-time";
        public const string MEDIATOR_CLASS_KEY = "mediator-class";
        public const string MEDIATOR_ID_KEY = "mediator-id";
        public const string OWNER_KEY = "owner";
        public const string AGENTS_KEY = "agents";
        public const string OUTCNT_KEY = "outcnt";
        public const string LASTID_KEY = "lastid";
        public const string PLATFORM_ID_KEY = "platform-id";

        #endregion

        #region Error messages

        public const string NOT_FOUND_ERROR = "Not-found";
        public const string NOT_AUTHORIZED_ERROR = "Not-authorized";

        #endregion
    }
}
