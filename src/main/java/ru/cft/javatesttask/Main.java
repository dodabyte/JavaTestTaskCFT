package ru.cft.javatesttask;

import java.util.ArrayList;
import java.util.List;

public class Main {
    static boolean isAscending = true;
    static boolean isStrings = true;
    static String outputFileName = "";
    static List<String> inputFilesName = new ArrayList<>();

    public static void main(String[] args) {
        CommandLineArguments commandLineArguments = new CommandLineArguments();
        commandLineArguments.parseArguments(args);
    }
}
