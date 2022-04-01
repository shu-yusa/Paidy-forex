import adapter.InMemoryExchangeRateCache;
import domain.ApiConfig;
import domain.ExchangeRateService;
import adapter.OneFrameApi;
import com.sun.net.httpserver.HttpServer;
import handler.HttpHandler;

import java.io.InputStream;
import java.util.concurrent.Executors;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Properties;

public class ApiServer {
    public static void main(String... args) throws IOException {
        int serverPort;
        int cacheSize;
        int stalePeriod;
        ApiConfig apiConfig;
        try (InputStream input = ApiServer.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            serverPort = Integer.parseInt(prop.getProperty("server.port"));
            apiConfig = new ApiConfig(prop.getProperty("service.host"), prop.getProperty("service.token"));
            cacheSize = Integer.parseInt(prop.getProperty("server.cache_size"));
            stalePeriod = Integer.parseInt(prop.getProperty("server.stale_period"));
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }

        HttpHandler handler = new HttpHandler(
                new ExchangeRateService(
                        new OneFrameApi(apiConfig, new InMemoryExchangeRateCache(cacheSize), stalePeriod)));

        HttpServer server = HttpServer.create(new InetSocketAddress(serverPort), 0);
        server.setExecutor(Executors.newCachedThreadPool());
        server.createContext("/", handler::handle);
        server.start();
        System.out.printf("start listening on port %s%n", serverPort);
    }
}
