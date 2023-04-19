package backend.parser;

import java.util.*;

import backend.databaseActions.DatabaseAction;
import backend.databaseActions.createActions.*;
import backend.databaseActions.dropActions.*;
import backend.databaseActions.miscActions.*;
import backend.databaseModels.*;
import backend.exceptions.InvalidSQLCommand;
import backend.exceptions.SQLParseException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.iterators.PeekingIterator;

@Slf4j
public class Parser {
    public Parser() {}

    private static String[] RESERVED_KEYWORDS = {
        "create", "drop", "database", "table",
        "select", "from", "where", "group", "order", "by",
        "update", "set",
        "insert", "into", "values",
        "delete", "from",
        "(", ")", ",",
        "!=", "=", ">=", "<=", ">", "<",
        "foreign", "primary", "key", "unique", "references"
    };
    private static String[] ATTRIBUTE_TYPES = {
        "int", "float", "bit", "date", "datetime", "char"
    };

    private static Map<String, Integer> typeSizes = new HashMap<>();
    static {
        //Map<String, Integer> typeSizes = new HashMap<>();
        typeSizes.put("int", Integer.BYTES);
        typeSizes.put("float", Float.BYTES);
        typeSizes.put("bit", 1);
        //typeSizes.put("date", );
        //typeSizes.put("datetime", );
        //typeSizes.put("char", );
        typeSizes = Collections.unmodifiableMap(typeSizes);
    }

    /**
     * @param token
     * @return boolean if given token is keyword or not
     */
    private boolean isKeyword(String token) {
        return Arrays.asList(RESERVED_KEYWORDS).contains(token);
    }
    /**
     * @param token
     * @return boolean if given token is comprised of alphanumeric characters only
     */
    private boolean isAlphaNumeric(String token) {
        return token != null && token.matches("^[a-zA-Z0-9_]*$");
    }

    /**
     * @param token
     * @return boolean if given token is a valid field type
     */
    private boolean isValidFieldType(String token) {
        return Arrays.asList(ATTRIBUTE_TYPES).contains(token);
    } 

    /**
     * @param token
     * @return checks if token is valid name for database/table/column
     * @throws SQLParseException
     */
    private enum NAME_TYPE {
        DATABASE("database"), TABLE("table"), COLUMN("column");
        private final String value;
        private NAME_TYPE(String value) {
            this.value = value;
        }
        public String getValue() {
            return value;
        }
    }
    private boolean checkName(String token, NAME_TYPE nt) throws SQLParseException {
        if (isKeyword(token)) {
            throw(new SQLParseException("Invalid name for " + nt.getValue() + ": " + token + " - Reserved keyword"));
        }
        if (!isAlphaNumeric(token)) {
            throw(new SQLParseException("Invalid name for " + nt.getValue() + ": " + token + " - Name can only contain alphanumeric characters"));
        }
        return true;
    } 

    // TODO: remove function parameter `databaseName` and replace it with field referencing ServerController
    /**
     * @param input SQL string
     * @return database action corresponding to string
     * @throws InvalidSQLCommand
     */
    public DatabaseAction parseInput(String input, String databaseName) throws SQLParseException {
        List<String> tokens = tokenize(input);
        PeekingIterator<String> it = new PeekingIterator<>(tokens.iterator());

        try {
            String firstWord = it.next();

            if (firstWord.equals(("use"))) {
                return parseUseDatabase(tokens, it);
            }
            else {
                String secondWord = it.next();

                if (firstWord.equals("create")) {
                    if (secondWord.equals("database")) {
                        return parseCreateDatabase(tokens, it);
                    }
                    if (secondWord.equals("table")) {
                        return parseCreateTable(tokens, databaseName, it);
                    }
                }
                if (firstWord.equals("drop")) {
                    if (secondWord.equals("database")) {
                        return parseDropDatabase(tokens, it);
                    }
                    if (secondWord.equals("table")) {
                        return parseDropTable(tokens, databaseName, it);
                    }
                }
                if (firstWord.equals("insert") && secondWord.equals("into")) {
                    return parseInsertInto(tokens, databaseName, it);
                }
                if (firstWord.equals("delete") && secondWord.equals("from")) {
                    return parseDeleteFrom(tokens, databaseName, it);
                }
            }
        }
        catch (NoSuchElementException e) {
            throw(new SQLParseException("Missing token after `" + tokens.get(tokens.size() - 1) + "`"));
        }

        throw (new SQLParseException("Invalid SQL command"));
    }

