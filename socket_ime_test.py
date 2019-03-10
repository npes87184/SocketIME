import signal
import socket

class GracefulKiller:
    kill_now = False
    def __init__(self):
        signal.signal(signal.SIGINT, self.exit_gracefully)
        signal.signal(signal.SIGTERM, self.exit_gracefully)

    def exit_gracefully(self,signum, frame):
        self.kill_now = True

if __name__ == '__main__':
    killer = GracefulKiller()
    s = socket.socket()
    port = 5566

    s.connect(('127.0.0.1', port))
    while not killer.kill_now:
        cmd = raw_input("Please input msg: ")
        s.send(cmd)

    print("Stop client")
    s.close()
