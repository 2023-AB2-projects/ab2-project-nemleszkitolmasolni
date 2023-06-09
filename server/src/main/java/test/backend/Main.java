package test.backend;


import backend.service.ServerController;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class Main {
    public static void main(String[] args) throws IOException {
        log.info("Server Started!");
        ServerController serverController = new ServerController();
        serverController.start(4444);
    }
}
