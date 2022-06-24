import CreatSocketConnection from "./connect";
import DoneBox from "./DoneBox";
import WaitBuyBox from "./WaitBuyBox";
import WaitSellBox from "./WaitSellBox";

function ResultPage() {
  return (
    <div>
      <div className="ResultPage-head">
        현재가
        <CreatSocketConnection />
      </div>
      <div className="ResultPage-row">
        <WaitBuyBox />
        <WaitSellBox />
        <DoneBox />
      </div>
    </div>
  );
}

export default ResultPage;
