package elec332.kmaplanner.planner;

import com.google.common.base.Preconditions;
import elec332.kmaplanner.group.GroupManager;
import elec332.kmaplanner.io.ProjectSettings;
import elec332.kmaplanner.persons.PersonManager;
import elec332.kmaplanner.planner.opta.Roster;
import elec332.kmaplanner.util.IOHelper;
import elec332.kmaplanner.util.ObjectReference;
import elec332.kmaplanner.util.UpdatableTreeSet;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.event.BestSolutionChangedEvent;
import org.optaplanner.core.api.solver.event.SolverEventListener;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 14-6-2019
 */
public class Planner {

    public Planner(PersonManager personManager, GroupManager groupManager, UpdatableTreeSet<Event> events, ProjectSettings settings, Runnable save) {
        this.personManager = personManager;
        this.groupManager = groupManager;
        this.events = events;
        this.settings = settings;
        this.save = save;
    }

    private final PersonManager personManager;
    private final GroupManager groupManager;
    private final UpdatableTreeSet<Event> events;
    private final ProjectSettings settings;
    private final Runnable save;

    public void initialize() {
        //Maybe..
    }

    public ProjectSettings getSettings() {
        return settings;
    }

    public PersonManager getPersonManager() {
        return personManager;
    }

    public GroupManager getGroupManager() {
        return groupManager;
    }

    public UpdatableTreeSet<Event> getEvents() {
        return events;
    }

    public void saveProject(){
        save.run();
    }

    @SuppressWarnings("unused")
    public Date getFirstDate() {
        if (getEvents().isEmpty()) {
            return new Date();
        }
        return (Date) getEvents().first().start.clone();
    }

    public Date getLastDate() {
        if (getEvents().isEmpty()) {
            return new Date();
        }
        return (Date) getEvents().last().end.clone();
    }

    public void plan(Component component){
        plan(component, () -> new Roster(this, Preconditions.checkNotNull(settings.sortingType.createEventAssigner())));
    }

    public void plan(Component component, Supplier<Roster> roster) {
        initialize();
        getPersonManager().getPersons().forEach(p -> p.events.clear());
        if (getPersonManager().getPersons().isEmpty() || getEvents().isEmpty()) {
            return;
        }
        plan_(component, roster.get());
    }

    private void plan_(Component component, Roster roster) {
        SolverFactory<Roster> factory = SolverFactory.createFromXmlResource("config.xml");
        factory.getSolverConfig().getTerminationConfig().setUnimprovedSecondsSpentLimit((long) settings.unimprovedSeconds);
        factory.getSolverConfig().setRandomSeed(settings.seed);
        Solver<Roster> solver = factory.buildSolver();
        solver.addEventListener(event -> System.out.println(event.getNewBestScore()));

        ObjectReference<Roster> ref = new ObjectReference<>();
        PlannerUI ui = new PlannerUI();
        solver.addEventListener(ui);
        new Thread(() -> {
            ref.accept(solver.solve(roster));
            Optional.ofNullable(SwingUtilities.getWindowAncestor(ui))
                    .ifPresent(Window::dispose);
        }).start();

        while (!(component instanceof Window)){
            component = component.getParent();
        }

        JDialog frame = new JDialog((Window) component, "Planning...", Dialog.DEFAULT_MODALITY_TYPE);
        frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        ui.setOpaque(true);
        frame.setContentPane(ui);
        frame.pack();
        frame.setLocationRelativeTo(component);
        frame.setVisible(true);

        if (ref.get() == null){
            solver.terminateEarly();
            writeRoster(solver.getBestSolution());
            return;
        }
        writeRoster(ref.get());
        ref.get().print();
    }

    private void writeRoster(Roster roster){
        File file = new File(IOHelper.getExecFolder(), new Date().getTime() + ".kpa");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            roster.write(fos);
            fos.close();
        } catch (IOException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(new JFrame(), "Failed to save assignment data.", "Export failed!", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class PlannerUI extends JPanel implements SolverEventListener<Roster> {

        private PlannerUI(){
            add(new JLabel("Score: "));
            add(scoreLabel = new JLabel("?????????????????????????????"));
        }

        private JLabel scoreLabel;

        @Override
        public void bestSolutionChanged(BestSolutionChangedEvent<Roster> event) {
            scoreLabel.setText(event.getNewBestScore().toString());
        }

    }

}
