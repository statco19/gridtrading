import math
import time
import requests
import datetime

import pyupbit

# 호가 단위 함수
def get_price_scale_tick(_price):
    if _price >= 2000000: 
        return 1000
    elif _price >= 1000000: 
        return 500
    elif _price >= 500000: 
        return 100
    elif _price >= 100000: 
        return 50
    elif _price >= 10000: 
        return 10
    elif _price >= 1000: 
        return 5
    elif _price >= 100: 
        return 1
    elif _price >= 10: 
        return 0.1
    elif _price >= 0: 
        return 0.01

def get_tick_size(price, method="floor"):
    """원화마켓 주문 가격 단위 

    Args:
        price (float]): 주문 가격 
        method (str, optional): 주문 가격 계산 방식. Defaults to "floor".

    Returns:
        float: 업비트 원화 마켓 주문 가격 단위로 조정된 가격 
    """

    if method == "floor":
        func = math.floor
    elif method == "round":
        func = round 
    else:
        func = math.ceil 

    if price >= 2000000:
        tick_size = func(price / 1000) * 1000
    elif price >= 1000000:
        tick_size = func(price / 500) * 500
    elif price >= 500000:
        tick_size = func(price / 100) * 100
    elif price >= 100000:
        tick_size = func(price / 50) * 50
    elif price >= 10000:
        tick_size = func(price / 10) * 10
    elif price >= 1000:
        tick_size = func(price / 5) * 5
    elif price >= 100:
        tick_size = func(price / 1) * 1
    elif price >= 10:
        tick_size = func(price / 0.1) / 10
    elif price >= 1:
        tick_size = func(price / 0.01) / 100
    elif price >= 0.1:
        tick_size = func(price / 0.001) / 1000
    else:
        tick_size = func(price / 0.0001) / 10000

    return tick_size

# grid level 생성
def make_levels(mode, upper, lower, grids):
    lower = get_tick_size(lower)
    upper = get_tick_size(upper)
    levels = [lower]
    cnt = 1
    # 동일한 가격 차이로 levels 생성
    if mode == "Arithmetic":
        while cnt < grids:
            diff =  (upper - lower)/(grids) # 주문들 사이의 가격 차이
            price = lower + diff * cnt
            price = get_tick_size(price) # 업비트 호가에 맞춰서 조정
            levels.append(price)
            cnt += 1
    # 동일한 가격 비율로 levels 생성
    elif mode == "Geometric":
        while cnt < grids:
            diff = (upper / lower) ** (1/(grids)) # 주문들 사이의 가격 비율 차이
            price = lower * (diff ** cnt)
            price = get_tick_size(price) # 업비트 호가에 맞춰서 조정
            levels.append(price)
            cnt += 1

    # levels의 마지막은 그리드 차이
    levels.append(diff)
    return levels

# 과거 데이터 조회해서 변동성 계산
def get_std(coin, interval, std_num):
    # API
    url = f"https://api.upbit.com/v1/candles/minutes/{interval}?market=KRW-{coin}&count={std_num}"
    headers = {"Accept": "application/json"}
    response = requests.get(url, headers=headers)
    
    price_list = []

    for x in response.json():
        price_list.append(x["trade_price"])

    n = len(price_list)
    m = sum(price_list) / n
    ss = sum((x-m)**2 for x in price_list)
    pvar = ss/n
    return pvar**0.5

# 상한선 하한선 계산
def get_boundary(current, std, lower_std):
    upper = current 
    lower = current - lower_std*std
    if current < upper:
        raise Exception("상한선은 현재가보다 낮아야합니다.")
    return upper, lower

# 주문량 계산
def get_volume(current, budget, grids):
    volume = math.floor((budget / grids)/current*1e8)/1e8 # 업비트는 소수 8자리까지 주문 가능
    if volume * current < 5000: # 최소 주문 단위 5000보다 작을 경우 error
        raise Exception(f"최소 주문 단위보다 작습니다. {volume * current:.2f}원")
    return volume

