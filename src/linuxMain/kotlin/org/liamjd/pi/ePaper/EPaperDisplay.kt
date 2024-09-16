package org.liamjd.pi.ePaper

import kotlinx.cinterop.ExperimentalForeignApi
import libbcm.*
import org.liamjd.pi.bcm.*
import org.liamjd.pi.console.printDebug
import platform.posix.uint8_t

@ExperimentalForeignApi
@ExperimentalUnsignedTypes
class EPaperDisplay(val model: EPDModel) : EPaperDisplayCommands {

    val uint8_ZERO: uint8_t = 0u
    val uint8_ONE: uint8_t = 1u
    val buttonActions: MutableMap<uint8_t, () -> Unit> = mutableMapOf()
    private val bcmLOW = LOW.toUByte()

    init {
        printDebug("Call GPIOInit")
        GPIOInit()

        printDebug("\tBegin SPI Interface")
        bcm2835_spi_begin()  //Start spi interface, set spi pin for the reuse function
        printDebug("\tSet SPI Bit Order ${SPIBitOrder.MSB_FIRST}")
        bcm2835_spi_setBitOrder(SPIBitOrder.MSB_FIRST.value)     //High first transmission
        printDebug("\tSet SPI Data mode ${SPIMode.MODE_0}")
        bcm2835_spi_setDataMode(SPIMode.MODE_0.value)                  //spi mode 0
        printDebug("\tSet SPI Clock divider ${SPIClockDivider.DIVIDER_128}")
        bcm2835_spi_setClockDivider(SPIClockDivider.DIVIDER_128.value)  //Frequency
        printDebug("\tSet SPI ChipSelect ${SPIChipSelect.CS0}")
        bcm2835_spi_chipSelect(SPIChipSelect.CS0.value)                     //set CE0
        printDebug("\tEnable SPI ChipSelect Polarity ${SPIChipSelect.CS0}")
        bcm2835_spi_setChipSelectPolarity(SPIChipSelect.CS0.value, bcmLOW)     //enable cs0

        initializeModel()
        printDebug("Model initialized; ready to display")
    }

    /**
     * Initialize the ePaper device, making it ready to receive commands and display images
     */
    private fun initializeModel() {
        printDebug("Initializing model $model")
        when (model) {
            EPDModel.TWO_IN7_B -> {
                reset()
                printDebug("Sending initialization commands:")
                //https://github.com/waveshareteam/e-Paper/blob/a824b4f8f34dee7e721183c0154788dcde41c460/RaspberryPi_JetsonNano/c/lib/e-Paper/EPD_2in7b.c#L237

                sendCommand(0x06u)         //boost soft start
                sendData(0x07u)        //A
                sendData(0x07u)        //B
                sendData(0x17u)        //C

                sendCommand(0x04u)
                readBusy()//waiting for the electronic paper IC to release the idle signal

                sendCommand(0x00u)           //panel setting
                sendData(0x0fu)        //LUT from OTP￡?128x296

                sendCommand(0x16u)
                sendData(0x00u)               //KW-BF   KWR-AF	BWROTP 0f

                sendCommand(0xF8u)         //boostéè?¨
                sendData(0x60u)
                sendData(0xa5u)

                sendCommand(0xF8u)         //boostéè?¨
                sendData(0x90u)
                sendData(0x00u)

                sendCommand(0xF8u)         //boostéè?¨
                sendData(0x93u)
                sendData(0x2Au)

                sendCommand(0x01u) // PANEL_SETTING
                sendData(0x03u) // VDS_EN, VDG_EN
                sendData(0x00u) // VCOM_HV, VGHL_LV[1], VGHL_LV[0]
                sendData(0x2bu) // VDH
                sendData(0x2bu) // VDL
                sendData(0x2bu) // VDHR

                readBusy()
                delay(150u)

            }

            EPDModel.TWO_IN7 -> {
                TODO("Not yet implemented")
            }
        }
        printDebug("Initialization complete")

        // when this function is called, the linux terminal stops displaying text, the screen continues to refresh, then the pi crashes
        initializeKeys()
    }

    override fun clear() {
        printDebug("Clearing displays")
        val width: Int = if (model.pixelWidth % 8 == 0) model.pixelWidth / 8 else model.pixelWidth / 8 + 1
        val height: Int = model.pixelHeight

        when (model) {
            EPDModel.TWO_IN7_B -> {
                // clear black
                printDebug("\tClearing black image")
                sendCommand(0x10u)
                for (j in 0 until height) {
                    for (i in 0 until width) {
                        sendData(0X00u)
                    }
                }
                sendCommand(0x11u) // DATA_STOP

                // clear red
                printDebug("\tClearing red image")
                sendCommand(0x13u)
                for (j in 0 until height) {
                    for (i in 0 until width) {
                        sendData(0x00u)
                    }
                }
                sendCommand(0x11u) // DATA_STOP

                sendCommand(0x12u)
                readBusy()
            }

            EPDModel.TWO_IN7 -> {
                TODO("Not yet implemented")
            }
        }
        printDebug("Clearing complete")
    }

