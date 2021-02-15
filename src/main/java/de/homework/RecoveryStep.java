package de.homework;

import java.util.function.Function;

import io.vavr.control.Either;

public class RecoveryStep<P, T> {

    private final Function<P, Either<P, T>> action;

    public RecoveryStep(Function<P, Either<P, T>> action) {
        this.action = action;
    }

    public Function<P, Either<P, T>> getAction() {
        return action;
    }

    public Either<P, T> run(P p) {
        return action.apply(p);
    }
}
