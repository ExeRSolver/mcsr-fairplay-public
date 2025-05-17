package exersolver.mcsrfairplaypublic.output;

import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.jcajce.provider.digest.Blake3;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.function.BiConsumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CryptoZipWriter extends OutputStreamWriter {
    private final Blake3.Blake3_256 hasher;
    private final ZipOutputStream outputStream;
    private final String hashFileName;
    private String hashHex = null;
    private boolean closed = false;

    public static BiConsumer<String, String> onHash;

    public CryptoZipWriter(@NotNull ZipOutputStream outputStream, String logFileName, String hashFileName) throws IOException {
        super(outputStream);
        this.outputStream = outputStream;
        this.outputStream.putNextEntry(new ZipEntry(logFileName));
        this.hashFileName = hashFileName;
        this.hasher = new Blake3.Blake3_256();
    }

    @Override
    public synchronized void write(char @NotNull [] cbuf, int off, int len) throws IOException {
        if (this.closed)
            return;
        this.hasher.update((new String(cbuf, off, len)).getBytes());
        super.write(cbuf, off, len);
    }

    @Override
    public synchronized void close() throws IOException {
        if (this.closed)
            return;
        this.flush();
        this.outputStream.closeEntry();
        this.outputStream.putNextEntry(new ZipEntry(this.hashFileName));
        this.hashHex = Hex.encodeHexString(this.hasher.digest());
        this.write(this.hashHex);
        super.close();
        this.closed = true;
        if (onHash != null)
            onHash.accept(this.getHashFileName(), this.hashHex);
    }

    public String getHashFileName() {
        return this.hashFileName.substring(0, this.hashFileName.indexOf(".blake3"));
    }

    public String getHashHex() {
        return this.hashHex;
    }
}
