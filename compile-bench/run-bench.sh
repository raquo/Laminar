#!/usr/bin/env bash
set -euo pipefail

# ---------------------------------------------------------------------------
# Compile-time benchmark: Laminar 17 vs 18
# Measures clean-compile wall-clock time across 5 iterations per project.
# ---------------------------------------------------------------------------

cd "$(dirname "$0")"

ITERATIONS=${1:-5}
SBT="sbt -batch -no-colors"

# Temp files for timing data
V17_TIMES=$(mktemp)
V18_TIMES=$(mktemp)
MICRO_V17_TIMES=$(mktemp)
MICRO_V18_TIMES=$(mktemp)

trap 'rm -f "$V17_TIMES" "$V18_TIMES" "$MICRO_V17_TIMES" "$MICRO_V18_TIMES"' EXIT

echo "=============================================="
echo " Compile-Time Benchmark: Laminar 17 vs 18"
echo "=============================================="
echo ""
echo "Iterations: $ITERATIONS"
echo "Scala version: 3.3.7"
echo "Date: $(date)"
echo ""

# Step 1: Resolve dependencies upfront
echo "[1/6] Resolving dependencies..."
$SBT "bench-v17/update; bench-v18/update; micro-v17/update; micro-v18/update" 2>&1 | tail -5
echo ""

# Step 2: JVM warmup — compile once, discard
echo "[2/6] JVM warmup (compile once, discard)..."
$SBT "bench-v17/clean; bench-v17/compile; bench-v18/clean; bench-v18/compile" 2>&1 | tail -5
echo "  Warmup complete."
echo ""

# ---------------------------------------------------------------------------
# Helper: run a clean compile and record elapsed wall-clock seconds
# ---------------------------------------------------------------------------
time_compile() {
  local project="$1"
  local out_file="$2"

  $SBT "${project}/clean" >/dev/null 2>&1

  local start end elapsed
  start=$(date +%s%3N 2>/dev/null || python3 -c 'import time; print(int(time.time()*1000))')
  $SBT "${project}/compile" >/dev/null 2>&1
  end=$(date +%s%3N 2>/dev/null || python3 -c 'import time; print(int(time.time()*1000))')

  elapsed=$(( end - start ))
  echo "$elapsed" >> "$out_file"
  # Print seconds with 1 decimal
  printf "    Run: %6.1fs\n" "$(echo "scale=1; $elapsed / 1000" | bc)"
}

# Step 3: Bench-v17 iterations
echo "[3/6] bench-v17 — $ITERATIONS clean compiles"
for i in $(seq 1 "$ITERATIONS"); do
  printf "  [%d/%d]" "$i" "$ITERATIONS"
  time_compile "bench-v17" "$V17_TIMES"
done
echo ""

# Step 4: Bench-v18 iterations
echo "[4/6] bench-v18 — $ITERATIONS clean compiles"
for i in $(seq 1 "$ITERATIONS"); do
  printf "  [%d/%d]" "$i" "$ITERATIONS"
  time_compile "bench-v18" "$V18_TIMES"
done
echo ""

# Step 5: Micro benchmarks
echo "[5/6] micro-v17 — $ITERATIONS clean compiles"
for i in $(seq 1 "$ITERATIONS"); do
  printf "  [%d/%d]" "$i" "$ITERATIONS"
  time_compile "micro-v17" "$MICRO_V17_TIMES"
done
echo ""

echo "[5/6] micro-v18 — $ITERATIONS clean compiles"
for i in $(seq 1 "$ITERATIONS"); do
  printf "  [%d/%d]" "$i" "$ITERATIONS"
  time_compile "micro-v18" "$MICRO_V18_TIMES"
done
echo ""

# Step 6: Final run with -Vprofile
echo "[6/6] Final compile with -Vprofile..."
mkdir -p profiles

$SBT "bench-v17/clean" >/dev/null 2>&1
$SBT 'set bench-v17/scalacOptions += "-Vprofile"' "bench-v17/compile" 2>&1 | tee profiles/v17-profile.txt | tail -3

$SBT "bench-v18/clean" >/dev/null 2>&1
$SBT 'set bench-v18/scalacOptions += "-Vprofile"' "bench-v18/compile" 2>&1 | tee profiles/v18-profile.txt | tail -3

