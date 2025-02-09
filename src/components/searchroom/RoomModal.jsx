import React from 'react';
import { useNavigate } from 'react-router-dom'; // React Router 사용
import { publicRequest } from '../../hooks/requestMethod';
const ProgressBar = ({ step, totalSteps }) => {
  const progress = (step / totalSteps) * 100;

  return (
    <div className="mb-4">
      <div className="relative w-full h-4 overflow-hidden bg-gray-200 rounded-full">
        <div
          className="h-full transition-all duration-300 ease-in-out bg-yellow"
          style={{ width: `${progress}%` }}
        />
      </div>
      <div className="flex justify-end mt-2 text-sm text-gray-600">
        단계 {step} / {totalSteps}
      </div>
    </div>
  );
};

function RoomModal({
  isOpen,
  onClose,
  step,
  totalSteps,
  onNext,
  onPrev,
  selectedCard,
  people,
  handlePeopleChange,
  onIncrement,
  onDecrement,
  onComplete,
}) {
  const navigate = useNavigate();

  if (!isOpen) return null;

  // 오버레이 클릭 핸들러
  const handleOverlayClick = (e) => {
    // 만약 클릭 대상이 overlay(div) 자신이라면 모달 닫기
    if (e.target === e.currentTarget) {
      onClose();
    }
  };
  // 입장하기 버튼 클릭 시 UserRoom으로 라우팅
  const handleEnterRoom = async () => {
    if (!selectedCard || !selectedCard.id) {
      alert('🚨 여행방 정보를 찾을 수 없습니다.');
      return;
    }

    const travelPlanId = selectedCard.id; // ✅ 선택된 여행방의 ID
    const requestBody = {
      adultCount: people.adult,
      childCount: people.child,
      infantCount: people.infant,
    };

    try {
      const response = await publicRequest.post(
        `/travel-plans/${travelPlanId}`,
        requestBody,
        {
          headers: {
            'Content-Type': 'application/json',
          },
        },
      );

      console.log('✅ 여행방 입장 성공:', response.data);

      // ✅ UserRoom 페이지로 이동하면서 상태 전달
      navigate('/user-room', { state: { selectedCard: response.data } });
    } catch (error) {
      console.error('🚨 여행방 입장 실패:', error);
      alert('🚨 여행방 입장 중 오류가 발생했습니다.');
    }
  };
  return (
    // 클릭 이벤트를 배경(오버레이) div에 등록
    <div
      className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50"
      onClick={handleOverlayClick}
    >
      {/* 자식 컨테이너에서 이벤트 버블링 막기 */}
      <div
        className="w-full max-w-lg p-6 bg-white shadow-lg rounded-xl"
        onClick={(e) => e.stopPropagation()}
      >
        {/* 1단계: 방 정보 확인 */}
        {step === 1 && selectedCard && (
          <div>
            <h2 className="pb-2 mb-4 text-xl font-bold border-b-2">
              방 정보 확인
            </h2>
            <p className="mb-2">
              <strong>방 이름:</strong> {selectedCard.title}
            </p>
            <p className="mb-2">
              <strong>나라:</strong> {selectedCard.country}
            </p>
            <p className="mb-2">
              <strong>여행 날짜:</strong> {selectedCard.date}
            </p>

            {/* 진행바 + 버튼 영역 */}
            <div className="mt-6">
              <ProgressBar step={step} totalSteps={totalSteps} />
              <div className="flex justify-between space-x-2">
                <button
                  className="px-4 py-2 text-white bg-gray-400 rounded-md"
                  onClick={onClose}
                >
                  닫기
                </button>
                <button
                  className="px-4 py-2 text-white rounded-md bg-brown"
                  onClick={onNext}
                >
                  다음
                </button>
              </div>
            </div>
          </div>
        )}

        {/* 2단계: 인원 입력 */}
        {step === 2 && (
          <div>
            <h2 className="pb-2 mb-4 text-xl font-bold border-b-2">
              인원 입력
            </h2>
            <div className="space-y-4">
              {['adult', 'child', 'infant'].map((type) => (
                <div key={type} className="flex items-center justify-between">
                  <label className="font-medium text-gray-700 capitalize">
                    {type === 'adult'
                      ? '성인'
                      : type === 'child'
                      ? '아동'
                      : '유아'}
                  </label>
                  <div className="flex items-center space-x-2">
                    <button
                      className="px-3 py-1 text-gray-700 bg-gray-200 rounded hover:bg-gray-300"
                      onClick={() => onDecrement(type)}
                    >
                      -
                    </button>
                    <input
                      type="number"
                      value={people[type]}
                      onChange={(e) =>
                        handlePeopleChange(type, Number(e.target.value))
                      }
                      className="
                        w-20 p-2 border border-gray-300 rounded-md text-center
                        [appearance:textfield]
                        [&::-webkit-outer-spin-button]:appearance-none
                        [&::-webkit-inner-spin-button]:appearance-none
                      "
                      min={0}
                    />
                    <button
                      className="px-3 py-1 text-gray-700 bg-gray-200 rounded hover:bg-gray-300"
                      onClick={() => onIncrement(type)}
                    >
                      +
                    </button>
                  </div>
                </div>
              ))}
            </div>

            <div className="mt-6">
              <ProgressBar step={step} totalSteps={totalSteps} />
              <div className="flex justify-between">
                <button
                  className="px-4 py-2 text-white bg-gray-400 rounded-md"
                  onClick={onPrev}
                >
                  이전
                </button>
                <button
                  className="px-4 py-2 text-white rounded-md bg-brown"
                  onClick={handleEnterRoom}
                >
                  입장하기
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default RoomModal;
