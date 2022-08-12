package ru.cft.javatesttask;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SortingWithFiles {
    private static String outputFile;
    private static List<String> inputFiles;
    private static List<List<String>> dataFromFiles;
    private static Scanner scanner;

    public SortingWithFiles(String outputFileName, List<String> inputFilesNames) {
        inputFiles = new ArrayList<>();
        dataFromFiles = new ArrayList<>();
        outputFile = outputFileName;
        inputFiles = fileProcessing(outputFileName, inputFilesNames);
        dataFromFiles = readFiles(inputFiles);
    }

    private static List<List<String>> readFiles(List<String> files) {
        for (String file : files) {
            List<String> sortableDataFromFile = new ArrayList<>();
            boolean isSuccessfulVerification = true;

            try {
                FileInputStream inputStream = new FileInputStream(file);
                scanner = new Scanner(inputStream);
            }
            catch (FileNotFoundException e) {
                System.out.println("Не удалось найти файл.");
            }

            while (scanner.hasNextLine()) {
                String data = scanner.nextLine();

                if (data == null) continue;

                if (checkErrors(data, file)) {
                    isSuccessfulVerification = false;
                    break;
                }

                sortableDataFromFile.add(data);
            }

            updatingSortedDataInFile(sortableDataFromFile, file);

            if (isSuccessfulVerification) dataFromFiles.add(sortableDataFromFile);
        }
        return dataFromFiles;
    }
    private static boolean checkErrors(String data, String fileName) {
        if (Main.isStrings) {
            if (data.indexOf(" ") > 0) {
                System.out.println("Найдена строка с символом пробела: " + data +
                        "\nФайл " + fileName + " исключён из обработки");
                return true;
            }
        }
        else {
            try {
                int integerNum = Integer.parseInt(data);
            }
            catch (NumberFormatException e) {
                System.out.println("Невозможно преобразовать символ(ы) в целое число в строке: " + data +
                        "\nФайл " + fileName + " исключён из обработки");
                return true;
            }
        }
        return false;
    }

    private static void updatingSortedDataInFile(List<String> file, String fileName) {
        if (file.size() < 2) return;

        for (int index = 0; index < file.size() - 1; index++) {
            if (checkSortedDataInFile(file, index)) {
                while (index < file.size()) {
                    file.remove(index);
                    // TODO Удаление всех данных после косячной строки переделать в удаление одной строки и смещения всех
                    // TODO последующих
                }
                return;
            }
        }
    }

    private static boolean checkSortedDataInFile(List<String> file, int currentIndex) {
        if (Main.isStrings) {
            int numSign = file.get(currentIndex).compareTo(file.get(currentIndex + 1));

            if (Main.isAscending) {
                if (numSign >= 0) return true;
            }
            else {
                if (numSign < 0) return true;
            }
        }
        else {
            int currentValue = Integer.parseInt(file.get(currentIndex));
            int nextValue = Integer.parseInt(file.get(currentIndex + 1));

            if (Main.isAscending) {
                if (currentValue > nextValue) return true;
            }
            else {
                if (currentValue < nextValue) return true;
            }
        }
        return false;
    }

    private static void writeFile(List<String> sortedData, String fileName) {
        File file = new File(fileName);

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            for (String data : sortedData) {
                fileOutputStream.write(data.getBytes(), 0, data.length());
                fileOutputStream.write("\n".getBytes());
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("Не удалось найти файл.");
        }
        catch (IOException e) {
            System.out.println("Не удалось произвести запись в файл.");
        }
    }

    private static List<String> fileProcessing(String outputFileName, List<String> inputFilesNames) {
        File outputFile = new File(outputFileName);

        try {
            if (outputFile.createNewFile()) {
                System.out.println("Файл " + outputFileName + " успешно создан.");
            }
            else {
                System.out.println("Файл " + outputFileName + " уже существует.");

                if (!outputFile.canWrite()) {
                    System.out.println("Доступ к записи в файл " + outputFileName + " ограничен.");
                    outputFileName = "";
                    inputFilesNames.clear();
                    System.exit(200);
                }
            }
        }
        catch (IOException e) {
            System.out.println("Непредвиденная ошибка при открытии файла.");
        }

        for (int index = 0; index < inputFilesNames.size();) {
            String inputFileName = inputFilesNames.get(index);
            File inputFile = new File(inputFileName);

            if (inputFile.exists()) {
                System.out.println("Файл " + inputFileName + " успешно найден.");

                if (inputFile.canRead()) {
                    index++;
                }
                else {
                    System.out.println("Доступ к чтению из файла " + inputFileName + " ограничен." +
                            "Произойдет удаление файла из списка.");
                    inputFilesNames.remove(index);
                }
            }
            else {
                System.out.println("Файл " + inputFileName + " не найден. Произойдёт удаление файла из списка.");
                inputFilesNames.remove(index);
            }
        }

        if (outputFileName.equals("")) {
            System.out.println("Отсутствует выходной файл.");
            System.exit(201);
        }

        if (inputFilesNames.size() < 1) {
            System.out.println("Отсутствуют входн(ой)/(ые) файл(ы).");
            inputFilesNames.clear();
            System.exit(202);
        }

        return inputFilesNames;
    }

    public void mergeSorting() {
        while (dataFromFiles.size() > 1) {
            int sizeDataFromFiles = dataFromFiles.size();
            List<String> tempData = new ArrayList<>();

            while (dataFromFiles.get(sizeDataFromFiles - 1).size() > 0 && dataFromFiles.get(sizeDataFromFiles - 2).size() > 0) {
                String value1 = dataFromFiles.get(sizeDataFromFiles - 1).get(0);
                String value2 = dataFromFiles.get(sizeDataFromFiles - 2).get(0);

                if (Main.isStrings) {
                    int numSign = value1.compareTo(value2);

                    if (Main.isAscending) {
                        if (numSign <= 0) {
                            tempData.add(value1);
                            dataFromFiles.get(sizeDataFromFiles - 1).remove(0);
                        }
                        else {
                            tempData.add(value2);
                            dataFromFiles.get(sizeDataFromFiles - 2).remove(0);
                        }
                    }
                    else {
                        if (numSign >= 0) {
                            tempData.add(value1);
                            dataFromFiles.get(sizeDataFromFiles - 1).remove(0);
                        }
                        else {
                            tempData.add(value2);
                            dataFromFiles.get(sizeDataFromFiles - 2).remove(0);
                        }
                    }
                }
                else {
                    int number1 = Integer.parseInt(value1);
                    int number2 = Integer.parseInt(value2);

                    if (Main.isAscending) {
                        if (number1 <= number2) {
                            tempData.add(value1);
                            dataFromFiles.get(sizeDataFromFiles - 1).remove(0);
                        }
                        else {
                            tempData.add(value2);
                            dataFromFiles.get(sizeDataFromFiles - 2).remove(0);
                        }
                    }
                    else {
                        if (number1 >= number2) {
                            tempData.add(value1);
                            dataFromFiles.get(sizeDataFromFiles - 1).remove(0);
                        }
                        else {
                            tempData.add(value2);
                            dataFromFiles.get(sizeDataFromFiles - 2).remove(0);
                        }
                    }
                }
            }

            while (dataFromFiles.get(sizeDataFromFiles - 1).size() > 0) {
                tempData.add(dataFromFiles.get(sizeDataFromFiles - 1).get(0));
                dataFromFiles.get(sizeDataFromFiles - 1).remove(0);
            }

            while (dataFromFiles.get(sizeDataFromFiles - 2).size() > 0) {
                tempData.add(dataFromFiles.get(sizeDataFromFiles - 2).get(0));
                dataFromFiles.get(sizeDataFromFiles - 2).remove(0);
            }

            dataFromFiles.set(sizeDataFromFiles - 2, tempData);
            dataFromFiles.remove(sizeDataFromFiles - 1);
        }

        writeFile(dataFromFiles.get(0), outputFile);
    }
}