    override fun display(images: Array<UByteArray>) {
        val width: Int = if (model.pixelWidth % 8 == 0) model.pixelWidth / 8 else model.pixelWidth / 8 + 1
        val height: Int = model.pixelHeight

        when (model) {
            EPDModel.TWO_IN7_B -> {
                require(images.size == 2) {
                    "Model $model requires two images; black and red"
                }
                sendCommand(0x10u)

                if(images[0].isEmpty()) {
                    printDebug("No black image supplied")
                    return
                }
                printDebug("Displaying black image")
                for (j in 0 until height) {
                    for (i in 0 until width) {
                        sendData(images[0][i + j * width])
                    }
                }
                sendCommand(0x11u) // DATA_STOP

                if(images[1].isEmpty()) {
                    printDebug("No red image supplied")
                    return
                }
                printDebug("Displaying red image")
                sendCommand(0x13u)
                for (j in 0 until height) {
                    for (i in 0 until width) {
                        sendData(images[1][i + j * width])
                    }
                }
                sendCommand(0x11u) // DATA_STOP
                sendCommand(0x12u)
                readBusy()
            }

            EPDModel.TWO_IN7 -> {
                require(images.size == 1) {
                    "Model $model can only display one image"
                }
                TODO("Not yet implemented")
            }
        }
    }

    override fun sleep() {
        when (model) {
            EPDModel.TWO_IN7_B -> {
                printDebug("Going to sleep")
                sendCommand(0x50u)
                sendData(0xf7u)
                sendCommand(0x02u) //power off
                sendCommand(0x07u) //deep sleep
                sendData(0xA5u)
                delay(2000u) // wait for the device to sleep, apparently important
            }

            EPDModel.TWO_IN7 -> {
                TODO("Not yet implemented")
            }
        }
    }

    override fun sendData(reg: UByte) {
        when (model) {
            EPDModel.TWO_IN7_B -> {
                //https://github.com/waveshareteam/e-Paper/blob/a824b4f8f34dee7e721183c0154788dcde41c460/RaspberryPi_JetsonNano/c/lib/e-Paper/EPD_2in7b_V2.c#L67
                digitalWrite(model.pins.dataHighCommandLow, 1u)
                digitalWrite(model.pins.chipSelect, 0u)
                spiWriteByte(reg)
                digitalWrite(model.pins.chipSelect, 1u)
            }

            EPDModel.TWO_IN7 -> {
                TODO("Not yet implemented")
            }
        }
    }

    override fun sendCommand(cmd: UByte) {
        when (model) {
            EPDModel.TWO_IN7_B -> {
                //https://github.com/waveshareteam/e-Paper/blob/a824b4f8f34dee7e721183c0154788dcde41c460/RaspberryPi_JetsonNano/c/lib/e-Paper/EPD_2in7b_V2.c#L54
                digitalWrite(model.pins.dataHighCommandLow, 0u)
                digitalWrite(model.pins.chipSelect, 0u)
                spiWriteByte(cmd)
                digitalWrite(model.pins.chipSelect, 1u)
            }

            EPDModel.TWO_IN7 -> {
                TODO("Not yet implemented")
            }
        }
    }

    override fun delay(ms: UInt) {
        platform.posix.sleep(ms / 1000u)
        bcm2835_delay(ms)
    }

    override fun shutdown() {
        clear()
        sleep()
        exit()
    }

    override fun exit() {
        printDebug("Shutting down interface")
        digitalWrite(model.pins.chipSelect, LOW.toUByte())
        digitalWrite(model.pins.power, LOW.toUByte())
        digitalWrite(model.pins.dataHighCommandLow, LOW.toUByte())
        digitalWrite(model.pins.reset, LOW.toUByte())

        bcm2835_spi_end()
        bcm2835_close()
    }

    /**
     * Initialize the ePaper device GPIO pins
     */
    private fun GPIOInit() {
        printDebug("Initializing pins")
        setPinMode(model.pins.reset, 1u)
        setPinMode(model.pins.dataHighCommandLow, 1u)
        setPinMode(model.pins.chipSelect, 1u)
        setPinMode(model.pins.busy, 0u)
        setPinMode(model.pins.power, 1u)
        digitalWrite(model.pins.chipSelect, 1u)
        digitalWrite(model.pins.power, 1u)
    }

    override fun setPinMode(pin: UByte, mode: UByte) {
        if (mode == uint8_ZERO || mode == FunctionSelect.INPUT.value) {
            bcm2835_gpio_fsel(pin, FunctionSelect.INPUT.value)
        } else {
            bcm2835_gpio_fsel(pin, FunctionSelect.OUTPUT.value)
        }
    }