    /**
     * @param tokens
     * @return
     * @throws SQLParseException
     */
    private UseDatabaseAction parseUseDatabase(List<String> tokens, PeekingIterator<String> it) throws SQLParseException{
        if (!it.hasNext()) {
            throw(new SQLParseException("Missing token for database name"));
        }
            
        String databaseName = it.next();
        
        if (it.hasNext()) {
            throw(new SQLParseException("Too many tokens after database name: `" + databaseName + "`"));
        }

        checkName(databaseName, NAME_TYPE.DATABASE);
        
        DatabaseModel databaseModel = new DatabaseModel(databaseName, new ArrayList<>());
        UseDatabaseAction uda = new UseDatabaseAction(databaseModel);

        return uda;
    }

    /**
     * @param tokens
     * @return
     * @throws SQLParseException
     */
    private CreateDatabaseAction parseCreateDatabase(List<String> tokens, PeekingIterator<String> it) throws SQLParseException {
        if (!it.hasNext()) {
            throw(new SQLParseException("Missing token for database name"));
        }

        String databaseName = it.next();

        if (it.hasNext()) {
            throw(new SQLParseException("Too many tokens after database name: `" + databaseName + "`"));
        }

        checkName(databaseName, NAME_TYPE.DATABASE);
        
        DatabaseModel databaseModel = new DatabaseModel(databaseName, new ArrayList<>());
        CreateDatabaseAction cda = new CreateDatabaseAction(databaseModel);

        return cda;
    }

    private enum CreateTableStates {
        GET_TABLE_NAME, GET_FIELD_NAME_TYPE, GET_FIELD_CONSTRAINTS, COMMA, CLOSING_BRACKET
    }
    private CreateTableAction parseCreateTable(List<String> tokens, String databaseName, PeekingIterator<String> it) throws SQLParseException {
        // Used for constructing multiple AttributeModels
        String fieldName = "";
        String fieldType = "";
        int fieldLength = 0;

        // used for constructing TableModel
        String                      tableName           = null;
        String                      fileName            = "";
        int                         rowLength           = 0;
        ArrayList<FieldModel>       attributes          = new ArrayList<FieldModel>();
        PrimaryKeyModel             primaryKey          = null;
        ArrayList<ForeignKeyModel>  foreignKeys         = new ArrayList<ForeignKeyModel>();
        ArrayList<String>           uniqueAttributes    = new ArrayList<String>();
        ArrayList<IndexFileModel>   indexFiles          = new ArrayList<IndexFileModel>();

        // used for costructing PrimaryKeyModel
        ArrayList<String>           primaryKeyAttributes = new ArrayList<String>();

        CreateTableStates state = CreateTableStates.GET_TABLE_NAME;
        try {
            while (it.hasNext()) {
                switch (state) {
                case GET_TABLE_NAME:
                    // check if table name is valid
                    if (checkName(it.peek(), NAME_TYPE.TABLE)) {
                        tableName = it.next();
                    }

                    // if no more tokens after table name we can exit (table is specified without any fields, constraints)
                    if (!it.hasNext()) {
                        break;
                    }
                    // if there are more tokens but next one isn't a '(', invalid instruction
                    else if (!it.next().equals("(")) {
                        throw(new SQLParseException("Expected '(' after table name"));
                    }
                    // else skip the '(' and start reading fields
                    else {
                        state = CreateTableStates.GET_FIELD_NAME_TYPE;
                    }
                    break;

                case GET_FIELD_NAME_TYPE:
                    // read field name and type - mandatory
                    fieldName = it.next();
                    fieldType = it.next();

                    checkName(fieldName, NAME_TYPE.COLUMN);

                    if (!isValidFieldType(fieldType)) {
                        throw(new SQLParseException("Invalid field type: \"" + fieldType + "\""));
                    }
                    // char field type needs to have following structure: (  num  )
                    if (fieldType.equals("char")) {
                        if (!it.next().equals("(")) {
                            throw(new SQLParseException("Expected length of char attribute in form -> (len)"));
                        }
                        
                        String len = it.next();
                        try {
                            fieldLength = Integer.parseInt(len);
                        } catch (NumberFormatException e) {
                            throw(new SQLParseException("Invalid length of char: " + len));
                        }

                        if (!it.next().equals(")")) {
                            throw(new SQLParseException("Expected length of char attribute in parentheses -> (len)"));
                        }
                    }
                    else {
                        fieldLength = typeSizes.get(fieldType);
                    }
                    rowLength += fieldLength;

                    if (it.peek().equals(")")) {
                        //log.info("Finished reading all fields");
                        state = CreateTableStates.CLOSING_BRACKET;
                    }
                    else if (it.peek().equals(",")) {
                        //log.info("Finished reading field");
                        state = CreateTableStates.COMMA;
                    }
                    else {
                        state = CreateTableStates.GET_FIELD_CONSTRAINTS;
                    }
                    break;
                    
                case GET_FIELD_CONSTRAINTS:
                    if (it.peek().equals("unique")) {
                        uniqueAttributes.add(fieldName);
                        it.next();
                        break;
                    }
                    else if (it.peek().equals("primary")) {
                        it.next();

                        String token = it.next();
                        if (it.next().equals("key")) {
                            primaryKeyAttributes.add(fieldName);
                        }
                        else {
                            throw(new SQLParseException("Unknown constraint foreign " + token));
                        }
                        break;
                    }
                    else if (it.peek().equals("foreign")) {
                        it.next();

                        String tokenFirst = it.next();
                        String tokenSecond = it.next();
                        if (!tokenFirst.equals("key") || !tokenSecond.equals("references")) {
                            throw(new SQLParseException("Unknown constraint, expected (foreign key references), instead of " + tokenFirst + " " + tokenSecond));
                        }

                        String foreignTable = it.next();
                        String openingBracket = it.next();
                        String foreignField = it.next();
                        String closingBracket = it.next();
                        if (!openingBracket.equals("(") || !closingBracket.equals(")")) {
                            throw(new SQLParseException("Expected following structure for foreign key constraint -> foreign key references table(field)"));
                        }

                        String finalFieldName = fieldName; //TODO
                        var fkm = new ForeignKeyModel(foreignTable, new ArrayList<String>(Collections.singletonList(foreignField)), new ArrayList<>(){{
                            add(finalFieldName);
                        }});
                        foreignKeys.add(fkm);
                        break;
                    }
                    else if (it.peek().equals(",")) {
                        state = CreateTableStates.COMMA;
                        break;
                    }
                    else if (it.peek().equals(")")) {
                        state = CreateTableStates.CLOSING_BRACKET;
                        break;
                    }
                    else {
                        throw new SQLParseException("Undefined constraint: " + it.peek());
                    }                    
                    //break;

                case COMMA, CLOSING_BRACKET:
                    attributes.add(new FieldModel(fieldName, fieldType, fieldLength, false, false));
                    it.next();

                    fieldName = "";
                    fieldType = "";
                    fieldLength = 0;

                    if (state == CreateTableStates.CLOSING_BRACKET) {
                        if (it.hasNext()) {
                            throw new SQLParseException("Expected end of input after closing bracket");
                        }

                        break;
                    }
                    else if (state == CreateTableStates.COMMA) {
                        state = CreateTableStates.GET_FIELD_NAME_TYPE;
                    }
                    break;

                default:
                    break;
                }
            }
        } 
        catch (NoSuchElementException e) {
            throw (new SQLParseException("Unexpected end of string"));
        }
        
        fileName = databaseName + "." + tableName + ".bin";
        primaryKey = new PrimaryKeyModel(primaryKeyAttributes);
        TableModel tableModel = new TableModel(tableName, fileName, rowLength, attributes, primaryKey, foreignKeys, uniqueAttributes, indexFiles);
        CreateTableAction cta = new CreateTableAction(tableModel, databaseName);
        return cta;
    }

