import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import useAuthStore from '../../stores/authStore';
import { publicRequest } from '../../hooks/requestMethod';
import Swal from 'sweetalert2'; 
import logo from '../../assets/logo.png';
import defaultProfile from '../../assets/profile.png';
import NavLink from '../common/NavLink';

const Header = () => {
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const { user, logout } = useAuthStore();
  const navigate = useNavigate();
  const [activeButton, setActiveButton] = useState(null);

  const handleLogout = async () => {
    // ✅ 먼저 확인 알림창 띄우기
    const result = await Swal.fire({
      title: '로그아웃 하시겠습니까?',
      text: '로그아웃하면 다시 로그인해야 합니다.',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6',
      confirmButtonText: '로그아웃',
      cancelButtonText: '취소',
    });

    if (!result.isConfirmed) {
      return; // 사용자가 '취소' 버튼을 누르면 아무 작업도 하지 않음
    }

    try {
      // ✅ 백엔드로 로그아웃 요청 (쿠키 삭제)
      const response = await publicRequest.post(
        '/api/v1/auth/logout',
        {},
        { withCredentials: true }, // ✅ 쿠키 포함 요청
      );

      if (response.status === 200) {
        console.log('✅ 로그아웃 성공:', response.data);
        useAuthStore.getState().setUser(null);
        // localStorage.removeItem('auth-store');

        logout();
        navigate('/');

        // ✅ 로그아웃 성공 알림
        Swal.fire({
          title: '로그아웃 되었습니다!',
          text: '메인 페이지로 이동합니다.',
          icon: 'success',
          confirmButtonColor: '#3085d6',
          confirmButtonText: '확인',
        });
      }
    } catch (error) {
      console.error('🚨 로그아웃 실패:', error);

      // ✅ 로그아웃 실패 알림
      Swal.fire({
        title: '로그아웃 실패',
        text: '다시 시도해주세요.',
        icon: 'error',
        confirmButtonColor: '#d33',
        confirmButtonText: '확인',
      });
    }
  };

  const handleCreateRoomClick = () => {
    setActiveButton('createRoom');
    navigate('/', { state: { createRoom: true } });
  };

  return (
    <header className="flex items-center justify-between h-20 px-6 bg-white shadow-sm">
      <Link to="/">
        <img src={logo} alt="Logo" className="object-contain w-32 h-32 ml-10" />
      </Link>
      <nav className="block mr-10 space-x-6 md:flex">
        {!user ? (
          <>
            <NavLink to="/about">서비스 소개</NavLink>
            <NavLink to="/login">회원가입 | 로그인</NavLink>
          </>
        ) : (
          <>
            <NavLink to="/search-room">전체여행방</NavLink>
            <NavLink to="/myroom">내여행방</NavLink>
            <button
              onClick={handleCreateRoomClick}
              className={`flex items-center justify-center text-center transition px-2 py-1 rounded-md 
                ${
                  activeButton === 'createRoom'
                    ? 'text-yellow'
                    : 'text-gray-600 hover:bg-gray-50'
                }`}
            >
              방만들기
            </button>
            <div className="relative">
              <button
                onClick={() => setIsDropdownOpen(!isDropdownOpen)}
                className="flex items-center space-x-2 focus:outline-none"
              >
                <img
                  src={defaultProfile}
                  alt="프로필"
                  className="w-10 h-10 border border-gray-300 rounded-full"
                />
              </button>
              {isDropdownOpen && (
                <div className="absolute right-0 z-50 w-40 mt-2 bg-white border border-gray-200 rounded-lg shadow-lg">
                  <Link
                    to="/mypage"
                    className="block px-4 py-2 text-gray-700 hover:bg-gray-100"
                  >
                    마이페이지
                  </Link>
                  <button
                    onClick={handleLogout}
                    className="block w-full px-4 py-2 text-left text-gray-700 hover:bg-gray-100"
                  >
                    로그아웃
                  </button>
                </div>
              )}
            </div>
          </>
        )}
      </nav>
    </header>
  );
};

export default Header;
