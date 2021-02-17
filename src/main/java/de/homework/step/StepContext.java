package de.homework.step;

import java.util.Optional;

import io.vavr.control.Either;

public class StepContext<T, P> {

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private final Optional<Either<P, T>> output;

    private final int timeTakenNanos;

    private final String functionName;

    public StepContext(String functionName) {
        this.output = Optional.empty();
        this.timeTakenNanos = 0;
        this.functionName = functionName;
    }

    public StepContext(Either<P, T> output, int timeTakenNanos, String functionName) {
        this.output = Optional.of(output);
        this.timeTakenNanos = timeTakenNanos;
        this.functionName = functionName;
    }

    public boolean isExecutedSuccessfully() {
        return output.isPresent() && output.orElseThrow().isRight();
    }
}
