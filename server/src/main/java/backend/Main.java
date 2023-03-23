package backend;

import backend.databaseactions.createactions.CreateDatabaseAction;
import backend.databaseactions.DatabaseAction;
import backend.exceptions.DatabaseNameAlreadyExists;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class Main {
    public static void main(String[] args) throws IOException {
        log.info(System.getProperty("user.dir"));

        DatabaseAction databaseAction = new CreateDatabaseAction("Akoska kis bazisa");

        try {
            databaseAction.actionPerform();
        } catch (DatabaseNameAlreadyExists exception) {
            System.out.println("Database already exists!");
        }
    }
}
