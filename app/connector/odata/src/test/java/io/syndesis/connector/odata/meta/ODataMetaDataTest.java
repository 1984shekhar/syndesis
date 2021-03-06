/*
 * Copyright (C) 2016 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.syndesis.connector.odata.meta;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.apache.camel.component.extension.MetaDataExtension;
import org.apache.camel.impl.DefaultCamelContext;
import org.assertj.core.api.Condition;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import io.syndesis.connector.odata.AbstractODataTest;

public class ODataMetaDataTest extends AbstractODataTest {

    @Before
    public void setup() throws Exception {
        context = new DefaultCamelContext();
        context.disableJMX();
        context.start();
    }

    @After
    public void tearDown() throws Exception {
        if (context != null) {
            context.stop();
            context = null;
        }
    }

    @Test
    public void testMetaDataExtensionRetrieval() throws Exception {
        ODataMetaDataExtension extension = new ODataMetaDataExtension(context);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put(SERVICE_URI, defaultTestServer.serviceUrl());

        Optional<MetaDataExtension.MetaData> meta = extension.meta(parameters);
        assertThat(meta).isPresent();

        Object payload = meta.get().getPayload();
        assertThat(payload).isInstanceOf(ODataMetadata.class);
        ODataMetadata odataMetadata = (ODataMetadata) payload;
        assertThat(odataMetadata.getEntityNames().size()).isEqualTo(1);
        assertThat(odataMetadata.getEntityNames().iterator().next()).isEqualTo(defaultTestServer.resourcePath());
    }

    @Test
    @Ignore("Useful for manual testing but cannot guarantee access to odata service")
    public void testMetaDataExtensionOnRealServer() throws Exception {
        ODataMetaDataExtension extension = new ODataMetaDataExtension(context);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put(SERVICE_URI, "https://services.odata.org/TripPinRESTierService/");
        parameters.put(SKIP_CERT_CHECK, true);

        Optional<MetaDataExtension.MetaData> meta = extension.meta(parameters);
        assertThat(meta).isPresent();
        assertThat(meta.get().getPayload()).isInstanceOf(Set.class);
        assertThat(meta.get().getAttributes()).hasEntrySatisfying(MetaDataExtension.MetaData.JAVA_TYPE, new Condition<Object>() {
            @Override
            public boolean matches(Object val) {
                return Objects.equals(String.class, val);
            }
        });
        assertThat(meta.get().getAttributes()).hasEntrySatisfying(MetaDataExtension.MetaData.CONTENT_TYPE, new Condition<Object>() {
            @Override
            public boolean matches(Object val) {
                return Objects.equals("text/plain", val);
            }
        });
    }

}