$SBT "micro-v17/clean" >/dev/null 2>&1
$SBT 'set micro-v17/scalacOptions += "-Vprofile"' "micro-v17/compile" 2>&1 | tee profiles/micro-v17-profile.txt | tail -3

$SBT "micro-v18/clean" >/dev/null 2>&1
$SBT 'set micro-v18/scalacOptions += "-Vprofile"' "micro-v18/compile" 2>&1 | tee profiles/micro-v18-profile.txt | tail -3

echo ""

# ---------------------------------------------------------------------------
# Statistics
# ---------------------------------------------------------------------------
calc_stats() {
  local file="$1"
  awk '
  {
    sum += $1; sumsq += $1*$1; n++
    vals[n] = $1
  }
  END {
    mean = sum / n
    if (n > 1) { sd = sqrt((sumsq - sum*sum/n) / (n-1)) } else { sd = 0 }
    # Median
    for (i = 1; i <= n; i++)
      for (j = i+1; j <= n; j++)
        if (vals[i] > vals[j]) { t = vals[i]; vals[i] = vals[j]; vals[j] = t }
    if (n % 2 == 1) median = vals[int(n/2)+1]
    else median = (vals[n/2] + vals[n/2+1]) / 2
    printf "%.1f %.1f %.1f %d %.1f %.1f", mean/1000, sd/1000, median/1000, n, vals[1]/1000, vals[n]/1000
  }' "$file"
}

echo "=============================================="
echo " RESULTS"
echo "=============================================="
echo ""
printf "%-14s %8s %8s %8s %5s %8s %8s\n" "Project" "Mean(s)" "SD(s)" "Med(s)" "N" "Min(s)" "Max(s)"
printf "%-14s %8s %8s %8s %5s %8s %8s\n" "---------" "------" "------" "------" "---" "------" "------"

read -r mean sd med n mn mx <<< "$(calc_stats "$V17_TIMES")"
printf "%-14s %8s %8s %8s %5s %8s %8s\n" "bench-v17" "$mean" "$sd" "$med" "$n" "$mn" "$mx"

read -r mean sd med n mn mx <<< "$(calc_stats "$V18_TIMES")"
printf "%-14s %8s %8s %8s %5s %8s %8s\n" "bench-v18" "$mean" "$sd" "$med" "$n" "$mn" "$mx"

read -r mean sd med n mn mx <<< "$(calc_stats "$MICRO_V17_TIMES")"
printf "%-14s %8s %8s %8s %5s %8s %8s\n" "micro-v17" "$mean" "$sd" "$med" "$n" "$mn" "$mx"

read -r mean sd med n mn mx <<< "$(calc_stats "$MICRO_V18_TIMES")"
printf "%-14s %8s %8s %8s %5s %8s %8s\n" "micro-v18" "$mean" "$sd" "$med" "$n" "$mn" "$mx"

echo ""

# Delta calculation
V17_MEAN=$(awk '{ sum += $1; n++ } END { print sum/n }' "$V17_TIMES")
V18_MEAN=$(awk '{ sum += $1; n++ } END { print sum/n }' "$V18_TIMES")
DELTA=$(echo "scale=1; ($V18_MEAN - $V17_MEAN) / 1000" | bc)
PCT=$(echo "scale=1; ($V18_MEAN - $V17_MEAN) * 100 / $V17_MEAN" | bc)

echo "Delta (bench): v18 is ${DELTA}s (${PCT}%) slower than v17"

MV17_MEAN=$(awk '{ sum += $1; n++ } END { print sum/n }' "$MICRO_V17_TIMES")
MV18_MEAN=$(awk '{ sum += $1; n++ } END { print sum/n }' "$MICRO_V18_TIMES")
MDELTA=$(echo "scale=1; ($MV18_MEAN - $MV17_MEAN) / 1000" | bc)
MPCT=$(echo "scale=1; ($MV18_MEAN - $MV17_MEAN) * 100 / $MV17_MEAN" | bc)

echo "Delta (micro): v18 is ${MDELTA}s (${MPCT}%) slower than v17"
echo ""
echo "Profile data saved to profiles/"
echo "Run ./analyze.sh to compare phase timings."