    override fun reset() {
        printDebug("Resetting $model")
        when (model) {
            EPDModel.TWO_IN7_B -> {
                // reset by sending 1,0,1 to the reset pin
                // https://github.com/waveshareteam/e-Paper/blob/a824b4f8f34dee7e721183c0154788dcde41c460/RaspberryPi_JetsonNano/c/lib/e-Paper/EPD_2in7b_V2.c#L39
                digitalWrite(model.pins.reset, 1u)
                delay(200u)
                digitalWrite(model.pins.reset, 0u)
                delay(10u)
                digitalWrite(model.pins.reset, 1u)
                delay(200u)
            }

            EPDModel.TWO_IN7 -> TODO("Not yet implemented")
        }
        printDebug("\tReset done")
    }

    override fun readBusy() {
        printDebug("\\\\ e-Paper busy //")
        when (model) {
            //https://github.com/waveshareteam/e-Paper/blob/a824b4f8f34dee7e721183c0154788dcde41c460/RaspberryPi_JetsonNano/c/lib/e-Paper/EPD_2in7b_V2.c#L79
            EPDModel.TWO_IN7_B -> {
                // 0: busy, 1: idle // this seems to contradict the Waveshare code
                val zero: uint8_t = 0u
                val one: uint8_t = 1u
                while (bcm2835_gpio_lev(model.pins.busy) == zero) {
                    delay(100u)
                }
            }

            EPDModel.TWO_IN7 -> TODO("Not yet implemented")
        }
        printDebug("//e-Paper busy release\\\\")
    }

    /**
     * Write a single byte [value] to the given GPIO [pin]
     */
    private fun digitalWrite(pin: UByte, value: UByte) {
        bcm2835_gpio_write(pin, value)
    }

    /**
     * Read a single byte value from the given GPIO [pin]
     */
    private fun digitalRead(pin: UByte): uint8_t {
        return bcm2835_gpio_lev(pin)
    }

    /**
     * Write a byte [value] over the SPI interface
     */
    private fun spiWriteByte(value: UByte) {
        bcm2835_spi_transfer(value)
    }

    /**
     * For each of the buttons on the display, initialize them as pull-up inputs
     */
    private fun initializeKeys() {
        printDebug("Initializing keys")
        if (!model.buttons.isNullOrEmpty()) {
            printDebug("\tThere are ${model.buttons.size} keys on ${model.name}")
            delay(200u)
            readBusy()
            for (key in model.buttons) {
                printDebug("\t\tSetting key $key to pin mode FunctionSelect.INPUT")
                setPinMode(key, FunctionSelect.INPUT.value)
                // TODO: wrap the bcm2835 calls, perhaps a more general setPin(pin,mode,pud,len) option?
                bcm2835_gpio_set_pud(key, PUDControl.PUD_UP.value)
//                bcm2835_gpio_hen(key)  // enable rising edge detection // THIS LINE CRASHES THE PI
                delay(100u)
                readBusy()
            }
            printDebug("Keys NOT initialized")
        } else {
            printDebug("Model has no keys to initialize")
        }
    }

    /**
     * Loop round each of the buttons. If one has been pressed, invoke the function defined for that button
     * and return the key number
     */
    fun pollKeys(): uint8_t? {
        if (!model.buttons.isNullOrEmpty()) {
            for (key in model.buttons) {
                val lev: uint8_t = bcm2835_gpio_lev(key)
                if (lev == uint8_ZERO) {
                    if (buttonActions[key] != null) {
                        buttonActions[key]?.invoke()
//                        delay(1000u)
                        return key
                    } else {
                        printDebug("no action defined for keypress $key")
                    }
                    delay(100u)
                }
            }
        }
        return null
    }
}


@ExperimentalUnsignedTypes
interface EPaperDisplayCommands {
    /**
     * Clear the display on the device; takes several seconds, depending on model
     */
    fun clear()

    /**
     * Display the supplied images on the device. A separate UByteArray should be supplied for each 'ink colour'.
     * For instance, the Waveshare model 2.7inch B has Black and Red inks, so the [images] array should contain
     * two separate unsigned byte arrays.
     */
    fun display(images: Array<UByteArray>)

    /**
     * Sends the display to 'sleep', essentially an zero-power state.
     * Use this when the display will not need to be refreshed for a while.
     */
    fun sleep()

    /**
     * Send a byte [reg] as data to the device
     */
    fun sendData(reg: UByte)

    /**
     * Send a byte [cmd] as a command to the device
     */
    fun sendCommand(cmd: UByte)

    /**
     * 'Pause' for [ms] milliseconds before sending the next command or data
     */
    fun delay(ms: UInt)

    /**
     * Shut down the device, bring it to a zero power state.
     * The last image shown will still be visible
     */
    fun exit()

    /**
     * Send the reset command to the device
     */
    fun reset()

    /**
     * Read the busy status of the device, and wait until the busy flag has been cleared
     */
    fun readBusy()

    /**
     * Set the [mode] of the given [pin]. The mode is typically "high" (1u) or "low" (0u)
     */
    fun setPinMode(pin: UByte, mode: UByte)

    /**
     * Shut down the device, bring it to a zero power state.
     * Typically, this will clear, sleep and exit the device
     */
    fun shutdown()
}