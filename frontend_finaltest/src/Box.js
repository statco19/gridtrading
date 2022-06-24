import "./Box.css";
import Button from "./Button";
import LimitForm from "./LimitForm";
import { test } from "./api";
import { useState } from "react";

function Box() {
  const startTrade = () => {};
  const [items, setItems] = useState([]);
  const handleCreateSuccess = (newItem) => {
    setItems((prevItems) => [newItem, ...prevItems]);
  };
  return (
    <div className="Box">
      <h1 className="Box-heading">Arithmetic</h1>
      <div className="Content App-box">
        <div className="Content-inner">
          <div className="LimitForm">
            <LimitForm onSubmit={test} onSubmitSuccess={handleCreateSuccess} />
          </div>
        </div>
      </div>
    </div>
  );
}

export default Box;
