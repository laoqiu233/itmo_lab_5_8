package com.dmtri.client;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import com.dmtri.client.collectionmanagers.FileCollectionManager;
import com.dmtri.client.collectionmanagers.SaveableCollectionManager;
import com.dmtri.client.commands.AddCommand;
import com.dmtri.client.commands.ClearCommand;
import com.dmtri.client.commands.ExecuteScriptCommand;
import com.dmtri.client.commands.ExitCommand;
import com.dmtri.client.commands.HeadCommand;
import com.dmtri.client.commands.HelpCommand;
import com.dmtri.client.commands.InfoCommand;
import com.dmtri.client.commands.PrintUniqueDistance;
import com.dmtri.client.commands.RemoveAllByDistanceCommand;
import com.dmtri.client.commands.RemoveByIdCommand;
import com.dmtri.client.commands.RemoveFirstCommand;
import com.dmtri.client.commands.RemoveGreaterCommand;
import com.dmtri.client.commands.ShowCommand;
import com.dmtri.client.commands.SumOfDistanceCommand;
import com.dmtri.client.commands.UpdateCommand;
import com.dmtri.client.userio.BasicUserIO;
import com.dmtri.common.exceptions.IncorrectFileStructureException;
import com.dmtri.common.util.TerminalColors;

import org.xml.sax.SAXException;

public final class Client {
    private Client() {
        throw new UnsupportedOperationException("This is an utility class and can not be instantiated");
    }

    private static void addBasicCommands(CommandHandler ch, SaveableCollectionManager cm) {
        ch.addCommand(new HelpCommand(ch));
        ch.addCommand(new InfoCommand(cm));
        ch.addCommand(new ShowCommand(cm));
        ch.addCommand(new AddCommand(cm));
        ch.addCommand(new RemoveByIdCommand(cm));
        ch.addCommand(new UpdateCommand(cm));
        ch.addCommand(new ExitCommand());
        ch.addCommand(new ClearCommand(cm));
        ch.addCommand(new SumOfDistanceCommand(cm));
        ch.addCommand(new HeadCommand(cm));
        ch.addCommand(new RemoveFirstCommand(cm));
        ch.addCommand(new RemoveGreaterCommand(cm));
        ch.addCommand(new RemoveAllByDistanceCommand(cm));
        ch.addCommand(new PrintUniqueDistance(cm));
        ch.addCommand(new ExecuteScriptCommand());
    }

    public static void main(String[] args) {
        // Get file name from environment variable
        String fileName = System.getenv("FILENAME");

        if (fileName == null) {
            System.err.println(TerminalColors.colorString("ERROR: The collection file should be specified in environment variables with the name FILENAME", TerminalColors.RED));
            return;
        }

        FileCollectionManager cm;
        CommandHandler ch = new CommandHandler();
        BasicUserIO io = new BasicUserIO();

        try {
            cm = new FileCollectionManager(fileName);
        } catch (
            SAXException
            | IOException
            | IncorrectFileStructureException
            | ParserConfigurationException
            | TransformerException e
        ) {
            System.out.println(TerminalColors.colorString("Failed to parse provided file \"" + fileName + "\"", TerminalColors.RED));
            System.out.println(e);
            System.exit(1);
            return;
        }

        addBasicCommands(ch, cm);

        ConsoleClient console = new ConsoleClient(io, ch);
        console.run();
    }
}
