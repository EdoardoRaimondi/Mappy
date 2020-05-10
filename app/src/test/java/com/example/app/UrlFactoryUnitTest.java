package com.example.app;

import com.example.app.factories.UrlFactory;
import org.junit.Test;
import static org.junit.Assert.*;

public class UrlFactoryUnitTest {
    @Test
    public void working() {
        String[] label = {"location", "radius", "type", "sensor", "key"};
        String location = "" + 12.68575 + "," + 15.2589;
        String[] value = {location, Integer.toString(1000), "restaurant", "true", "google_api_key"};
        String url = UrlFactory.getUrl("www.domain.com", label, value);
        assertEquals("www.domain.com?location=12.68575,15.2589&radius=1000&type=restaurant&sensor=true&key=google_api_key", url);
    }
}