    private DropDatabaseAction parseDropDatabase(List<String> tokens, PeekingIterator<String> it) throws SQLParseException {
        if (!it.hasNext()) {
            throw(new SQLParseException("Missing token for database name"));
        }

        String databaseName = it.next();

        if (it.hasNext()) {
            throw(new SQLParseException("Too many tokens after database name: `" + databaseName + "`"));
        }

        checkName(databaseName, NAME_TYPE.DATABASE);

        DatabaseModel databaseModel = new DatabaseModel(databaseName, new ArrayList<>());
        DropDatabaseAction dda = new DropDatabaseAction(databaseModel);

        return dda;
    }    

    private DropTableAction parseDropTable(List<String> tokens, String databaseName, PeekingIterator<String> it) throws SQLParseException {
        if (!it.hasNext()) {
            throw(new SQLParseException("Missing token for table name"));
        }

        String tableName = it.next();

        if (it.hasNext()) {
            throw(new SQLParseException("Too many tokens after table name: `" + tableName + "`"));
        }

        checkName(tableName, NAME_TYPE.TABLE);

        DropTableAction dta = new DropTableAction(tableName, databaseName);

        return dta;
    }  

    private enum InsertIntoStates {
        GET_TABLE_NAME, GET_FIELD_NAMES, GET_VALUES, GET_VALUES_STRINGS, CLOSING_BRACKET
    }
    // TODO: create DatabaseAction for insert into
    private InsertAction parseInsertInto(List<String> tokens, String databaseName, PeekingIterator<String> it) throws SQLParseException {
        String tableName = "";
        ArrayList<String> fieldNames = new ArrayList<>();
        ArrayList<ArrayList<String>> values = new ArrayList<>();

        ArrayList<String> currentValues = null;

        InsertIntoStates state = InsertIntoStates.GET_TABLE_NAME;
        try {
            while (it.hasNext()) {
                switch (state) {
                    case GET_TABLE_NAME: {
                        if (!it.hasNext()) {
                            throw(new SQLParseException("Missing token for table name"));
                        }
                
                        if (checkName(it.peek(), NAME_TYPE.TABLE)) {
                            tableName = it.next();
                        }
                        
                        if (!it.hasNext() || !it.next().equals("(")) {
                            throw new SQLParseException("Expected a list of attribute names after table name. e.g.: table(field1, field2, ...)");
                        }

                        state = InsertIntoStates.GET_FIELD_NAMES;
                    }
                    case GET_FIELD_NAMES: {
                        String fieldName = it.next();

                        checkName(fieldName, NAME_TYPE.COLUMN);

                        fieldNames.add(fieldName);

                        // if closing bracket, start reading insert values 
                        if (it.peek().equals(")")) {
                            state = InsertIntoStates.CLOSING_BRACKET;
                            break;
                        }
                        // if comma, continue reading another field name
                        else if (it.next().equals(",")) {
                            break;
                        } 
                        else {
                            throw new SQLParseException("Expected comma and another field name, or parenthesis after \"" + fieldName + "\"");
                        }
                    }
                    case GET_VALUES: {
                        // `values` keyword only needed before first values list
                        if (values.isEmpty()) {
                            if (!it.peek().equals("values")) {
                                throw new SQLParseException("Expected values(value1, value2, ...)");
                            }
                            // pop `values`
                            it.next();
                        }

                        if (!it.next().equals("(")) {
                            throw new SQLParseException("Expected parenthesis after keyword `values`");
                        }

                        currentValues = new ArrayList<>();
                        state = InsertIntoStates.GET_VALUES_STRINGS;

                        break;
                    }
                    case GET_VALUES_STRINGS: {
                        String value = it.next();

                        currentValues.add(value);

                        String nextToken = it.peek();
                        if (it.peek().equals(")")) {
                            state = InsertIntoStates.CLOSING_BRACKET;
                            break;
                        }
                        else if (it.next().equals(",")) {
                            break;
                        }
                        else {
                            throw new SQLParseException("Expected comma and another value, or parenthesis after \"" + value + "\"");
                        }
                    }
                    case CLOSING_BRACKET: {
                        // pop bracket
                        it.next();

                        if (currentValues != null) {
                            values.add(currentValues);
                            currentValues = null;
                        }

                        if (it.peek().equals(",")) {
                            if (values.size() == 0) {
                                throw new SQLParseException("No comma expected between list of names and VALUES()");
                            }
                            // pop comma
                            it.next();
                        }

                        if (it.hasNext()) {
                            state = InsertIntoStates.GET_VALUES;
                        }

                        break;
                    }
                    default: {
                        break;
                    }
                }
            }
        } catch (NoSuchElementException e) {
            throw new SQLParseException("Unexpected end of command");
        }
        
        log.info(tableName);
        log.info(fieldNames.toString());
        log.info(values.toString());

        return new InsertAction(tableName, databaseName, values);
    }

