package test;

import org.apache.commons.cli.*;

public class CompetitiveTest {
    public static class Params {
        public int bot1, bot2;
        public Params() {};
    };

    private static Params ParseParams(String[] args) {
        Options options = new Options();

        Option bot1 = new Option("b1", "bot1", true, "bot id");
        bot1.setRequired(true);
        options.addOption(bot1);

        Option bot2 = new Option("b2", "bot2", true, "bot id");
        bot1.setRequired(true);
        options.addOption(bot2);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;//not a good practice, it serves it purpose 

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);

            System.exit(1);
        }

        Params params = new Params();
        try {
            params.bot1 = Integer.parseInt(cmd.getOptionValue("bot1"));
            params.bot2 = Integer.parseInt(cmd.getOptionValue("bot2"));
        } catch (NumberFormatException e) {    
            System.out.print(e.getMessage());
            System.exit(1);
        }

        return params;
    }
    public static void main(String[] args) throws Exception {
        Params params = ParseParams(args);

        

    }
}
