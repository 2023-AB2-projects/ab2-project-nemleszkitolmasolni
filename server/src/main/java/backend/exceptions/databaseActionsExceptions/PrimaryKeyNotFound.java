package backend.exceptions.databaseActionsExceptions;

public class PrimaryKeyNotFound extends Exception {
    public PrimaryKeyNotFound(String tableName, String primaryKeyName) {
        super("In table '" + tableName + "' there's no primary key with name '" + primaryKeyName + "'");
    }
}
