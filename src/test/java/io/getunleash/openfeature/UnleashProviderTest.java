package io.getunleash.openfeature;

import dev.openfeature.javasdk.Client;
import dev.openfeature.javasdk.OpenFeatureAPI;
import io.getunleash.FakeUnleash;
import io.getunleash.Variant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class UnleashProviderTest {

    private static Client client;
    private static UnleashProvider provider;
    private static FakeUnleash unleash;

    @BeforeAll
    static void createOpenFeatureClient() {
        unleash = new FakeUnleash();
        provider = new UnleashProvider(unleash);
        OpenFeatureAPI api = OpenFeatureAPI.getInstance();
        api.setProvider(provider);
        client = api.getClient();
    }

    @AfterEach
    void resetUnleash() {
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
    void cantResolveObjectValue() {
        assertThrows(RuntimeException.class, () -> client.getObjectValue("object", null));
    }

}
