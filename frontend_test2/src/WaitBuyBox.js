import { useMemo, useState } from "react";
import { useTable } from "react-table";
import "./ResultBoxes.css";
import reactTable from "react-table";

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

function WaitBuyBox({
  initialValues = INITIAL_VALUES,
  columnData = COLUMN_DATA,
}) {
  const columns = useMemo(() => columnData, []);
  const [data, setData] = useState(initialValues);
  const dataTest = useMemo(() => data, []);

  const [info, setInfo] = useState();
  const { getTableProps, getTableBodyProps, headerGroups, rows, prepareRow } =
    useTable({ columns, data: dataTest });

  // const getWaitBuy = () => {
  //   dataTest.getWaitBuy().then(item => setInfo(item))
  // }
  // const dataTest = useMemo(() => info, [info])
  console.log(rows);
  return (
    <div className="ResultBox">
      <h1 className="ResultBox-heading">미체결(Buy)</h1>

      <table {...getTableProps()}>
        <thead>
          {headerGroups.map((headerGroup) => (
            <tr {...headerGroup.getHeaderGroupProps()}>
              {headerGroup.headers.map((column) => (
                <th {...column.getHeaderProps()}> {column.render("Header")}</th>
              ))}
            </tr>
          ))}
        </thead>
        <tbody {...getTableBodyProps()}>
          {rows.map((row) => {
            prepareRow(row);
            return (
              <tr {...row.getRowProps()}>
                {row.cells.map((cell) => {
                  return (
                    <td {...cell.getCellProps()}>{cell.render("Cell")}</td>
                  );
                })}
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
}

export default WaitBuyBox;
