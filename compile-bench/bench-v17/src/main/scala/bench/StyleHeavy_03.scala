package bench

import com.raquo.laminar.api.L.{*, given}

object StyleHeavy03 {

  // Style-related state
  val themeColor_03 = Var("blue")
  val isDark_03 = Var(false)
  val sizeScale_03 = Var(1.0)
  val isExpanded_03 = Var(false)
  val activePanel_03 = Var(0)
  val animating_03 = Var(false)

  // Derived style signals
  val bgColor_03: Signal[String] = isDark_03.signal.map(if (_) "#1a1a2e" else "#ffffff")
  val textColor_03: Signal[String] = isDark_03.signal.map(if (_) "#e0e0e0" else "#333333")
  val borderColor_03: Signal[String] = themeColor_03.signal.map(c => s"1px solid $c")
  val fontSize_03: Signal[Int] = sizeScale_03.signal.map(s => (16 * s).toInt)
  val containerWidth_03: Signal[Int] = isExpanded_03.signal.map(if (_) 1200 else 800)

  // Component with very heavy style prop usage
  def styledCard_03(title: String, idx: Int): HtmlElement = {
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
      backgroundColor <-- bgColor_03,
      color <-- textColor_03,
      fontSize.px <-- fontSize_03,
      width.px <-- containerWidth_03.map(_ / 3),
      padding.px <-- sizeScale_03.signal.map(s => (16 * s).toInt),
      margin.px <-- sizeScale_03.signal.map(s => (8 * s).toInt),
      borderWidth.px := 1,
      borderStyle := "solid",
      borderColor <-- themeColor_03.signal,

      // Header area
      div(
        display := "flex",
        justifyContent := "space-between",
        alignItems := "center",
        padding.px := 12,
        borderBottom := "1px solid #eee",
        backgroundColor <-- themeColor_03.signal.map(c => s"${c}10"),

        span(
          fontWeight := "600",
          fontSize.px := 18,
          letterSpacing := "0.5px",
          textTransform := "uppercase",
          color <-- themeColor_03.signal,
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
        maxHeight.px <-- isExpanded_03.signal.map(if (_) 500 else 200),
        overflow := "auto",
        fontSize.px <-- fontSize_03,

        p(
          margin.px := 0,
          padding.px := 8,
          textAlign := "left",
          s"Content for card $title in section $idx",
        ),
        p(
          marginTop.px := 8,
          color <-- textColor_03.map(c => s"${c}99"),
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
        backgroundColor <-- isDark_03.signal.map(if (_) "rgba(255,255,255,0.05)" else "rgba(0,0,0,0.02)"),

        button(
          padding.px := 8,
          paddingLeft.px := 16,
          paddingRight.px := 16,
          borderRadius.px := 4,
          border := "none",
          backgroundColor <-- themeColor_03.signal,
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
          borderColor <-- themeColor_03.signal,
          backgroundColor := "transparent",
          color <-- themeColor_03.signal,
          fontWeight := "500",
          fontSize.px := 14,
          cursor := "pointer",
          "Cancel",
        ),
      ),
    )
  }

  // A panel with many style setters
  def styledPanel_03: HtmlElement = {
    div(
      display := "flex",
      flexWrap := "wrap",
      gap := "16px",
      padding.px := 24,
      width.px <-- containerWidth_03,
      minHeight.px := 600,
      backgroundColor <-- bgColor_03,
      transition := "all 0.3s ease",

      // Generate multiple cards
      styledCard_03("Alpha", 1),
      styledCard_03("Beta", 2),
      styledCard_03("Gamma", 3),
      styledCard_03("Delta", 4),
    )
  }

  // Toolbar with style-heavy buttons
  def styleToolbar_03: HtmlElement = {
    div(
      display := "flex",
      alignItems := "center",
      gap := "12px",
      padding.px := 16,
      backgroundColor <-- isDark_03.signal.map(if (_) "#2d2d44" else "#f5f5f5"),
      borderBottom := "1px solid #ddd",

      button(
        padding.px := 8, fontSize.px := 14, borderRadius.px := 4,
        border := "none", backgroundColor := "#4CAF50", color := "white",
        cursor := "pointer", fontWeight := "500",
        onClick --> (_ => isDark_03.update(!_)),
        child.text <-- isDark_03.signal.map(if (_) "Light Mode" else "Dark Mode"),
      ),
      button(
        padding.px := 8, fontSize.px := 14, borderRadius.px := 4,
        border := "none", backgroundColor := "#2196F3", color := "white",
        cursor := "pointer", fontWeight := "500",
        onClick --> (_ => isExpanded_03.update(!_)),
        child.text <-- isExpanded_03.signal.map(if (_) "Collapse" else "Expand"),
      ),
      button(
        padding.px := 8, fontSize.px := 14, borderRadius.px := 4,
        border := "none", backgroundColor := "#FF9800", color := "white",
        cursor := "pointer", fontWeight := "500",
        onClick --> (_ => sizeScale_03.update(s => if (s >= 2.0) 0.5 else s + 0.25)),
        "Scale Up",
      ),
      span(
        fontSize.px := 12,
        color <-- textColor_03,
        opacity := "0.6",
        child.text <-- sizeScale_03.signal.map(s => f"Scale: $s%.2f"),
      ),
    )
  }

  def render03: HtmlElement = {
    div(
      cls := "style-heavy-container-03",
      fontFamily := "system-ui, -apple-system, sans-serif",
      minHeight.px := 800,
      backgroundColor <-- bgColor_03,
      color <-- textColor_03,
      transition := "background-color 0.3s, color 0.3s",

      styleToolbar_03,
      styledPanel_03,
    )
  }
}
