import org.junit.Before;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ApiServerTest {
    OneFrameApi apiClient;
    ApiServer apiServer;

    @Before
    public void setUp() {
        ApiConfig config = new ApiConfig("http://localhost:8080", "10dc303535874aeccc86a8251e6992f5");
        this.apiClient = new OneFrameApi(config);
        this.apiServer = new ApiServer(this.apiClient);
    }
}