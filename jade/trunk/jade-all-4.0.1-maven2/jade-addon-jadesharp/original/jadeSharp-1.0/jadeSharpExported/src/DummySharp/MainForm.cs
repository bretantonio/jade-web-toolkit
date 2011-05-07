#region Using directives

using System;
using System.Drawing;
using System.Collections;
using System.Windows.Forms;
using System.Data;

using JadeSharp;

#endregion

namespace DummySharp
{
    /// <summary>
    /// Summary description for form.
    /// </summary>
    public class MainForm : System.Windows.Forms.Form
    {
        private Label lblReceiver;
        private TextBox txtReceiver;
        private Label lblMessage;
        private TextBox txtMessage;
        /// <summary>
        /// Main menu for the form.
        /// </summary>
        private System.Windows.Forms.MainMenu mainMenu1;

        #region Fields

        private NAgent _JadeSharpAgent;

        private bool _Exiting;
        private bool _FirstTime;
        private ConnectionEventArgs _LastConnectionEvent;

        private System.Windows.Forms.Timer tmr;

        private ListBox lstMessages;
        private Panel panel1;
        private Panel panel2;
        private Label lblPerformative;
        private ComboBox cmbPerformative;
        private CheckBox chkLocalName;
        private Panel pnlWarning;
        private MenuItem mItemConnection;
        private MenuItem mItemOptions;
        private MenuItem mItemSendMessage;
        private MenuItem mItemClearText;
        private MenuItem mItemSettings;
        private Label lblStatus;
        private MenuItem mItemAbout;
        private MenuItem mItemExit;
        private PictureBox pictureBoxLogo;
        private IList _MessageList;

        #endregion

