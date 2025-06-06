package com.dancing_orangutan.ukkikki.travelPlan.ui.facade.dto.response;

import com.dancing_orangutan.ukkikki.geography.domain.city.CityEntity;
import com.dancing_orangutan.ukkikki.place.domain.place.PlaceEntity;
import com.dancing_orangutan.ukkikki.place.domain.placeTag.PlaceTagEntity;
import com.dancing_orangutan.ukkikki.travelPlan.domain.constant.PlanningStatus;
import com.dancing_orangutan.ukkikki.travelPlan.domain.keyword.KeywordEntity;
import com.dancing_orangutan.ukkikki.travelPlan.domain.travelPlan.TravelPlanEntity;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public record FetchTravelPlanDetailsByCompanyResponse(TravelPlanResponse travelPlan) {

    public static FetchTravelPlanDetailsByCompanyResponse fromEntity(final TravelPlanEntity entity) {
        return FetchTravelPlanDetailsByCompanyResponse.builder()
                .travelPlan(TravelPlanResponse.fromEntity(entity))
                .build();
    }

    @Builder
    public FetchTravelPlanDetailsByCompanyResponse {}

    @Builder
    private record TravelPlanResponse(
            Integer travelPlanId,
            String name,
            CityResponse arrivalCity,
            CityResponse departureCity,
            LocalDate startDate,
            LocalDate endDate,
            PlanningStatus planningStatus,
            int minPeople,
            int maxPeople,
            int currentParticipants,
            List<KeywordResponse> keywords,
            List<PlaceResponse> places
    ) {
        private static TravelPlanResponse fromEntity(TravelPlanEntity entity) {
            return TravelPlanResponse.builder()
                    .travelPlanId(entity.getTravelPlanId())
                    .name(entity.getName())
                    .arrivalCity(CityResponse.fromEntity(entity.getArrivalCity()))
                    .departureCity(CityResponse.fromEntity(entity.getDepartureCity()))
                    .startDate(entity.getStartDate())
                    .endDate(entity.getEndDate())
                    .planningStatus(entity.getPlanningStatus())
                    .minPeople(entity.getMinPeople())
                    .maxPeople(entity.getMaxPeople())
                    .currentParticipants(entity.calCurrentParticipants())
                    .keywords(entity.getTravelPlanKeywords().stream()
                            .map(travelPlanKeywordEntity -> KeywordResponse.fromEntity(travelPlanKeywordEntity.getKeyword()))
                            .collect(Collectors.toList()))
                    .places(entity.getPlaces().stream()
                            .map(PlaceResponse::fromEntity)
                            .toList())
                    .build();
        }
    }

    @Builder
    private record CityResponse(
            Integer cityId,
            String name
    ) {
        private static CityResponse fromEntity(CityEntity entity) {
            return CityResponse.builder()
                    .cityId(entity.getCityId())
                    .name(entity.getName())
                    .build();
        }
    }

    @Builder
    private record KeywordResponse(
            Integer keywordId,
            String name
    ) {
        private static KeywordResponse fromEntity(KeywordEntity entity) {
            return KeywordResponse.builder()
                    .keywordId(entity.getKeywordId())
                    .name(entity.getName())
                    .build();
        }
    }

    @Builder
    private record PlaceResponse(
            Integer placeId,
            String name,
            Integer likeCount,
            double latitude,
            double longitude,
            List<PlaceTagResponse> tags
    ) {
        private static PlaceResponse fromEntity(PlaceEntity entity) {
            return PlaceResponse.builder()
                    .placeId(entity.getPlaceId())
                    .name(entity.getName())
                    .likeCount(entity.countLikes())
                    .latitude(entity.getLatitude())
                    .longitude(entity.getLongitude())
                    .tags(entity.getPlaceTags().stream()
                            .map(PlaceTagResponse::fromEntity)
                            .toList())
                    .build();
        }
    }

    @Builder
    private record PlaceTagResponse(
            Integer placeTagId,
            String name
    ) {
        private static PlaceTagResponse fromEntity(PlaceTagEntity entity) {
            return PlaceTagResponse.builder()
                    .placeTagId(entity.getPlaceTagId())
                    .name(entity.getPlaceTagName())
                    .build();
        }
    }
}
