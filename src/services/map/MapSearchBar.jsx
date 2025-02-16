import React, { useRef, useState } from 'react';
import { Autocomplete } from '@react-google-maps/api';
import { FaSearch } from 'react-icons/fa';
import { publicRequest } from '../../hooks/requestMethod';
import Swal from 'sweetalert2';

const MapSearchBar = ({
  onPlaceSelected,
  selectedTravelPlanId,
  favorites = [],
}) => {
  const [searchedPlace, setSearchedPlace] = useState(null);
  const [isRegistered, setIsRegistered] = useState(false);
  const autocompleteRef = useRef(null);

  // Autocomplete 로드 시 ref 설정
  const handleLoad = (autocomplete) => {
    autocompleteRef.current = autocomplete;
  };

  // Autocomplete에서 장소 선택 시 처리
  const onPlaceChanged = () => {
    if (!autocompleteRef.current) return;
    const place = autocompleteRef.current.getPlace();
    if (!place || !place.geometry) {
      console.warn('유효한 장소가 선택되지 않았습니다.');
      return;
    }
    console.log('선택된 place:', place);
    const photoUrl =
      place.photos && place.photos.length > 0
        ? place.photos[0].getUrl({ maxWidth: 100, maxHeight: 100 })
        : null;
    const rating = place.rating || null;
    const newPlace = {
      name: place.name,
      address: place.formatted_address,
      latitude: place.geometry.location.lat(),
      longitude: place.geometry.location.lng(),
      photoUrl,
      rating,
      placeId: place.place_id || Date.now().toString(),
    };
    console.log('새 장소 정보:', newPlace);

    // 중복 등록 방지: 이미 등록된 장소가 있는지 확인
    const isDuplicate = favorites.some(
      (fav) => fav.placeId === newPlace.placeId || fav.name === newPlace.name,
    );
    if (isDuplicate) {
      Swal.fire('알림', '이미 등록된 장소입니다.', 'info');
      setIsRegistered(true);
      setSearchedPlace(newPlace);
      return;
    }
    // 중복이 아니면 상태 업데이트
    setSearchedPlace(newPlace);
    setIsRegistered(false);
  };

  // "장소 등록" 버튼 클릭 시 처리
  const handleToggleBookmark = async () => {
    if (!searchedPlace) return;
    if (isRegistered) {
      Swal.fire('알림', '이미 등록된 장소입니다.', 'info');
      return;
    }
    try {
      const response = await publicRequest.post(
        `/api/v1/travel-plans/${selectedTravelPlanId}/places`,
        searchedPlace,
      );
      if (response.status === 200) {
        // 응답에서 DB의 고유 ID를 받아옴
        const dbPlaceId = response.data.data.placeId;
        console.log('DB 응답, 새 장소 ID:', dbPlaceId);
        // DB의 ID로 업데이트
        const updatedPlace = { ...searchedPlace, placeId: dbPlaceId };
        onPlaceSelected(updatedPlace);
        setIsRegistered(true);
        Swal.fire('성공', '장소가 등록되었습니다.', 'success');
        console.log('등록된 장소:', updatedPlace);
      }
    } catch (error) {
      console.error('새 장소 등록 실패:', error);
      Swal.fire('알림', '🚨 장소 등록 중 오류가 발생했습니다.', 'error');
    }
  };

  return (
    <div>
      <Autocomplete onLoad={handleLoad} onPlaceChanged={onPlaceChanged}>
        <div className="relative w-[320px]">
          <input
            type="text"
            placeholder="장소 검색"
            className="w-full h-[44px] pl-4 pr-[48px] text-sm border border-gray-300 focus:outline-none"
          />
          <div className="absolute text-xl text-gray-500 -translate-y-1/2 pointer-events-none right-3 top-1/2">
            <FaSearch />
          </div>
        </div>
      </Autocomplete>

      {searchedPlace && (
        <div className="mt-2 flex flex-col border border-gray-300 rounded-md p-2 bg-white w-[320px]">
          <div className="flex items-center">
            {searchedPlace.photoUrl && (
              <img
                src={searchedPlace.photoUrl}
                alt="Place"
                className="w-[60px] h-[60px] rounded object-cover mr-2"
              />
            )}
            <div className="flex-1">
              <div className="font-bold">{searchedPlace.name}</div>
              <div className="text-sm text-gray-600">
                {searchedPlace.address}
              </div>
            </div>
          </div>
          {searchedPlace.rating !== null && (
            <div className="mt-2 text-sm text-gray-700">
              별점: {searchedPlace.rating}
            </div>
          )}
          <button
            onClick={handleToggleBookmark}
            className="self-end px-3 mt-2 text-white bg-orange-400 rounded cursor-pointer h-9"
            disabled={isRegistered}
          >
            {isRegistered ? '등록 완료' : '장소 등록'}
          </button>
        </div>
      )}
    </div>
  );
};

export default MapSearchBar;
