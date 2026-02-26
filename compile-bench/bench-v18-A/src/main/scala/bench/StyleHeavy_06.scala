package bench

import com.raquo.laminar.api.L.{*, given}

object StyleHeavy06 {

  // Style-related state
  val themeColor_06 = Var("blue")
  val isDark_06 = Var(false)
  val sizeScale_06 = Var(1.0)
  val isExpanded_06 = Var(false)
  val activePanel_06 = Var(0)
  val animating_06 = Var(false)

  // Derived style signals
  val bgColor_06: Signal[String] = isDark_06.signal.map(if (_) "#1a1a2e" else "#ffffff")
  val textColor_06: Signal[String] = isDark_06.signal.map(if (_) "#e0e0e0" else "#333333")
  val borderColor_06: Signal[String] = themeColor_06.signal.map(c => s"1px solid $c")
  val fontSize_06: Signal[Int] = sizeScale_06.signal.map(s => (16 * s).toInt)
  val containerWidth_06: Signal[Int] = isExpanded_06.signal.map(if (_) 1200 else 800)

  // Component with very heavy style prop usage
  def styledCard_06(title: String, idx: Int): HtmlElement = {
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
      backgroundColor <-- bgColor_06,
      color <-- textColor_06,
      fontSize.px <-- fontSize_06,
      width.px <-- containerWidth_06.map(_ / 3),
      padding.px <-- sizeScale_06.signal.map(s => (16 * s).toInt),
      margin.px <-- sizeScale_06.signal.map(s => (8 * s).toInt),
      borderWidth.px := 1,
      borderStyle := "solid",
      borderColor <-- themeColor_06.signal,

      // Header area
      div(
        display := "flex",
        justifyContent := "space-between",
        alignItems := "center",
        padding.px := 12,
        borderBottom := "1px solid #eee",
        backgroundColor <-- themeColor_06.signal.map(c => s"${c}10"),

        span(
          fontWeight := "600",
          fontSize.px := 18,
          letterSpacing := "0.5px",
          textTransform := "uppercase",
          color <-- themeColor_06.signal,
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
        maxHeight.px <-- isExpanded_06.signal.map(if (_) 500 else 200),
        overflow := "auto",
        fontSize.px <-- fontSize_06,

        p(
          margin.px := 0,
          padding.px := 8,
          textAlign := "left",
          s"Content for card $title in section $idx",
        ),
        p(
          marginTop.px := 8,
          color <-- textColor_06.map(c => s"${c}99"),
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
        backgroundColor <-- isDark_06.signal.map(if (_) "rgba(255,255,255,0.05)" else "rgba(0,0,0,0.02)"),

        button(
          padding.px := 8,
          paddingLeft.px := 16,
          paddingRight.px := 16,
          borderRadius.px := 4,
          border := "none",
          backgroundColor <-- themeColor_06.signal,
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
          borderColor <-- themeColor_06.signal,
          backgroundColor := "transparent",
          color <-- themeColor_06.signal,
          fontWeight := "500",
          fontSize.px := 14,
          cursor := "pointer",
          "Cancel",
        ),
      ),
    )
  }

  // A panel with many style setters
  def styledPanel_06: HtmlElement = {
    div(
      display := "flex",
      flexWrap := "wrap",
      gap := "16px",
      padding.px := 24,
      width.px <-- containerWidth_06,
      minHeight.px := 600,
      backgroundColor <-- bgColor_06,
      transition := "all 0.3s ease",

      // Generate multiple cards
      styledCard_06("Alpha", 1),
      styledCard_06("Beta", 2),
      styledCard_06("Gamma", 3),
      styledCard_06("Delta", 4),
    )
  }

  // Toolbar with style-heavy buttons
  def styleToolbar_06: HtmlElement = {
    div(
      display := "flex",
      alignItems := "center",
      gap := "12px",
      padding.px := 16,
      backgroundColor <-- isDark_06.signal.map(if (_) "#2d2d44" else "#f5f5f5"),
      borderBottom := "1px solid #ddd",

      button(
        padding.px := 8, fontSize.px := 14, borderRadius.px := 4,
        border := "none", backgroundColor := "#4CAF50", color := "white",
        cursor := "pointer", fontWeight := "500",
        onClick --> (_ => isDark_06.update(!_)),
        child.text <-- isDark_06.signal.map(if (_) "Light Mode" else "Dark Mode"),
      ),
      button(
        padding.px := 8, fontSize.px := 14, borderRadius.px := 4,
        border := "none", backgroundColor := "#2196F3", color := "white",
        cursor := "pointer", fontWeight := "500",
        onClick --> (_ => isExpanded_06.update(!_)),
        child.text <-- isExpanded_06.signal.map(if (_) "Collapse" else "Expand"),
      ),
      button(
        padding.px := 8, fontSize.px := 14, borderRadius.px := 4,
        border := "none", backgroundColor := "#FF9800", color := "white",
        cursor := "pointer", fontWeight := "500",
        onClick --> (_ => sizeScale_06.update(s => if (s >= 2.0) 0.5 else s + 0.25)),
        "Scale Up",
      ),
      span(
        fontSize.px := 12,
        color <-- textColor_06,
        opacity := "0.6",
        child.text <-- sizeScale_06.signal.map(s => f"Scale: $s%.2f"),
      ),
    )
  }

  def render06: HtmlElement = {
    div(
      cls := "style-heavy-container-06",
      fontFamily := "system-ui, -apple-system, sans-serif",
      minHeight.px := 800,
      backgroundColor <-- bgColor_06,
      color <-- textColor_06,
      transition := "background-color 0.3s, color 0.3s",

      styleToolbar_06,
      styledPanel_06,
    )
  }
}
