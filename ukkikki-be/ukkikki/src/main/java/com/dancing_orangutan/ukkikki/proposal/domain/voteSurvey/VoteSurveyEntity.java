package com.dancing_orangutan.ukkikki.proposal.domain.voteSurvey;

import com.dancing_orangutan.ukkikki.travelPlan.domain.travelPlan.TravelPlanEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vote_surveys")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class VoteSurveyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer voteSurveyId;

    @Column(nullable = false, name = "survey_start_time")
    private LocalDateTime surveyStartTime;

    @Column(nullable = false, name = "survey_end_time")
    private LocalDateTime surveyEndTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_plan_id")
    private TravelPlanEntity travelPlan;

    @Builder
    public VoteSurveyEntity(Integer voteSurveyId, LocalDateTime surveyStartTime, LocalDateTime surveyEndTime, TravelPlanEntity travelPlan) {
        this.voteSurveyId = voteSurveyId;
        this.surveyStartTime = surveyStartTime;
        this.surveyEndTime = surveyEndTime;
        this.travelPlan = travelPlan;
    }
}
