#!/bin/bash

if [ "$#" -ne 1 ]; then
    echo "Usage: bash deploy-jetty.bash NAMESPACE"
    exit 1
fi

CURRENT_NS="$(oc project -q)"
NAMESPACE="$1"

#echo "current namespace: $CURRENT_NS"

if [[ "$CURRENT_NS" != "$NAMESPACE" ]]; then
    echo "Wrong namespace: $CURRENT_NS"
    exit 1
fi

oc process --local -f jetty.yaml -p NAMESPACE="$NAMESPACE" | oc apply -f -