        public MainForm()
        {
            InitializeComponent();

            Logger.StartLog("JadeSharp.txt");
            _Exiting = false;
            _FirstTime = true;

            _LastConnectionEvent = null;

            tmr = new Timer();
            tmr.Interval = 2000;
            tmr.Tick += new EventHandler(tmr_Tick);

            InitializeAgent();
            InitializeList();
        }

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        protected override void Dispose(bool disposing)
        {
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code
        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(MainForm));
            this.mainMenu1 = new System.Windows.Forms.MainMenu();
            this.mItemConnection = new System.Windows.Forms.MenuItem();
            this.mItemOptions = new System.Windows.Forms.MenuItem();
            this.mItemSendMessage = new System.Windows.Forms.MenuItem();
            this.mItemClearText = new System.Windows.Forms.MenuItem();
            this.mItemSettings = new System.Windows.Forms.MenuItem();
            this.mItemAbout = new System.Windows.Forms.MenuItem();
            this.mItemExit = new System.Windows.Forms.MenuItem();
            this.lblReceiver = new System.Windows.Forms.Label();
            this.txtReceiver = new System.Windows.Forms.TextBox();
            this.lblMessage = new System.Windows.Forms.Label();
            this.txtMessage = new System.Windows.Forms.TextBox();
            this.lstMessages = new System.Windows.Forms.ListBox();
            this.panel1 = new System.Windows.Forms.Panel();
            this.panel2 = new System.Windows.Forms.Panel();
            this.pnlWarning = new System.Windows.Forms.Panel();
            this.lblPerformative = new System.Windows.Forms.Label();
            this.cmbPerformative = new System.Windows.Forms.ComboBox();
            this.chkLocalName = new System.Windows.Forms.CheckBox();
            this.lblStatus = new System.Windows.Forms.Label();
            this.pictureBoxLogo = new System.Windows.Forms.PictureBox();
            // 
            // mainMenu1
            // 
            this.mainMenu1.MenuItems.Add(this.mItemConnection);
            this.mainMenu1.MenuItems.Add(this.mItemOptions);
            // 
            // mItemConnection
            // 
            this.mItemConnection.Text = "Connect";
            this.mItemConnection.Click += new System.EventHandler(this.mItemConnection_Click);
            // 
            // mItemOptions
            // 
            this.mItemOptions.MenuItems.Add(this.mItemSendMessage);
            this.mItemOptions.MenuItems.Add(this.mItemClearText);
            this.mItemOptions.MenuItems.Add(this.mItemSettings);
            this.mItemOptions.MenuItems.Add(this.mItemAbout);
            this.mItemOptions.MenuItems.Add(this.mItemExit);
            this.mItemOptions.Text = "Options";
            // 
            // mItemSendMessage
            // 
            this.mItemSendMessage.Text = "Send Message";
            this.mItemSendMessage.Click += new System.EventHandler(this.mItemSendMessage_Click);
            // 
            // mItemClearText
            // 
            this.mItemClearText.Text = "Clear Text";
            this.mItemClearText.Click += new System.EventHandler(this.mItemClearText_Click);
            // 
            // mItemSettings
            // 
            this.mItemSettings.Text = "Settings";
            this.mItemSettings.Click += new System.EventHandler(this.mItemSettings_Click);
            // 
            // mItemAbout
            // 
            this.mItemAbout.Text = "About";
            this.mItemAbout.Click += new System.EventHandler(this.mItemAbout_Click);
            // 
            // mItemExit
            // 
            this.mItemExit.Text = "Exit";
            this.mItemExit.Click += new System.EventHandler(this.mItemExit_Click);
            // 
            // lblReceiver
            // 
            this.lblReceiver.Location = new System.Drawing.Point(3, 0);
            this.lblReceiver.Size = new System.Drawing.Size(100, 20);
            this.lblReceiver.Text = "Agent AID:";
            // 
            // txtReceiver
            // 
            this.txtReceiver.Location = new System.Drawing.Point(3, 14);
            this.txtReceiver.Size = new System.Drawing.Size(110, 21);
            // 
            // lblMessage
            // 
            this.lblMessage.Location = new System.Drawing.Point(119, 0);
            this.lblMessage.Size = new System.Drawing.Size(100, 20);
            this.lblMessage.Text = "Content:";
            // 
            // txtMessage
            // 
            this.txtMessage.Location = new System.Drawing.Point(119, 14);
            this.txtMessage.Multiline = true;
            this.txtMessage.Size = new System.Drawing.Size(118, 98);
            // 
            // lstMessages
            // 
            this.lstMessages.Location = new System.Drawing.Point(3, 146);
            this.lstMessages.Size = new System.Drawing.Size(234, 86);
            this.lstMessages.SelectedIndexChanged += new System.EventHandler(this.lstMessages_SelectedIndexChanged);
            // 
            // panel1
            // 
            this.panel1.Location = new System.Drawing.Point(3, 232);
            this.panel1.Size = new System.Drawing.Size(234, 33);
            // 
            // panel2
            // 
            this.panel2.BackColor = System.Drawing.Color.Black;
            this.panel2.Location = new System.Drawing.Point(3, 238);
            this.panel2.Size = new System.Drawing.Size(234, 1);
            // 
            // pnlWarning
            // 
            this.pnlWarning.BackColor = System.Drawing.Color.Red;
            this.pnlWarning.Location = new System.Drawing.Point(217, 245);
            this.pnlWarning.Size = new System.Drawing.Size(18, 20);
            // 
            // lblPerformative
            // 
            this.lblPerformative.Location = new System.Drawing.Point(4, 103);
            this.lblPerformative.Size = new System.Drawing.Size(100, 20);
            this.lblPerformative.Text = "Performative:";
            // 
            // cmbPerformative
            // 
            this.cmbPerformative.Location = new System.Drawing.Point(4, 118);
            this.cmbPerformative.Size = new System.Drawing.Size(234, 22);
            // 
            // chkLocalName
            // 
            this.chkLocalName.Location = new System.Drawing.Point(3, 35);
            this.chkLocalName.Size = new System.Drawing.Size(100, 20);
            this.chkLocalName.Text = "Local Name";
            // 
            // lblStatus
            // 
            this.lblStatus.Location = new System.Drawing.Point(4, 245);
            this.lblStatus.Size = new System.Drawing.Size(207, 20);
            this.lblStatus.Text = "lblStatus";
            // 
            // pictureBoxLogo
            // 
            this.pictureBoxLogo.Image = ((System.Drawing.Image)(resources.GetObject("pictureBoxLogo.Image")));
            this.pictureBoxLogo.Location = new System.Drawing.Point(3, 63);
            this.pictureBoxLogo.Size = new System.Drawing.Size(110, 30);
            this.pictureBoxLogo.SizeMode = System.Windows.Forms.PictureBoxSizeMode.StretchImage;
            // 
            // Form1
            // 
            this.ClientSize = new System.Drawing.Size(240, 268);
            this.Controls.Add(this.pictureBoxLogo);
            this.Controls.Add(this.panel2);
            this.Controls.Add(this.chkLocalName);
            this.Controls.Add(this.cmbPerformative);
            this.Controls.Add(this.lblPerformative);
            this.Controls.Add(this.pnlWarning);
            this.Controls.Add(this.lblStatus);
            this.Controls.Add(this.lstMessages);
            this.Controls.Add(this.txtMessage);
            this.Controls.Add(this.lblMessage);
            this.Controls.Add(this.txtReceiver);
            this.Controls.Add(this.lblReceiver);
            this.Menu = this.mainMenu1;
            this.MinimizeBox = false;
            this.Text = "Dummy# Agent";
            this.Resize += new System.EventHandler(this.Form1_Resize);
            this.Closing += new System.ComponentModel.CancelEventHandler(this.Form1_Closing);

        }

