# java-cmd-exec
A simple wrapper for ProcessBuilder

example

public class Md5sumCommand extends Command {

    @Override
    protected File directory() {
        return new File(".");
    }

    @Override
    protected String command() {
        return "md5sum";
    }


    public static void main(String[] args) {
	   
       Md5sumCommand command = new Md5sumCommand("test.txt");
       Command.Result result = command.execute();

        System.out.println(result.code); // exit code
       System.out.println(result.output); // stdout output

	    

	}
}
