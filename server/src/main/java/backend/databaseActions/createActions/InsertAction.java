package backend.databaseActions.createActions;

import backend.databaseActions.DatabaseAction;
import backend.exceptions.databaseActionsExceptions.*;
import backend.exceptions.recordHandlingExceptions.RecordNotFoundException;
import backend.exceptions.validatorExceptions.ForeignKeyValueNotFoundInParentTable;
import backend.exceptions.validatorExceptions.PrimaryKeyValueAlreadyInTable;
import backend.exceptions.validatorExceptions.UniqueValueAlreadyInTable;
import backend.recordHandling.RecordDeleter;
import backend.recordHandling.RecordInserter;
import backend.service.InsertRowValidator;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@AllArgsConstructor
public class InsertAction implements DatabaseAction {
    @Setter
    private String databaseName;
    @Setter
    private String tableName;
    @Setter
    private ArrayList<ArrayList<String>> values;

    @Override
    public Object actionPerform() throws IOException {
        InsertRowValidator rowValidator = new InsertRowValidator(this.databaseName, this.tableName);

        for (final ArrayList<String> row : values) {
            try {
                rowValidator.validateRow(row);
            } catch (PrimaryKeyValueAlreadyInTable e) {
                log.error("PK already in table!");
                throw new RuntimeException(e);
            } catch (UniqueValueAlreadyInTable e) {
                log.error("Unique field already in table!");
                throw new RuntimeException(e);
            } catch (ForeignKeyValueNotFoundInParentTable e) {
                log.error("Foreign key value not found in parent table!");
                throw new RuntimeException(e);
            }
        }

        RecordInserter recordInserter = new RecordInserter(this.databaseName, this.tableName);

        for (final ArrayList<String> row : values) {
            recordInserter.insert(row);
        }

        return null;
    }
}
