package elec332.kmaplanner.planner;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import elec332.kmaplanner.Main;
import elec332.kmaplanner.events.Event;
import elec332.kmaplanner.events.EventManager;
import elec332.kmaplanner.group.Group;
import elec332.kmaplanner.group.GroupManager;
import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.persons.PersonManager;
import elec332.kmaplanner.planner.opta.Roster;
import elec332.kmaplanner.planner.opta.RosterIO;
import elec332.kmaplanner.planner.opta.RosterScoreCalculator;
import elec332.kmaplanner.planner.opta.solver.*;
import elec332.kmaplanner.planner.opta.util.IAbstractPhaseLifecycleListener;
import elec332.kmaplanner.project.KMAPlannerProject;
import elec332.kmaplanner.project.PlannerSettings;
import elec332.kmaplanner.util.FileHelper;
import elec332.kmaplanner.util.ObjectReference;
import elec332.kmaplanner.util.WeakCallbackHandler;
import elec332.kmaplanner.util.swing.DialogHelper;
import elec332.kmaplanner.util.swing.FileChooserHelper;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.event.BestSolutionChangedEvent;
import org.optaplanner.core.api.solver.event.SolverEventListener;
import org.optaplanner.core.config.domain.ScanAnnotatedClassesConfig;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.solver.AbstractSolver;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 14-6-2019
 */
public class Planner {

    public Planner(KMAPlannerProject project) {
        this(project.getPersonManager(), project.getGroupManager(), project.getEventManager(), project.getPlannerSettings(), project.getUuid(), project::saveIfPossible);
    }

    private Planner(PersonManager personManager, GroupManager groupManager, EventManager events, PlannerSettings settings, UUID projectUuid, Runnable saveIfPossible) {
        this.personManager = personManager;
        this.groupManager = groupManager;
        this.events = events;
        this.settings = settings;
        this.projectUuid = projectUuid;
        this.save = saveIfPossible;
        this.callbacks = new WeakCallbackHandler();
    }

    private final PersonManager personManager;
    private final GroupManager groupManager;
    private final EventManager events;
    private final PlannerSettings settings;
    private final UUID projectUuid;
    private final Runnable save;
    private final WeakCallbackHandler callbacks;
    private Roster roster;

    public void initialize() {
        //Maybe..
    }

    public PlannerSettings getSettings() {
        return settings;
    }

    public PersonManager getPersonManager() {
        return personManager;
    }

    public GroupManager getGroupManager() {
        return groupManager;
    }

    public EventManager getEventManager() {
        return events;
    }

    public UUID getProjectUuid() {
        return projectUuid;
    }

    public void addRosterCallback(Object ref, Runnable callback) {
        callbacks.addCallback(ref, callback);
    }

    public Roster getRoster() {
        return roster;
    }

    public void setRoster(Roster roster) {
        this.roster = roster;
        callbacks.runCallbacks();
    }

    public void openRosterFromUI() {
        save.run();
        setRoster(null);
        File file = FileChooserHelper.openFileChooser(null, new FileNameExtensionFilter("KMAPlanner Assignments file (*.kpa)", "kpa"), "Open");
        if (file != null) {
            Roster roster;
            try {
                roster = RosterIO.readRoster(file, this);
                setRoster(roster);
            } catch (Exception e) {
                e.printStackTrace();
                DialogHelper.showErrorMessageDialog("Failed to import assignments from file: " + file.getAbsolutePath(), "Import failed!");
            }
        }
    }

    public void save(Component component) {
        writeRoster(roster, component);
    }

    public void plan(Component component) {
        if (getRoster() == null) {
            setRoster(new Roster(this, Preconditions.checkNotNull(settings.sortingType.createEventAssigner())));
        }
        initialize();
        getPersonManager().forEach(Person::clearEvents);
        if (getPersonManager().getObjects().isEmpty() || getEventManager().getObjects().isEmpty()) {
            return;
        }
        long time = System.currentTimeMillis();
        plan_(component, getRoster(), new Solver1(),
                new Solver2A(),
                new Solver2B(),
                new Solver2C(),
                new Solver3Pre(),
                new Solver2D(false),
                new Solver2D(true),
                new Solver3(),
                new Solver3());
        System.out.println("TIME: " + ((System.currentTimeMillis() - time) / 1000f) / 60f);
    }

