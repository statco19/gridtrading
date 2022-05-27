import React from "react";
import ReactDOM from "react-dom/client";
import "./index.css";
import reportWebVitals from "./reportWebVitals";
import CreatSocketConnection from "./connect";
import App from "./App";

const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(
  <React.StrictMode>
    <App />
    <CreatSocketConnection type="ticker" />
  </React.StrictMode>
);
reportWebVitals();
