package websocket;

import java.io.File;
import java.net.URI;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
 
import org.glassfish.tyrus.server.Server;
 
public class WebSocketChatServer {
 
    public static void main(String[] args) {
        runServer();
    }
    public static final String BASE_URI = "http://0.0.0.0:9080/rest";//wanna auf mi horchen soi dann auf 0.0.0.1
    
    public static org.glassfish.grizzly.http.server.HttpServer startServer() {
        // Im Package "service" alle Klassen durchsuchen, um REST Services zu finden
        final ResourceConfig rc = new ResourceConfig().packages("service","filter","WebUntis","entity");
        // Server starten
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }
 
    public static void runServer() {
        org.glassfish.tyrus.server.Server server = new Server();
        final org.glassfish.grizzly.http.server.HttpServer restServer = startServer();
        server = new Server("0.0.0.0",
                                   9090,
                                   "/websockets",
                                   DoorSignEndpoint.class);
        try {
            String documentRoot = new File(".").getAbsolutePath();
            restServer.getServerConfiguration().addHttpHandler(new StaticHttpHandler(documentRoot));
            server.start();
            restServer.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } 
    }
}
