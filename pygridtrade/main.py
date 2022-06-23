from grid_trade import *
import pyupbit

# 지정 파라미터

params = {}

params["MODE"] = "Arithmetic" # Arithmetic: 동일한 가격 차이 / Geometric: 동일한 가격 비율 차이
params["GRIDS"] = 5 # 그리드 수
params["COIN"] = "BTC" # 거래할 코인
params["BUDGET"] = 80000 # 사용할 예산
params["INTERVAL"] = 30 # 변동성 계산할 때 몇분 봉 사용
params["STD_NUM"] = 20 # 변동성 계산할 때 과거 몇개 사용
params["LOWER_STD"] = 6 # 하한선 변동성 배수
params["STOP_LOSS"] = 3 # 손절매 퍼센트
params["RESET_GRID"] = 3 # 계속 상승할 경우 그리드 리셋하는 퍼센트

if __name__ == "__main__":
    # trading bot 생성
    key_path = "upbit_key.txt"
    bot = trading_bot(key_path)
    
    # bot에 파라미터 넘겨주고 env 받기
    env = bot.set_env(**params)
    
    # 트레이딩 시작
    bot.trade(**env)