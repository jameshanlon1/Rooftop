# Rooftop

A clean, fast IPTV player for Android TV and mobile. Built for Nvidia Shield, Fire TV, and Android phones/tablets.

Rooftop connects to your existing IPTV service — it does not provide any content or subscriptions.

---

## Features

- Live TV with full Electronic Programme Guide (EPG)
- Movies and Series with metadata, posters, and progress tracking
- M3U / M3U8 playlist support
- Xtream Codes API support
- Catchup / replay for supported providers
- Favourites, Continue Watching, and universal search
- Multiple playlist and provider support
- Customisable home screen
- Parental controls
- Chromecast support
- Dark theme optimised for TV viewing
- TV-first remote navigation with full phone/tablet support

---

## Installation

### Sideload APK
1. Download the latest APK from [Releases](https://github.com/jameshanlon1/rooftop/releases)
2. On your device enable **Install from unknown sources**
3. Install the APK and launch Rooftop
4. Add your M3U URL or Xtream Codes credentials to get started

### Build from Source
```bash
git clone https://github.com/jameshanlon1/rooftop.git
cd rooftop
./gradlew assembleDebug
```

Requires JDK 17+ and Android SDK 26+. See [Development Setup](docs/development-setup.md) for full instructions.

---

## Tech Stack

Kotlin, Jetpack Compose for TV, Media3 ExoPlayer, MVVM + Clean Architecture, Hilt, Room, Retrofit.

---

## Contributing

Issues and pull requests are welcome. Please open an issue before starting any significant work so we can discuss the approach.

---

## Disclaimer

Rooftop is a media player only. It does not include, distribute, or provide any IPTV content or subscriptions. Users are solely responsible for the legality of the content they access.

---

## License

MIT License — see [LICENSE](LICENSE) for details.
