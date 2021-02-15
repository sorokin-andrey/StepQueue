package de.homework;

import java.util.Objects;
import java.util.Optional;

import io.vavr.control.Either;

public class StepContext<T, P> {

    private final Step<T, P> input;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private final Optional<Either<P, T>> output;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private final Optional<Either<P, T>> recoveryOutput;

    private final int timeTakenNanos;

    String functionName;

    public StepContext(Step<T, P> input) {
        this.input = input;
        this.output = Optional.empty();
        this.recoveryOutput = Optional.empty();
        this.timeTakenNanos = 0;
        this.functionName = input.getAction().getClass().getSimpleName();
    }

    public StepContext(Step<T, P> input, Either<P, T> output, int timeTakenNanos) {
        this.input = input;
        this.output = Optional.of(output);
        this.recoveryOutput = Optional.empty();
        this.timeTakenNanos = timeTakenNanos;
        this.functionName = input.getAction().getClass().getSimpleName();
    }

    public StepContext(Step<T, P> input, Either<P, T> output, Either<P, T> recoveryOutput, int timeTakenNanos) {
        this.input = input;
        this.output = Optional.of(output);
        this.recoveryOutput = Optional.of(recoveryOutput);
        this.timeTakenNanos = timeTakenNanos;
        this.functionName = input.getAction().getClass().getSimpleName();
    }

    public Step<T, P> getInput() {
        return input;
    }

    public Optional<Either<P, T>> getOutput() {
        return output;
    }

    public int getTimeTakenNanos() {
        return timeTakenNanos;
    }

    public String getFunctionName() {
        return functionName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StepContext<?, ?> that = (StepContext<?, ?>) o;
        return timeTakenNanos == that.timeTakenNanos && Objects.equals(input, that.input) && Objects.equals(output, that.output) && Objects.equals(recoveryOutput, that.recoveryOutput) && Objects.equals(functionName, that.functionName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(input, output, recoveryOutput, timeTakenNanos, functionName);
    }
}