    private DeleteAction parseDeleteFrom(List<String> tokens, String databaseName, PeekingIterator<String> it) throws SQLParseException {
        String tableName = "";
        ArrayList<String> keys = new ArrayList<>();

        if (!it.hasNext()) {
            throw(new SQLParseException("Missing token for table name"));
        }

        tableName = it.next();

        while (it.hasNext()) {
            keys.add(it.next());
        }

        log.info(tableName);
        log.info(keys.toString());

        DeleteAction da = new DeleteAction(tableName, databaseName, keys);
        return da;
    }

    /**
     * Converts string to lowercase
     * @param input SQL string
     * @return List of tokens
     */
    private List<String> tokenize(String input) {
        String inputLwr = input.toLowerCase(); 
        
        // tokens with some empty lines and extra whitespaces
        String[] tokensDirty = inputLwr.split("\\s*(?=[(),])|(?<=[(),])|\\s");

        List<String> tokens = new ArrayList<>(List.of(tokensDirty));

        // remove empty strings and delete whitespaces from words (maybe update regex?)
        for (int i=0; i< tokens.size(); i++) {
            if (tokens.get(i).isBlank()) {
                tokens.remove(i);
                i--;
            }
            else {
                tokens.set(i, tokens.get(i).trim());
            }
        }

        return tokens;
    }
}