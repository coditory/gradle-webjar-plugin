#!/bin/bash -e

if [[ "$TRAVIS_BRANCH" == "master" ]] && [[ "$TRAVIS_PULL_REQUEST" == "false" ]]; then
  # Disable TLS 1.3  https://github.com/kt3k/coveralls-gradle-plugin/issues/85
  ./gradlew jacocoTestReport coveralls -Djdk.tls.client.protocols="TLSv1,TLSv1.1,TLSv1.2"
else
  echo "Skipping coverage for non master branch"
fi
