# Twocan

## macOS developer workstation setup

To run and develop this app on macOS you need these dependencies installed on your workstation:

- NodeJS
- Java JDK
- Docker

The cool kids install stuff with [Homebrew](https://brew.sh/).

```
brew install node openjdk
brew install --cask docker
```

Next you need to install the frontend project dependencies.

```
cd frontend
npm install
```

Then you can run the application. Run these two command in two different terminal windows.

```
cd frontend
npm start
```

```
./gradlew :server:run
```
