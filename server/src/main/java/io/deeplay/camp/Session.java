package io.deeplay.camp;

import java.io.*;
import java.net.Socket;
import java.util.UUID;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Session implements AutoCloseable {
    private final Socket clientSocket;
    private final BufferedReader reader;
    private final BufferedWriter writer;
    @Getter
    private final UUID sessionId;
    private static final Logger logger = LoggerFactory.getLogger(Session.class);

    public Session(final Socket clientSocket) {
        this.clientSocket = clientSocket;
        try {
            this.reader = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            this.writer = new BufferedWriter(new OutputStreamWriter(this.clientSocket.getOutputStream()));
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
        this.sessionId = UUID.randomUUID();
    }



    @Override
    public void close() throws IOException {
        clientSocket.close();
    }

    public synchronized void sendResponse(final String responseText) {
        try {
            writer.write(responseText);
            writer.newLine();
            writer.flush();
        } catch (final IOException ioException) {
            throw new IllegalStateException(ioException);
        }
    }

    public String waitForRequest() throws IOException {
        return reader.readLine();
    }

    public boolean isClosed() {
        return clientSocket.isClosed();
    }

}
