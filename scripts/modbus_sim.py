#!/usr/bin/env python3
"""Modbus TCP slave simulator for simple-iot demo. pymodbus 3.14 API.

Holding registers (function=3, address 1-based in pymodbus):
  1-2: temperature (int32, scale 0.1 -> e.g. 256 = 25.6C)
  3:   humidity (int16, scale 1 -> e.g. 45 = 45%)
  4:   pressure (int16, scale 0.1 -> e.g. 1013 = 101.3kPa)
  5:   switch (bool, 0/1)

Note: simple-iot ModbusFrame uses 0-based addressing, so platform config
should use address=0 for temp, address=2 for humidity, etc.
"""
import random, time, threading
from pymodbus.server import StartTcpServer
from pymodbus.datastore import ModbusServerContext, ModbusDeviceContext
from pymodbus.datastore import ModbusSequentialDataBlock

def run():
    block = ModbusSequentialDataBlock(1, [256, 0, 45, 1013, 1] + [0]*100)
    dev = ModbusDeviceContext(hr=block)
    ctx = ModbusServerContext(devices={1: dev}, single=False)

    print("Modbus simulator on 0.0.0.0:502, unit=1", flush=True)
    print("Registers: addr1-2=temp(25.6C) addr3=hum(45%) addr4=press(101.3kPa) addr5=sw(1)", flush=True)

    def updater():
        while True:
            time.sleep(5)
            temp = 200 + random.randint(0, 120)       # 20.0 - 32.0 C
            hum = 30 + random.randint(0, 40)           # 30 - 70 %
            press = 1000 + random.randint(0, 30)       # 100.0 - 103.0 kPa
            sw = random.choice([0, 1])
            # pymodbus 3.14: simdata[0].values is the register list
            v = block.simdata[0].values
            v[0] = temp & 0xFFFF
            v[1] = (temp >> 16) & 0xFFFF
            v[2] = hum
            v[3] = press
            v[4] = sw

    threading.Thread(target=updater, daemon=True).start()
    StartTcpServer(context=ctx, address=("0.0.0.0", 5020))

if __name__ == "__main__":
    run()
