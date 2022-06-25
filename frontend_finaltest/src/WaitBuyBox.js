import { useEffect, useMemo, useState } from "react";
import { useTable } from "react-table";
import "./ResultBoxes.css";
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
    order_price: "dmdpdpmdm",
    trade_price: "dmdpdpmdm",
    volume: "dmdpdpmdm",
    side: "dmdpdpmdm"
  },
];

function WaitBuyBox({
  initialValues = INITIAL_VALUES,
  columnData = COLUMN_DATA,
  updateData = UPDATE_DATA,
}) {
  const [updateTest, setUpdateTest] = useState(updateData);

  const columns = useMemo(() => columnData, []);
  const [data, setData] = useState(initialValues);
  const dataTest = useMemo(() => data, []);

  const [info, setInfo] = useState();
  const { getTableProps, getTableBodyProps, headerGroups, rows, prepareRow } =
    useTable({ columns, data: dataTest });

  const asyncTest = async () => {
    const options = { method: "GET", headers: { Accept: "application/json" } };
    fetch(
      "http://localhost:8080/fetch-wait",
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

  useEffect(() => {
    asyncTest();
  }, []);

  useInterval(() => {
    //const selectedTagIdParam = updateData.map(({ id }) => id).join(",");
    asyncTest();
  }, 1000);

  // useTable()

  // useEffect(() => {

  // }, [updateTest]);

  // const getWaitBuy = () => {
  //   dataTest.getWaitBuy().then(item => setInfo(item))
  // }
  // const dataTest = useMemo(() => info, [info])
  console.log(rows);
  return (
    <div className="ResultBox">
          <h1 className="ResultBox-heading">미체결(Buy)</h1>
          <table>
            <thead>
              <tr>
                <th>order_price</th>
                <th>trade_price</th>
                <th>volume</th>
                <th>side</th>
              </tr>
            </thead>
            <tbody>
              {updateTest.map((updateTest) => (
                <tr key={updateTest.order_price}>
                  <td>{updateTest.trade_price}</td>
                  <td>{updateTest.volume}</td>
                  <td>{updateTest.side}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
  );
}

export default WaitBuyBox;
