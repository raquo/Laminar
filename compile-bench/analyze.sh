#!/usr/bin/env bash
set -euo pipefail

# ---------------------------------------------------------------------------
# Analyze -Vprofile output from Laminar 17 vs 18 compile benchmarks
# Parses phase timings and prints a comparison table.
# ---------------------------------------------------------------------------

cd "$(dirname "$0")"

PROFILE_DIR="profiles"

if [ ! -d "$PROFILE_DIR" ]; then
  echo "Error: profiles/ directory not found. Run ./run-bench.sh first."
  exit 1
fi

# ---------------------------------------------------------------------------
# Extract phase timings from -Vprofile output
# Format: "phase name  Xms  Y%" or similar patterns
# ---------------------------------------------------------------------------
extract_phases() {
  local file="$1"
  if [ ! -f "$file" ]; then
    echo "  (file not found: $file)"
    return
  fi
  # -Vprofile outputs lines like:
  #   typer                 12345ms  (80.2%)
  # We look for lines that match "phase_name  Nms" pattern
  grep -E '^\s*[a-zA-Z]+\s+[0-9]+ms' "$file" 2>/dev/null || \
  grep -E '[a-zA-Z]+ +[0-9]+ *ms' "$file" 2>/dev/null || \
  # Alternative: look for phase timing summary
  grep -iE '(phase|total|typer|parser|erasure|pickler|refchecks|patmat|superaccessors|explicitouter|mixin|cleanup|jvm|flatten)' "$file" 2>/dev/null || \
  echo "  (no phase timing data found)"
}

echo "=============================================="
echo " Phase Timing Analysis: Laminar 17 vs 18"
echo "=============================================="
echo ""
echo "Date: $(date)"
echo ""

# ---------------------------------------------------------------------------
# Full bench comparison
# ---------------------------------------------------------------------------
echo "--- bench-v17 profile ---"
echo ""
extract_phases "$PROFILE_DIR/v17-profile.txt"
echo ""

echo "--- bench-v18 profile ---"
echo ""
extract_phases "$PROFILE_DIR/v18-profile.txt"
echo ""

# ---------------------------------------------------------------------------
# Micro bench comparison
# ---------------------------------------------------------------------------
echo "--- micro-v17 profile ---"
echo ""
extract_phases "$PROFILE_DIR/micro-v17-profile.txt"
echo ""

echo "--- micro-v18 profile ---"
echo ""
extract_phases "$PROFILE_DIR/micro-v18-profile.txt"
echo ""

# ---------------------------------------------------------------------------
# Try to extract and diff specific phase timings
# ---------------------------------------------------------------------------
echo "=============================================="
echo " Phase-by-Phase Diff (bench)"
echo "=============================================="
echo ""

extract_ms() {
  local file="$1"
  local phase="$2"
  grep -i "$phase" "$file" 2>/dev/null | grep -oE '[0-9]+ms' | head -1 | tr -d 'ms' || echo "0"
}

printf "%-20s %10s %10s %10s %8s\n" "Phase" "v17 (ms)" "v18 (ms)" "Delta" "Pct"
printf "%-20s %10s %10s %10s %8s\n" "-------------------" "--------" "--------" "--------" "------"

for phase in typer parser patmat erasure pickler refchecks superaccessors explicitouter mixin cleanup flatten jvm total; do
  v17_ms=$(extract_ms "$PROFILE_DIR/v17-profile.txt" "$phase")
  v18_ms=$(extract_ms "$PROFILE_DIR/v18-profile.txt" "$phase")

  if [ "$v17_ms" != "0" ] || [ "$v18_ms" != "0" ]; then
    delta=$((v18_ms - v17_ms))
    if [ "$v17_ms" != "0" ]; then
      pct=$(echo "scale=1; $delta * 100 / $v17_ms" | bc 2>/dev/null || echo "N/A")
    else
      pct="N/A"
    fi
    printf "%-20s %10s %10s %10s %7s%%\n" "$phase" "$v17_ms" "$v18_ms" "$delta" "$pct"
  fi
done

echo ""
echo "=============================================="
echo " Phase-by-Phase Diff (micro)"
echo "=============================================="
echo ""

printf "%-20s %10s %10s %10s %8s\n" "Phase" "v17 (ms)" "v18 (ms)" "Delta" "Pct"
printf "%-20s %10s %10s %10s %8s\n" "-------------------" "--------" "--------" "--------" "------"

for phase in typer parser patmat erasure pickler refchecks superaccessors explicitouter mixin cleanup flatten jvm total; do
  v17_ms=$(extract_ms "$PROFILE_DIR/micro-v17-profile.txt" "$phase")
  v18_ms=$(extract_ms "$PROFILE_DIR/micro-v18-profile.txt" "$phase")

  if [ "$v17_ms" != "0" ] || [ "$v18_ms" != "0" ]; then
    delta=$((v18_ms - v17_ms))
    if [ "$v17_ms" != "0" ]; then
      pct=$(echo "scale=1; $delta * 100 / $v17_ms" | bc 2>/dev/null || echo "N/A")
    else
      pct="N/A"
    fi
    printf "%-20s %10s %10s %10s %7s%%\n" "$phase" "$v17_ms" "$v18_ms" "$delta" "$pct"
  fi
done

echo ""
echo "Full profile data in: $PROFILE_DIR/"
echo "Tip: For detailed per-file breakdown, look for lines matching specific .scala files in the profile output."
