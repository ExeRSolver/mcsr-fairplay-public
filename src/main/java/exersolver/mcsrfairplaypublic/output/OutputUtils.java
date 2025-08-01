package exersolver.mcsrfairplaypublic.output;

import exersolver.mcsrfairplaypublic.InputListener;
import exersolver.mcsrfairplaypublic.MCSRFairplayPublic;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.zip.ZipOutputStream;

public class OutputUtils {

    public static void setFileWriter(Path worldPath) {
        if (System.getProperty("os.name").toLowerCase().startsWith("mac"))
            return;

        try {
            Path logDirectory = worldPath.resolve("mcsrfairplay").resolve("input-logs");
            Files.createDirectories(logDirectory);

            int logNum = getLogCount(logDirectory.toFile()) + 1;
            Path logPath = logDirectory.resolve("input_log-" + logNum + ".zip");
            String logFileName = "input_log-" + logNum + ".log";
            String hashFileName = "input_log_hash-" + logNum + ".blake3";

            ZipOutputStream outputStream = new ZipOutputStream(Files.newOutputStream(logPath));
            BufferedCryptoZipWriter fileWriter = new BufferedCryptoZipWriter(new CryptoZipWriter(outputStream, logFileName, hashFileName));
            InputListener.setFileWriter(fileWriter);
        } catch (IOException e) {
            MCSRFairplayPublic.LOGGER.error(e.getMessage(), e);
        }
    }

    private static int getLogCount(File logFile) throws NullPointerException {
        return Objects.requireNonNull(logFile.list()).length;
    }
}
