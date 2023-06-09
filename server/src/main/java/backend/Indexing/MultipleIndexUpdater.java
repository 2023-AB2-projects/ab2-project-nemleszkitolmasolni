package backend.Indexing;

import backend.databaseModels.IndexFileModel;
import backend.service.CatalogManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MultipleIndexUpdater {
    // List of field names, PK, FK and unique
    private final List<String> tableFieldNames;
    private final List<List<String>> uniqueIndexFieldNames, nonUniqueIndexFieldNames;

    // Primary key has one index manager, each FK and unique fields have one manager
    private final ArrayList<UniqueIndexManager> uniqueIndexManagers;

    private final ArrayList<NonUniqueIndexManager> nonUniqueIndexManagers;

    public MultipleIndexUpdater(String databaseName, String tableName) {
        // Get table field names
        this.tableFieldNames = CatalogManager.getFieldNames(databaseName, tableName);

        // All the unique indexes
        List<IndexFileModel> uniqueIndexes = CatalogManager.getUniqueIndexes(databaseName, tableName);
        ArrayList<String> uniqueIndexNames = uniqueIndexes.stream().map(IndexFileModel::getIndexName).collect(Collectors.toCollection(ArrayList::new));
        uniqueIndexFieldNames = uniqueIndexes.stream().map(IndexFileModel::getIndexFields).collect(Collectors.toCollection(ArrayList::new));
        uniqueIndexManagers = new ArrayList<>();
        for (final String indexName : uniqueIndexNames) {
            this.uniqueIndexManagers.add(new UniqueIndexManager(databaseName, tableName, indexName));
        }

        // All non-unique indexes
        List<IndexFileModel> nonUniqueIndexes = CatalogManager.getNonUniqueIndexes(databaseName, tableName);
        ArrayList<String> nonUniqueIndexNames = nonUniqueIndexes.stream().map(IndexFileModel::getIndexName).collect(Collectors.toCollection(ArrayList::new));
        nonUniqueIndexFieldNames = nonUniqueIndexes.stream().map(IndexFileModel::getIndexFields).collect(Collectors.toCollection(ArrayList::new));
        nonUniqueIndexManagers = new ArrayList<>();
        for (final String indexName : nonUniqueIndexNames) {
            this.nonUniqueIndexManagers.add(new NonUniqueIndexManager(databaseName, tableName, indexName));
        }
    }

    public void insert(ArrayList<String> row, Integer pointer) {
        ArrayList<ArrayList<String>> uniqueValues = new ArrayList<>();
        for (var uniqueFieldNames : uniqueIndexFieldNames) {
            ArrayList<String> uniqueValue =new ArrayList<>();
            for (var uniqueFieldName : uniqueFieldNames) {
                uniqueValue.add(row.get(tableFieldNames.indexOf(uniqueFieldName)));
            }
            uniqueValues.add(uniqueValue);
        }

        ArrayList<ArrayList<String>> nonUniqueValues = new ArrayList<>();
        for (var nonUniqueFieldNames : nonUniqueIndexFieldNames) {
            ArrayList<String> nonUniqueValue =new ArrayList<>();
            for (var nonUniqueFieldName : nonUniqueFieldNames) {
                nonUniqueValue.add(row.get(tableFieldNames.indexOf(nonUniqueFieldName)));
            }
            nonUniqueValues.add(nonUniqueValue);
        }

        try {
            for(int i = 0; i < uniqueIndexManagers.size(); i++) {
                uniqueIndexManagers.get(i).insert(uniqueValues.get(i), pointer);
            }
            for(int i = 0; i < nonUniqueIndexManagers.size(); i++) {
                nonUniqueIndexManagers.get(i).insert(nonUniqueValues.get(i), pointer);
            }
        }catch (Exception e){
            System.out.println("Something went wrong with insert");
            System.out.println(e.getMessage());
        }
    }

    public void delete(ArrayList<String> row, Integer pointer){
        ArrayList<ArrayList<String>> uniqueValues = new ArrayList<>();
        for (var uniqueFieldNames : uniqueIndexFieldNames) {
            ArrayList<String> uniqueValue =new ArrayList<>();
            for (var uniqueFieldName : uniqueFieldNames) {
                uniqueValue.add(row.get(tableFieldNames.indexOf(uniqueFieldName)));
            }
            uniqueValues.add(uniqueValue);
        }

        ArrayList<ArrayList<String>> nonUniqueValues = new ArrayList<>();
        for (var nonUniqueFieldNames : nonUniqueIndexFieldNames) {
            ArrayList<String> nonUniqueValue =new ArrayList<>();
            for (var nonUniqueFieldName : nonUniqueFieldNames) {
                nonUniqueValue.add(row.get(tableFieldNames.indexOf(nonUniqueFieldName)));
            }
            nonUniqueValues.add(nonUniqueValue);
        }

        try {
            for(int i = 0; i < uniqueIndexManagers.size(); i++) {
                uniqueIndexManagers.get(i).delete(uniqueValues.get(i));
            }
            for(int i = 0; i < nonUniqueIndexManagers.size(); i++) {
                nonUniqueIndexManagers.get(i).delete(nonUniqueValues.get(i), pointer);
            }
        }catch (Exception e){
            System.out.println("Something went wrong with delete");
            System.out.println(e.getMessage());
        }
    }

    public void close() throws IOException {
        for(var manager : uniqueIndexManagers){
            manager.close();
        }
        for(var manager : nonUniqueIndexManagers){
            manager.close();
        }
    }
}
