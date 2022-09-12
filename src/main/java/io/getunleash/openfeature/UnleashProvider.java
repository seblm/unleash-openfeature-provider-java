package io.getunleash.openfeature;

import dev.openfeature.javasdk.*;
import io.getunleash.Unleash;
import io.getunleash.Variant;

public class UnleashProvider implements FeatureProvider {

    private static final String NAME = "Unleash Provider";
    private final Unleash unleash;

    public UnleashProvider(Unleash unleash) {
        this.unleash = unleash;
    }

    @Override
    public Metadata getMetadata() {
        return () -> NAME;
    }

    @Override
    public ProviderEvaluation<Boolean> getBooleanEvaluation(String key, Boolean defaultValue, EvaluationContext ctx) {
        boolean enabled = unleash.isEnabled(key);
        return ProviderEvaluation.<Boolean>builder().value(enabled).build();
    }

    @Override
    public ProviderEvaluation<String> getStringEvaluation(String key, String defaultValue, EvaluationContext ctx) {
        return ProviderEvaluation.<String>builder().value(unleash.getVariant(key, new Variant(defaultValue, (String) null, false)).getName()).build();
    }

    @Override
    public ProviderEvaluation<Integer> getIntegerEvaluation(String key, Integer defaultValue, EvaluationContext ctx) {
        String variantName = unleash.getVariant(key).getName();
        Integer value;
        try {
            value = Integer.parseInt(variantName);
        } catch (NumberFormatException e) {
            value = defaultValue;
        }
        return ProviderEvaluation.<Integer>builder().value(value).build();
    }

    @Override
    public ProviderEvaluation<Double> getDoubleEvaluation(String key, Double defaultValue, EvaluationContext ctx) {
        String variantName = unleash.getVariant(key).getName();
        Double value;
        try {
            value = Double.parseDouble(variantName);
        } catch (NumberFormatException e) {
            value = defaultValue;
        }
        return ProviderEvaluation.<Double>builder().value(value).build();
    }

    @Override
    public ProviderEvaluation<Structure> getObjectEvaluation(String key, Structure defaultValue, EvaluationContext ctx) {
        throw new RuntimeException("Not implemented - Unleash does not support an 'Object' type. Only String, Number and Boolean");
    }

}
