FROM alpine:3.13.5

ENV KUSTOMIZE_VERSION=4.1.2
    # https://git.io/JOEpy

RUN wget -q -O - https://github.com/kubernetes-sigs/kustomize/releases/download/kustomize/v${KUSTOMIZE_VERSION}/kustomize_v${KUSTOMIZE_VERSION}_linux_amd64.tar.gz | tar xz -C /bin
