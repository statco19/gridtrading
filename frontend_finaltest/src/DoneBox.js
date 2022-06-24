import "./ResultBoxes.css";
import { useEffect, useMemo, useState } from "react";
import { useTable } from "react-table";
import reactTable from "react-table";
import useInterval from "./api";

const INITIAL_VALUES = [
  {
    timeStamp: "2018-04-17T19:16:00",
    volume: 10,
    price: 20,
  },
  {
    timeStamp: "2018-04-18T19:16:00",
    volume: 90,
    price: 100,
  },
];

const COLUMN_DATA = [
  {
    accessor: "timeStamp",
    Header: "timeStamp",
  },
  {
    accessor: "volume",
    Header: "volume",
  },
  {
    accessor: "price",
    Header: "price",
  },
];

const UPDATE_DATA = [
  {
    timeStamp: "dmdpdpmdm",
    opening_price: 111111,
    trading_price: 222222,
  },
];

function DoneBox(
  initialValues = INITIAL_VALUES,
  columnData = COLUMN_DATA,
  updateData = UPDATE_DATA
) {
  const [updateTest, setUpdateTest] = useState(updateData);

  const asyncTest = async () => {
    const options = { method: "GET", headers: { Accept: "application/json" } };
    fetch(
      "https://api.upbit.com/v1/candles/minutes/1?market=KRW-BTC&count=10",
      options
    )
      .then((response) => response.json())
      .then((result) => {
        console.log(result);
        setUpdateTest(result);
      })
      .catch((err) => console.error(err));
    console.log(updateTest);
  };

  useInterval(() => {
    //const selectedTagIdParam = updateData.map(({ id }) => id).join(",");
    asyncTest();
  }, 1000);

  return (
    <div className="ResultBox">
      <h1 className="ResultBox-heading">미체결(Sell)</h1>
      <table>
        <thead>
          <tr>
            <th>타임스탬프</th>
            <th>오픈</th>
            <th>종가</th>
          </tr>
        </thead>
        <tbody>
          {updateTest.map((updateTest) => (
            <tr key={updateTest.candle_date_time_kst}>
              <td>{updateTest.candle_date_time_utc}</td>
              <td>{updateTest.opening_price}</td>
              <td>{updateTest.trade_price}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
export default DoneBox;
