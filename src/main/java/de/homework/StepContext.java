package de.homework;

import io.vavr.control.Either;

import java.util.Objects;

public class StepContext<T, P> {

    private final Step<T, P> in;

    private final Either<P, T> out;

    private final int timeTakenNanos;

    String functionName;

    public StepContext(Step<T, P> in, Either<P, T> out, int timeTakenNanos) {
        this.in = in;
        this.out = out;
        this.timeTakenNanos = timeTakenNanos;
        this.functionName = in.getAction().getClass().getSimpleName();
    }

    public Step<T, P> getIn() {
        return in;
    }

    public Either<P, T> getOut() {
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
