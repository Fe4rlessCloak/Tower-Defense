B#!/bin/bash

BASE_X=147
BASE_Y=18

# Loop through all subfolders
for dir in */; do
    [ -d "$dir" ] || continue

    echo "📁 Processing folder: $dir"

    # Find tower.* (png/jpg/jpeg)
    tower=""
    for ext in png jpg jpeg; do
        if [ -f "${dir}tower.$ext" ]; then
            tower="${dir}tower.$ext"
            break
        fi
    done

    if [ -z "$tower" ]; then
        echo "  ❌ No tower image found"
        continue
    fi

    # Find archer*.*
    archer_files=()
    for ext in png jpg jpeg; do
        for f in "${dir}"archer*.$ext; do
            [ -e "$f" ] || continue
            archer_files+=("$f")
        done
    done

    if [ ${#archer_files[@]} -eq 0 ]; then
        echo "  ⚠️ No archer images found"
        continue
    fi

    # Sort files by number extracted from filename
    IFS=$'\n' sorted=($(printf "%s\n" "${archer_files[@]}" | sort -V))

    index=1
    for archer in "${sorted[@]}"; do
        output="${dir}${index}.png"

        echo "  ➜ Creating $output"

        convert "$tower" "$archer" \
            -geometry +$BASE_X+$BASE_Y \
            -composite "$output"

        index=$((index+1))
    done

done

echo "✅ Done!"

