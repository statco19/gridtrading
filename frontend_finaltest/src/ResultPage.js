import CreatSocketConnection from "./connect";
import DoneBox from "./DoneBox";
import WaitBuyBox from "./WaitBuyBox";
import WaitSellBox from "./WaitSellBox";

function ResultPage() {
  return (
    <div>
      <div className="ResultPage-head">
        <div className="ResultPage-head-name">현재가</div>
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
