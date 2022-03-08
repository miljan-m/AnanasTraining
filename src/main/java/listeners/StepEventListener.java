package listeners;

import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.Argument;
import io.cucumber.plugin.event.EventPublisher;
import io.cucumber.plugin.event.PickleStepTestStep;
import io.cucumber.plugin.event.TestStepStarted;
import java.util.List;
import java.util.stream.Collectors;

public class StepEventListener implements ConcurrentEventListener {
    private static String stepName;
    private static List<String> stepArguments;
    private static String stepCodeLocation;

    @Override
    public void setEventPublisher(EventPublisher eventPublisher) {
        eventPublisher.registerHandlerFor(TestStepStarted.class, event -> {
            if (event.getTestStep() instanceof PickleStepTestStep) {
                PickleStepTestStep step = (PickleStepTestStep) event.getTestStep();
                stepName = step.getStep().getText();
                stepArguments = step.getDefinitionArgument().stream().map(Argument::getValue).collect(Collectors.toList());
                stepCodeLocation = step.getCodeLocation();
            }
        });
    }

    public static String getStepName() {
        return stepName;
    }

    public static List<String> getStepArguments() {
        return stepArguments;
    }

    public static String getStepCodeLocation() {
        return stepCodeLocation;
    }
}
