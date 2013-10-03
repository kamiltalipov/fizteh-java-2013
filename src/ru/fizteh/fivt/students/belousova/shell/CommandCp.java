package ru.fizteh.fivt.students.belousova.shell;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class CommandCp implements Command {
    private final String name = "cp";
    private ShellState state;

    public String getName() {
        return name;
    }

    public CommandCp(ShellState state) {
        this.state = state;
    }

    public void execute(String args) throws IOException {
        try {
            Scanner scanner = new Scanner(args);
            scanner.next();
            String sourceName = scanner.next();
            String destinationName = scanner.next();

            File source = FileUtils.getFileFromString(sourceName, state);
            File destination = FileUtils.getFileFromString(destinationName, state);

            if (!source.exists()) {
                throw new IOException("cannot copy '" + source.getName() + "': No such file or directory");
            }
            if (destination.isFile() && source.isDirectory()) {
                throw new IOException("cannot overwrite non-directory '" + destination.getName()
                        + "' with directory '" + source.getName() + "'");
            }

            if (!destination.exists() || destination.isFile()) {
                FileUtils.copyFileToFile(source, destination);
            } else if (destination.isDirectory()) {
                if (source.isFile()) {
                    FileUtils.copyFileToFolder(source, destination);
                } else {
                    FileUtils.copyFolderToFolder(source, destination);
                }
            }

        } catch (NoSuchElementException e) {
            throw new IOException(name + ": missing file operand");
        } catch (IOException e) {
            throw new IOException(name + ": " + e.getMessage());
        }
    }
}