        #endregion

        #region Initialization Methods

        private void InitializeAgent()
        {
            _JadeSharpAgent = new NAgent();
            _JadeSharpAgent.ReceiveMessageEvent += new ReceiveMessageEventHandler(JadeConnector_ReceiveEvent);
            _JadeSharpAgent.ConnectionEvent += new ConnectionEventHandler(JadeConnector_ConnectionEvent);

            _MessageList = new ArrayList();

            JadeConnector_ConnectionEvent(null, new ConnectionEventArgs(ConnectionEventType.disconnected));
        }

        private void InitializeList()
        {
            cmbPerformative.Items.Clear();

            int cnt = ACLMessage.Performatives.Length;
            for (int i = 0; i < cnt; i++)
            {
                cmbPerformative.Items.Add(ACLMessage.Performatives.GetValue(i));
            }

            cmbPerformative.SelectedIndex = ACLMessage.NOT_UNDERSTOOD;
        }

        #endregion

        #region Jade Connector Events

        private void JadeConnector_ConnectionEvent(JadeConnector sender, ConnectionEventArgs args)
        {
            _LastConnectionEvent = args;
            switch (args.EventType)
            {
                case ConnectionEventType.connected:
                    this.Invoke(new EventHandler(OnConnect));
                    break;
                case ConnectionEventType.disconnected:
                    this.Invoke(new EventHandler(OnDisconnect));
                    break;
                case ConnectionEventType.error:
                    this.Invoke(new EventHandler(OnError));
                    break;
            }
        }

        private void OnConnect(object sender, EventArgs e)
        {
            mItemConnection.Text = "Disconnect";
            pnlWarning.BackColor = Color.Green;
            mItemSendMessage.Enabled = true;
            mItemClearText.Enabled = true;
            mItemSettings.Enabled = true;
            lblStatus.Text = "Connected";
        }

