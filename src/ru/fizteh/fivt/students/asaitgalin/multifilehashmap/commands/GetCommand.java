package ru.fizteh.fivt.students.asaitgalin.multifilehashmap.commands;

import ru.fizteh.fivt.students.asaitgalin.multifilehashmap.MultiFileTableState;
import ru.fizteh.fivt.students.asaitgalin.shell.Command;

import java.io.IOException;

public class GetCommand implements Command {
    private  MultiFileTableState state;

    public GetCommand(MultiFileTableState state) {
        this.state = state;
    }

    @Override
    public String getName() {
        return "get";
    }

    @Override
    public void execute(String[] args) throws IOException {
       if (state.currentTable == null) {
            System.out.println("no table");
        } else {
            String value =  state.currentTable.get(args[1]);
            if (value != null) {
                System.out.println("found");
                System.out.println(value);
            } else {
                System.out.println("not found");
            }
        }
    }

    @Override
    public int getArgsCount() {
        return 1;
    }
}
