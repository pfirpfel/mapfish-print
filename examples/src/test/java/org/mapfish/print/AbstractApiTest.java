package org.mapfish.print;

import org.apache.commons.io.IOUtils;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        ExamplesTest.DEFAULT_SPRING_XML,
        ExamplesTest.TEST_SPRING_XML
})
public abstract class AbstractApiTest {

    protected static final String PRINT_SERVER = "http://localhost:8080/print/";

    @Autowired
    protected ClientHttpRequestFactory httpRequestFactory;

    protected ClientHttpRequest getRequest(String path, HttpMethod method) throws IOException,
            URISyntaxException {
        return httpRequestFactory.createRequest(new URI(PRINT_SERVER + path), method);
    }

    protected String getBodyAsText(ClientHttpResponse response) throws IOException {
        return IOUtils.toString(response.getBody(), "UTF-8");
    }

    protected String getPrintSpec(String file) throws IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(file)) {
            assert is != null;
            return IOUtils.toString(is, "UTF-8");
        }
    }

    protected void setPrintSpec(String printSpec, ClientHttpRequest request) throws IOException {
        request.getHeaders().setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        OutputStreamWriter writer = new OutputStreamWriter(request.getBody());
        writer.write(URLEncoder.encode(printSpec, Constants.DEFAULT_ENCODING));
        writer.flush();
    }

    protected MediaType getJsonMediaType() {
        Map<String, String> params = new HashMap<>();
        params.put("charset", "utf-8");
        return new MediaType("application", "json", params);
    }

    protected MediaType getJavaScriptMediaType() {
        Map<String, String> params = new HashMap<>();
        params.put("charset", "utf-8");
        return new MediaType("application", "javascript", params);
    }
}
