package monitoring.service.dev.utils;

import monitoring.service.dev.InitialCommandProcessor;
import monitoring.service.dev.config.AppConstants;

import java.util.HashMap;
import java.util.Map;

public class ArgsParser {

    public static Map<String, String> parseArgs(String args) {
        if(args.isEmpty()) return new HashMap<>();

        Map<String, String> argsMap = new HashMap<>();
        String[] tokens = args.split("\\s+");

        for (int i = 0; i < tokens.length; i++) {
            if (AppConstants.ARG_USERNAME.equals(tokens[i]) || AppConstants.ARG_PASSWORD.equals(tokens[i]) ||
                    AppConstants.ARG_SCANNER.equals(tokens[i]) || AppConstants.ARG_INDICATION.equals(tokens[i]) ||
                    AppConstants.ARG_MONTH.equals(tokens[i]) || AppConstants.ARG_YEAR.equals(tokens[i]) ||
                    AppConstants.ARG_ACTION.equals(tokens[i])) {
                if (i + 1 < tokens.length) {
                    argsMap.put(tokens[i], tokens[i + 1]);
                    i++;
                }
            }
        }

        return argsMap;
    }
}
