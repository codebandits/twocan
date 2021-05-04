FROM alpine:3.13.5

ENV REGCLIENT_VERSION=0.3.2 \
    # https://git.io/J3BCQ
    KBLD_VERSION=0.29.0 \
    # https://git.io/JOEpN
    IMGPKG_VERSION=0.7.0
    # https://git.io/JOaky

RUN apk add --no-cache jq libc6-compat && \
    wget -q -O /bin/regctl https://github.com/regclient/regclient/releases/download/v${REGCLIENT_VERSION}/regctl-linux-amd64 && chmod +x /bin/regctl && \
    wget -q -O /bin/kbld https://github.com/k14s/kbld/releases/download/v${KBLD_VERSION}/kbld-linux-amd64 && chmod +x /bin/kbld && \
    wget -q -O /bin/imgpkg https://github.com/k14s/imgpkg/releases/download/v${IMGPKG_VERSION}/imgpkg-linux-amd64 && chmod +x /bin/imgpkg

COPY resource/ /opt/resource/
