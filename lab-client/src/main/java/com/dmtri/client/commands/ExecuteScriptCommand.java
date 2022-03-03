package com.dmtri.client.commands;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import com.dmtri.client.commandhandlers.CommandHandler;
import com.dmtri.client.userio.BasicUserIO;
import com.dmtri.common.util.TerminalColors;

public class ExecuteScriptCommand extends AbstractCommand {
    private BasicUserIO io;
    private CommandHandler ch;
    private Set<File> openedFiles = new HashSet<>();

    public ExecuteScriptCommand(BasicUserIO io, CommandHandler ch) {
        super("execute_script");
        this.io = io;
        this.ch = ch;
    }

    @Override
    public String getUsage() {
        return TerminalColors.colorString("execute_script [fileName]", TerminalColors.GREEN)
             + " - executes all command in the specified file";
    }

    @Override
    public void execute(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("execute_script takes exactly 1 argument, recieved " + args.length);
        }

        File file = new File(args[0]);

        if (openedFiles.contains(file)) {
            throw new IllegalArgumentException("Potential recursion with file \"" + args[0] + '"');
        }

        openedFiles.add(file);

        try (InputStreamReader input = new InputStreamReader(new FileInputStream(file))) {
            handleInput(input);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("Cannot locate file with the name " + args[0]);
        } catch (IOException e) {
            io.writeln("Error when trying to read from file:");
            io.writeln(e);
        } finally {
            openedFiles.remove(file);
        }
    }

    private void handleInput(InputStreamReader input) {
        StringBuilder sb = new StringBuilder();
        int i;
        try {
            while (true) {
                i = input.read();
                if (i >= 0) {
                    sb.append((char) i);
                }
                if (i == -1 || (char) i == '\n') {
                    String inputString = sb.toString().trim();

                    if (!inputString.isEmpty()) {
                        io.writeln("$ " + inputString);
                        ch.handle(inputString);
                    }

                    sb = new StringBuilder();

                    if (i == -1) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            io.writeln("Failed to execute command");
            io.writeln(e);
        }
    }
}
