package elec332.kmaplanner.planner.opta.assignment;

/**
 * Created by Elec332 on 26-8-2019
 */
public enum PersonSortingType {

    NAME {
        @Override
        public IInitialEventAssigner<?> createEventAssigner() {
            return new DefaultEventAssigner();
        }

    },
    GROUP {
        @Override
        public IInitialEventAssigner<?> createEventAssigner() {
            return new GroupEventAssignerV2<>(new DefaultEventAssigner());
        }

    },
    RANDOM {
        @Override
        public IInitialEventAssigner<?> createEventAssigner() {
            return new RandomEventAssigner();
        }

    };

    public abstract IInitialEventAssigner<?> createEventAssigner();

}
