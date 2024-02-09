import React, { useState } from 'react';
import categoryData from '../../stores/categoryData';

const VotePageHeader = ({ onSort, onSearch, onSearchTerm, onSearchCategory }) => {
  const [selectedCategory, setSelectedCategory] = useState(""); // 상태 초기화

  // 카테고리 선택 변경 이벤트 핸들러
  const handleCategoryChange = (event) => {
    const selectedCategoryId = event.target.value; // 선택된 카테고리의 ID 가져오기
    setSelectedCategory(selectedCategoryId); // 선택된 카테고리 ID로 상태 업데이트
    onSearchCategory(selectedCategoryId); // 선택된 카테고리 ID를 상위 컴포넌트에 전달
  };
  // 검색 입력 변경 이벤트 핸들러
  const handleChange = (event) => {
    onSearchTerm(event.target.value); // 입력된 검색어로 상태 업데이트
  };

  // 검색 버튼 클릭 이벤트 핸들러
  const handleSearchClick = () => {
    onSearch(); // 입력 완료된 검색어를 상위 컴포넌트에 전달
  };
   
  return (
    <div className="vote-controls container mx-auto my-4 max-w-5xl px-4">
      <div className="flex justify-between">
        <div>
        <button 
          onClick={() => onSort('latest')} 
          className="border border-orange-500 mr-2 mt-4 fontsize-max-sm rounded-full px-4 py-2 hover:bg-orange-500 hover:text-white active:bg-orange-600 active:text-white">
          최신순
        </button>
        <button
          onClick={() => onSort('popular')}
          className="border border-orange-500 mr-2 mt-4 fontsize-max-sm rounded-full px-4 py-2 hover:bg-orange-500 hover:text-white active:bg-orange-600 active:text-white">
          인기순
        </button>
        </div>
        <select value={selectedCategory} onChange={handleCategoryChange}>
          {categoryData.map((category) => (
            <option key={category.id} value={category.id}>
              {category.name}
            </option>
          ))}
        </select>
        <input
          type="search"
          placeholder="투표 검색..."
          onChange={handleChange}
          className="border p-1 mt-4 fontsize-md"
        />
        <button 
          onClick={handleSearchClick}
          className="ml-2 border border-orange-500 mt-4 fontsize-max-sm rounded-full px-4 py-2 hover:bg-orange-500 hover:text-white active:bg-orange-600 active:text-white">
          검색
        </button>
      </div>
    </div>
  );
};

export default VotePageHeader;