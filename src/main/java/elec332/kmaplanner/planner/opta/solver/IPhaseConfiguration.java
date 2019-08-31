package elec332.kmaplanner.planner.opta.solver;

import elec332.kmaplanner.io.ProjectSettings;
import org.optaplanner.core.config.phase.PhaseConfig;

/**
 * Created by Elec332 on 30-8-2019
 */
public interface IPhaseConfiguration<C extends PhaseConfig<? extends C>> {

    C createPhase(ProjectSettings settings);

}
