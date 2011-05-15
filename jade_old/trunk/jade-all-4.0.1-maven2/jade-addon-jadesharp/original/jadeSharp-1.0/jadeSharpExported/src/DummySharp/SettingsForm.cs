#region Using directives

using System;
using System.Drawing;
using System.Collections;
using System.Windows.Forms;
using System.Data;

#endregion

namespace DummySharp
{
    /// <summary>
    /// Summary description for Form2.
    /// </summary>
    public class FormSettings : System.Windows.Forms.Form
    {
        private Label lblHost;
        private TextBox txtHost;
        private MenuItem mItemOK;
        private MenuItem mItemCancel;
        private TextBox txtPort;
        private Label lblPort;
        private TextBox txtPlatformName;
        private Label lblPlatformName;
        private TextBox txtDisconnectionTime;
        private Label lblDisconnectiontime;
        private TextBox txtKeepAlive;
        private Label lblKeepAlive;
        /// <summary>
        /// Main menu for the form.
        /// </summary>
        private System.Windows.Forms.MainMenu mainMenu1;

        #region Fields

        private bool _ReadOnly;

        #endregion

        public FormSettings()
        {
            InitializeComponent();

            _ReadOnly = false;
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
            this.mainMenu1 = new System.Windows.Forms.MainMenu();
            this.lblHost = new System.Windows.Forms.Label();
            this.txtHost = new System.Windows.Forms.TextBox();
            this.txtPort = new System.Windows.Forms.TextBox();
            this.lblPort = new System.Windows.Forms.Label();
            this.txtPlatformName = new System.Windows.Forms.TextBox();
            this.lblPlatformName = new System.Windows.Forms.Label();
            this.txtDisconnectionTime = new System.Windows.Forms.TextBox();
            this.lblDisconnectiontime = new System.Windows.Forms.Label();
            this.txtKeepAlive = new System.Windows.Forms.TextBox();
            this.lblKeepAlive = new System.Windows.Forms.Label();
            this.mItemOK = new System.Windows.Forms.MenuItem();
            this.mItemCancel = new System.Windows.Forms.MenuItem();
            // 
            // mainMenu1
            // 
            this.mainMenu1.MenuItems.Add(this.mItemOK);
            this.mainMenu1.MenuItems.Add(this.mItemCancel);
            // 
            // lblHost
            // 
            this.lblHost.Location = new System.Drawing.Point(12, 16);
            this.lblHost.Size = new System.Drawing.Size(100, 20);
            this.lblHost.Text = "Host:";
            // 
            // txtHost
            // 
            this.txtHost.Location = new System.Drawing.Point(12, 30);
            this.txtHost.Size = new System.Drawing.Size(119, 21);
            // 
            // txtPort
            // 
            this.txtPort.Location = new System.Drawing.Point(137, 30);
            this.txtPort.Size = new System.Drawing.Size(100, 21);
            // 
            // lblPort
            // 
            this.lblPort.Location = new System.Drawing.Point(137, 16);
            this.lblPort.Size = new System.Drawing.Size(100, 20);
            this.lblPort.Text = "Port:";
            // 
            // txtPlatformName
            // 
            this.txtPlatformName.Location = new System.Drawing.Point(12, 68);
            this.txtPlatformName.Size = new System.Drawing.Size(225, 21);
            // 
            // lblPlatformName
            // 
            this.lblPlatformName.Location = new System.Drawing.Point(12, 54);
            this.lblPlatformName.Size = new System.Drawing.Size(100, 20);
            this.lblPlatformName.Text = "Platform Name:";
            // 
            // txtDisconnectionTime
            // 
            this.txtDisconnectionTime.Location = new System.Drawing.Point(12, 126);
            this.txtDisconnectionTime.Size = new System.Drawing.Size(119, 21);
            // 
            // lblDisconnectiontime
            // 
            this.lblDisconnectiontime.Location = new System.Drawing.Point(12, 112);
            this.lblDisconnectiontime.Size = new System.Drawing.Size(119, 20);
            this.lblDisconnectiontime.Text = "Max Disconnection Time;";
            // 
            // txtKeepAlive
            // 
            this.txtKeepAlive.Location = new System.Drawing.Point(137, 126);
            this.txtKeepAlive.Size = new System.Drawing.Size(100, 21);
            // 
            // lblKeepAlive
            // 
            this.lblKeepAlive.Location = new System.Drawing.Point(137, 112);
            this.lblKeepAlive.Size = new System.Drawing.Size(100, 20);
            this.lblKeepAlive.Text = "Keep Alive Time:";
            // 
            // mItemOK
            // 
            this.mItemOK.Text = "OK";
            this.mItemOK.Click += new System.EventHandler(this.mItemOK_Click);
            // 
            // mItemCancel
            // 
            this.mItemCancel.Text = "Cancel";
            this.mItemCancel.Click += new System.EventHandler(this.mItemCancel_Click);
            // 
            // FormSettings
            // 
            this.ClientSize = new System.Drawing.Size(240, 268);
            this.Controls.Add(this.txtKeepAlive);
            this.Controls.Add(this.lblKeepAlive);
            this.Controls.Add(this.txtDisconnectionTime);
            this.Controls.Add(this.lblDisconnectiontime);
            this.Controls.Add(this.txtPlatformName);
            this.Controls.Add(this.lblPlatformName);
            this.Controls.Add(this.txtPort);
            this.Controls.Add(this.lblPort);
            this.Controls.Add(this.txtHost);
            this.Controls.Add(this.lblHost);
            this.Menu = this.mainMenu1;
            this.Text = "Settings";

        }

        #endregion

        #region Properties

        public string PlatformName
        {
            get { return txtPlatformName.Text; }
            set { txtPlatformName.Text = value; }
        }

        public string Host
        {
            get { return txtHost.Text; }
            set { txtHost.Text = value; }
        }

        public string Port
        {
            get { return txtPort.Text; }
            set { txtPort.Text = value; }
        }

        public string MaxDisconnectionTime
        {
            get { return txtDisconnectionTime.Text; }
            set { txtDisconnectionTime.Text = value; }
        }

        public string KeepAliveTime
        {
            get { return txtKeepAlive.Text; }
            set { txtKeepAlive.Text = value; }
        }

        public bool ReadOnly
        {
            get { return _ReadOnly; }
            set
            {
                _ReadOnly = value;

                txtDisconnectionTime.Enabled = !_ReadOnly;
                txtKeepAlive.Enabled = !_ReadOnly;
                txtPort.Enabled = !_ReadOnly;
                txtHost.Enabled = !_ReadOnly;
                txtPlatformName.Enabled = !_ReadOnly;                
            }
        }

        #endregion

        #region Menu Item Buttons

        private void mItemOK_Click(object sender, EventArgs e)
        {
            this.DialogResult = DialogResult.OK;
        }

        private void mItemCancel_Click(object sender, EventArgs e)
        {
            this.DialogResult = DialogResult.Cancel;
        }

        #endregion
    }
}
