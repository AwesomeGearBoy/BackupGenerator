import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.FileVisitResult;
import java.nio.file.StandardCopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

public class BackupGenerator {
    public static void main(String[] args) throws Exception {
        Path baseDir = Paths.get("").toAbsolutePath();
        Path worldFilePath = baseDir.resolve("world");
        Path backupsFilePath = baseDir.resolve("backups");
        checkDirectories(worldFilePath, backupsFilePath);
        createBackup(worldFilePath, backupsFilePath);
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
        System.out.println("Creating a backup of your world...");
        System.out.println("Copying " + humanReadableSize(calculateDirectorySize(world)) + " of data...");
        Path targetDir = backups.resolve("backup_" + formattedDateTime());
        Files.walkFileTree(world, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path targetPath = targetDir.resolve(world.relativize(dir));
                Files.createDirectories(targetPath);
                return FileVisitResult.CONTINUE;
            }
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path targetPath = targetDir.resolve(world.relativize(file));
                Files.copy(file, targetPath, StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }
        });
        System.out.println("Done!");
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
    
    private static String formattedDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String formatted = now.format(formatter);
        return formatted;
    }
}
