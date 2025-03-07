package com.dancing_orangutan.ukkikki.travelPlan.ui.facade.internal.mapper;


import com.dancing_orangutan.ukkikki.proposal.domain.voteSurvey.VoteSurveyEntity;
import com.dancing_orangutan.ukkikki.travelPlan.domain.keyword.KeywordEntity;
import com.dancing_orangutan.ukkikki.travelPlan.domain.travelPlan.TravelPlanEntity;
import com.dancing_orangutan.ukkikki.travelPlan.ui.facade.dto.response.FetchAvailableTravelPlansResponse;
import com.dancing_orangutan.ukkikki.travelPlan.ui.facade.dto.response.FetchKeywordsResponse;
import com.dancing_orangutan.ukkikki.travelPlan.ui.facade.dto.response.FetchTravelPlanDetailsByMemberResponse;
import com.dancing_orangutan.ukkikki.travelPlan.ui.facade.dto.response.FetchTravelPlanDetailsByCompanyResponse;
import com.dancing_orangutan.ukkikki.travelPlan.ui.facade.dto.response.CreateTravelPlanResponse;
import com.dancing_orangutan.ukkikki.travelPlan.ui.facade.dto.response.FetchSuggestedTravelPlansResponse;
import com.dancing_orangutan.ukkikki.travelPlan.ui.facade.dto.response.JoinTravelPlanResponse;
import com.dancing_orangutan.ukkikki.travelPlan.ui.facade.dto.response.SearchTravelPlanResponse;
import com.dancing_orangutan.ukkikki.travelPlan.ui.facade.dto.response.*;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class TravelPlanResponseMapper {

	public CreateTravelPlanResponse createTravelPlanResponse(final TravelPlanEntity entity) {
		return CreateTravelPlanResponse.fromEntity(entity);
	}

	public JoinTravelPlanResponse joinTravelPlanResponse(final TravelPlanEntity entity,
			Integer memberId) {
		return JoinTravelPlanResponse.fromEntity(entity, memberId);
	}

	public SearchTravelPlanResponse searchTravelPlanResponse(
			final List<TravelPlanEntity> entities, final Integer memberId) {
		return SearchTravelPlanResponse.fromEntities(entities, memberId);
	}

	public SearchMyTravelPlanResponse searchMyTravelPlanResponse(
			final List<TravelPlanEntity> entities, final Integer memberId) {
		return SearchMyTravelPlanResponse.fromEntities(entities, memberId);
	}

	public FetchSuggestedTravelPlansResponse fetchSuggestedTravelPlansResponse(
			final List<TravelPlanEntity> entities) {
		return FetchSuggestedTravelPlansResponse.fromEntities(entities);
	}

	public FetchAvailableTravelPlansResponse fetchAvailableTravelPlansResponse(
			final Page<TravelPlanEntity> page, Integer memberId) {
		return FetchAvailableTravelPlansResponse.fromEntities(page,memberId);
	}

	public FetchAllTravelPlansResponse fetchAllTravelPlansResponse(
			final Page<TravelPlanEntity> page) {
		return FetchAllTravelPlansResponse.fromEntities(page);
	}

	public FetchTravelPlanDetailsByCompanyResponse fetchTravelPlanDetailsResponse(
			final TravelPlanEntity entity) {
		return FetchTravelPlanDetailsByCompanyResponse.fromEntity(entity);
	}

	public FetchTravelPlanDetailsByMemberResponse fetchTravelPlanDetailsByMemberResponse(
			final TravelPlanEntity entity, final Integer memberId, boolean canVote,
			final VoteSurveyEntity voteSurveyEntity) {
		return FetchTravelPlanDetailsByMemberResponse.fromEntity(entity, memberId, canVote,
				voteSurveyEntity);
	}

	public FetchKeywordsResponse fetchKeywordsResponse(final List<KeywordEntity> entities) {
		return FetchKeywordsResponse.fromEntities(entities);
	}
}