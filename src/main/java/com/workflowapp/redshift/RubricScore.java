// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.workflowapp.redshift;

import java.util.HashMap;
import java.util.Map;

public class RubricScore {
    private String decisionGuide;
    private String gettingStarted;
    private String popularActions;
    private String essentialUseCase;

    private Map<String, Integer> rubricScore;

    public RubricScore() {
        rubricScore = new HashMap<>();
        initializeCategoryWeights();
    }

    // Constructor to set all fields
    public RubricScore(String decisionGuide, String gettingStarted, String popularActions, String essentialUseCase) {
        this.decisionGuide = decisionGuide;
        this.gettingStarted = gettingStarted;
        this.popularActions = popularActions;
        this.essentialUseCase = essentialUseCase;
        rubricScore = new HashMap<>();
        initializeCategoryWeights();
    }

    private void initializeCategoryWeights() {
        // Initialize category weights
        rubricScore.put("Decision Guide", 3);
        rubricScore.put("Getting Started", 3);
        rubricScore.put("Popular Actions", 2);
        rubricScore.put("Essential Use Case", 2);
    }

    public String getDecisionGuide() {
        return decisionGuide;
    }

    public void setDecisionGuide(String redshiftData) {
        this.decisionGuide = redshiftData;
    }

    public String getGettingStarted() {
        return gettingStarted;
    }

    public void setGettingStarted(String gettingStarted) {
        this.gettingStarted = gettingStarted;
    }

    public String getPopularActions() {
        return popularActions;
    }

    public void setPopularActions(String popularActions) {
        this.popularActions = popularActions;
    }

    public String getEssentialUseCase() {
        return essentialUseCase;
    }

    public void setEssentialUseCase(String essentialUseCase) {
        this.essentialUseCase = essentialUseCase;
    }

    public int calculateTotalScore() {
        int totalScore = 0;

        if (this.getDecisionGuide().compareTo("Yes") == 0)
            totalScore += 3;

        if (this.getGettingStarted().compareTo("Yes") == 0)
            totalScore += 3;

        if (this.getEssentialUseCase().compareTo("Yes") == 0)
            totalScore += 2;

        if (this.getPopularActions().compareTo("Yes") == 0)
            totalScore += 2;

        return totalScore;
    }
}
