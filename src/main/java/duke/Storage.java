package duke;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.io.File;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Stores the list for future calls of the duke function to retain the list of tasks
 * @author Cedric
 */
public class Storage {
    private File folder = new File("data");
    private File file = new File(folder, "data.txt");

    private final Pattern pTodo = Pattern.compile("[T]");
    private final Pattern pDeadline = Pattern.compile("[D]");
    private final Pattern pEvent2 = Pattern.compile("[E]");
    private final Pattern pUnmarked = Pattern.compile("[ ]");
    private final Pattern pMarked = Pattern.compile("[X]");
    /**
     * Checks that the file and folder exists, else creates them
     */
    public void check() {
        folder = new File("data");
        // Check if the directory exists, create it if it doesn't
        if (!folder.exists()) {
            if (folder.mkdirs()) {
                System.out.println("Directory created: " + folder);
            } else {
                System.err.println("Failed to create directory: " + folder);
                return;
            }
        }

        // Create a File object for the file
        file = new File(folder, "data.txt");

        // Check if the file exists, create it if it doesn't
        try {
            file.createNewFile();
        } catch (IOException e) {
            System.err.println("Error creating file: " + e.getMessage());
        }
    }
    /**
     * Clears the txt file
     */
    public void clear() {
        if (!folder.exists()) {
            folder.mkdir();
            file = new File(folder, "data.txt");
        }
        if (file.exists()) {
            try (PrintWriter writer = new PrintWriter(file)) {
                writer.print(""); // This clears all lines in the file
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    }
    /**
     * Writes a line to the txt file then calls newline
     * @param n the String to write
     */
    public void add(String n) {
        assert folder.exists();
        assert file.exists();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(n);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
    /**
     * finds the line to delete and rewrites txt with all other lines
     * @param number the line to delete
     */
    public void delete(int number) {
        assert folder.exists();
        assert file.exists();
        // Read the content of the file
        ArrayList<String> lines = new ArrayList<>();
        String currentLine;
        int currLine = 0;
        while ((currentLine = read(currLine)) != null) {
            // Check if the line should be deleted
            if (currLine != number) {
                lines.add(currentLine);
            }
            currLine = currLine + 1;
        }
        clear();
        for (String Line : lines) {
            add(Line);
        }
    }
    /**
     * Reads and returns the nth line in the storage
     * @param n the line which to return
     * @return returns the string found
     */
    public String read(int n) {
        assert folder.exists();
        assert file.exists();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String currentLine = null;
            int currLine = 0;
            while (currLine <= n) {
                currentLine = reader.readLine();
                currLine = currLine + 1;
            }
            return currentLine;
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return null;
        }
    }
    /**
     * finds the input line, replaces it with String L, and rewrites the whole file
     * @param number line to edit
     * @param L String to change the line into
     */

}
