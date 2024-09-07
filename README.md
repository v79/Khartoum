## Khartoum

Experimenting with Waveshare ePaper for the Raspberry Pi

Writing code in Kotlin Native to control and display images and text on a Waveshare ePaper HAT for the Raspbery Pi.

Makes use of the [bcm2835](https://www.airspayce.com/mikem/bcm2835/index.html) C library for hardware control. It recreates some of the [Waveshare GUI C](https://github.com/waveshare/e-Paper/tree/master/RaspberryPi%26JetsonNano/c) example functions, in Kotlin, in a library I am calling *Khartoum*. So far, the following functions are available:

The __Khartoum__ binary must be run as root (`sudo ./Khartoum.kexe`) on the Raspberry Pi.


### Libraries used

- Kotlinx serialization
- Kotlinx datetime
- Native c-libraries:
  - libCurl
  - [bcm2835](https://www.airspayce.com/mikem/bcm2835/index.html) C library

### Links and references
