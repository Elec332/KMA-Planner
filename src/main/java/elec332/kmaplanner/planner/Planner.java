package elec332.kmaplanner.planner;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import elec332.kmaplanner.group.GroupManager;
import elec332.kmaplanner.io.ProjectSettings;
import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.persons.PersonManager;
import elec332.kmaplanner.planner.ical.ICalPrinter;
import elec332.kmaplanner.planner.opta.Roster;
import elec332.kmaplanner.planner.opta.RosterScoreCalculator;
import elec332.kmaplanner.planner.opta.helpers.AssignmentSorter;
import elec332.kmaplanner.planner.opta.helpers.DifferentPersonChangeFilter;
import elec332.kmaplanner.planner.opta.helpers.DifferentPersonEventSwapFilter;
import elec332.kmaplanner.planner.opta.helpers.PersonSorter;
import elec332.kmaplanner.planner.print.PersonPrinter;
import elec332.kmaplanner.util.FileHelper;
import elec332.kmaplanner.util.ObjectReference;
import elec332.kmaplanner.util.UpdatableTreeSet;
import elec332.kmaplanner.util.ZipHelper;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.event.BestSolutionChangedEvent;
import org.optaplanner.core.api.solver.event.SolverEventListener;
import org.optaplanner.core.config.domain.ScanAnnotatedClassesConfig;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.composite.UnionMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.ChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.SwapMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
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

    public void saveProject() {
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

    public void plan(Component component) {
        plan(component, () -> new Roster(this, Preconditions.checkNotNull(settings.sortingType.createEventAssigner())));
    }

    public void plan(Component component, Supplier<Roster> roster) {
        initialize();
        getPersonManager().getPersons().forEach(Person::clearEvents);
        if (getPersonManager().getPersons().isEmpty() || getEvents().isEmpty()) {
            return;
        }
        plan_(component, roster.get());
    }

    private void configureSolver(SolverFactory<?> factory) {
        factory.getSolverConfig().setTerminationConfig(new TerminationConfig());
        factory.getSolverConfig().getTerminationConfig().setUnimprovedSecondsSpentLimit((long) settings.unimprovedSeconds);

        factory.getSolverConfig().setRandomSeed(settings.seed);
        factory.getSolverConfig().setMoveThreadCount(SolverConfig.MOVE_THREAD_COUNT_AUTO);
        factory.getSolverConfig().setEnvironmentMode(EnvironmentMode.FAST_ASSERT);

        factory.getSolverConfig().setScanAnnotatedClassesConfig(new ScanAnnotatedClassesConfig());
        factory.getSolverConfig().getScanAnnotatedClassesConfig().setPackageIncludeList(Lists.newArrayList("elec332"));  //Fix for Launch4j classloader

        factory.getSolverConfig().setScoreDirectorFactoryConfig(new ScoreDirectorFactoryConfig());
        factory.getSolverConfig().getScoreDirectorFactoryConfig().setEasyScoreCalculatorClass(RosterScoreCalculator.class);

        //A lot of *sigh*'s below, you've been warned

        //ConstructionHeuristicPhaseConfig chpc = new ConstructionHeuristicPhaseConfig();
        LocalSearchPhaseConfig lcpc = new LocalSearchPhaseConfig();
        factory.getSolverConfig().withPhases(lcpc/*, chpc*/);

        UnionMoveSelectorConfig unionMoveSelectorConfig = new UnionMoveSelectorConfig();
        SwapMoveSelectorConfig swapMoveSelectorConfig = new SwapMoveSelectorConfig();
        ChangeMoveSelectorConfig changeMoveSelectorConfig = new ChangeMoveSelectorConfig();
        unionMoveSelectorConfig.setMoveSelectorConfigList(Lists.newArrayList(changeMoveSelectorConfig, swapMoveSelectorConfig));
        lcpc.setMoveSelectorConfig(unionMoveSelectorConfig);

        //finally
        swapMoveSelectorConfig.setFilterClassList(Lists.newArrayList(DifferentPersonEventSwapFilter.class));
        changeMoveSelectorConfig.setFilterClassList(Lists.newArrayList(DifferentPersonChangeFilter.class));

        ValueSelectorConfig valueSelectorConfig = new ValueSelectorConfig();
        changeMoveSelectorConfig.setValueSelectorConfig(valueSelectorConfig);
        valueSelectorConfig.setCacheType(SelectionCacheType.STEP);
        valueSelectorConfig.setSelectionOrder(SelectionOrder.SORTED);
        valueSelectorConfig.setSorterComparatorClass(PersonSorter.class);

        EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig();
        changeMoveSelectorConfig.setEntitySelectorConfig(entitySelectorConfig);
        entitySelectorConfig.setCacheType(SelectionCacheType.STEP);
        entitySelectorConfig.setSelectionOrder(SelectionOrder.SORTED);
        entitySelectorConfig.setSorterComparatorClass(AssignmentSorter.class);
    }

    private void plan_(Component component, Roster roster) {
        SolverFactory<Roster> factory = SolverFactory.createEmpty();
        configureSolver(factory);

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

        while (!(component instanceof Window)) {
            component = component.getParent();
        }

        JDialog frame = new JDialog((Window) component, "Planning...", Dialog.DEFAULT_MODALITY_TYPE);
        frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        ui.setOpaque(true);
        frame.setContentPane(ui);
        frame.pack();
        frame.setLocationRelativeTo(component);
        frame.setVisible(true);

        if (ref.get() == null) {
            solver.terminateEarly();
            writeRoster(solver.getBestSolution());
            return;
        }
        printRoster(ref.get());
    }

    public void printRoster(Roster roster) {
        writeRoster(roster);
        roster.print();
        File tempFolder = new File(FileHelper.getExecFolder(), UUID.randomUUID().toString());
        if (!tempFolder.mkdirs()) {
            throw new RuntimeException(new IOException()); //No, this doesn't exist, look away please....
        }
        for (Person p : roster.getPersons()) {
            try {
                File personFile = new File(tempFolder, p.getLastName() + " - " + p.getFirstName() + ".xlsx");
                Workbook workbook = new XSSFWorkbook();
                PersonPrinter.printRoster(p, workbook);
                FileOutputStream fos = new FileOutputStream(personFile);
                workbook.write(fos);
                fos.close();

                if (p.getPrintableEvents().isEmpty()) {
                    continue;
                }

                File calFile = new File(tempFolder, p.getLastName() + " - " + p.getFirstName() + ".ics");
                CalendarOutputter co = new CalendarOutputter();
                fos = new FileOutputStream(calFile);
                Calendar cal = new Calendar();
                ICalPrinter.fillCalender(cal, p);
                co.output(cal, fos);
                fos.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            File dest = new File(FileHelper.getExecFolder(), "user_progs.zip");
            Files.deleteIfExists(dest.toPath());
            ZipHelper.zip(tempFolder, dest);
            FileHelper.deleteDir(tempFolder);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void writeRoster(Roster roster) {
        File file = new File(FileHelper.getExecFolder(), new Date().getTime() + ".kpa");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            roster.write(fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(new JFrame(), "Failed to save assignment data.", "Export failed!", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class PlannerUI extends JPanel implements SolverEventListener<Roster> {

        private PlannerUI() {
            add(new JLabel("Score: "));
            add(scoreLabel = new JLabel("?????????????????????????????")); //Shhh
        }

        private JLabel scoreLabel;

        @Override
        public void bestSolutionChanged(BestSolutionChangedEvent<Roster> event) {
            scoreLabel.setText(event.getNewBestScore().toString());
        }

    }

}
