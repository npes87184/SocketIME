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
        if (mListened) {
            // mark thread as interrupted
            mThread.interrupt();
            Log.i(SOCKET_IME, "Interrupted thread");

            // now send connect request to myself to trigger leaving accept()
            LocalSocket ls = new LocalSocket();
            try {
                ls.connect(mLocalSocketServer.getLocalSocketAddress());
                ls.close();
            } catch (Exception e) {

            }
            Log.i(SOCKET_IME, "Leave accept");

            try {
                mLocalSocketServer.close();
                mLocalSocket.shutdownInput();
                mLocalSocket.close();
                Log.i(SOCKET_IME, "Close server socket");
            } catch (Exception e) {

            }
        }
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
    }

    private void listenSocket() {
        String msg;

        Log.i(SOCKET_IME, "Thread started");
        try {
            mLocalSocketServer = new LocalServerSocket(SOCKET_NAME);
            Log.i(SOCKET_IME, "Server created");
            mLocalSocket = mLocalSocketServer.accept();
            Log.i(SOCKET_IME, "Client connected");
        } catch (Exception e) {
            Log.i(SOCKET_IME, "Failed to create server");
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
