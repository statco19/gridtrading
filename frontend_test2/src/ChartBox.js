import ApexCharts from "apexcharts";
import { useEffect, useState } from "react";
import ReactApexChart from "react-apexcharts";
// import { ChartDraw } from "./api";
import ChartDraw from "./api";

const INITIAL_VALUES = {
  random: {
    candle_date_time_kst: "2018-04-18T19:16:00",
    low_price: 90,
    high_price: 100,
    opening_price: 0,
    trade_price: 70,
  },
};

function DateConvert(date) {
  let tmp = new Date(date);
  const utc = tmp.getTime() + tmp.getTimezoneOffset() * 60 * 1000;
  const KR_TIME_DIFF = 9 * 60 * 60 * 1000;
  const kr_curr = new Date(utc + KR_TIME_DIFF);
  return kr_curr;
}

function ChartBox(initialValues = INITIAL_VALUES) {
  const [isLoading, setIsLoading] = useState(false);
  const [loadingError, setLoadingError] = useState(null);
  const [dataTest, setDataTest] = useState([{ initialValues }]);
  const data = [
    {
      time_close: "2018-04-18T19:16:00",
      open: 8615000,
      high: 8618000,
      low: 8611000,
      close: 8611000,
    },
  ];

  const asyncTest = async () => {
    const options = { method: "GET", headers: { Accept: "application/json" } };
    fetch(
      "https://api.upbit.com/v1/candles/minutes/1?market=KRW-BTC&count=30",
      options
    )
      .then((response) => response.json())
      .then((result) => {
        console.log(result);
        setDataTest(result);
      })
      .catch((err) => console.error(err));
    console.log(dataTest);
  };

  useEffect(() => {
    asyncTest();
  }, []);

  return (
    <div style={{ width: 550 }}>
      {loadingError && <p>{loadingError.message}</p>}
      <ReactApexChart
        type="candlestick"
        options={{
          tooltip: {
            theme: "dark",
          },
          // theme: {
          //   mode: "dark",
          // },
          chart: { type: "candlestick", width: 550 },
          xaxis: {
            type: "datetime",
            categories: data?.map((price) =>
              DateConvert(price.candle_date_time_kst)
            ),
          },
        }}
        series={[
          {
            data: dataTest?.map((price) => {
              return [
                // DateConvert(price.candle_date_time_kst),
                Date.parse(price.candle_date_time_kst),
                price.opening_price,
                price.high_price,
                price.low_price,
                price.trade_price,
              ];
            }),
          },
        ]}
      />
    </div>
  );
}

export default ChartBox;
