package com.github.pubnubjtools.pusher.model;


public interface Describable {

    default String getDescription(VerboseLevel verboseLevel) {
        switch (verboseLevel) {
            case TRACE: return getTraceDescription();
            case DEBUG: return getDebugDescription();
            case INFO: return getInfoDescription();
            default: throw new IllegalArgumentException("Unknown level " + verboseLevel);
        }
    }

    String getTraceDescription();

    String getDebugDescription();

    String getInfoDescription();

}
