#!/usr/bin/env zsh

set -e

rm -fr docs/index.html target/prod
cp resources/public/index.html docs
lein cljsbuild once prod
