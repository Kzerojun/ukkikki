package com.dancing_orangutan.ukkikki.place.mapper;

import com.dancing_orangutan.ukkikki.place.domain.like.Like;
import com.dancing_orangutan.ukkikki.place.domain.like.LikeEntity;
import com.dancing_orangutan.ukkikki.place.domain.like.LikeId;

public class PlaceLikeMapper {

    public static LikeEntity mapToEntity(Like like) {

        LikeId likeId = new LikeId(like.getPlaceId(), like.getCreatorId());

        return LikeEntity.builder()
                .likesCnt(like.getLikeCount())
                .likeId(likeId)
                .build();
    }

    public static Like mapToDomain(LikeEntity likeEntity) {
        return Like.builder()
                .placeId(likeEntity.getLikeId().getPlaceId())
                .creatorId(likeEntity.getLikeId().getMemberId())
                .likeCount(likeEntity.getLikesCnt())
                .build();
    }
}
