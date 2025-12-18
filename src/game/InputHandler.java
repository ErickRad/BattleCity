package game;

import java.io.IOException;

public class InputHandler {

    private volatile char lastKey = '.';

    public InputHandler() {
        setRawMode();

        Thread inputThread = new Thread(() -> {
            try {
                while (true) {
                    int c = System.in.read();
                    if (c != -1) {
                        lastKey = (char) c;
                    }
                }
            } catch (IOException ignored) {}
        });

        inputThread.setDaemon(true);
        inputThread.start();

        Runtime.getRuntime().addShutdownHook(new Thread(this::restoreTerminal));
    }

    public char poll() {
        char k = lastKey;
        lastKey = '.';
        return k;
    }

    private void setRawMode() {
        try {
            new ProcessBuilder("sh", "-c", "stty -icanon -echo min 1 </dev/tty")
                    .inheritIO()
                    .start()
                    .waitFor();
        } catch (Exception ignored) {}
    }

    private void restoreTerminal() {
        try {
            new ProcessBuilder("sh", "-c", "stty sane </dev/tty")
                    .inheritIO()
                    .start()
                    .waitFor();
        } catch (Exception ignored) {}
    }
}

