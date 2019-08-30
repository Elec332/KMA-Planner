package elec332.kmaplanner.util;

import elec332.kmaplanner.planner.opta.assignment.DefaultEventAssigner;
import elec332.kmaplanner.planner.opta.assignment.GroupEventAssigner;
import elec332.kmaplanner.planner.opta.assignment.IInitialEventAssigner;
import elec332.kmaplanner.planner.opta.assignment.RandomEventAssigner;

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
            return new GroupEventAssigner<>(new DefaultEventAssigner());
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
