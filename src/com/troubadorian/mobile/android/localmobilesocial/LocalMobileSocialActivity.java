package com.troubadorian.mobile.android.localmobilesocial;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class LocalMobileSocialActivity extends Activity
{
    private Button btnStart;

    private Button btnStop;

    private Button btnBind;

    private Button btnUnbind;

    private Button btnUpby1;

    private Button btnUpby10;

    private TextView txtStatus;

    private TextView txtIntValue;

    private TextView txtStrValue;

    private Messenger mService = null;

    private boolean mIsBound;

    final Messenger mMessenger = new Messenger(new IncomingHandler());

    class IncomingHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
            case LocalMobileSocialService.MSG_SET_INT_VALUE:
                txtIntValue.setText("Int Message : " + msg.arg1);
                break;

            case LocalMobileSocialService.MSG_SET_STRING_VALUE:
                String str1 = msg.getData().getString("str1");
                txtStrValue.setText("Str Message : " + str1);
                break;

            default:
                super.handleMessage(msg);
            }
        }

    }

    private ServiceConnection mConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            mService = new Messenger(service);
            txtStatus.setText("Attached");
            try
            {
                Message msg = Message.obtain(null, LocalMobileSocialService.MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mService.send(msg);

            }

            catch (RemoteException e)
            {
                // The service has crashed before we could even do anything with
                // it
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            mService = null;
            txtStatus.setText("Disconnected");

        }
    };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        btnStart = (Button) findViewById(R.id.btnStart);
        btnStop = (Button) findViewById(R.id.btnStop);
        btnBind = (Button) findViewById(R.id.btnBind);
        btnUnbind = (Button) findViewById(R.id.btnUnbind);
        btnUpby1 = (Button) findViewById(R.id.btnUpby1);
        btnUpby10 = (Button) findViewById(R.id.btnUpby10);
        txtStatus = (TextView) findViewById(R.id.txtStatus);
        txtIntValue = (TextView) findViewById(R.id.txtIntValue);
        txtStrValue = (TextView) findViewById(R.id.txtStrValue);

        btnStart.setOnClickListener(btnStartListener);
        btnStop.setOnClickListener(btnStopListener);
        btnBind.setOnClickListener(btnBindListener);
        btnUnbind.setOnClickListener(btnUnbindListener);
        btnUpby1.setOnClickListener(btnUpby1Listener);
        btnUpby10.setOnClickListener(btnUpby10Listener);

        restoreMe(savedInstanceState);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {

    }

    private void restoreMe(Bundle savedInstanceState)
    {

    }

    private OnClickListener btnStartListener = new OnClickListener()
    {

        @Override
        public void onClick(View v)
        {
            startService(new Intent(LocalMobileSocialActivity.this, LocalMobileSocialService.class));
        }

    };

    private OnClickListener btnStopListener = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            doUnbindService();
            stopService(new Intent(LocalMobileSocialActivity.this, LocalMobileSocialService.class));
        }

    };

    private OnClickListener btnBindListener = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            doBindService();
        }
    };

    private OnClickListener btnUnbindListener = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            doUnbindService();
        }
    };

    private OnClickListener btnUpby1Listener = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            sendMessageToService(1);
        }
    };

    private OnClickListener btnUpby10Listener = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            sendMessageToService(10);
        }
    };

    private void sendMessageToService (int intvaluetosend)
    {
        if (mIsBound)
        {
            if (mService != null)
            {
                try
                {
                    Message msg = Message.obtain(null, LocalMobileSocialService.MSG_SET_INT_VALUE, intvaluetosend, 0);
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                    
                }
                
                catch (RemoteException ex)
                {
                    
                }
            }
        }
    }
    
    private void doBindService()
    {
        bindService(new Intent(this, LocalMobileSocialService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
        txtStatus.setText("Binding");
        
    }
    
    
    private void doUnbindService()
    {
        if (mIsBound)
        {
            // If we have received the service and hence registered with it, then now is the time to unregister
            if (mService != null)
            {
                try
                {
                    Message msg = Message.obtain(null, LocalMobileSocialService.MSG_UNREGISTER_CLIENT);
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                }
                catch (Exception ex)
                {
                    Log.d(this.getClass().getSimpleName(), "---" + ex.toString());
                }
            }
            
        }
    }
    
    
}