# 현재가 정보 조회
def get_current_price(ticker="KRW-BTC"):
    url = f"https://api.upbit.com/v1/trades/ticks?market={ticker}&count=1"
    headers = {"Accept": "application/json"}
    response = requests.get(url, headers=headers)
    price = response.json()[0]["trade_price"]
    return price

class trading_bot:
    def __init__(self, key_path):
        self.upbit = self.connect_upbit(key_path)

    # upbit 연결
    def connect_upbit(self, key_path):
        with open(key_path, 'r') as f:
            lines = f.read().split('\n')
            access_key = lines[0]
            secret_key = lines[1]
        upbit = pyupbit.Upbit(access_key, secret_key)
        try:
            balances = upbit.get_balances()
            print("연결성공")
            print(balances)
            return upbit
        except:
            print("연결실패")

    # 미체결 주문 모두 취소
    def cancel_all_order(self, coin):
        order_list = self.upbit.get_order(f"KRW-{coin}")
        for order in order_list:
            ret_cancel = self.upbit.cancel_order(order["uuid"])
            if 'error' in ret_cancel.keys():
                print(ret_cancel['error']['message'])
            time.sleep(0.1)
            
    # 코인 보유량 모두 시장가 매도
    def sell_all_market(self, coin):
        volume = self.upbit.get_balance(coin)
        ret_stoploss = self.upbit.sell_market_order(f"KRW-{coin}", volume)
        # 에러 발생시 message 출력
        if 'error' in ret_stoploss.keys():
            print(ret_stoploss['error']['message'])
            
    # 잔고 중 해당하는 코인의 평단가 불러오기
    def get_avg(self, coin):
        balances_list = self.upbit.get_balances()
        for balances in balances_list:
            if balances["currency"] == coin: 
                avg_price = float(balances["avg_buy_price"])
                return avg_price
        return None

    # 미체결 주문들의 가격 불러오기
    def get_open_price(self, coin):
        bid_list = []
        ask_list = []    
        for order in self.upbit.get_order(f"KRW-{coin}"):
            if order["side"] == 'bid':
                bid_list.append(order["price"])
            else:
                ask_list.append(order["price"])            
        open_dict = {"bid" : sorted(bid_list), "ask" : sorted(ask_list)}    
        return open_dict

    # 초기 매수 주문
    def levels_order(self, coin, volume, levels):
        for price in levels:
            ret = self.upbit.buy_limit_order(f"KRW-{coin}", price, volume) # 지정된 가격에 매수 주문
            # 에러 발생시 message 출력
            if 'error' in ret.keys():
                print(ret['error']['message'])
            time.sleep(0.1)
            
    # 초기 환경 세팅
    def set_env(self, **params):
        env = params.copy()

        # 동적 변수 할당
        for key, value in params.items():
            globals()[key] = value

        # 현재가 조회
        current = get_current_price(f"KRW-{COIN}")

        # 잔고 조회
        balance_KRW = self.upbit.get_balance("KRW")
        if balance_KRW < BUDGET:
            raise Exception("현금 보유량이 예산보다 적습니다.")
        
        balance_coin = self.upbit.get_balance(COIN)
        if balance_coin != 0:
            raise Exception("코인 수가 0이 아닙니다.")

        # 호가 단위
        scale = get_price_scale_tick(current)
        env['scale'] = scale

        # 변동성 계산
#         std = get_std(COIN, INTERVAL, STD_NUM)
#         env['std'] = std
        
        # 상한선 하한선 계산
        upper, lower = get_boundary(current, std, LOWER_STD)
        env['upper'] = upper  # env['upper'] = current