    private void configureSolver(SolverFactory<Roster> factory, BiConsumer<SolverFactory<Roster>, PlannerSettings> cfg) {
        //factory.getSolverConfig().setTerminationConfig(new TerminationConfig());
        //factory.getSolverConfig().getTerminationConfig().setUnimprovedSecondsSpentLimit((long) settings.unimprovedSeconds);

        factory.getSolverConfig().setRandomSeed(settings.seed);
        factory.getSolverConfig().setMoveThreadCount(SolverConfig.MOVE_THREAD_COUNT_AUTO);
        factory.getSolverConfig().setEnvironmentMode(EnvironmentMode.FAST_ASSERT);

        factory.getSolverConfig().setScanAnnotatedClassesConfig(new ScanAnnotatedClassesConfig());
        factory.getSolverConfig().getScanAnnotatedClassesConfig().setPackageIncludeList(Lists.newArrayList("elec332"));  //Fix for Launch4j classloader

        factory.getSolverConfig().setScoreDirectorFactoryConfig(new ScoreDirectorFactoryConfig());
        factory.getSolverConfig().getScoreDirectorFactoryConfig().setEasyScoreCalculatorClass(RosterScoreCalculator.class);

        cfg.accept(factory, getSettings());

        //A lot of *sigh*'s in here, you've been warned


    }

    private void plan_(Component component, Roster roster, ISolverConfiguration... solvers) {
        if (solvers == null || solvers.length < 1) {
            throw new IllegalArgumentException();
        }
        System.out.println(roster.getPersons().size());
        System.out.println(" ==== ");
        for (Event evt : roster.getPlanner().getEventManager().getObjects()) {
            int i = 0;
            for (Person p : roster.getPersons()) {
                if (p == PersonManager.NULL_PERSON) {
                    continue;
                }
                if (evt.canPersonParticipate(p) && p.canParticipateIn(evt)) {
                    i++;
                }
            }
            System.out.println(i);
            System.out.println(evt);
            System.out.println(" ====== --- ===== ");
        }
        for (Group g : getGroupManager()) {
            System.out.println(" -_----___-");
            System.out.println(g);
            for (Iterator<Person> it = g.getPersonIterator(); it.hasNext(); ) {
                Person p = it.next();

                System.out.println(p);
            }
        }
        ObjectReference<Roster> ref = new ObjectReference<>();
        ObjectReference<Solver<Roster>> solver = new ObjectReference<>();
        ObjectReference<Boolean> exitThread = new ObjectReference<>(false);
        PlannerUI ui = new PlannerUI();

        List<Long> times = Lists.newArrayList();
        times.add(System.currentTimeMillis());

        new Thread(() -> {
            final ObjectReference<Roster> r = new ObjectReference<>(roster);
            for (ISolverConfiguration phaz : solvers) {
                if (exitThread.get()) {
                    return;
                }
                SolverFactory<Roster> factory = SolverFactory.createEmpty();
                configureSolver(factory, ((factory1, settings1) -> phaz.configureSolver(factory1, r.get(), settings1)));

                AbstractSolver<Roster> aSolver = (AbstractSolver<Roster>) factory.buildSolver();
                solver.set(aSolver);
                addSolverEventListeners(aSolver, ui);
                if (exitThread.get()) {
                    return;
                }
                phaz.preSolve(r.get(), getSettings());
                if (exitThread.get()) {
                    return;
                }
                r.use(aSolver::solve);
                if (exitThread.get()) {
                    return;
                }
                phaz.postSolve(r.get(), getSettings());
                times.add(System.currentTimeMillis());
            }
            if (exitThread.get()) {
                return;
            }
            ref.set(r.get());
            Optional.ofNullable(SwingUtilities.getWindowAncestor(ui))
                    .ifPresent(Window::dispose);
        }).start();
        Main.preventComputerSleepHack();

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
            exitThread.set(true);
            solver.get().terminateEarly();
            setRoster(solver.get().getBestSolution());
            writeRoster(solver.get().getBestSolution(), component);
            return;
        }

        Roster rosterP = ref.get();
        setRoster(rosterP);
        writeRoster(rosterP, component);
        Main.stopKeepAlive();
        RosterPrinter.printRoster(rosterP, SwingUtilities.getWindowAncestor(component), RosterPrinter::printAll);

