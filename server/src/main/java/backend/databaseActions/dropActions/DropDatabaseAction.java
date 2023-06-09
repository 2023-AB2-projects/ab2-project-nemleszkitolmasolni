package backend.databaseActions.dropActions;

import backend.config.Config;
import backend.databaseActions.DatabaseAction;
import backend.databaseActions.miscActions.UseDatabaseAction;
import backend.databaseModels.DatabaseModel;
import backend.exceptions.databaseActionsExceptions.DatabaseDoesntExist;
import backend.service.Utility;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

@Slf4j
public class DropDatabaseAction implements DatabaseAction {
    private final DatabaseModel database;

    public DropDatabaseAction(DatabaseModel databaseModel) {
        this.database = databaseModel;
    }

    @Override
    public Object actionPerform() throws DatabaseDoesntExist {
        // Check if master is being dropped
        if(this.database.getDatabaseName().equals("master"))  {
            log.info("Tried to delete 'master'!");
            return null;
        }

        // Object mapper with indented output
        ObjectMapper mapper = Utility.getObjectMapper();

        // Json catalog -> Java JsonNode
        JsonNode rootNode;
        try {
            rootNode = mapper.readTree(Config.getCatalogFile());
        } catch (IOException exception) {
            log.error("DropDatabaseAction -> Mapper couldn't build tree from catalog!");
            throw new RuntimeException(exception);
        }

        // Get current array of databases stored in 'Databases' json node
        ArrayNode databasesArray = (ArrayNode) rootNode.get(Config.getDbCatalogRoot());
        int removed_ind = 0;
        for (final JsonNode databaseNode : databasesArray) {
            // For each "Database" node find the name of the database
            JsonNode currentDatabaseNodeValue = databaseNode.get("database").get("databaseName");

            if (currentDatabaseNodeValue == null) {
                log.error("UseDatabaseAction -> Database null -> \"databaseName\" not found");
                continue;
            }

            // Check if a database exists with the given database name
            String currentDatabaseName = currentDatabaseNodeValue.asText();

            // If the current databaseName is equal to the deleted database
            if(currentDatabaseName.equals(this.database.getDatabaseName())) {
                // Remove database folder
                String databaseFolderPath = Config.getDbRecordsPath() + File.separator + this.database.getDatabaseName();
                if(!Utility.deleteDirectory(new File(databaseFolderPath))) {
                    log.error("Database directory=" + databaseFolderPath + " could not be deleted!");
                    throw new RuntimeException();
                }

                // Remove database and write back
                databasesArray.remove(removed_ind);

                // Mapper -> Write entire catalog
                try {
                    mapper.writeValue(Config.getCatalogFile(), rootNode);
                } catch (IOException e) {
                    log.error("CreateDatabaseAction -> Write value (mapper) failed");
                    throw new RuntimeException(e);
                }

                // Switch to 'master' database
                UseDatabaseAction useMaster = new UseDatabaseAction(new DatabaseModel("master", new ArrayList<>()));
                useMaster.actionPerform();

                return this.database.getDatabaseName();
            }

            // Next database index
            removed_ind += 1;
        }
        throw new DatabaseDoesntExist(this.database.getDatabaseName());
    }
}
