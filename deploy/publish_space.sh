#!/bin/sh
# Publish the committed app to a free Hugging Face Docker Space, using the in-memory demo database.
# Usage: deploy/publish_space.sh <hf-username>/<space-name>   (run `hf auth login` first)
set -eu
SPACE=${1:?usage: deploy/publish_space.sh <hf-username>/<space-name>}
HF=${HF:-hf}
STAGE=$(mktemp -d)
trap 'rm -rf "$STAGE"' EXIT
git archive HEAD | tar -x -C "$STAGE"
cp deploy/huggingface/README.md "$STAGE/README.md"
# Spaces serve port 7860 and have no database service, so run the self-contained demo profile.
printf '\nENV SPRING_PROFILES_ACTIVE=demo PORT=7860\nEXPOSE 7860\n' >> "$STAGE/Dockerfile"
"$HF" repos create "$SPACE" --type space --sdk docker --public --exist-ok
"$HF" upload "$SPACE" "$STAGE" . --type space --delete "*" --commit-message "Deploy $(git rev-parse --short HEAD)"
echo "Building at https://huggingface.co/spaces/$SPACE (the first build takes several minutes)"
