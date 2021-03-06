package ru.fizteh.fivt.students.adanilyak.multifilehashmap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.adanilyak.tools.CheckOnCorrect;
import ru.fizteh.fivt.students.adanilyak.tools.CountingTools;
import ru.fizteh.fivt.students.adanilyak.tools.WorkWithMFHM;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * User: Alexander
 * Date: 21.10.13
 * Time: 13:26
 */
public class TableStorage implements Table {
    private File tableStorageDirectory;
    private Map<String, String> data = new HashMap<String, String>();
    private Map<String, String> changes = new HashMap<String, String>();
    private Set<String> removedKeys = new HashSet<String>();
    private int amountOfChanges = 0;

    public TableStorage(File dataDirectory) {
        tableStorageDirectory = dataDirectory;
        try {
            WorkWithMFHM.readIntoDataBase(tableStorageDirectory, data);
        } catch (IOException exc) {
            throw new IllegalArgumentException("Read from file failed", exc);
        }
    }

    @Override
    public String getName() {
        return tableStorageDirectory.getName();
    }

    @Override
    public String get(String key) {
        if (!CheckOnCorrect.goodArg(key)) {
            throw new IllegalArgumentException("get: key is bad");
        }
        String resultOfGet = changes.get(key);
        if (resultOfGet == null) {
            if (removedKeys.contains(key)) {
                return null;
            }
            resultOfGet = data.get(key);
        }
        return resultOfGet;
    }

    @Override
    public String put(String key, String value) {
        if (!CheckOnCorrect.goodArg(key) || !CheckOnCorrect.goodArg(value)) {
            throw new IllegalArgumentException("put: key or value is bad");
        }
        String valueInData = data.get(key);
        String resultOfPut = changes.put(key, value);

        if (resultOfPut == null) {
            amountOfChanges++;
            if (!removedKeys.contains(key)) {
                resultOfPut = valueInData;
            }
        }
        if (valueInData != null) {
            removedKeys.add(key);
        }
        return resultOfPut;
    }

    @Override
    public String remove(String key) {
        if (!CheckOnCorrect.goodArg(key)) {
            throw new IllegalArgumentException("remove: key is null");
        }

        String resultOfRemove = changes.get(key);
        if (resultOfRemove == null && !removedKeys.contains(key)) {
            resultOfRemove = data.get(key);
        }
        if (changes.containsKey(key)) {
            amountOfChanges--;
            changes.remove(key);
            if (data.containsKey(key)) {
                removedKeys.add(key);
            }
        } else {
            if (data.containsKey(key) && !removedKeys.contains(key)) {
                removedKeys.add(key);
                amountOfChanges++;
            }
        }
        return resultOfRemove;
    }

    @Override
    public int size() {
        return data.size() + changes.size() - removedKeys.size();
    }

    @Override
    public int commit() {
        int result = CountingTools.correctCountingOfChanges(data, changes, removedKeys);
        for (String key : removedKeys) {
            data.remove(key);
        }
        data.putAll(changes);
        try {
            WorkWithMFHM.writeIntoFiles(tableStorageDirectory, data);
        } catch (Exception exc) {
            System.err.println("commit: " + exc.getMessage());
        }
        setDefault();
        return result;
    }

    @Override
    public int rollback() {
        int result = CountingTools.correctCountingOfChanges(data, changes, removedKeys);
        setDefault();
        return result;
    }

    private void setDefault() {
        changes.clear();
        removedKeys.clear();
        amountOfChanges = 0;
    }

    public int getAmountOfChanges() {
        return amountOfChanges;
    }
}
