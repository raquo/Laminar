package bench

import com.raquo.laminar.api.L.{*, given}

object StyleHeavy10 {

  // Style-related state
  val themeColor_10 = Var("blue")
  val isDark_10 = Var(false)
  val sizeScale_10 = Var(1.0)
  val isExpanded_10 = Var(false)
  val activePanel_10 = Var(0)
  val animating_10 = Var(false)

  // Derived style signals
  val bgColor_10: Signal[String] = isDark_10.signal.map(if (_) "#1a1a2e" else "#ffffff")
  val textColor_10: Signal[String] = isDark_10.signal.map(if (_) "#e0e0e0" else "#333333")
  val borderColor_10: Signal[String] = themeColor_10.signal.map(c => s"1px solid $c")
  val fontSize_10: Signal[Int] = sizeScale_10.signal.map(s => (16 * s).toInt)
  val containerWidth_10: Signal[Int] = isExpanded_10.signal.map(if (_) 1200 else 800)

  // Component with very heavy style prop usage
  def styledCard_10(title: String, idx: Int): HtmlElement = {
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
      backgroundColor <-- bgColor_10,
      color <-- textColor_10,
      fontSize.px <-- fontSize_10,
      width.px <-- containerWidth_10.map(_ / 3),
      padding.px <-- sizeScale_10.signal.map(s => (16 * s).toInt),
      margin.px <-- sizeScale_10.signal.map(s => (8 * s).toInt),
      borderWidth.px := 1,
      borderStyle := "solid",
      borderColor <-- themeColor_10.signal,

      // Header area
      div(
        display := "flex",
        justifyContent := "space-between",
        alignItems := "center",
        padding.px := 12,
        borderBottom := "1px solid #eee",
        backgroundColor <-- themeColor_10.signal.map(c => s"${c}10"),

        span(
          fontWeight := "600",
          fontSize.px := 18,
          letterSpacing := "0.5px",
          textTransform := "uppercase",
          color <-- themeColor_10.signal,
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
        maxHeight.px <-- isExpanded_10.signal.map(if (_) 500 else 200),
        overflow := "auto",
        fontSize.px <-- fontSize_10,

        p(
          margin.px := 0,
          padding.px := 8,
          textAlign := "left",
          s"Content for card $title in section $idx",
        ),
        p(
          marginTop.px := 8,
          color <-- textColor_10.map(c => s"${c}99"),
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
        backgroundColor <-- isDark_10.signal.map(if (_) "rgba(255,255,255,0.05)" else "rgba(0,0,0,0.02)"),

        button(
          padding.px := 8,
          paddingLeft.px := 16,
          paddingRight.px := 16,
          borderRadius.px := 4,
          border := "none",
          backgroundColor <-- themeColor_10.signal,
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
          borderColor <-- themeColor_10.signal,
          backgroundColor := "transparent",
          color <-- themeColor_10.signal,
          fontWeight := "500",
          fontSize.px := 14,
          cursor := "pointer",
          "Cancel",
        ),
      ),
    )
  }

  // A panel with many style setters
  def styledPanel_10: HtmlElement = {
    div(
      display := "flex",
      flexWrap := "wrap",
      gap := "16px",
      padding.px := 24,
      width.px <-- containerWidth_10,
      minHeight.px := 600,
      backgroundColor <-- bgColor_10,
      transition := "all 0.3s ease",

      // Generate multiple cards
      styledCard_10("Alpha", 1),
      styledCard_10("Beta", 2),
      styledCard_10("Gamma", 3),
      styledCard_10("Delta", 4),
    )
  }

  // Toolbar with style-heavy buttons
  def styleToolbar_10: HtmlElement = {
    div(
      display := "flex",
      alignItems := "center",
      gap := "12px",
      padding.px := 16,
      backgroundColor <-- isDark_10.signal.map(if (_) "#2d2d44" else "#f5f5f5"),
      borderBottom := "1px solid #ddd",

      button(
        padding.px := 8, fontSize.px := 14, borderRadius.px := 4,
        border := "none", backgroundColor := "#4CAF50", color := "white",
        cursor := "pointer", fontWeight := "500",
        onClick --> (_ => isDark_10.update(!_)),
        child.text <-- isDark_10.signal.map(if (_) "Light Mode" else "Dark Mode"),
      ),
      button(
        padding.px := 8, fontSize.px := 14, borderRadius.px := 4,
        border := "none", backgroundColor := "#2196F3", color := "white",
        cursor := "pointer", fontWeight := "500",
        onClick --> (_ => isExpanded_10.update(!_)),
        child.text <-- isExpanded_10.signal.map(if (_) "Collapse" else "Expand"),
      ),
      button(
        padding.px := 8, fontSize.px := 14, borderRadius.px := 4,
        border := "none", backgroundColor := "#FF9800", color := "white",
        cursor := "pointer", fontWeight := "500",
        onClick --> (_ => sizeScale_10.update(s => if (s >= 2.0) 0.5 else s + 0.25)),
        "Scale Up",
      ),
      span(
        fontSize.px := 12,
        color <-- textColor_10,
        opacity := "0.6",
        child.text <-- sizeScale_10.signal.map(s => f"Scale: $s%.2f"),
      ),
    )
  }

  def render10: HtmlElement = {
    div(
      cls := "style-heavy-container-10",
      fontFamily := "system-ui, -apple-system, sans-serif",
      minHeight.px := 800,
      backgroundColor <-- bgColor_10,
      color <-- textColor_10,
      transition := "background-color 0.3s, color 0.3s",

      styleToolbar_10,
      styledPanel_10,
    )
  }
}
