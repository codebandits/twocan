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

## CI/CD Pipeline [![CI/CD Pipeline](https://ci.distro.beer/api/v1/teams/codebandits/pipelines/twocan/badge)](https://ci.distro.beer/teams/codebandits/pipelines/twocan)

https://ci.distro.beer/teams/codebandits/pipelines/twocan

If you're changing the pipeline, you might find these commands helpful.

```
fly --target codebandits login --team-name codebandits --concourse-url https://ci.distro.beer
fly --target codebandits set-pipeline --pipeline twocan --config pipeline/pipeline.yml
```

Set the pipeline secrets:

```
export GOOGLE_APPLICATION_CREDENTIALS=$(pwd)/k8s/secrets/tools-275701.json
kubectl config use-context remus
kustomize build pipeline/brite-tank | \
    kapp deploy \
    --namespace apps \
    --app brite-tank-twocan \
    --yes \
    -f -
direnv reload
```
