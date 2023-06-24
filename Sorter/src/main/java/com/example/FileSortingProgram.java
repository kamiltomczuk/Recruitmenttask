package com.example;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileSortingProgram {
    private static final String HOME_DIR = "/home";
    private static final String DEV_DIR = "/DEV";
    private static final String TEST_DIR = "/TEST";
    private static final String COUNT_FILE_PATH = "/count.txt";

    private static int totalFilesMoved = 0;
    private static int devFilesMoved = 0;
    private static int testFilesMoved = 0;

    public static void main(String[] args) {
        createDirectories();

        try {
            Files.walkFileTree(Paths.get(HOME_DIR), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (file.toFile().isFile()) {
                        String extension = getFileExtension(file.toFile());
                        if (extension.equals("jar")) {
                            int creationHour = getFileCreationHour(file.toFile());
                            if (creationHour % 2 == 0) {
                                moveFile(file, DEV_DIR);
                                devFilesMoved++;
                            } else {
                                moveFile(file, TEST_DIR);
                                testFilesMoved++;
                            }
                        } else if (extension.equals("xml")) {
                            moveFile(file, DEV_DIR);
                            devFilesMoved++;
                        }
                        totalFilesMoved++;
                    }
                    return FileVisitResult.CONTINUE;
                }
            });

            updateCountFile(totalFilesMoved, devFilesMoved, testFilesMoved);
            System.out.println("File sorting completed successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while sorting files: " + e.getMessage());
        }
    }

    private static void createDirectories() {
        createDirectoryIfNotExists(HOME_DIR);
        createDirectoryIfNotExists(DEV_DIR);
        createDirectoryIfNotExists(TEST_DIR);
    }

    private static void createDirectoryIfNotExists(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    private static String getFileExtension(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex != -1 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1);
        }
        return "";
    }

    private static int getFileCreationHour(File file) throws IOException {
        long creationTime = Files.readAttributes(file.toPath(), BasicFileAttributes.class).creationTime().toMillis();
        Date creationDate = new Date(creationTime);
        SimpleDateFormat sdf = new SimpleDateFormat("HH");
        return Integer.parseInt(sdf.format(creationDate));
    }

    private static void moveFile(Path file, String destinationDirectory) throws IOException {
        File destDir = new File(destinationDirectory);
        Path destPath = destDir.toPath().resolve(file.getFileName());
        Files.move(file, destPath, StandardCopyOption.REPLACE_EXISTING);
    }

    private static void updateCountFile(int totalFilesMoved, int devFilesMoved, int testFilesMoved) {
        String countContent = "Total files moved: " + totalFilesMoved +
                "\nFiles moved to DEV directory: " + devFilesMoved +
                "\nFiles moved to TEST directory: " + testFilesMoved;

        try {
            Files.write(Paths.get(COUNT_FILE_PATH), countContent.getBytes(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            System.out.println("An error occurred while updating the count file: " + e.getMessage());
        }
    }
}


