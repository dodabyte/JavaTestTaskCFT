package ru.cft.javatesttask;

import org.apache.commons.cli.*;

import java.util.ArrayList;
import java.util.List;

public class CommandLineArguments {
    public static List<String> files = new ArrayList<>();

    public void parseArguments(String[] args) {
        Options options = new Options();
        createOptions(options);

        CommandLineParser parser = new DefaultParser();
        CommandLine commands = getCommands(parser, options, args);

        checkErrors(commands, options);

        if (commands != null) {
            if (commands.hasOption("h")) printHelpAndClose(options, 0);
            if (commands.hasOption("d")) Main.isAscending = false;
            if (commands.hasOption("i")) Main.isStrings = false;
            Main.outputFileName = files.get(0);
            files.remove(0);
            Main.inputFilesNames = files;
        }
    }

    private CommandLine getCommands(CommandLineParser parser, Options options, String[] args) {
        CommandLine commands = null;
        try {
            commands = parser.parse(options, args);
        }
        catch (MissingOptionException e) {
            System.out.println("Отсутствуют обязательные параметры -i или -s.\n");
            printHelpAndClose(options, 100);
        }
        catch (UnrecognizedOptionException e) {
            System.out.println("Параметр " + e.getOption() + " не существует.\n");
            printHelpAndClose(options, 101);
        }
        catch (ParseException e) {
            System.out.println("Параметры не были найдены.\n");
            printHelpAndClose(options, 102);
        }
        return commands;
    }

    private void checkErrors(CommandLine commands, Options options) {
        if (commands != null) {
            if (commands.hasOption("a") && commands.hasOption("d")) {
                System.out.println("Конфликт параметров. Введите одну из опций: -a или -d.\n");
                printHelpAndClose(options, 103);
            }
            if (commands.hasOption("i") && commands.hasOption("s")) {
                System.out.println("Конфликт параметров. Введите одну из опций: -i или -s.\n");
                printHelpAndClose(options, 104);
            }

            files = commands.getArgList();
            if (files.size() < 2) {
                System.out.println("Отсутствует название выходного файла или входн(ого)/(ых) файлов.\n");
                files.clear();
                printHelpAndClose(options, 105);
            }
        }
        else {
            System.out.println("Сбой программы по неизвестной причине.");
            printHelpAndClose(options, 106);
        }
    }

    private void createOptions(Options options) {
        OptionGroup helpGroup = new OptionGroup();
        helpGroup.addOption(new Option("h", "help", false, "Помощь."));
        options.addOptionGroup(helpGroup);

        OptionGroup sortingModeGroup = new OptionGroup();
        sortingModeGroup.addOption(new Option("a", "ascending", false, "Сортировка по возрастанию. Параметр по-умолчанию, необязательный."));
        sortingModeGroup.addOption(new Option("d", "descending", false, "Сортировка по убыванию. Параметр необязательный."));
        options.addOptionGroup(sortingModeGroup);

        OptionGroup typeOfFilesGroup = new OptionGroup();
        typeOfFilesGroup.addOption(new Option("i", "integer", false, "Сортировка целочисленных объектов. Параметр обязательный."));
        typeOfFilesGroup.addOption(new Option("s", "string", false, "Сортировка строковых объектов. Параметр обязательный."));
        typeOfFilesGroup.setRequired(true);
        options.addOptionGroup(typeOfFilesGroup);
    }

    private static void printHelpAndClose(Options options, int status) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("""
                java -jar javatesttask.jar [OPTIONS] output.txt input.txt\n
                out.txt - имя выходного файла с результатом сортировки слиянием (Параметр обязательный).\n
                in.txt - имя входных файлов, количество которых должно быть не менее одного (Параметр обязательный).\n
                """, options);
        System.exit(status);
    }
}
