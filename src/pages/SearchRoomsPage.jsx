import React, { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom'; // useNavigate 추가
import Footer from '../components/layout/Footer';
import Header from '../components/layout/Header';
import CardList from '../components/searchroom/CardList';
import Sidebar from '../components/searchroom/SideBar';

const SearchRoom = () => {
  const location = useLocation();
  const navigate = useNavigate(); // 여기서 navigate를 선언합니다.

  console.log('🔍 location.state:', location.state); // ✅ 추가
  const rooms = location.state?.rooms?.travelPlans || [];

  // 만약 아래 navigate 호출이 필요한 경우 (주의: 컴포넌트 렌더링 중 호출하면 무한 루프 등 부작용 발생 가능)
  // navigate('/search-room', {
  //   state: { travelPlan: response.data.data.travelPlan },
  // });

  // 🚀 디버깅 로그
  console.log('✅ rooms 데이터 확인:', rooms);
  console.log('✅ travelPlans 데이터:', location.state?.rooms?.travelPlans);
  console.log('✅ rooms 데이터 확인:', rooms); // ✅ 추가

  const [filteredRooms, setFilteredRooms] = useState(rooms); // ✅ 초기값을 API 데이터로 설정
  const handleFilter = (themes, states) => {
    let filtered = rooms; // cards 대신 rooms로 변경

    if (!themes.includes('전체보기')) {
      filtered = filtered.filter((room) =>
        themes.some((theme) => room.theme.includes(theme)),
      );
    }

    if (!states.includes('전체보기')) {
      filtered = filtered.filter((room) => states.includes(room.status));
    }

    setFilteredRooms(filtered);
  };

  return (
    <div className="flex flex-col min-h-screen">
      <Header />
      <div className="flex flex-1">
        <Sidebar onFilter={handleFilter} />
        <CardList cards={rooms} />
      </div>
      <Footer />
    </div>
  );
};

export default SearchRoom;
