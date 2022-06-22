import math
import time
import datetime

import pyupbit

# 호가 단위 함수
def get_price_scale_tick(_price):   
    if _price >= 2000000: 
        return -3, 1000
    elif _price >= 1000000: 
        return -2, 500
    elif _price >= 500000: 
        return -2, 100
    elif _price >= 100000: 
        return -1, 50
    elif _price >= 10000: 
        return -1, 10
    elif _price >= 1000: 
        return 0, 5
    elif _price >= 100: 
        return 0, 1
    elif _price >= 10: 
        return 1, 0.1
    elif _price >= 0: 
        return 2, 0.01

# grid level 생성
def make_levels(mode, upper, lower, grids):
    lower = pyupbit.get_tick_size(lower)
    upper = pyupbit.get_tick_size(upper)
    levels = [lower]
    cnt = 1
    # 동일한 가격 차이로 levels 생성
    if mode == "Arithmetic":
        while cnt < grids:
            diff =  (upper - lower)/(grids) # 주문들 사이의 가격 차이
            price = lower + diff * cnt
            price = pyupbit.get_tick_size(price) # 업비트 호가에 맞춰서 조정
            levels.append(price)
            cnt += 1
    # 동일한 가격 비율로 levels 생성
    elif mode == "Geometric":
        while cnt < grids:
            diff = (upper / lower) ** (1/(grids)) # 주문들 사이의 가격 비율 차이
            price = lower * (diff ** cnt)
            price = pyupbit.get_tick_size(price) # 업비트 호가에 맞춰서 조정
            levels.append(price)
            cnt += 1
    return levels, diff

# 과거 데이터 조회해서 변동성 계산
def get_std(coin, interval, std_num):
    df = pyupbit.get_ohlcv(f"KRW-{coin}", interval=f"minute{interval}")
    std = df.iloc[-std_num:].close.std()
    return std

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
        share = self.upbit.get_balance(coin)
        ret_stoploss = self.upbit.sell_market_order(f"KRW-{coin}", share)
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
    def get_evn(self, coin, mode, grids, budget, interval, std_num, lower_std):
        # 현재가 조회
        current = pyupbit.get_current_price(f"KRW-{coin}")

        # 잔고 조회
        balance_KRW = self.upbit.get_balance("KRW")
        if balance_KRW < budget:
            raise Exception("현금 보유량이 예산보다 적습니다.")
        
        balance_coin = self.upbit.get_balance(coin)
        if balance_coin != 0:
            raise Exception("코인 수가 0이 아닙니다.")

        # 호가 단위
        _, scale = get_price_scale_tick(current)

        # 변동성 계산
        std = get_std(coin, interval, std_num)
        
        # 상한선 하한선 계산
        upper, lower = get_boundary(current, std, lower_std)
        
        # grid levels 생성
        levels, diff = make_levels(mode, upper, lower, grids)
        
        # 주문량 생성
        volume = get_volume(current, budget, grids)
        
        print(datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S'))
        print()
        print(f"{coin} 현재가(KRW):",current)
        print("호가단위(KRW):", scale)
        print(f"{interval}분봉 {std_num}개 변동성: {std:.2f}")
        print(f"Lower Std: {lower_std}")
        print("Levels:", levels)
        print(f"Volume({coin}):", volume)
        
        if mode == "Arithmetic":
            print(f"Difference(KRW): {diff:.4f}")
            print(f"수익률: {diff/current*100:.2f}%")
        else:
            print(f"Difference(%): {(diff-1)*100:.2f}%")
            print(f"수익률: {(diff-1)*100:.2f}%")
        
        return levels, diff, volume