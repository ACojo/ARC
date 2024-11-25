#! /usr/bin/python3





from multiprocessing import Process
import sys
import RPi.GPIO as GPIO
from time import sleep
from threading import Thread, Lock

pins = {  'pin_r' : 16  ,'pin_g': 20 , 'pin_b' : 21 }


class RGB_led():

        def __init__(self):
                self.lock = Lock()
                self.lock.acquire(False , -1)
                self.pins = { 'pin_r' : 16   ,'pin_g': 20 , 'pin_b' : 21 }
                #the current values

                self.r_value = 0
                self.g_value = 0
                self.b_value = 0



def soft_pwm(led, opt):
                if  opt == 'r':
                        GPIO.output(led.pins['pin_r'], GPIO.HIGH)
                        sleep( led.r_value /100 * 0.02)
                        GPIO.output(led.pins['pin_r'], GPIO.LOW)
                        sleep(0.02 -  led.r_value /100 * 0.02)
                elif opt ==  'g':
                        GPIO.output(led.pins['pin_g'], GPIO.HIGH)
                        sleep( led.g_value /100 * 0.02)
                        GPIO.output(led.pins['pin_g'], GPIO.LOW)
                        sleep(0.02 -  led.g_value /100 * 0.02)
                elif opt == 'b':
                        GPIO.output(led.pins['pin_b'], GPIO.HIGH)
                        sleep( led.b_value /100 * 0.02)
                        GPIO.output(led.pins['pin_b'], GPIO.LOW)
                        sleep(0.02 -  led.b_value /100 * 0.02)
                else:
                        print("AN ERROR OCCURED")






def rgb_loop(led):
        while True:
                r_thread = Thread(target = soft_pwm , args = (led,'r',))
                g_thread = Thread(target = soft_pwm , args = (led,'g',))
                b_thread = Thread(target = soft_pwm , args = (led,'b',))
                #keeping the current setting

                r_thread.start()
                g_thread.start()
                b_thread.start()


                r_thread.join()
                g_thread.join()
                b_thread.join()
                #wait 1 second
                #sleep(0.021)
                #print(led.r_value , led.g_value, led.b_value)




def change_value(led,r_v , g_v , b_v):
        led.r_value = r_v
        led.g_value = g_v
        led.b_value = b_v



def pin_setup():
        global pwm_r , pwm_g, pwm_b
        GPIO.setmode(GPIO.BCM)
        for i in pins:
                GPIO.setup(pins[i], GPIO.OUT)
                GPIO.output(pins[i], GPIO.HIGH)
                sleep(1)
                GPIO.output(pins[i], GPIO.LOW)



def change_duty_cycle(r_value , g_value , b_value):
        pwm_r.ChangeDutyCycle(r_value)
        pwm_g.ChangeDutyCycle(g_value)
        pwm_b.ChangeDutyCycle(b_value)





if __name__ == '__main__':
        led = RGB_led()
        #led.pin_setup()
        print("finished setup")

        t_rgb = Thread(target = rgb_loop , args = (led,))
        t_modif =Thread(target =  change_value , args = (led, 100 , 100, 100,))
        t_modif2 =Thread(target =  change_value , args = (led, 0 , 0, 0,))
        t_modif3 =Thread(target = change_value , args = (led, 100 , 0,0,))
        t_modif4 =Thread(target = change_value , args = (led, 7 , 14,77,))


        t_rgb.start()
        sleep(3)
        print("rgb led  th started")

        t_modif.start()

        print(led.r_value)
        print(led.g_value)
        print(" ")
        sleep(3)


        t_modif2.start()
        print(led.r_value)
        print(led.g_value)
        print(" ")
        sleep(3)

