import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import handler.HttpHandler;

import java.util.concurrent.Executors;

import java.io.IOException;
import java.net.InetSocketAddress;

public class ApiServer {
    ExchangeRateApi apiClient;
    public ApiServer(ExchangeRateApi apiClient) {
        this.apiClient = apiClient;
    }

    public static void main(String... args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(80), 0);
        server.setExecutor(Executors.newCachedThreadPool());
        HttpHandler handler = new HttpHandler();
        server.createContext("/", handler::handle);
        server.start();
        System.out.println("start listening on port 80");
    }
}
