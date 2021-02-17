package de.homework.step.executor;

import java.util.function.Function;

import io.vavr.control.Either;

public interface StepExecutor<T, P> {

    Either<P, T> execute(Function<T, Either<P, T>> action, Function<P, Either<P, T>> recoveryAction, T input);
}
