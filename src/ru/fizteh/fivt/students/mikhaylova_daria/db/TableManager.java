package ru.fizteh.fivt.students.mikhaylova_daria.db;

import ru.fizteh.fivt.students.mikhaylova_daria.shell.Shell;

import java.io.File;
import java.util.HashMap;
import ru.fizteh.fivt.storage.strings.*;

public class TableManager implements TableProvider {
    private HashMap<String, TableData> bidDataBase = new HashMap<String, TableData>();
    private File mainDir;

    TableManager(String nameMainDir) throws IllegalArgumentException {
        mainDir = new File(nameMainDir);
        if (!mainDir.exists()) {
            throw new IllegalArgumentException(nameMainDir + " doesn't exist");
        }

        if (!mainDir.isDirectory()) {
            throw new IllegalArgumentException(nameMainDir + " is not a directory");
        }
        try {
            cleaner();
        } catch (IllegalStateException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void cleaner() throws Exception {
        HashMap<String, Short> fileNames = new HashMap<String, Short>();
        HashMap<String, Short> dirNames = new HashMap<String, Short>();
        for (short i = 0; i < 16; ++i) {
            fileNames.put(i + ".dat", i);
            dirNames.put(i + ".dir", i);
        }
        File[] tables = mainDir.listFiles();
        for (short i = 0; i < tables.length; ++i) {
            if (tables[i].isFile()) {
                throw new IllegalStateException(tables[i].toString() + " is not table");
            }
            File[] directories = tables[i].listFiles();
            if (directories.length > 16) {
                throw new IllegalStateException(tables[i].toString() + ": Wrong number of files in the table");
            }
            Short[] idFile = new Short[2];
            for (short j = 0; j < directories.length; ++j) {
                if (directories[j].isFile() || !dirNames.containsKey(directories[j].getName())) {
                    throw new IllegalStateException(directories[j].toString() + " is not directory of table");
                }
                idFile[0] = dirNames.get(directories[j].getName());
                File[] files = directories[j].listFiles();
                if (files.length > 16) {
                    throw new IllegalStateException(tables[i].toString() + ": " + directories[j].toString()
                            + ": Wrong number of files in the table");
                }
                for (short g = 0; g < files.length; ++g) {
                    if (files[g].isDirectory() || !fileNames.containsKey(files[g].getName())) {
                        throw new IllegalStateException(files[g].toString() + " is not a file of Date Base table");
                    }
                    idFile[1] = fileNames.get(files[g].getName());
                    FileMap currentFileMap = new FileMap(files[g].getCanonicalFile(), idFile);
                    currentFileMap.readerFile();
                    currentFileMap.setAside();
                }
                File[] checkOnEmpty = directories[j].listFiles();
                if (checkOnEmpty.length == 0) {
                    if (!directories[j].delete()) {
                        throw new Exception(directories[j] + ": Deleting error");
                    }
                }
            }
        }
    }

    public TableData createTable(String nameTable) throws IllegalArgumentException {
        if (nameTable == null) {
            throw new IllegalArgumentException("nameTable is null");
        }
        nameTable = nameTable.trim();
        if (nameTable.isEmpty()) {
            throw new IllegalArgumentException("nameTable is empty");
        }
        if (nameTable.contains("\\") || nameTable.contains("/")) {
            throw new IllegalArgumentException("bad symbol in tablename");
        }
        if (nameTable.startsWith(".") || (nameTable.endsWith("."))) {
            throw new IllegalArgumentException("bad symbol in tablename");
        }
        String correctName = mainDir.toPath().toAbsolutePath().normalize().resolve(nameTable).toString();
        File creatingTableFile = new File(correctName);
        TableData creatingTable = null;
        if (!creatingTableFile.exists()) {
            creatingTable = new TableData(creatingTableFile);
            if (!creatingTableFile.isDirectory()) {
                throw new RuntimeException(correctName + "is not directory");
            }
            if (!bidDataBase.containsKey(nameTable)) {
                bidDataBase.put(nameTable, creatingTable);
            }
        }
        return creatingTable;
    }

    public TableData getTable(String nameTable) throws IllegalArgumentException {
        if (nameTable == null) {
            throw new IllegalArgumentException();
        }
        nameTable = nameTable.trim();
        if (nameTable.isEmpty()) {
            throw new IllegalArgumentException("nameTable is empty");
        }
        if (nameTable.contains("\\") || nameTable.contains("/")) {
            throw new IllegalArgumentException("bad symbol in tablename");
        }
        TableData table = null;
        String correctName = mainDir.toPath().toAbsolutePath().normalize().resolve(nameTable).toString();
        File creatingTableFile = new File(correctName);
        if (bidDataBase.containsKey(nameTable)) {
            table = bidDataBase.get(nameTable);
        } else {
            if (creatingTableFile.exists()) {
                table = new TableData(creatingTableFile);
                if (!bidDataBase.containsKey(nameTable)) {
                    bidDataBase.put(nameTable, table);
                }
            }
        }
        return table;
    }

    public void removeTable(String nameTable) throws IllegalArgumentException, IllegalStateException {
        if (nameTable == null) {
            throw new IllegalArgumentException("nameTable is null");
        }
        nameTable = nameTable.trim();
        if (nameTable.isEmpty()) {
            throw new IllegalArgumentException("nameTable is empty");
        }
        if (nameTable.contains("\\") || nameTable.contains("/")) {
            throw new IllegalArgumentException("bad symbol in tablename");
        }

        if (nameTable.startsWith(".") || (nameTable.endsWith("."))) {
            throw new IllegalArgumentException("bad symbol in tablename");
        }
        String correctName = mainDir.toPath().toAbsolutePath().normalize().resolve(nameTable).toString();
        File creatingTableFile = new File(correctName);
        if (!creatingTableFile.exists()) {
            throw new IllegalStateException("Table " + nameTable + "does not exist");
        } else {
            String[] argShell = new String[] {
                    "rm",
                    creatingTableFile.toPath().toString()
            };
            Shell.main(argShell);
            bidDataBase.remove(nameTable);
        }
    }

}
