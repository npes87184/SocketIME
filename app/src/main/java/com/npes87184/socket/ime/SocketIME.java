package com.npes87184.socket.ime;

import android.inputmethodservice.InputMethodService;
import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.Button;

import java.io.InputStream;

import static java.lang.Thread.interrupted;

public class SocketIME extends InputMethodService {

    private LocalServerSocket mLocalSocketServer = null;
    private LocalSocket mLocalSocket = null;
    private Thread mThread = null;
    private static final String SOCKET_NAME = "scrcpy-input";
    private final String SOCKET_IME = "SocketIME";
    private boolean mListened = false;

    @Override
    public View onCreateInputView() {
        View mInputView = getLayoutInflater().inflate(R.layout.view, null);
        Button btnConnect = mInputView.findViewById(R.id.buttonConnect);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService();
            }
        });

        Button btnClose = mInputView.findViewById(R.id.buttonClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService();
            }
        });

        startService();

        return mInputView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void startService() {
        if (!mListened) {
            startListenSocket();
        }
    }

    private void stopService() {
        if (mLocalSocketServer != null) {
            try {
                mLocalSocket.close();
                mLocalSocketServer.close();
            } catch (Exception e) {

            }
        }

        if (mListened) {
            mThread.interrupt();
        }

        mLocalSocketServer = null;
        mLocalSocket = null;
    }

    private void startListenSocket() {
        mListened = true;
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                listenSocket();
            }
        });

        mThread.start();
        Log.i(SOCKET_IME, "Thread started");
    }

    private void listenSocket() {
        String msg;

        if (mLocalSocketServer == null || mLocalSocket == null) {
            try {
                mLocalSocketServer = new LocalServerSocket(SOCKET_NAME);
                mLocalSocket = mLocalSocketServer.accept();
                Log.i(SOCKET_IME, "Server created");
            } catch (Exception e) {
                Log.i(SOCKET_IME, "Failed to create server");
            }
        }

        while (!interrupted()) {
            msg = getMsgFromSocket();
            InputConnection ic = getCurrentInputConnection();
            if (ic != null) {
                ic.commitText(msg, 1);
            }
        }

        mListened = false;
        Log.i(SOCKET_IME, "Thread stopped");
    }

    private String getMsgFromSocket() {
        String msg = "";
        int readLen;
        byte[] buffer = new byte[4096];

        try {
            InputStream is = mLocalSocket.getInputStream();

            if ((readLen = is.read(buffer, 0, 4096)) != -1) {
                msg = new String(buffer, 0, readLen);
                Log.i(SOCKET_IME, msg);
            }
        } catch (Exception e) {
            Log.i(SOCKET_IME, "Exception");
            return "";
        }

        return msg;
    }
}
