package com.AutomationExercise.drivers;

public enum Browser {
    CHROME {
        @Override
        public AbstractDriver getBrowserFactory() {
            return new ChromeFactory();
        }
    },
    FIREFOX {
        @Override
        public AbstractDriver getBrowserFactory() {
            return new FirefoxFactory();
        }
    },
    EDGE {
        @Override
        public AbstractDriver getBrowserFactory() {
            return new EdgeFactory();
        }
    };
    public abstract AbstractDriver getBrowserFactory();
}
