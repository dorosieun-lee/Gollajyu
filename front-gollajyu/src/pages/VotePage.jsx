import React from "react";
import VotePageList from "../components/vote/VotePageList";
import VoteControls from "../components/vote/VoteControls";

const MainPage = () => {
  return (
    <div>
      <h1>투표모아쥬</h1>
      <VoteControls />
      <VotePageList />
    </div>
  );
};

export default MainPage;