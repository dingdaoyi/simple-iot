#!/usr/bin/env python3
"""Modbus TCP slave simulator for simple-iot demo.

Raw socket implementation - no pymodbus dependency needed.
Responds to Read Holding Registers (function code 3) only.

Registers (0-based address):
  0-1: temperature (int32 LE, scale 0.1 -> e.g. 256 = 25.6C)
  2:   humidity (int16, scale 1 -> e.g. 45 = 45%)
  3:   pressure (int16, scale 0.1 -> e.g. 1013 = 101.3kPa)
  4:   switch (bool, 0/1)
"""
import random, struct, threading, time
from socketserver import ThreadingTCPServer, BaseRequestHandler

REGS = [256, 0, 45, 1013, 1] + [0]*100

def updater():
    while True:
        time.sleep(5)
        temp = 200 + random.randint(0, 120)       # 20.0 - 32.0 C
        hum = 30 + random.randint(0, 40)           # 30 - 70 %
        press = 1000 + random.randint(0, 30)       # 100.0 - 103.0 kPa
        sw = random.choice([0, 1])
        REGS[0] = temp & 0xFFFF
        REGS[1] = (temp >> 16) & 0xFFFF
        REGS[2] = hum
        REGS[3] = press
        REGS[4] = sw

class Handler(BaseRequestHandler):
    def handle(self):
        try:
            while True:
                req = self.request.recv(12)
                if len(req) < 12:
                    break
                tx_id, proto, length, unit, fc, addr, qty = struct.unpack(">HHHBBHH", req)
                if fc != 3:
                    # exception: illegal function
                    resp = struct.pack(">HHHB BHH", tx_id, 0, 3, unit, 0x83, 1, 0)
                    self.request.sendall(resp)
                    continue
                vals = REGS[addr:addr+qty]
                byte_count = len(vals) * 2
                header = struct.pack(">HHHBBB", tx_id, 0, 3+byte_count, unit, 3, byte_count)
                body = b"".join(struct.pack(">H", v) for v in vals)
                self.request.sendall(header + body)
        except (ConnectionResetError, BrokenPipeError):
            pass

if __name__ == "__main__":
    threading.Thread(target=updater, daemon=True).start()
    with ThreadingTCPServer(("0.0.0.0", 5020), Handler) as srv:
        srv.serve_forever()
