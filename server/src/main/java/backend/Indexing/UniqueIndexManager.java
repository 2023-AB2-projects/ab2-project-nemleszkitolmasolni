package backend.Indexing;

import backend.exceptions.recordHandlingExceptions.KeyAlreadyInTreeException;
import backend.exceptions.recordHandlingExceptions.KeyNotFoundException;
import backend.exceptions.recordHandlingExceptions.UndefinedQueryException;
import backend.recordHandling.RecordReader;
import backend.recordHandling.TypeConverter;
import backend.service.CatalogManager;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;

@Slf4j
public class UniqueIndexManager implements Queryable{
    private final ArrayList<String> keyStructure;
    private final BPlusTree bPlusTree;

    private final String databaseName, tableName;

    public UniqueIndexManager(String databaseName, String tableName, String indexName) {
        this.databaseName = databaseName;
        this.tableName = tableName;

        // Index field types and file name
        keyStructure = (ArrayList<String>) CatalogManager.getIndexFieldTypes(databaseName, tableName, indexName);
        String filename = CatalogManager.getTableIndexFilePath(databaseName, tableName, indexName);

        try {
            bPlusTree = new BPlusTree(keyStructure, filename);
        } catch (IOException e) {
            log.error("Could not load B+ tree, databaseName=" + databaseName + ", tableName=" + tableName + ", indexName=" + indexName);
            throw new RuntimeException(e);
        }
    }

    public boolean isPresent(ArrayList<String> values){
        Key key = TypeConverter.toKey(keyStructure, values);
        try{
            bPlusTree.find(key);
            return true;
        } catch (KeyNotFoundException | IOException e) {
            return false;
        }
    }

    public Integer findLocation(ArrayList<String> values) throws IOException, KeyNotFoundException {
        Key key = TypeConverter.toKey(keyStructure, values);
        return bPlusTree.find(key);
    }

    public HashMap<Integer, Object> equalityQuery(Object key) throws UndefinedQueryException, IOException {
        return rangeQuery(key, key, true, true);
    }

    public HashMap<Integer, Object> rangeQuery(Object lowerBound, Object upperBound, boolean allowEqualityLower, boolean allowEqualityUpper) throws UndefinedQueryException, IOException {
        if(keyStructure.size() != 1){
            throw new UndefinedQueryException();
        }
        ArrayList<Object> lowerObjectList = new ArrayList<>(), upperObjectList = new ArrayList<>();
        lowerObjectList.add(lowerBound);
        upperObjectList.add(upperBound);
        Key lowerKey = new Key(lowerObjectList, keyStructure);
        Key upperKey = new Key(upperObjectList, keyStructure);

        int lowerCompareValue, upperCompareValue;
        if(allowEqualityLower){
            lowerCompareValue = 1;
        } else {
            lowerCompareValue = 0;
        }

        if(allowEqualityUpper){
            upperCompareValue = 1;
        } else {
            upperCompareValue = 0;
        }

        return bPlusTree.rangeQuery(lowerKey, upperKey, lowerCompareValue, upperCompareValue);
    }

    public HashMap<Integer, Object> lesserQuery(Object upperBound, boolean allowEquality) throws UndefinedQueryException, IOException {
        if(keyStructure.size() != 1){
            throw new UndefinedQueryException();
        }

        ArrayList<Object> upperObjectList = new ArrayList<>();
        upperObjectList.add(upperBound);
        Key upperKey = new Key(upperObjectList, keyStructure);

        int upperCompareValue;
        if(allowEquality){
            upperCompareValue = 1;
        } else {
            upperCompareValue = 0;
        }

        return bPlusTree.rangeQuery(TypeConverter.smallestKey(keyStructure), upperKey, 1, upperCompareValue);
    }

    public HashMap<Integer, Object> greaterQuery(Object lowerBound, boolean allowEquality) throws UndefinedQueryException, IOException {
        if(keyStructure.size() != 1){
            throw new UndefinedQueryException();
        }

        ArrayList<Object> lowerObjectList = new ArrayList<>();
        lowerObjectList.add(lowerBound);
        Key lowerKey = new Key(lowerObjectList, keyStructure);

        int lowerCompareValue;
        if(allowEquality){
            lowerCompareValue = 1;
        } else {
            lowerCompareValue = 0;
        }

        return bPlusTree.rangeQuery(lowerKey, lowerKey, lowerCompareValue, 2);
    }

    public void insert(ArrayList<String> values, Integer pointer) throws IOException, KeyAlreadyInTreeException {
        Key key = TypeConverter.toKey(keyStructure, values);
        bPlusTree.insert(key, pointer);
    }

    public void delete(ArrayList<String> values) throws IOException {
        Key key = TypeConverter.toKey(keyStructure, values);
        try {
            bPlusTree.delete(key);
        }catch (KeyNotFoundException ignored){}
    }

    @Override
    public void close() throws IOException {
        bPlusTree.close();
    }

    public static void createEmptyIndex(String databaseName, String tableName, String indexName) throws IOException {
         List<String> keyStruct = CatalogManager.getIndexFieldTypes(databaseName, tableName, indexName);
         String filename = CatalogManager.getTableIndexFilePath(databaseName, tableName, indexName);

         BPlusTree emptyTree = new BPlusTree((ArrayList<String>) keyStruct, filename);
         emptyTree.createEmptyTree();
    }

    public static void createIndex(String databaseName, String tableName, String indexName) throws IOException {
        ArrayList<String> keyStruct = (ArrayList<String>) CatalogManager.getIndexFieldTypes(databaseName, tableName, indexName);
        ArrayList<String> keyColumnNames = (ArrayList<String>) CatalogManager.getIndexFieldNames(databaseName, tableName, indexName);

        String filename = CatalogManager.getTableIndexFilePath(databaseName, tableName, indexName);

        BPlusTree tree = new BPlusTree(keyStruct, filename);
        tree.createEmptyTree();

        RecordReader reader = new RecordReader(databaseName, tableName);

        ArrayList<ArrayList<Object>> table = reader.scan(keyColumnNames);
        ArrayList<Integer> pointers = new ArrayList<>(reader.getAllPointers());
        for(int i = 0; i < table.size(); i++){
            try{
                tree.insert(new Key(table.get(i), keyStruct), pointers.get(i));
            }catch (KeyAlreadyInTreeException ignored){}
        }

        reader.close();
        tree.close();
    }

}
