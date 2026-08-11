package com.vividh.incident.agent.diagnosis;

import lombok.Getter;

@Getter
public class RemediationProposal {

    public enum Action {

        RESTART_SERVICE,
        SCALE_UP,
        ROLL_BACK
    }


    private final Action action;
    private final String serviceName;
    private final String reasoning;

    RemediationProposal(Action action, String serviceName, String reasoning) {
        this.action = action;
        this.serviceName = serviceName;
        this.reasoning = reasoning;
    }

    public static RemediationProposal restartService(String serviceName, String reasoning) {
        return new RemediationProposal(Action.RESTART_SERVICE,  serviceName, reasoning);
    }

    public static RemediationProposal scaleUp(String serviceName, String reasoning) {
        return new RemediationProposal(Action.SCALE_UP, serviceName, reasoning);
    }

    public static RemediationProposal rollBack(String serviceName, String reasoning) {
        return new RemediationProposal(Action.ROLL_BACK, serviceName, reasoning);
    }

    public static RemediationProposal of(Action action, String serviceName, String reasoning) {
        return new RemediationProposal(action, serviceName, reasoning);
    }
}
