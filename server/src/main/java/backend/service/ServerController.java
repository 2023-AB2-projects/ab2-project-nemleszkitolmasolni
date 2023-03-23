package backend.service;

import backend.config.Config;
import backend.databaseactions.DatabaseAction;
import backend.databaseactions.createactions.CreateDatabaseAction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.w3c.dom.Node;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ServerController {
    //sql command set from
    @Getter
    @Setter
    private String sqlCommand;

    public ServerController() {
        log.info("Server Started!");

        init();
    }

    private void init() {
        accessCatalog();

    }

    private void accessCatalog() {
        File catalog = Config.getCatalogFile();
        try {
            if (catalog.createNewFile()) {
                log.info("Catalog.json Created Succesfully!");
                initCatalog(catalog);
            } else {
                log.info("Catalog.json Already Exists!");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initCatalog(File catalog) throws IOException {
        String jsonCreate = "{\"Databases\":[{\"Database\":{\"databaseName\":\"master\"}}]}";
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

        Object jsonObject = mapper.readValue(jsonCreate, Object.class);
        mapper.writeValue(catalog, jsonObject);

    }


}