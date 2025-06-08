package test;

import java.util.Arrays;

public class ProxyMain {

    public static void main(String[] args) throws Exception{
        String[] newArgs;

        if(args.length == 0) {
            return;
        } else if(args.length > 1) {
            newArgs = Arrays.copyOfRange(args, 1, args.length-1);
        } else {
            newArgs = new String[0];
        }

        switch (args[0]) {
            case "AgentAnalysis":
                AgentAnalysis.main(newArgs);
                break;
            case "StartGame":
                StartGame.main(newArgs);
                break;
            case "TestMonte":
                TestMonte.main(newArgs);
                break;
        }
    }
}
