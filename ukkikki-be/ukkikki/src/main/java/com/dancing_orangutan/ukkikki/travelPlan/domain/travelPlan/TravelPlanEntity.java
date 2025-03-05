package com.dancing_orangutan.ukkikki.travelPlan.domain.travelPlan;

import com.dancing_orangutan.ukkikki.geography.domain.city.CityEntity;
import com.dancing_orangutan.ukkikki.global.error.ApiException;
import com.dancing_orangutan.ukkikki.global.error.ErrorCode;
import com.dancing_orangutan.ukkikki.place.domain.place.PlaceEntity;
import com.dancing_orangutan.ukkikki.travelPlan.domain.constant.PlanningStatus;
import com.dancing_orangutan.ukkikki.travelPlan.domain.event.TravelPlanCloseTimeChangedEvent;
import com.dancing_orangutan.ukkikki.travelPlan.domain.memberTravelPlan.MemberTravelPlanEntity;
import com.dancing_orangutan.ukkikki.travelPlan.domain.keyword.KeywordEntity;
import com.dancing_orangutan.ukkikki.travelPlan.domain.travelPlanKeyword.TravelPlanKeywordEntity;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@NamedEntityGraph(
		name = "travelPlanWithCitiesAndMembers",
		attributeNodes = {
				@NamedAttributeNode("arrivalCity"),
				@NamedAttributeNode("departureCity"),
				@NamedAttributeNode("memberTravelPlans"),
				@NamedAttributeNode("travelPlanKeywords")
		}
)
@Table(
		name = "travel_plans",
		indexes = {
				@Index(name = "idx_travel_plans_planning_status", columnList = "planning_status"),
		}
)
public class TravelPlanEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer travelPlanId;

	@Column(nullable = false, length = 20)
	private String name;

	@Column(nullable = false, name = "start_date")
	private LocalDate startDate;

	@Column(nullable = false, name = "end_date")
	private LocalDate endDate;

	@Column(length = 1000)
	private String hostComment;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, name = "planning_status")
	private PlanningStatus planningStatus;

	@Column(name = "close_time")
	private LocalDateTime closeTime;

	@CreatedDate
	@Column(nullable = false, name = "create_time")
	private LocalDateTime createTime;

	@Column(nullable = false, name = "min_people")
	private int minPeople;

	@Column(nullable = false, name = "max_people")
	private int maxPeople;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "departure_city_id")
	private CityEntity departureCity;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "arrival_city_id")
	private CityEntity arrivalCity;

	@OneToMany(mappedBy = "travelPlan", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<TravelPlanKeywordEntity> travelPlanKeywords;

	@OneToMany(mappedBy = "travelPlan", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<MemberTravelPlanEntity> memberTravelPlans;

	@OneToMany(mappedBy = "travelPlan", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<PlaceEntity> places;

	@Builder
	public TravelPlanEntity(String name, LocalDate startDate, LocalDate endDate,
			PlanningStatus planningStatus,
			int minPeople, int maxPeople, CityEntity departureCity,
			CityEntity arrivalCity, List<KeywordEntity> keywordEntities) {
		this.name = name;
		this.startDate = startDate;
		this.endDate = endDate;
		this.planningStatus = planningStatus;
		this.minPeople = minPeople;
		this.maxPeople = maxPeople;
		this.departureCity = departureCity;
		this.arrivalCity = arrivalCity;
		this.travelPlanKeywords = new HashSet<>();
		this.memberTravelPlans = new HashSet<>();
		this.places = new HashSet<>();
		addTravelKeywords(keywordEntities);
	}

	private void addTravelKeywords(final List<KeywordEntity> keywordEntities) {
		if (keywordEntities != null) {
			keywordEntities.forEach(this::addTravelKeyword);
		}
	}

	private void addTravelKeyword(final KeywordEntity keyword) {
		this.travelPlanKeywords.add(
				TravelPlanKeywordEntity.builder()
						.travelPlan(this)
						.keyword(keyword)
						.build()
		);
	}

	public void updateStatus(PlanningStatus planningStatus) {
		this.planningStatus = planningStatus;
	}

	public TravelPlanCloseTimeChangedEvent updateCloseTime(final LocalDateTime closeTime,
			final Integer memberId) {
		if (!isHost(memberId)) {
			throw new ApiException(ErrorCode.TRAVEL_PLAN_NOT_AUTHORIZED);
		}

		this.closeTime = closeTime;
		return TravelPlanCloseTimeChangedEvent.builder().travelPlanId(travelPlanId)
				.closeTime(closeTime).build();
	}

	public void updateCompanionCount(final Integer memberId, final int adultCount, int childCount,
			final int infantCount) {
		MemberTravelPlanEntity memberTravelPlan = findMemberTravelPlan(memberId);
		memberTravelPlan.updateHost(adultCount, childCount, infantCount);
	}

	public void submitPlan() {
		validateMinimumParticipantsRequirement();
		this.planningStatus = PlanningStatus.BIDDING;
	}

	private void validateMinimumParticipantsRequirement() {
		if (calCurrentParticipants() < minPeople) {
			throw new ApiException(ErrorCode.MINIMUM_PARTICIPANTS_NOT_FULFILLED);
		}
	}

	public int calCurrentParticipants() {
		return memberTravelPlans.stream()
				.mapToInt(memberTravelPlan -> memberTravelPlan.getChildCount()
						+ memberTravelPlan.getInfantCount() + memberTravelPlan.getAdultCount())
				.sum();
	}

	public void writeHostComment(final Integer memberId, final String hostComment) {
		if (!isHost(memberId)) {
			throw new ApiException(ErrorCode.TRAVEL_PLAN_NOT_AUTHORIZED);
		}
		this.hostComment = hostComment;
	}

	public void updatePlanningStatus(final PlanningStatus status) {
		this.planningStatus = status;
	}

	private boolean isHost(final Integer memberId) {
		return memberTravelPlans.stream()
				.filter(mtp -> mtp.getMemberTravelPlanId().getMemberId().equals(memberId))
				.anyMatch(MemberTravelPlanEntity::isHost);
	}

	private MemberTravelPlanEntity findMemberTravelPlan(final Integer memberId) {
		return memberTravelPlans
				.stream()
				.filter(mtp -> mtp.getMemberTravelPlanId().getMemberId().equals(memberId))
				.findFirst()
				.orElseThrow(() -> new ApiException(ErrorCode.MEMBER_TRAVEL_PLAN_NOT_FOUND));
	}
}