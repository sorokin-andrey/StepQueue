package de.homework;

import java.util.Objects;
import java.util.Optional;

import io.vavr.control.Either;

public class StepContext<T, P> {

    private final Step<T, P> in;

    private final Optional<Either<P, T>> out;

    private final int timeTakenNanos;

    String functionName;

    public StepContext(Step<T, P> in) {
        this.in = in;
        this.out = Optional.empty();
        this.timeTakenNanos = 0;
        this.functionName = in.getAction().getClass().getSimpleName();
    }

    public StepContext(Step<T, P> in, Either<P, T> out, int timeTakenNanos) {
        this.in = in;
        this.out = Optional.of(out);
        this.timeTakenNanos = timeTakenNanos;
        this.functionName = in.getAction().getClass().getSimpleName();
    }

    public Step<T, P> getIn() {
        return in;
    }

    public Optional<Either<P, T>> getOut() {
        return out;
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
        return timeTakenNanos == that.timeTakenNanos && Objects.equals(in, that.in) && Objects.equals(out, that.out) && Objects.equals(functionName, that.functionName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(in, out, timeTakenNanos, functionName);
    }

    @Override
    public String toString() {
        return "StepContext{" +
                "in=" + in +
                ", out=" + out +
                ", timeTakenNanos=" + timeTakenNanos +
                ", functionName='" + functionName + '\'' +
                '}';
    }
}
