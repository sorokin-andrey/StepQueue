package de.homework.workflow;

import de.homework.step.Step;
import io.vavr.collection.List;

public class Workflow<T, P> {

    private final List<Step<T, P>> steps;

    Workflow(List<Step<T, P>> steps) {
        this.steps = steps;
    }

    public List<Step<T, P>> getSteps() {
        return steps;
    }
}
