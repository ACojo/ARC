#! /usr/bin/python3

import sys
import subprocess
import RPi.GPIO as GPIO
import time
from threading import Thread, Lock


class Motor_control:

        def __init__(self):
                self.lock = Lock()
                self.lock.acquire(False , -1)
                self.current_pwm = 0
                self.pin = 12
                self.cw_pin = 24
                self.ccw_pin = 23
                self.rotation = 0 # by default the rotation is cw
                ###############
                ## TODO must add the pins for the motor
                #GPIO.setmode(GPIO.BCM)
                #GPIO.setup(self.pin, GPIO.OUT)
                #self.pwm_motor = GPIO.PWM(self.pin, 2000)
                #self.pwm_motor.start(0)

        def change_duty_cycle(self):
                self.pwm_motor.ChangeDutyCycle(self.current_pwm)



        def change_rotation(self):
                if self.rotation == 0:
                        GPIO.output(self.cw_pin, 1)
                        GPIO.output(self.ccw_pin, 0)
                else:
                        GPIO.output(self.cw_pin, 0)
                        GPIO.output(self.ccw_pin, 1)






def motor_loop(motor):
        while True:
                motor.change_duty_cycle()

def change_pmw(motor , new_pwm_value):
        motor.current_pwm = new_pwm_value
