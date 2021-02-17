package de.homework.workflow;

import de.homework.step.Step;
import io.vavr.control.Either;

public class WorkflowExecutor<P, T> {

    private final Workflow<T, P> workflow;

    public WorkflowExecutor(Workflow<T, P> workflow) {
        this.workflow = workflow;
    }

    public Either<P, T> execute(T input) {
        Either<P, T> result = null;

        for (Step<T, P> step : workflow.getSteps()) {
            if (step.isExecutedSuccessfully()) {
                continue;
            }

            if (result != null && result.isLeft()) {
                break;
            }

            result = step.execute(result != null ? result.get() : input);
        }

        // TODO Might return null value
        return result;
    }
}
