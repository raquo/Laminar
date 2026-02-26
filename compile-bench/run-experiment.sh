#!/usr/bin/env bash
set -euo pipefail

# ---------------------------------------------------------------------------
# Experiment A/B: Compile-time benchmark with modified Airstream
# ---------------------------------------------------------------------------

cd "$(dirname "$0")"

ITERATIONS=${1:-3}
SBT="sbt -batch -no-colors"

# Collect project names to benchmark
PROJECTS=("bench-v17" "bench-v18" "bench-v18-A" "bench-v18-B")

echo "=============================================="
echo " Experiment: Compile-Time Benchmark"
echo "=============================================="
echo ""
echo "Projects: ${PROJECTS[*]}"
echo "Iterations: $ITERATIONS"
echo "Date: $(date)"
echo ""

# Step 1: Resolve all dependencies
echo "[1/4] Resolving dependencies..."
UPDATE_CMD=$(printf "%s/update; " "${PROJECTS[@]}")
$SBT "$UPDATE_CMD" 2>&1 | tail -5
echo ""

# Step 2: JVM warmup
echo "[2/4] JVM warmup..."
WARMUP_CMD=""
for p in "${PROJECTS[@]}"; do
  WARMUP_CMD+="${p}/clean; ${p}/compile; "
done
$SBT "$WARMUP_CMD" 2>&1 | tail -5
echo "  Warmup complete."
echo ""

# ---------------------------------------------------------------------------
time_compile() {
  local project="$1"
  local out_file="$2"

  $SBT "${project}/clean" >/dev/null 2>&1

  local start end elapsed
  start=$(python3 -c 'import time; print(int(time.time()*1000))')
  $SBT "${project}/compile" >/dev/null 2>&1
  end=$(python3 -c 'import time; print(int(time.time()*1000))')

  elapsed=$(( end - start ))
  echo "$elapsed" >> "$out_file"
  printf "    Run: %6.1fs\n" "$(echo "scale=1; $elapsed / 1000" | bc)"
}

# Step 3: Timed iterations
declare -A TIME_FILES
for p in "${PROJECTS[@]}"; do
  TIME_FILES[$p]=$(mktemp)
done

cleanup() {
  for f in "${TIME_FILES[@]}"; do rm -f "$f"; done
}
trap cleanup EXIT

step=3
for p in "${PROJECTS[@]}"; do
  echo "[${step}/4] ${p} — $ITERATIONS clean compiles"
  for i in $(seq 1 "$ITERATIONS"); do
    printf "  [%d/%d]" "$i" "$ITERATIONS"
    time_compile "$p" "${TIME_FILES[$p]}"
  done
  echo ""
done

# Step 4: Profile run
echo "[4/4] Profile compiles with -Vprofile..."
mkdir -p profiles

for p in "${PROJECTS[@]}"; do
  $SBT "${p}/clean" >/dev/null 2>&1
  $SBT "set \`${p}\` / scalacOptions += \"-Vprofile\"" "${p}/compile" 2>&1 | tee "profiles/${p}-profile.txt" | tail -3
done
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

for p in "${PROJECTS[@]}"; do
  read -r mean sd med n mn mx <<< "$(calc_stats "${TIME_FILES[$p]}")"
  printf "%-14s %8s %8s %8s %5s %8s %8s\n" "$p" "$mean" "$sd" "$med" "$n" "$mn" "$mx"
done

echo ""

# Delta calculations vs v17
V17_MEAN=$(awk '{ sum += $1; n++ } END { print sum/n }' "${TIME_FILES[bench-v17]}")
for p in "${PROJECTS[@]}"; do
  if [ "$p" != "bench-v17" ]; then
    P_MEAN=$(awk '{ sum += $1; n++ } END { print sum/n }' "${TIME_FILES[$p]}")
    DELTA=$(echo "scale=1; ($P_MEAN - $V17_MEAN) / 1000" | bc)
    RATIO=$(echo "scale=2; $P_MEAN / $V17_MEAN" | bc)
    echo "  $p vs v17: +${DELTA}s (${RATIO}x)"
  fi
done

echo ""
echo "Profile data saved to profiles/"