        private void OnDisconnect(object sender, EventArgs e)
        {
            mItemConnection.Text = "Connect";
            pnlWarning.BackColor = Color.Red;
            mItemSendMessage.Enabled = false;
            mItemClearText.Enabled = false;
            mItemSettings.Enabled = true;
            lblStatus.Text = "Disconnected";

            if (!_Exiting)
            {
                if (_FirstTime)
                    _FirstTime = false;
                else
                {
                    mItemConnection.Enabled = false;
                    mItemConnection.Text = "Wait";
                    tmr.Enabled = true;
                }
            }
        }

        private void OnError(object sender, EventArgs e)
        {
            mItemConnection.Text = "Disconnect";
            pnlWarning.BackColor = Color.Orange;
            mItemSendMessage.Enabled = true;
            mItemClearText.Enabled = true;
            mItemSettings.Enabled = true;
            lblStatus.Text = "Error: " + _LastConnectionEvent.Reason;
        }

        private void tmr_Tick(object sender, EventArgs e)
        {
            if (!_JadeSharpAgent.IsConnected)
            {
                mItemConnection.Enabled = true;
                mItemConnection.Text = "Connect";
                tmr.Enabled = false;
            }
        }

        private void JadeConnector_ReceiveEvent(JadeConnector sender, ACLMessage message)
        {
            _MessageList.Insert(0, message);
            this.Invoke(new EventHandler(OnReceiveMessage));
        }

        private void OnReceiveMessage(object sender, EventArgs e)
        {
            if (_MessageList == null || _MessageList.Count == 0)
                return;

            ACLMessage msg = (ACLMessage)_MessageList[0];

            DateTime now = DateTime.Now;
            string s = "<< " + now.ToShortDateString() + " - " +
                now.Hour + "." + now.Minute + ": " + msg.PerformativeAsString;

            lstMessages.Items.Insert(0, s);
        }

        #endregion

        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        static void Main()
        {
            Application.Run(new MainForm());
        }

        #region Index Changed Methods

        private void lstMessages_SelectedIndexChanged(object sender, EventArgs e)
        {
            ACLMessage msg = (ACLMessage)_MessageList[lstMessages.SelectedIndex];

            txtMessage.Text = msg.ContentAsString;
            txtReceiver.Text = msg.Sender;
            cmbPerformative.SelectedIndex = msg.Performative;
            chkLocalName.Checked = false;
        }

        #endregion

        #region Form Methods

        private void Form1_Closing(object sender, System.ComponentModel.CancelEventArgs e)
        {
           string title = "Warning";
            string text = "Closing application.\n Are you sure?";
            DialogResult dResult = MessageBox.Show(text, title, MessageBoxButtons.YesNo, MessageBoxIcon.Exclamation, MessageBoxDefaultButton.Button1);

            if (dResult == DialogResult.Yes)
            {
                _Exiting = true;
                _JadeSharpAgent.Disconnect();
                Logger.StopLog();
            }
            else
                e.Cancel = true;
        }

        #endregion

        #region Menu Item Buttons

        private void mItemConnection_Click(object sender, EventArgs e)
        {
            if (_JadeSharpAgent.IsConnected)
            {
                string title = "Warning";
                string text = "Disconnecting from the network.\nAre you sure?";
                DialogResult dResult = MessageBox.Show(text, title, MessageBoxButtons.YesNo, MessageBoxIcon.Exclamation, MessageBoxDefaultButton.Button1);

                if (dResult == DialogResult.Yes)
                {
                    _JadeSharpAgent.Disconnect();
                }
            }
            else
            {
                _JadeSharpAgent.Connect();
                lblStatus.Text = "Connecting...";
            }
        }

        private void mItemSendMessage_Click(object sender, EventArgs e)
        {
            if (_JadeSharpAgent.IsConnected)
            {
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.ContentAsString = txtMessage.Text;
                msg.Receiver = txtReceiver.Text;

                if (chkLocalName.Checked)
                {
                    msg.Receiver += "@" + _JadeSharpAgent.PlatformName;
                }

                msg.Sender = null;
                msg.Performative = cmbPerformative.SelectedIndex;

                _JadeSharpAgent.Send(msg);

                DateTime now = DateTime.Now;
                string s = ">> " + now.ToShortDateString() + " - " +
                    now.Hour + "." + now.Minute + ": " + msg.PerformativeAsString;

                _MessageList.Insert(0, msg);
                lstMessages.Items.Insert(0, s);
            }
        }

