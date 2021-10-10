import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpGetClientTest {

    @Test
    void shouldTestClient() throws IOException {
        HttpGetClient client = new HttpGetClient("www.httpbin.org", 80, "/html");
    }
}