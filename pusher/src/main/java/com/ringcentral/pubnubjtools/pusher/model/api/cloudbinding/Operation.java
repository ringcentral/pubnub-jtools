package com.ringcentral.pubnubjtools.pusher.model.api.cloudbinding;

public enum Operation {
    ADD("add"),
    REMOVE("remove");

    final String command;

    Operation(String command) {
        this.command = command;
    }

    @Override
    public String toString() {
        return command;
    }
}


