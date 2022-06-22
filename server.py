#! /usr/bin/python3

from threading import Thread
from multiprocessing import Process
import subprocess
from flask import Flask, request
from rgb_led import *
from motor_control import *
#from time import sleep
import time
import RPi.GPIO as GPIO
import os


led = RGB_led()
motor = Motor_control()
app = Flask(__name__)



def setup():
        #the motor setup
        GPIO.setmode(GPIO.BCM)
        GPIO.setup(motor.pin, GPIO.OUT)
        GPIO.setup(motor.cw_pin, GPIO.OUT, initial = 1)
        GPIO.setup(motor.ccw_pin, GPIO.OUT, initial = 0)
        motor.pwm_motor = GPIO.PWM(motor.pin, 2000)
        motor.pwm_motor.start(0)
        #the rgb led setup
        for i in led.pins:
                GPIO.setup(led.pins[i],GPIO.OUT)
                print(led.pins[i])


# for the url
@app.route('/led/' , methods=[ 'GET', 'PUT' ])
def index_rgb():
        global led
        if request.method == "GET" :
                return str(led.r_value) + " " + str(led.g_value) + " " + str(led.b_value)
        if request.method == "PUT" :
                #print(type(request.get_data()))
                data = request.get_data().decode('UTF-8')
                data = data.split("&")
                print(data)
                g_value = data[0].split("=")[1]
                r_value  = data[1].split("=")[1]
                b_value = data[2].split("=")[1]

                led.r_value = int( r_value )
                led.g_value = int( g_value )
                led.b_value = int( b_value )

                return "HELLO " + str(led.r_value) + str(led.g_value) + str(led.b_value)
        else:
                return "NOT WELCOME"


@app.route('/ac_motor/' , methods=['PUT', 'GET'])
def index_motor():
        global motor
        if request.method == "GET":
                return str(motor.current_pwm) + " " + str(motor.rotation)
        if request.method == "PUT":
                data = request.get_data().decode('UTF-8')
                data = data.split("&")
                rotation_data_value = False
                rotation_data_value = False

                if int(data[0].split("=")[1]) != motor.current_pwm:
                        motor.current_pwm = int(data[0].split("=")[1])
                        motor.change_duty_cycle()
                if data[1].split("=")[1] == "true":
                        rotation_data_value = True

                if motor.rotation != rotation_data_value:
                        motor.rotation = rotation_data_value
                        motor.change_rotation()
                        print(rotation_data_value)

                return  "HELLO MOTOR"
        else:
                return "NOT WELCOME"




@app.route('/',methods = ['GET'])
def test():
        return "GOOD"



if __name__ == "__main__":
        setup()

        th_server = Thread(target = app.run, args = ('0.0.0.0',))

        t_rgb = Thread(target = rgb_loop , args = (led,))

        th_server.start()

        t_rgb.start()

        x = input()
        if x == 'x':
                print("BYE")
                os._exit(0)