        private void mItemClearText_Click(object sender, EventArgs e)
        {
            txtMessage.Text = string.Empty;
            txtReceiver.Text = string.Empty;
            cmbPerformative.SelectedIndex = ACLMessage.NOT_UNDERSTOOD;
            chkLocalName.Checked = false;
        }

        private void mItemSettings_Click(object sender, EventArgs e)
        {
            FormSettings fSettings = new FormSettings();
            fSettings.ReadOnly = true;

            Hashtable hash = _JadeSharpAgent.Hash;
            IDictionaryEnumerator idEn = hash.GetEnumerator();
            while (idEn.MoveNext())
            {
                switch (idEn.Key.ToString())
                {
                    case "platform-name":
                        fSettings.PlatformName = idEn.Value.ToString();
                        break;
                    case "host":
                        fSettings.Host = idEn.Value.ToString();
                        break;
                    case "port":
                        fSettings.Port = idEn.Value.ToString();
                        break;
                    case "max-disconnection-time":
                        fSettings.MaxDisconnectionTime = idEn.Value.ToString();
                        break;
                    case "keep-alive-time":
                        fSettings.KeepAliveTime = idEn.Value.ToString();
                        break;
                }
            }

            DialogResult dResult = fSettings.ShowDialog();
            if (dResult == DialogResult.OK)
            {

            }
        }

        private void mItemAbout_Click(object sender, EventArgs e)
        {
            string clientVer = System.Reflection.Assembly.GetExecutingAssembly().GetName().Version.ToString();
            string libraryVer = System.Reflection.Assembly.Load("JadeSharp").GetName().Version.ToString();

            string title = "About...";
            string text = "Dummy# Agent\n" +
                "Version: " + clientVer + "\n" +
                "Jade# Library\n" +
                "Version: " + libraryVer;

            MessageBox.Show(text, title, MessageBoxButtons.OK, MessageBoxIcon.Asterisk, MessageBoxDefaultButton.Button1);
        }

        private void mItemExit_Click(object sender, EventArgs e)
        {
            this.Close();
        }

        #endregion

        #region Orientation Methods

        private void Landscape()
        {
            // 
            // txtReceiver
            // 
            this.txtReceiver.Location = new System.Drawing.Point(3, 16);
            this.txtReceiver.Size = new System.Drawing.Size(110, 21);
            // 
            // lblReceiver
            // 
            this.lblReceiver.Location = new System.Drawing.Point(3, 2);
            this.lblReceiver.Size = new System.Drawing.Size(100, 20);
            // 
            // chkLocalName
            // 
            this.chkLocalName.Location = new System.Drawing.Point(116, 17);
            this.chkLocalName.Size = new System.Drawing.Size(60, 20);
            this.chkLocalName.Text = "Local";
            // 
            // cmbPerformative
            // 
            this.cmbPerformative.Location = new System.Drawing.Point(3, 62);
            this.cmbPerformative.Size = new System.Drawing.Size(193, 22);
            // 
            // lblPerformative
            // 
            this.lblPerformative.Location = new System.Drawing.Point(3, 45);
            this.lblPerformative.Size = new System.Drawing.Size(100, 36);
            // 
            // txtMessage
            // 
            this.txtMessage.Location = new System.Drawing.Point(0, 90);
            this.txtMessage.Size = new System.Drawing.Size(196, 69);
            // 
            // lblMessage
            // 
            this.lblMessage.Location = new System.Drawing.Point(119, 0);
            this.lblMessage.Size = new System.Drawing.Size(100, 20);
            this.lblMessage.Visible = false;
            // 
            // lstMessages
            // 
            this.lstMessages.Location = new System.Drawing.Point(202, 3);
            this.lstMessages.Size = new System.Drawing.Size(115, 156);
            // 
            // lblStatus
            // 
            this.lblStatus.Location = new System.Drawing.Point(-1, 166);
            this.lblStatus.Size = new System.Drawing.Size(294, 20);
            this.lblStatus.BringToFront();
            // 
            // pnlWarning
            // 
            this.pnlWarning.Location = new System.Drawing.Point(299, 166);
            this.pnlWarning.Size = new System.Drawing.Size(18, 20);
            this.pnlWarning.BringToFront();
            ///
            /// pictureBoxLogo
            ///
            this.pictureBoxLogo.Location = new System.Drawing.Point(-200, -200);
            this.pictureBoxLogo.Size = new System.Drawing.Size(110, 30);
            // 
            // panel2
            // 
            this.panel2.Location = new System.Drawing.Point(3, 162);
            this.panel2.Size = new System.Drawing.Size(313, 1);
            // 
            // panel1
            // 
            this.panel1.Location = new System.Drawing.Point(0, 41);
            this.panel1.Size = new System.Drawing.Size(196, 1);
        }

