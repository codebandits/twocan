#!/usr/bin/env sh

ensure_env_var_set () {
  eval "value=\$${1}"
  if [ -z "$value" ]; then
    echo "Environment variable $1 not set. Aborting."
    exit 1
  fi
}

ensure_env_var_set KUBECONFIG_FILE
ensure_env_var_set GCP_SERVICE_ACCOUNT_FILE

mkdir -p .config
printenv KUBECONFIG_FILE > .config/kubeconfig
printenv GCP_SERVICE_ACCOUNT_FILE > .config/gcp-service-account.json
export KUBECONFIG=$(realpath .config/kubeconfig)
export GOOGLE_APPLICATION_CREDENTIALS=$(realpath .config/gcp-service-account.json)

if [[ "$#" != "0" ]]; then
  "$@"
fi
