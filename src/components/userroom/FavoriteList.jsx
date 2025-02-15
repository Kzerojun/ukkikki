import React, { useState, useEffect, useMemo } from 'react';
import { publicRequest } from '../../hooks/requestMethod';
import useAuthStore from '../../stores/authStore';
import Swal from 'sweetalert2';
import MapSearchBar from '../../services/map/MapSearchBar';
import { CiCirclePlus } from 'react-icons/ci';

const FavoriteList = ({ selectedCard }) => {
  const { user } = useAuthStore(); // 현재 로그인한 유저 정보
  const [favorites, setFavorites] = useState([]);
  const [expandedPlaceId, setExpandedPlaceId] = useState(null);
  const [showTagInput, setShowTagInput] = useState(false);
  const [newTag, setNewTag] = useState('');

  useEffect(() => {
    if (selectedCard && selectedCard.places) {
      // 좋아요 상태는 place.likeYn를 사용하고, 태그 배열은 없으면 빈 배열로 설정
      const updatedPlaces = selectedCard.places.map((place) => ({
        ...place,
        tags: place.tags || [],
        // 기존 likedUsers 로 체크하던 것 대신, API에서 전달된 likeYn 값을 사용
        isLiked: place.likeYn,
      }));
      setFavorites(updatedPlaces);
      console.log('📌 좋아요 목록 업데이트:', updatedPlaces);
    }
  }, [selectedCard, user]);

  // MapSearchBar에서 장소 선택 시 호출
  const handlePlaceSelected = (newPlace) => {
    setFavorites((prev) => {
      if (prev.some((fav) => fav.name === newPlace.name)) return prev;
      return [
        ...prev,
        { ...newPlace, likeCount: 0, isLiked: false, likeYn: false, tags: [] },
      ];
    });
  };

  // 좋아요 순 정렬
  const sortedWishlists = useMemo(() => {
    return [...favorites].sort((a, b) => b.likeCount - a.likeCount);
  }, [favorites]);

  // 좋아요 토글 핸들러
  const handleLikeToggle = async (place) => {
    const travelPlanId = selectedCard.travelPlanId;
    const placeId = place.placeId;
    const isLiked = place.isLiked;
    const totalMember = selectedCard.member.totalParticipants;

    try {
      if (!isLiked) {
        const response = await publicRequest.post(
          `/api/v1/travel-plans/${travelPlanId}/places/${placeId}/likes`,
        );
        if (response.status === 200) {
          setFavorites((prev) =>
            prev.map((fav) =>
              fav.placeId === placeId
                ? {
                    ...fav,
                    likeCount: fav.likeCount + totalMember,
                    isLiked: true,
                    likeYn: true,
                  }
                : fav,
            ),
          );
          console.log('👍 좋아요 처리 결과:', response.data);
        }
      } else {
        const response = await publicRequest.delete(
          `/api/v1/travel-plans/${travelPlanId}/places/${placeId}/likes`,
        );
        if (response.status === 200) {
          setFavorites((prev) =>
            prev.map((fav) =>
              fav.placeId === placeId
                ? {
                    ...fav,
                    likeCount: fav.likeCount - totalMember,
                    isLiked: false,
                    likeYn: false,
                  }
                : fav,
            ),
          );
        }
      }
    } catch (error) {
      console.error('🚨 좋아요 처리 실패:', error);
      Swal.fire('알림', '🚨 좋아요 처리 중 오류가 발생했습니다.', 'error');
    }
  };

  // 태그 삭제 핸들러 (내가 쓴 태그 클릭 시)
  const handleTagDelete = async (placeId, tagId) => {
    Swal.fire({
      title: '태그 삭제',
      text: '정말로 이 태그를 삭제하시겠습니까?',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: '삭제',
      cancelButtonText: '취소',
      reverseButtons: true,
    }).then(async (result) => {
      if (result.isConfirmed) {
        try {
          const travelPlanId = selectedCard.travelPlanId;
          const response = await publicRequest.delete(
            `/api/v1/travel-plans/${travelPlanId}/tags/${tagId}`,
          );
          if (response.status === 200) {
            setFavorites((prev) =>
              prev.map((fav) =>
                fav.placeId === placeId
                  ? {
                      ...fav,
                      tags: fav.tags.filter((tag) => tag.placeTagId !== tagId),
                    }
                  : fav,
              ),
            );
            Swal.fire('성공', '태그가 삭제되었습니다.', 'success');
          }
        } catch (error) {
          console.error('태그 삭제 실패:', error);
          Swal.fire('알림', '태그 삭제에 실패했습니다.', 'error');
        }
      }
    });
  };

  // 헤더 클릭 시 확장/축소 토글
  const handleToggleExpand = (place) => {
    if (expandedPlaceId === place.placeId) {
      setExpandedPlaceId(null);
      setShowTagInput(false);
      setNewTag('');
    } else {
      setExpandedPlaceId(place.placeId);
      setShowTagInput(false);
      setNewTag('');
    }
  };

  // 플러스 버튼 클릭 시 태그 입력창 표시
  const handleShowTagInput = (e) => {
    e.stopPropagation();
    setShowTagInput(true);
  };

  // 태그 입력 값 변경 (최대 20자)
  const handleTagInputChange = (e) => {
    if (e.target.value.length <= 20) {
      setNewTag(e.target.value);
    }
  };

  // 태그 제출 핸들러 - API 호출 후 로컬 업데이트
  const handleTagSubmit = async (e) => {
    e.stopPropagation();
    if (newTag.trim() === '') return;
    const travelPlanId = selectedCard.travelPlanId;
    const placeId = expandedPlaceId;
    try {
      const response = await publicRequest.post(
        `/api/v1/travel-plans/${travelPlanId}/places/${placeId}/tags`,
        { placeTagName: newTag.trim() },
      );
      if (response.status === 200) {
        // response.data에 새 태그의 id가 포함되어 있다고 가정합니다.
        setFavorites((prev) =>
          prev.map((fav) =>
            fav.placeId === placeId
              ? {
                  ...fav,
                  tags: [
                    ...fav.tags,
                    {
                      placeTagId: response.data.id,
                      name: newTag.trim(),
                      isMyTag: true,
                    },
                  ],
                }
              : fav,
          ),
        );
        setNewTag('');
        setShowTagInput(false);
      }
    } catch (error) {
      console.error('태그 추가 실패:', error);
      Swal.fire('알림', '태그 추가에 실패했습니다.', 'error');
    }
  };

  return (
    <div className="space-y-4">
      {/* MapSearchBar */}
      <MapSearchBar onPlaceSelected={handlePlaceSelected} />

      {/* 찜한 장소 목록 */}
      {sortedWishlists.map((item, index) => (
        <div
          key={index}
          className="p-4 transition-all duration-300 bg-gray-100 rounded-lg hover:bg-gray-200"
        >
          {/* 헤더 영역 (클릭 시 확장/축소) */}
          <div
            className="flex items-center justify-between cursor-pointer"
            onClick={() => handleToggleExpand(item)}
          >
            <div className="flex items-center space-x-2">
              <h3 className="text-lg font-semibold text-gray-700">
                {index + 1}. {item.name}
              </h3>
            </div>
            <button
              className={`px-2 py-1 text-sm rounded-md ${
                item.isLiked
                  ? 'text-red-500 bg-gray-300'
                  : 'text-gray-500 bg-gray-200'
              }`}
              onClick={(e) => {
                e.stopPropagation();
                handleLikeToggle(item);
              }}
            >
              {item.isLiked ? '❤️' : '🤍'} {item.likeCount}
            </button>
          </div>

          {/* 확장 영역: 태그 목록 및 태그 추가 */}
          {expandedPlaceId === item.placeId && (
            <div className="mt-4 transition-all duration-300">
              {item.tags && item.tags.length > 0 ? (
                <div className="flex flex-wrap gap-2">
                  {item.tags.map((tag, idx) => (
                    <span
                      key={tag.placeTagId || idx}
                      onClick={
                        tag.isMyTag
                          ? () => handleTagDelete(item.placeId, tag.placeTagId)
                          : undefined
                      }
                      className={`px-2 py-1 text-sm rounded-full cursor-pointer ${
                        tag.isMyTag
                          ? 'bg-green-500 text-white'
                          : 'bg-yellow-200 text-brown'
                      }`}
                    >
                      {typeof tag === 'object' ? tag.name : tag}
                    </span>
                  ))}
                </div>
              ) : (
                <p className="text-sm text-gray-500">등록된 태그가 없습니다.</p>
              )}
              <div className="flex justify-center mt-2">
                {showTagInput ? (
                  <div
                    className="flex items-center gap-2 p-2 rounded"
                    onClick={(e) => e.stopPropagation()}
                  >
                    <input
                      type="text"
                      value={newTag}
                      onChange={handleTagInputChange}
                      placeholder="태그를 입력해주세요."
                      className="px-2 py-1 border rounded"
                      maxLength={20}
                    />
                    <button
                      onClick={(e) => {
                        e.stopPropagation();
                        handleTagSubmit(e);
                      }}
                      className="flex items-center justify-center px-2 py-1 text-white bg-blue-500 rounded hover:bg-blue-600"
                    >
                      <CiCirclePlus
                        size={35}
                        style={{ color: 'black', fontWeight: 'bold' }}
                      />
                    </button>
                  </div>
                ) : (
                  <button
                    className="px-3 py-1 text-white rounded"
                    onClick={(e) => {
                      e.stopPropagation();
                      handleShowTagInput(e);
                    }}
                  >
                    <CiCirclePlus size={35} style={{ color: 'black' }} />
                  </button>
                )}
              </div>
            </div>
          )}
        </div>
      ))}
    </div>
  );
};

export default FavoriteList;
