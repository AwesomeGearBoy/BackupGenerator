import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.IOException;
import java.io.FileInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

public class BackupGenerator {
    private static final int BAR_WIDTH = 20;

    public static void main(String[] args) throws Exception {
        Path baseDir = Paths.get("").toAbsolutePath();
        Path worldFilePath = baseDir.resolve("world");
        Path backupsFilePath = baseDir.resolve("backups");
        checkDirectories(worldFilePath, backupsFilePath);
        createBackup(worldFilePath, backupsFilePath);
        System.out.println("\nDone!");
        System.exit(0);
    }

     private static void checkDirectories(Path world, Path backups) {
        System.out.println("Checking directories...");
        System.out.println("Looking for a \"world\" directory.");
        if (Files.exists(world) && Files.isDirectory(world)) {
            System.out.println("Found a \"world\" directory. Continuing...");
        } else {
            System.out.println("Could not find a \"world\" directory. Did you create a world yet?");
            System.out.println("Exiting...");
            System.exit(1);
        }
        System.out.println("Searching for a \"backups\" directory.");
        if (Files.exists(backups) && Files.isDirectory(backups)) {
            System.out.println("Found a \"backups\" directory. Continuing...");
        } else {
            System.out.println("Could not find a \"backups\" directory. Creating a new directory...");
            try {
                Files.createDirectories(backups);
                System.out.println("Created a \"backups\" directory.");
            } catch (Exception e) {
                System.out.println("Failed to create a \"backups\" directory.");
                System.out.println("Exiting...");
                System.exit(1);
            }
        }
    }

    private static void createBackup(Path world, Path backups) throws IOException {
        System.out.println("Creating backup...");
        System.out.println("Packaging " + humanReadableSize(calculateDirectorySize(world)) + " of data...");
        long totalFiles = Files.walk(world).filter(Files::isRegularFile).count();
        final long[] processedFiles = {0};
        final int[] lastFilledBars = {-1};
        Path zipPath = backups.resolve(formattedDateTime() + ".zip");
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
            zos.setLevel(9);
            Files.walkFileTree(world, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    Path relativePath = world.getParent().relativize(dir);
                    String entryName = relativePath.toString().replace("\\", "/") + "/";
                    zos.putNextEntry(new ZipEntry(entryName));
                    zos.closeEntry();
                    return FileVisitResult.CONTINUE;
                }
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Path relativePath = world.getParent().relativize(file);
                    String entryName = relativePath.toString().replace("\\", "/");
                    zos.putNextEntry(new ZipEntry(entryName));
                    try (FileInputStream fis = new FileInputStream(file.toFile())) {
                        byte[] buffer = new byte[8192];
                        int len;
                        while ((len = fis.read(buffer)) != -1) {
                            zos.write(buffer, 0, len);
                        }
                    }
                    zos.closeEntry();
                    processedFiles[0]++;
                    int filledBars = (int) ((processedFiles[0] * BAR_WIDTH) / totalFiles);
                    if (filledBars != lastFilledBars[0]) {
                        lastFilledBars[0] = filledBars;
                        printProgressBar(filledBars);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }

    private static void printProgressBar(int filled) {
        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < BAR_WIDTH; i++) {
            if (i < filled) {
                bar.append("=");
            } else {
                bar.append("~");
            }
        }
        bar.append("]");
        System.out.print("\r" + bar.toString());
    }

    private static String humanReadableSize(long bytes) {
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        double size = bytes;
        int unitIndex = 0;
        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }
        return String.format("%.2f %s", size, units[unitIndex]);
    }

    private static long calculateDirectorySize(Path path) throws IOException {
        final long[] size = {0};
    
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                size[0] += attrs.size();
                return FileVisitResult.CONTINUE;
            }
        });
    
        return size[0];
    }

    private static String formattedDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        return now.format(formatter);
    }
}
