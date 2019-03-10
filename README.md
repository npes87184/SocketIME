# SocketIME

An android IME which reads input from socket.

Hopefully, it can be integrated into [scrcpy](https://github.com/Genymobile/scrcpy) so that, we can type Chinese from keyboard to android.

# Demo

[![IMAGE ALT TEXT](https://img.youtube.com/vi/s9LZpAnIf4A/0.jpg)](https://youtu.be/s9LZpAnIf4A "Video Title")

# HowTo

1. adb forward tcp:5566 localabstract:scrcpy-input
2. Change IME to SocketIME in android
3. python socket_ime_test.py
4. Enjoy!