        private void Portrait()
        {
            // 
            // txtReceiver
            // 
            this.txtReceiver.Location = new System.Drawing.Point(3, 14);
            this.txtReceiver.Size = new System.Drawing.Size(110, 21);
            // 
            // lblMessage
            // 
            this.lblMessage.Location = new System.Drawing.Point(119, 0);
            this.lblMessage.Size = new System.Drawing.Size(100, 20);
            this.lblMessage.Visible = true;
            // 
            // txtMessage
            // 
            this.txtMessage.Location = new System.Drawing.Point(119, 14);
            this.txtMessage.Size = new System.Drawing.Size(118, 79);
            // 
            // lstMessages
            // 
            this.lstMessages.Location = new System.Drawing.Point(3, 134);
            this.lstMessages.Size = new System.Drawing.Size(234, 86);
            // 
            // panel1
            // 
            this.panel1.Location = new System.Drawing.Point(3, 232);
            this.panel1.Size = new System.Drawing.Size(234, 33);
            // 
            // pnlWarning
            // 
            this.pnlWarning.Location = new System.Drawing.Point(216, 237);
            this.pnlWarning.Size = new System.Drawing.Size(18, 20);
            // 
            // lblPerformative
            // 
            this.lblPerformative.Location = new System.Drawing.Point(3, 76);
            this.lblPerformative.Size = new System.Drawing.Size(100, 20);
            // 
            // cmbPerformative
            // 
            this.cmbPerformative.Location = new System.Drawing.Point(3, 99);
            this.cmbPerformative.Size = new System.Drawing.Size(234, 22);
            // 
            // chkLocalName
            // 
            this.chkLocalName.Location = new System.Drawing.Point(3, 41);
            this.chkLocalName.Size = new System.Drawing.Size(100, 20);
            this.chkLocalName.Text = "Local Name";
            ///
            /// pictureBoxLogo
            ///
            this.pictureBoxLogo.Location = new System.Drawing.Point(3, 63);
            this.pictureBoxLogo.Size = new System.Drawing.Size(110, 30);
            // 
            // lblStatus
            // 
            this.lblStatus.Location = new System.Drawing.Point(3, 237);
            this.lblStatus.Size = new System.Drawing.Size(207, 20);
            // 
            // panel2
            // 
            this.panel2.Location = new System.Drawing.Point(2, 230);
            this.panel2.Size = new System.Drawing.Size(234, 1);
        }

        private void Form1_Resize(object sender, EventArgs e)
        {
            if (this.Width > this.Height)
                Landscape();
            else
                Portrait();
        }

        #endregion
    }
}
