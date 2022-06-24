import React from "react";
import ReactDOM from "react-dom/client";
import "./index.css";
import reportWebVitals from "./reportWebVitals";
import CreatSocketConnection from "./connect";
import App from "./App";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import ResultPage from "./ResultPage";

const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(
  <React.StrictMode>
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<App />}></Route>
        <Route path="results" element={<ResultPage />} />
      </Routes>
    </BrowserRouter>
  </React.StrictMode>
);
reportWebVitals();
