package com.dancing_orangutan.ukkikki.travelPlan.application.impl;


import com.dancing_orangutan.ukkikki.global.error.ApiException;
import com.dancing_orangutan.ukkikki.global.error.ErrorCode;
import com.dancing_orangutan.ukkikki.proposal.domain.voteSurvey.VoteSurveyEntity;
import com.dancing_orangutan.ukkikki.proposal.infrastructure.voteSurvey.JpaVoteSurveyRepository;
import com.dancing_orangutan.ukkikki.travelPlan.application.QueryTravelPlanService;
import com.dancing_orangutan.ukkikki.travelPlan.application.query.FetchAllTravelPlansQuery;
import com.dancing_orangutan.ukkikki.travelPlan.application.query.FetchAvailableTravelPlanQuery;
import com.dancing_orangutan.ukkikki.travelPlan.application.query.FetchTravelPlanDetailsQuery;
import com.dancing_orangutan.ukkikki.travelPlan.application.query.FetchTravelPlanDetailsQueryByMember;
import com.dancing_orangutan.ukkikki.travelPlan.application.query.SearchMyTravelPlanQuery;
import com.dancing_orangutan.ukkikki.travelPlan.application.query.SearchTravelPlanQuery;
import com.dancing_orangutan.ukkikki.travelPlan.domain.constant.PlanningStatus;
import com.dancing_orangutan.ukkikki.travelPlan.domain.keyword.KeywordRepository;
import com.dancing_orangutan.ukkikki.travelPlan.domain.keyword.KeywordEntity;
import com.dancing_orangutan.ukkikki.travelPlan.domain.travelPlan.TravelPlanEntity;
import com.dancing_orangutan.ukkikki.travelPlan.domain.travelPlan.TravelPlanRepository;
import com.dancing_orangutan.ukkikki.travelPlan.infrastructure.travelPlan.QueryDslTravelPlanRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueryTravelPlanServiceImpl implements QueryTravelPlanService {

	private final TravelPlanRepository travelPlanRepository;
	private final QueryDslTravelPlanRepository queryDslTravelPlanRepository;
	private final KeywordRepository keywordRepository;
	private final JpaVoteSurveyRepository voteSurveyRepository;

	@Override
	public TravelPlanEntity findWithRelationsByTravelPlanId(final Integer travelPlanId) {
		return travelPlanRepository.findWithRelationsByTravelPlanId(travelPlanId)
				.orElseThrow(() -> new ApiException(
						ErrorCode.TRAVEL_PLAN_NOT_FOUND));
	}

	@Override
	public List<TravelPlanEntity> searchTravelPlans(final SearchTravelPlanQuery query) {
		return queryDslTravelPlanRepository.searchTravelPlan(query);
	}

	@Override
	public List<TravelPlanEntity> fetchSuggestedTravelPlans(final Integer companyId) {
		return queryDslTravelPlanRepository.fetchSuggestedTravelPlans(companyId);
	}

	@Override
	public Page<TravelPlanEntity> fetchAvailableTravelPlans(final FetchAvailableTravelPlanQuery query) {
		return travelPlanRepository.findByPlanningStatusNot(PlanningStatus.CONFIRMED, query.pageable());
	}

	@Override
	public Page<TravelPlanEntity> fetchAllTravelPlans(FetchAllTravelPlansQuery query) {
		return travelPlanRepository.findByPlanningStatusNot(PlanningStatus.CONFIRMED,
				query.pageable());
	}

	@Override
	public TravelPlanEntity fetchTravelPlanDetails(final FetchTravelPlanDetailsQuery query) {
		return travelPlanRepository.findWithRelationsByTravelPlanId(query.travelPlanId())
				.orElseThrow(() ->
						new ApiException(ErrorCode.TRAVEL_PLAN_NOT_FOUND));
	}

	@Override
	public TravelPlanEntity fetchTravelPlanDetailsByMember(
			FetchTravelPlanDetailsQueryByMember query) {
		return travelPlanRepository.findWithRelationsByTravelPlanId(query.travelPlanId())
				.orElseThrow(() -> new ApiException(ErrorCode.TRAVEL_PLAN_NOT_FOUND));
	}

	@Override
	public List<KeywordEntity> fetchKeywords() {
		return keywordRepository.findAll();
	}

	@Override
	public List<TravelPlanEntity> searchMyTravelPlans(SearchMyTravelPlanQuery query) {
		return queryDslTravelPlanRepository.searchMyTravelPlan(query);
	}

	@Override
	public boolean fetchCanVote(Integer travelPlanId) {
		return voteSurveyRepository.existsByTravelPlan_TravelPlanId(travelPlanId);
	}

	@Override
	public VoteSurveyEntity fetchVoteSurveyEntity(Integer travelPlanId) {
		return voteSurveyRepository.findByTravelPlan_TravelPlanId(travelPlanId);
	}
}
