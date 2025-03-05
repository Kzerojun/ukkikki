package com.dancing_orangutan.ukkikki.travelPlan.infrastructure.travelPlan;

import com.dancing_orangutan.ukkikki.proposal.domain.proposal.QProposalEntity;
import com.dancing_orangutan.ukkikki.travelPlan.application.query.SearchMyTravelPlanQuery;
import com.dancing_orangutan.ukkikki.travelPlan.application.query.SearchTravelPlanQuery;
import com.dancing_orangutan.ukkikki.travelPlan.domain.constant.PlanningStatus;
import com.dancing_orangutan.ukkikki.travelPlan.domain.travelPlan.QTravelPlanEntity;
import com.dancing_orangutan.ukkikki.travelPlan.domain.travelPlan.TravelPlanEntity;
import com.dancing_orangutan.ukkikki.travelPlan.domain.travelPlanKeyword.QTravelPlanKeywordEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QueryDslTravelPlanRepository {

	private final JPAQueryFactory queryFactory;
	@PersistenceContext
	private EntityManager entityManager;

	public List<TravelPlanEntity> searchTravelPlan(
			SearchTravelPlanQuery query
	) {
		QTravelPlanEntity entity = QTravelPlanEntity.travelPlanEntity;
		QTravelPlanKeywordEntity keywordEntity = QTravelPlanKeywordEntity.travelPlanKeywordEntity;
		BooleanBuilder booleanBuilder = new BooleanBuilder();

		// 기본 검색 조건
		booleanBuilder.and(entity.startDate.between(query.startDate(), query.startDate()));
		booleanBuilder.and(entity.departureCity.cityId.eq(query.departureCityId()));
		booleanBuilder.and(entity.arrivalCity.cityId.eq(query.arrivalCityId()));

		// 추가 조건(상태)
		if (query.status() != null) {
			booleanBuilder.and(entity.planningStatus.eq(query.status()));
		}

		// 추가 조건(키워드)
		if (query.keywords() != null && !query.keywords().isEmpty()) {
			booleanBuilder.and(
					entity.travelPlanKeywords.any().keyword.keywordId.in(query.keywords()));
		}

		return queryFactory
				.selectFrom(entity)
				.leftJoin(entity.travelPlanKeywords, keywordEntity).fetchJoin()
				.where(booleanBuilder)
				.distinct()
				.fetch();
	}

	public List<TravelPlanEntity> fetchSuggestedTravelPlans(Integer companyId) {
		QTravelPlanEntity travelPlan = QTravelPlanEntity.travelPlanEntity;
		QProposalEntity proposal = QProposalEntity.proposalEntity;
		BooleanBuilder booleanBuilder = new BooleanBuilder();

		// 입찰 중인 여행 계획만 조회
		booleanBuilder.and(travelPlan.planningStatus.eq(PlanningStatus.CONFIRMED));

		// 해당 여행사가 제안서를 작성하지 않은 TravelPlan만 조회
		booleanBuilder.and(
				JPAExpressions
						.selectOne()
						.from(proposal)
						.where(proposal.travelPlan.travelPlanId.eq(travelPlan.travelPlanId)
								.and(proposal.company.companyId.eq(companyId))) // 특정 여행사가 제안했는지 확인
						.notExists()
		);

		// EntityGraph를 사용해 연관 엔티티를 한 번에 로드
		EntityGraph<?> entityGraph = entityManager.getEntityGraph("travelPlanWithCitiesAndMembers");
		List<TravelPlanEntity> plans = queryFactory
				.selectFrom(travelPlan)
				.where(booleanBuilder)
				.distinct()
				.setHint("jakarta.persistence.fetchgraph", entityGraph) // 변경된 부분
				.fetch();

		return plans;
	}


	public List<TravelPlanEntity> searchMyTravelPlan(SearchMyTravelPlanQuery query) {
		QTravelPlanEntity entity = QTravelPlanEntity.travelPlanEntity;
		QTravelPlanKeywordEntity keywordEntity = QTravelPlanKeywordEntity.travelPlanKeywordEntity;
		BooleanBuilder booleanBuilder = new BooleanBuilder();

		if (query.status() != null) {
			booleanBuilder.and(entity.planningStatus.eq(query.status()));
		}

		booleanBuilder.and(entity.memberTravelPlans.any().member.memberId.eq(query.memberId()));

		return queryFactory
				.selectFrom(entity)
				.leftJoin(entity.travelPlanKeywords, keywordEntity).fetchJoin()
				.where(booleanBuilder)
				.distinct()
				.fetch();
	}
}
