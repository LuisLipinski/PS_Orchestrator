package com.mypetadmin.ps_orchestrator.exception;

public class OnboardingDependencyException extends RuntimeException {

    private final String step;
    private final int upstreamStatus;

    public OnboardingDependencyException(String step, int upstreamStatus) {
        super("Falha na etapa " + step + " do onboarding.");
        this.step = step;
        this.upstreamStatus = upstreamStatus;
    }

    public String getStep() {
        return step;
    }

    public int getUpstreamStatus() {
        return upstreamStatus;
    }
}
