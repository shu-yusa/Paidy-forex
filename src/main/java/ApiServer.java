import domain.ApiConfig;
import domain.ExchangeRateService;
import adapter.OneFrameApi;
import com.sun.net.httpserver.HttpServer;
import handler.HttpHandler;

import java.util.concurrent.Executors;

import java.io.IOException;
import java.net.InetSocketAddress;

public class ApiServer {
    public static void main(String... args) throws IOException {
        HttpHandler handler = new HttpHandler(
            new ExchangeRateService(
                new OneFrameApi(
                    new ApiConfig(
                        "http://localhost:8080", "10dc303535874aeccc86a8251e6992f5"))));

        HttpServer server = HttpServer.create(new InetSocketAddress(80), 0);
        server.setExecutor(Executors.newCachedThreadPool());
        server.createContext("/", handler::handle);
        server.start();
        System.out.println("start listening on port 80");
    }
}
