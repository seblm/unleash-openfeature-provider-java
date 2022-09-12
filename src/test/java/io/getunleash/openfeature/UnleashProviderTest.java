package io.getunleash.openfeature;

import dev.openfeature.javasdk.*;
import io.getunleash.FakeUnleash;
import io.getunleash.Variant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


public class UnleashProviderTest {

    private static Client client;
    private static UnleashProvider provider;
    private static FakeUnleash unleash;
    private static OpenFeatureAPI api;

    @BeforeAll
    static void createOpenFeatureApiAndUleash() {
        unleash = new FakeUnleash();
        provider = new UnleashProvider(unleash);
        api = OpenFeatureAPI.getInstance();
        api.setProvider(provider);
        client = api.getClient();
    }

    @AfterEach
    void resetOpenFeatureAPIAndUnleash() {
        api.clearHooks();
        unleash.resetAll();
    }

    @Test
    void mustDefineMetadataName() {
        assertEquals("Unleash Provider", provider.getMetadata().getName());
    }

    @Test
    void mustResolveBooleanValue() {
        unleash.enable("new-welcome-message");

        assertTrue(client.getBooleanValue("new-welcome-message", false));
    }

    @Test
    void mustResolveStringValue() {
        unleash.enable("fib-algo");
        unleash.setVariant("fib-algo", new Variant("recursive", (String) null, true));

        assertEquals("recursive", client.getStringValue("fib-algo", "default"));
    }

    @Test
    void mustResolveIntegerValue() {
        unleash.enable("fib-value");
        unleash.setVariant("fib-value", new Variant("8", (String) null, true));

        assertEquals(8, client.getIntegerValue("fib-value", 0));
    }

    @Test
    void mustResolveDoubleValue() {
        unleash.enable("fib-value");
        unleash.setVariant("fib-value", new Variant("8.0", (String) null, true));

        assertEquals(8.0, client.getDoubleValue("fib-value", 0.0));
    }

    @Test
    void cantResolveObjectValue() {
        final Exception[] errors = {null};
        client.addHooks(new Hook<Structure>() {
            @Override
            public void error(HookContext<Structure> ctx, Exception error, Map<String, Object> hints) {
                errors[0] = error;
            }
        });
        Structure defaultValue = new Structure(Map.of("test", new Value(true)));
        Structure result = client.getObjectValue("object", defaultValue);

        assertEquals(result, defaultValue);
        assertNotNull(errors[0]);
        assertInstanceOf(RuntimeException.class, errors[0]);
    }

}
