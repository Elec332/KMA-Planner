package elec332.kmaplanner.planner.opta.solver;

import elec332.kmaplanner.project.ProjectSettings;
import org.optaplanner.core.api.solver.SolverFactory;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 30-8-2019
 */
public class SolverConfigurator {

    public static void configureSolver(SolverFactory<?> factory, ProjectSettings settings, IPhaseConfiguration<?>... phases) {
        factory.getSolverConfig().setPhaseConfigList(Arrays.stream(phases)
                .filter(Objects::nonNull)
                .map(c -> c.createPhase(settings))
                .collect(Collectors.toList())
        );
    }

}
