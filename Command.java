import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@SuppressWarnings("unused")
public abstract class Command {

    protected final String[] args;
    public Command(String... args) {
        this.args = args;
    }

    protected abstract String command();

    protected abstract File directory();

    protected int timeout() {
        return 1000 * 60;// default timeout
    }

    public Result execute() throws Exception {

        List<String> args = new ArrayList<>();
        args.add(command());
        String[] params = this.args;
        if (this.args != null) {
            for (String param : params) {
                args.add(param);
            }
        }

        ProcessBuilder builder = new ProcessBuilder(args);
        builder.directory(directory());

        final Process process = builder.start();
        final Timer timeout = new Timer();
        timeout.schedule(new TimerTask() {
            @Override
            public void run() {
                process.destroy();
            }
        }, timeout());// timeout
        int exitCode = process.waitFor();
        timeout.cancel();

        return new Result(exitCode, output(process));
    }

    private String output(Process process) throws IOException {
        InputStream inputStream = process.getInputStream();

        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        int readBytes;
        byte[] buffer = new byte[2048];
        while ((readBytes = inputStream.read(buffer)) >= 0) {
            bytes.write(buffer, 0, readBytes);
        }

        return new String(bytes.toByteArray());
    }

    public class Result {
        public final int code;
        public final String output;

        public Result(int code, String output) {
            this.code = code;
            this.output = output;
        }
    }

    @Override
    public String toString() {
        return String.format("%s %s", command(), org.apache.commons.lang.StringUtils.join(args, " "));
    }
}

