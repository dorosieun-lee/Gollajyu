import React from "react";
import {
  BrowserRouter as Router,
  Routes,
  Route,
  Outlet,
} from "react-router-dom";
import NavigationBar from "./components/NavigationBar";
import VoteButton from "./components/VoteButton";
import MainPage from "./pages/MainPage";
import VotePage from "./pages/VotePage";
import BroadcastPage from "./pages/BroadcastPage";
import StatisticPage from "./pages/StatisticPage";
import TestPage from "./pages/TestPage";
import MyPage from "./pages/MyPage";
import VideoComponent from "./components/openvidu/VideoComponent";
import CreateVideoRoom from "./components/openvidu/CreateVideoRoom";
import "./App.css";

// 추후 적용
// NavigationBar가 나타나지 않아야하는 곳: EnterVideoRoom, CreateVideoRoom => 적용 완료
// VoteButton이 나타나야하는 곳: VotePage, BroadcastPage, MainPage => 추후에 컴포넌트 내부로 넣기

const Navbar = () => {
  return (
    <>
      <NavigationBar />
      {/* body 배경색 있음 */}
      <div className="body">
        <Outlet />
      </div>
    </>
  );
};

const App = () => {
  return (
    <Router>
      <VoteButton />
      <Routes>
        <Route path="/EnterVideoRoom" element={<VideoComponent />} />
        <Route path="/CreateVideoRoom" element={<CreateVideoRoom />} />
        <Route element={<Navbar />}>
          <Route path="/" element={<MainPage />} />
          <Route path="/VotePage" element={<VotePage />} />
          <Route path="/BroadcastPage" element={<BroadcastPage />} />
          <Route path="/StatisticPage" element={<StatisticPage />} />
          <Route path="/TestPage" element={<TestPage />} />
          <Route path="/MyPage/*" element={<MyPage />} />
        </Route>
      </Routes>
    </Router>
  );
};

export default App;