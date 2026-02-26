package bench

import com.raquo.laminar.api.L.{*, given}

object StyleHeavy02 {

  // Style-related state
  val themeColor_02 = Var("blue")
  val isDark_02 = Var(false)
  val sizeScale_02 = Var(1.0)
  val isExpanded_02 = Var(false)
  val activePanel_02 = Var(0)
  val animating_02 = Var(false)

  // Derived style signals
  val bgColor_02: Signal[String] = isDark_02.signal.map(if (_) "#1a1a2e" else "#ffffff")
  val textColor_02: Signal[String] = isDark_02.signal.map(if (_) "#e0e0e0" else "#333333")
  val borderColor_02: Signal[String] = themeColor_02.signal.map(c => s"1px solid $c")
  val fontSize_02: Signal[Int] = sizeScale_02.signal.map(s => (16 * s).toInt)
  val containerWidth_02: Signal[Int] = isExpanded_02.signal.map(if (_) 1200 else 800)

  // Component with very heavy style prop usage
  def styledCard_02(title: String, idx: Int): HtmlElement = {
    div(
      // Static styles - many properties
      display := "flex",
      flexDirection := "column",
      position := "relative",
      overflow := "hidden",
      borderRadius.px := 8,
      boxShadow := "0 2px 8px rgba(0,0,0,0.1)",
      transition := "all 0.3s ease",
      cursor := "pointer",

      // Reactive styles
      backgroundColor <-- bgColor_02,
      color <-- textColor_02,
      fontSize.px <-- fontSize_02,
      width.px <-- containerWidth_02.map(_ / 3),
      padding.px <-- sizeScale_02.signal.map(s => (16 * s).toInt),
      margin.px <-- sizeScale_02.signal.map(s => (8 * s).toInt),
      borderWidth.px := 1,
      borderStyle := "solid",
      borderColor <-- themeColor_02.signal,

      // Header area
      div(
        display := "flex",
        justifyContent := "space-between",
        alignItems := "center",
        padding.px := 12,
        borderBottom := "1px solid #eee",
        backgroundColor <-- themeColor_02.signal.map(c => s"${c}10"),

        span(
          fontWeight := "600",
          fontSize.px := 18,
          letterSpacing := "0.5px",
          textTransform := "uppercase",
          color <-- themeColor_02.signal,
          title,
        ),
        span(
          fontSize.px := 12,
          opacity := "0.7",
          fontStyle := "italic",
          s"#$idx",
        ),
      ),

      // Body area
      div(
        padding.px := 16,
        lineHeight := "1.6",
        minHeight.px := 120,
        maxHeight.px <-- isExpanded_02.signal.map(if (_) 500 else 200),
        overflow := "auto",
        fontSize.px <-- fontSize_02,

        p(
          margin.px := 0,
          padding.px := 8,
          textAlign := "left",
          s"Content for card $title in section $idx",
        ),
        p(
          marginTop.px := 8,
          color <-- textColor_02.map(c => s"${c}99"),
          fontSize.em := 0.9,
          "Secondary text with reduced opacity",
        ),
      ),

      // Footer area
      div(
        display := "flex",
        justifyContent := "flex-end",
        gap := "8px",
        padding.px := 12,
        borderTop := "1px solid #eee",
        backgroundColor <-- isDark_02.signal.map(if (_) "rgba(255,255,255,0.05)" else "rgba(0,0,0,0.02)"),

        button(
          padding.px := 8,
          paddingLeft.px := 16,
          paddingRight.px := 16,
          borderRadius.px := 4,
          border := "none",
          backgroundColor <-- themeColor_02.signal,
          color := "white",
          fontWeight := "500",
          fontSize.px := 14,
          cursor := "pointer",
          "Action",
        ),
        button(
          padding.px := 8,
          paddingLeft.px := 16,
          paddingRight.px := 16,
          borderRadius.px := 4,
          borderWidth.px := 1,
          borderStyle := "solid",
          borderColor <-- themeColor_02.signal,
          backgroundColor := "transparent",
          color <-- themeColor_02.signal,
          fontWeight := "500",
          fontSize.px := 14,
          cursor := "pointer",
          "Cancel",
        ),
      ),
    )
  }

  // A panel with many style setters
  def styledPanel_02: HtmlElement = {
    div(
      display := "flex",
      flexWrap := "wrap",
      gap := "16px",
      padding.px := 24,
      width.px <-- containerWidth_02,
      minHeight.px := 600,
      backgroundColor <-- bgColor_02,
      transition := "all 0.3s ease",

      // Generate multiple cards
      styledCard_02("Alpha", 1),
      styledCard_02("Beta", 2),
      styledCard_02("Gamma", 3),
      styledCard_02("Delta", 4),
    )
  }

  // Toolbar with style-heavy buttons
  def styleToolbar_02: HtmlElement = {
    div(
      display := "flex",
      alignItems := "center",
      gap := "12px",
      padding.px := 16,
      backgroundColor <-- isDark_02.signal.map(if (_) "#2d2d44" else "#f5f5f5"),
      borderBottom := "1px solid #ddd",

      button(
        padding.px := 8, fontSize.px := 14, borderRadius.px := 4,
        border := "none", backgroundColor := "#4CAF50", color := "white",
        cursor := "pointer", fontWeight := "500",
        onClick --> (_ => isDark_02.update(!_)),
        child.text <-- isDark_02.signal.map(if (_) "Light Mode" else "Dark Mode"),
      ),
      button(
        padding.px := 8, fontSize.px := 14, borderRadius.px := 4,
        border := "none", backgroundColor := "#2196F3", color := "white",
        cursor := "pointer", fontWeight := "500",
        onClick --> (_ => isExpanded_02.update(!_)),
        child.text <-- isExpanded_02.signal.map(if (_) "Collapse" else "Expand"),
      ),
      button(
        padding.px := 8, fontSize.px := 14, borderRadius.px := 4,
        border := "none", backgroundColor := "#FF9800", color := "white",
        cursor := "pointer", fontWeight := "500",
        onClick --> (_ => sizeScale_02.update(s => if (s >= 2.0) 0.5 else s + 0.25)),
        "Scale Up",
      ),
      span(
        fontSize.px := 12,
        color <-- textColor_02,
        opacity := "0.6",
        child.text <-- sizeScale_02.signal.map(s => f"Scale: $s%.2f"),
      ),
    )
  }

  def render02: HtmlElement = {
    div(
      cls := "style-heavy-container-02",
      fontFamily := "system-ui, -apple-system, sans-serif",
      minHeight.px := 800,
      backgroundColor <-- bgColor_02,
      color <-- textColor_02,
      transition := "background-color 0.3s, color 0.3s",

      styleToolbar_02,
      styledPanel_02,
    )
  }
}
