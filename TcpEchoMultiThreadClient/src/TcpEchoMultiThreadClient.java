import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Logger;

import static java.lang.Integer.parseInt;
import static java.net.InetAddress.getLocalHost;
import static java.util.logging.Logger.getAnonymousLogger;

public class TcpEchoMultiThreadClient {

    public static void main(String[] args) {
        Logger logger = getAnonymousLogger();
        final int port;
        if (args.length == 0) {
            logger.info("В аргументах не задан порт");
            port = 80;
        } else {
            port = parseInt(args[0]);
        }
        for (int threadIndex = 0; threadIndex < 1000; threadIndex++) {
            new TcpEchoThread(port, threadIndex).start();
        }
    }

    public static class TcpEchoThread extends Thread {
        private final Integer PORT;
        private final Integer NUMBER;
        private final Logger logger = getAnonymousLogger();

        public TcpEchoThread(Integer port, Integer number) {
            this.PORT = port;
            this.NUMBER = number;
        }

        @Override
        public void run() {
            try {
                logger.info("Подключаемся к серверу");
                final InetAddress LOCALHOST = getLocalHost();
                Socket socket = new Socket(LOCALHOST, PORT);
                logger.info("Подключились к серверу, отправляем данные");

                OutputStream outputStream = socket.getOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

                String stringToSend = "Hello world! №" + NUMBER;
                dataOutputStream.writeUTF(stringToSend);
                logger.info("Отправлено: " + stringToSend);

                logger.info("Получаем данные от сервера");
                InputStream inputStream = socket.getInputStream();
                DataInputStream dataInputStream = new DataInputStream(inputStream);

                String acceptedInfo = dataInputStream.readUTF();
                logger.info("Принято: " + acceptedInfo);

                inputStream.close();
                outputStream.close();
                socket.close();
                logger.info("Клиент закончил свою работу");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}