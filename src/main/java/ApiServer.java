import adapter.InMemoryExchangeRateCache;
import domain.ApiConfig;
import domain.CachingExchangeRateService;
import domain.ExchangeRateApiService;
import adapter.OneFrameApi;
import com.sun.net.httpserver.HttpServer;
import handler.HttpHandler;

import java.io.InputStream;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Properties;

public class ApiServer {
    public static void main(String... args) throws IOException {
        int serverPort;
        int stalePeriod;
        ApiConfig apiConfig;
        try (InputStream input = ApiServer.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            serverPort = Integer.parseInt(prop.getProperty("server.port"));
            apiConfig = new ApiConfig(prop.getProperty("service.host"), prop.getProperty("service.token"));
            stalePeriod = Integer.parseInt(prop.getProperty("server.stale_period"));
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
        ResourceBundle bundle = ResourceBundle.getBundle("messages");

        HttpHandler handler = new HttpHandler(
                new CachingExchangeRateService(
                        new ExchangeRateApiService(new OneFrameApi(apiConfig)),
                        new InMemoryExchangeRateCache(),
                        stalePeriod),
                bundle);

        HttpServer server = HttpServer.create(new InetSocketAddress(serverPort), 0);
        server.setExecutor(Executors.newCachedThreadPool());
        server.createContext("/rate", handler::handle);
        server.start();
        System.out.printf("start listening on port %s%n", serverPort);
    }
}