#         env['lower'] = lower
        
        # grid levels 생성
        levels= make_levels(MODE, upper, lower, GRIDS)
        diff = levels.pop()
        env['levels'] = levels
        env['diff'] = diff

        # 주문량 생성
        volume = get_volume(current, BUDGET, GRIDS)
        env['volume'] = volume

        print(datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S'))
        print()
        print(f"{COIN} 현재가(KRW):",current)
        print("호가단위(KRW):", scale)
        print(f"{INTERVAL}분봉 {STD_NUM}개 변동성: {std:.2f}")
        print(f"Lower Std: {LOWER_STD}")
        print("Levels:", levels)
        print(f"Volume({COIN}):", volume)
        
        if MODE == "Arithmetic":
            print(f"Difference(KRW): {diff:.4f}")
            print(f"수익률: {diff/current*100:.2f}%")
        else:
            print(f"Difference(%): {(diff-1)*100:.2f}%")
            print(f"수익률: {(diff-1)*100:.2f}%")
    
        return env

    def trade(self, **env):

        # 동적 변수 할당
        for key, value in env.items():
            globals()[key] = value

        # 전체 주문 취소
        self.cancel_all_order(COIN)

        # 초기 매수 주문
        self.levels_order(COIN, volume, levels)
        print(self.get_open_price(COIN))

        while True:
            time.sleep(0.5) # 0.5초마다 업데이트
            
            current = get_current_price(f"KRW-{COIN}") # 현재 코인 가격
            avg_price = self.get_avg(COIN) # 현재 평단가 가져오기
            
            
            # 수익률이 STOP_LOSS 보다 작을 경우 모두 시장가로 청산
            if avg_price != None:
                # 현재 가격과 비교해서 수익률 계산
                profit = (current-avg_price)/avg_price*100
                if profit < -STOP_LOSS:
                    print("**STOP LOSS 실행**")
                    print(f"{COIN} 현재가(KRW): {current}")
                    print(f"{COIN} 평단가(KRW): {avg_price}")
                    print(f"현재 손익: {profit:.2f}%")
                    
                    # 모든 주문 취소
                    self.cancel_all_order(COIN)
                    # 모든 코인 시장가 매도
                    self.sell_all_market(COIN)
                    
                    print("종료")
                    break
                    
                    
            # 가격이 일정 기준 이상 오르면 GRID 리셋
#             if (current-levels[-1])/levels[-1]*100 > RESET_GRID:
#                 print("**GRID 리셋**")
#                 # 모든 주문 취소
#                 self.cancel_all_order(COIN)
#
#                 # 현재 가격에 맞춰서 levels 다시 설정
#                 env = self.set_env(COIN, MODE, GRIDS, BUDGET, INTERVAL, STD_NUM, LOWER_STD)
#
#                 # 동적 변수 할당
#                 for key, value in env.items():
#                     globals()[key] = value
#
#                 # 초기 매수 주문 다시 넣기
#                 self.levels_order(COIN, volume, levels)
            
            
            # 미체결 주문수가 GRIDS와 다르면 추가 주문
            now_open = len(self.upbit.get_order(f"KRW-{COIN}")) # 현재 미체결 주문수
            if GRIDS != now_open:
                balance_coin = self.upbit.get_balance(COIN) # 코인 보유량으로 매수 매도 판단
                last_price = get_current_price(f"KRW-{COIN}") # 최근 체결 가격
                
                if balance_coin != 0: # 보유량이 0이 아니면 최근 체결은 매수
                    side = "bid"
                    if MODE == "Arithmetic": # MODE가 Arithmetic이면 목표가는 diff 만큼 더한 값
                        price = last_price + diff 
                    else: # MODE가 GEOMETRIC이면 매도 목표가는 diff 만큼 곱한 값
                        price = last_price * diff 
                    price = get_tick_size(price)
                    ret = self.upbit.sell_limit_order(f"KRW-{COIN}", price, volume)
                    
                else: # 보유량이 0이면 최근 체결은 매도
                    side = "ask"
                    if MODE == "Arithmetic": # MODE가 Arithmetic이면 목표가는 diff 만큼 뺀 값
                        price = last_price - diff 
                    else: # MODE가 GEOMETRIC이면 매도 목표가는 diff 만큼 나눈 값
                        price = last_price / diff
                    price = get_tick_size(price)
                    ret = self.upbit.buy_limit_order(f"KRW-{COIN}", price, volume)
                
                # 에러 발생시 message 출력
                if 'error' in ret.keys():
                    print(ret['error']['message'])
                    
                # 최근 체결 정보 표시
                else:
                    print("")
                    print(datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S'))
                    print(f"{side} -> {last_price}KRW  {volume}{COIN} 체결")
                    print(self.get_open_price(COIN))