        if (times.size() < 2) {
            return;
        }
        int phaz = 1;
        for (int i = 1; i < times.size(); i++) {
            System.out.println("Phase" + phaz + " time: " + ((times.get(i) - times.get(i - 1)) / 1000f) / 60f);
        }
    }

    private static void addSolverEventListeners(AbstractSolver<Roster> solver, PlannerUI ui) {
        ui.nextSolver();
        solver.addEventListener(event -> {
            Roster roster1 = event.getNewBestSolution();
            System.out.println("------------------");
            RosterScoreCalculator.calculateScore(event.getNewBestSolution(), true);
            System.out.println(event.getNewBestSolution().getAveragePersonTimeSoft(false));
            System.out.println(event.getNewBestSolution().getAveragePersonTimeSoft(true));
            System.out.println(event.getNewBestSolution().getAveragePersonTimeReal());
            Set<Group> gr = event.getNewBestSolution().getPlanner().getGroupManager().getMainGroups().stream()
                    .filter(g -> g.getPersonIterator().hasNext()).collect(Collectors.toSet());
            System.out.println(gr.stream()
                    .mapToLong(g -> g.getAverageSoftTime(roster1))
                    .sum() / gr.size()
            );
            event.getNewBestSolution().getPlanner().getGroupManager().getMainGroups().stream()
                    .filter(g -> g.getPersonIterator().hasNext())
                    .sorted(Comparator.comparingLong(g -> g.getAverageSoftTime(roster1)))
                    .forEach(g -> System.out.print(g + " " + g.getAverageSoftTime(roster1) + " | "));
            System.out.println();
            System.out.println(event.getNewBestScore());
            System.out.println("------------------");
        });
        solver.addEventListener(ui);
        solver.addPhaseLifecycleListener(ui);
    }


    @SuppressWarnings("UnusedReturnValue")
    private File writeRoster(Roster roster, Component component) {
        File file = new File(FileHelper.getExecFolder(), new Date().getTime() + ".kpa");
        try {
            RosterIO.writeRoster(roster, file);
            DialogHelper.showDialog(component, "Saved roster data location: " + file.getAbsolutePath(), "Roster saved");
        } catch (IOException e) {
            e.printStackTrace();
            DialogHelper.showErrorMessageDialog("Failed to save assignment data.", "Export failed!");
            return null;
        }
        return file;
    }

    private static class PlannerUI extends JPanel implements SolverEventListener<Roster>, IAbstractPhaseLifecycleListener<Roster> {

        private PlannerUI() {
            super(new GridLayout(3, 1));
            JPanel ph = new JPanel();
            ph.add(new JLabel("Solver: "));
            ph.add(solverLabel = new JLabel(solver + "  "));
            ph.add(new JLabel("   ")); //Spacer
            ph.add(new JLabel("Phase: "));
            ph.add(phaseLabel = new JLabel(phase + "  "));
            ph.add(new JLabel("   ")); //Spacer
            ph.add(new JLabel("Step: "));
            ph.add(stepLabel = new JLabel(step + "  "));

            JPanel sc = new JPanel();
            sc.add(new JLabel("Score: "));
            sc.add(scoreLabel = new JLabel("???????????????????????????????????????")); //Shhh

            add(ph);
            add(new JPanel());
            add(sc);
        }

        private int phase = 1, step, solver;
        private JLabel scoreLabel, phaseLabel, stepLabel, solverLabel;

        private void nextSolver() {
            solver++;
            solverLabel.setText(solver + "");
            phase = 1;
            step = 0;
            setPhaseAndStep();
        }

        @Override
        public void bestSolutionChanged(BestSolutionChangedEvent<Roster> event) {
            scoreLabel.setText(event.getNewBestScore().toString());
        }

        @Override
        public void stepEnded(AbstractStepScope<Roster> stepScope) {
            step++;
            stepLabel.setText("" + step);
        }

        @Override
        public void phaseEnded(AbstractPhaseScope<Roster> phaseScope) {
            phase++;
            step = 0;
            setPhaseAndStep();
        }

        private void setPhaseAndStep() {
            phaseLabel.setText("" + phase);
            stepLabel.setText("" + step);
        }

    }

}
