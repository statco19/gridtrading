import "./App.css";
import Box from "./Box";
import ChartBox from "./ChartBox";
import CreatSocketConnection from "./connect";

function App() {
  // let payload = 0;
  // payload = creatSocketConnection("ticker");
  // console.log(payload);
  return (
    <div className="App">
      <div>
        <h1 className="App-title">Grid Trading</h1>
      </div>
      <div className="Container">
        <div className="Column-left">
          BTC 현재가
          <CreatSocketConnection type="ticker" />
          <ChartBox />
        </div>
        <div className="Column-right">
          <Box />
        </div>
      </div>
    </div>
  );
}

export default App;
