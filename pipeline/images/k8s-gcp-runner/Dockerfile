FROM alpine:3.13.5

ENV KUBERNETES_VERSION=1.20.6 \
    # https://git.io/JOEhv
    KUSTOMIZE_VERSION=4.1.2 \
    # https://git.io/JOEpy
    KAPP_VERSION=0.36.0
    # https://git.io/JOEp7

RUN wget -q -P /bin https://storage.googleapis.com/kubernetes-release/release/v${KUBERNETES_VERSION}/bin/linux/amd64/kubectl && chmod +x /bin/kubectl && \
    wget -q -O - https://github.com/kubernetes-sigs/kustomize/releases/download/kustomize/v${KUSTOMIZE_VERSION}/kustomize_v${KUSTOMIZE_VERSION}_linux_amd64.tar.gz | tar xz -C /bin && \
    wget -q -O /bin/kapp https://github.com/k14s/kapp/releases/download/v${KAPP_VERSION}/kapp-linux-amd64 && chmod +x /bin/kapp

COPY entrypoint.sh /usr/bin/
ENTRYPOINT ["entrypoint.sh"]
