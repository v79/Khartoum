## Khartoum

Experimenting with Waveshare ePaper for the Raspberry Pi

![badge][badge-linux]

Writing code in Kotlin Native to control and display images and text on a Waveshare ePaper HAT for the Raspbery Pi.

Makes use of the [bcm2835](https://www.airspayce.com/mikem/bcm2835/index.html) C library for hardware control. It recreates some of the [Waveshare GUI C](https://github.com/waveshare/e-Paper/tree/master/RaspberryPi%26JetsonNano/c) example functions, in Kotlin, in a library I am calling *Khartoum*. So far, the following functions are available:
