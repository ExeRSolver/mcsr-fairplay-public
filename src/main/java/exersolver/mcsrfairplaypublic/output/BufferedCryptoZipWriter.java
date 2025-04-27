package exersolver.mcsrfairplaypublic.output;

import exersolver.mcsrfairplaypublic.MCSRFairplay;

import java.io.BufferedWriter;
import java.io.IOException;

public class BufferedCryptoZipWriter extends BufferedWriter {
    private final CryptoZipWriter fileWriter;
    public boolean closed = false;

    public BufferedCryptoZipWriter(CryptoZipWriter fileWriter) throws IOException {
        super(fileWriter, 16384);
        this.fileWriter = fileWriter;
    }

    @Override
    public void close() throws IOException {
        this.closed = true;
        super.close();
        this.fileWriter.close();
    }

    public void log(long nanoTime, String message) {
        this.log(nanoTime + " " + message);
    }

    public void log(String message) {
        if (this.closed)
            return;

        try {
            this.write(message + "\n");
        }
        catch (IOException e) {
            MCSRFairplay.LOGGER.error(String.format("Error occurred when logging \"%s\"", message), e);
        }
    }

    public String getHashFileName() {
        return this.fileWriter.getHashFileName();
    }

    public String getHashHex() {
        return this.fileWriter.getHashHex();
    }
}
