# SocketIME

An android IME which reads input from socket.

Hopefully, it can be integrated into [scrcpy](https://github.com/Genymobile/scrcpy) so that, we can type Chinese from keyboard to android.

# Demo

[YouTube](https://youtu.be/s9LZpAnIf4A)

# HowTo

1. adb forward tcp:5566 localabstract:scrcpy-input
2. Change IME to SocketIME in android
3. python socket_ime_test.py
4. Enjoy!
