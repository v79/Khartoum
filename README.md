## Khartoum

Experimenting with Waveshare ePaper for the Raspberry Pi

Writing code in Kotlin Native to control and display images and text on a Waveshare ePaper HAT for the Raspbery Pi.

It is not possible to build this application on a Raspberry Pi, as Jetbrains do not support the Raspberry as a build platform (despite it having the same core architecture as the M-series ARM processors used on Macs).

Makes use of the [bcm2835](https://www.airspayce.com/mikem/bcm2835/index.html) C library for hardware control. It recreates some of the [Waveshare GUI C](https://github.com/waveshare/e-Paper/tree/master/RaspberryPi%26JetsonNano/c) example functions, in Kotlin, in a library I am calling *Khartoum*. So far, the following functions are available:

The __Khartoum__ binary must be run as root (`sudo -E ./Khartoum.kexe`) on the Raspberry Pi.


### Libraries used

- Kotlinx serialization
- Kotlinx datetime
- Native c-libraries:
  - libCurl
  - [bcm2835](https://www.airspayce.com/mikem/bcm2835/index.html) C library

### Links and references


### Spotify

Requires two environment variables to be set:

- SPOTIFY_CLIENT
- SPOTIFY_SECRET
- SPOTIFY_REFRESH_TOKEN

To create a client ID, register a new application through the [developer dashboard](https://developer.spotify.com/dashboard). You'll also find the secret here.

To authorize the application, I should be requesting the authorization through the [code flow](https://developer.spotify.com/documentation/web-api/tutorials/code-flow) mechanism. But this application runs headless and in the background. Instead, I need to get a code from:

`https://accounts.spotify.com/authorize?response_type=code&client_id=<<CLIENT_ID>>&scope=user-read-currently-playing&redirect_uri=https://www.liamjd.org/spotCallback
  `

Which redirects to `https://www.liamjd.org/spotCallback?code=<<CODE>>`

And from that code, I can request a refresh token from the URL:

` curl -d client_id=<<CLIENT>> -d client_secret=<<SECRET>> -d grant_type=authorization_code -d code=<<CODE>> -d redirect_uri=https%3A%2F%2Fwww.liamjd.org%2FspotCallback https://accounts.spotify.com/api/token`

That refresh token can then be used to request a new access token and is stored as an environment variable.

Need the `-E` option to be passed to sudo for them to be available. Run the command with `nohup` to ensure it keeps running even after logging out (`sudo -E nohup ./Khartoum.kexe &`).
