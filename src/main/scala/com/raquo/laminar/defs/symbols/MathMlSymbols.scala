package com.raquo.laminar.defs.symbols

trait MathMlSymbols {

  // -- Invisible operators --

  /** Applied between function name and arguments: f(x) */
  final val invisibleFunctionApplication: String = "\u2061"

  /** Used for implicit multiplication where no operator is written: 2x, xy */
  final val invisibleTimes: String = "\u2062"

  /** Separates arguments in multi-argument functions without visible comma */
  final val invisibleSeparator: String = "\u2063"

  /** Combines whole number and fraction in mixed numbers: 3½ */
  final val invisiblePlus: String = "\u2064"

  // -- Basic arithmetic --

  /** Addition, positive sign, set union in some contexts */
  final val plus: String = "+"

  /** Subtraction, negative sign, set difference */
  final val minus: String = "−"

  /** Multiplication sign, Cartesian product, cross product */
  final val multiplicationSign: String = "×"

  /** Division sign */
  final val divisionSign: String = "÷"

  /** Fraction bar, division, quotient */
  final val solidus: String = "/"

  /** Alternative fraction bar, set difference */
  final val backslash: String = "\\"

  /** Scalar multiplication, dot product */
  final val dotOperator: String = "⋅"

  /** Convolution, binary operation */
  final val asteriskOperator: String = "∗"

  /** Alternative multiplication, dot */
  final val bulletOperator: String = "∙"

  /** Indicates two possible values (positive or negative) */
  final val plusMinus: String = "±"

  /** Indicates two possible values (negative or positive) */
  final val minusPlus: String = "∓"

  /** Percent, hundredths, percentage */
  final val percent: String = "%"

  /** Factorial, subfactorial when preceded by exclamation */
  final val exclamation: String = "!"

  // -- Exponents and roots --

  /** Exponentiation, power, superscript indicator */
  final val caret: String = "^"

  /** Underscore for subscripts, placeholder */
  final val underscore: String = "_"

  /** Principal square root, radical */
  final val squareRoot: String = "√"

  /** Principal cube root */
  final val cubeRoot: String = "∛"

  /** Principal fourth root */
  final val fourthRoot: String = "∜"

  // -- Equality and comparison --

  /** Equality relation, assignment in some contexts
    * Note: this can't be named `equals` due to sporadic conflicts with java.Object.equals method
    */
  final val equalsSign: String = "="

  /** Negation of equality */
  final val notEqual: String = "≠"

  /** Less than comparison */
  final val lessThan: String = "<"

  /** Greater than comparison */
  final val greaterThan: String = ">"

  /** Less than or equal comparison */
  final val lessThanOrEqual: String = "≤"

  /** Greater than or equal comparison */
  final val greaterThanOrEqual: String = "≥"

  /** Order of magnitude smaller */
  final val muchLessThan: String = "≪"

  /** Order of magnitude larger */
  final val muchGreaterThan: String = "≫"

  /** Precedes in order theory */
  final val precedes: String = "≺"

  /** Succeeds in order theory */
  final val succeeds: String = "≻"

  /** Asymptotic equality, approximate value */
  final val almostEqual: String = "≈"

  /** Asymptotically equivalent, similar */
  final val approximatelyEqual: String = "≃"

  /** Definitional equality, congruence, equivalence */
  final val identicalTo: String = "≡"

  /** Negation of identical/congruent */
  final val notIdenticalTo: String = "≢"

  /** Direct proportionality between quantities */
  final val proportionalTo: String = "∝"

  /** Tilde for equivalence, similarity, approximation */
  final val tilde: String = "∼"

  /** Negated tilde */
  final val notTilde: String = "≁"

  /** Asymptotic to, homotopic */
  final val asymptotic: String = "∼"

  // -- Set theory --

  /** Set membership relation */
  final val elementOf: String = "∈"

  /** Negation of set membership */
  final val notElementOf: String = "∉"

  /** Reverse membership, set contains element */
  final val containsAsMember: String = "∋"

  /** Proper subset (not equal) */
  final val subsetOf: String = "⊂"

  /** Proper superset (not equal) */
  final val supersetOf: String = "⊃"

  /** Subset or equal to */
  final val subsetOfOrEqual: String = "⊆"

  /** Superset or equal to */
  final val supersetOfOrEqual: String = "⊇"

  /** The set with no elements, null set */
  final val emptySet: String = "∅"

  /** Set union, join in lattice theory */
  final val cup: String = "∪"

  /** Set intersection, meet in lattice theory */
  final val cap: String = "∩"

  /** Set difference, relative complement */
  final val setMinus: String = "∖"

  /** Symmetric difference, disjunctive union */
  final val triangle: String = "△"

  /** Union over a family of sets */
  final val union: String = "⋃"

  /** Intersection over a family of sets */
  final val intersection: String = "⋂"

  // -- Logic --

  /** Conjunction, both conditions must be true */
  final val logicalAnd: String = "∧"

  /** Disjunction, at least one condition must be true */
  final val logicalOr: String = "∨"

  /** Negation, inverts truth value */
  final val logicalNot: String = "¬"

  /** Universal quantifier, for every element */
  final val forAll: String = "∀"

  /** Existential quantifier, at least one element */
  final val thereExists: String = "∃"

  /** Negation of existence */
  final val thereDoesNotExist: String = "∄"

  /** Therefore, conclusion indicator */
  final val therefore: String = "∴"

  /** Because, premise indicator */
  final val because: String = "∵"

  // -- Arrows and implications --

  /** Implication, function domain, limit direction */
  final val leftArrow: String = "←"

  /** Function mapping, implication, limit direction */
  final val rightArrow: String = "→"

  /** Bijection, if and only if, equivalence */
  final val leftRightArrow: String = "↔"

  /** Limit from below, increasing sequence */
  final val upArrow: String = "↑"

  /** Limit from above, decreasing sequence */
  final val downArrow: String = "↓"

  /** Logical implication (if...then) */
  final val doubleLeftArrow: String = "⇐"

  /** Logical implication (if...then) */
  final val doubleRightArrow: String = "⇒"

  /** Logical equivalence (if and only if) */
  final val doubleLeftRightArrow: String = "⇔"

  /** Function maps element to result */
  final val mapsTo: String = "↦"

  // -- Calculus --

  /** Partial differentiation with respect to a variable */
  final val partialDerivative: String = "∂"

  /** Vector differential operator (gradient, divergence, curl) */
  final val nabla: String = "∇"

  /** Finite change or difference, also Laplacian */
  final val increment: String = "∆"

  /** Definite or indefinite integration */
  final val integral: String = "∫"

  /** Integration over a two-dimensional region */
  final val doubleIntegral: String = "∬"

  /** Integration over a three-dimensional region */
  final val tripleIntegral: String = "∭"

  /** Line integral around a closed curve */
  final val contourIntegral: String = "∮"

  // -- Large operators --

  /** Sum over a sequence or set, typically with bounds */
  final val summation: String = "∑"

  /** Product over a sequence or set, typically with bounds */
  final val product: String = "∏"

  /** Categorical coproduct or disjoint union */
  final val coproduct: String = "∐"

  // -- Abstract algebra --

  /** Tensor product, Kronecker product, direct product */
  final val circledTimes: String = "⊗"

  /** Direct sum, exclusive or, parity */
  final val circledPlus: String = "⊕"

  /** Symmetric difference of sets */
  final val circledMinus: String = "⊖"

  /** Quotient, division in group theory */
  final val circledSlash: String = "⊘"

  /** Function composition */
  final val ringOperator: String = "∘"

  /** Composition, center dot */
  final val smallCircle: String = "∘"

  /** Convolution, star operator, Hodge star */
  final val star: String = "⋆"

  /** Wreath product, semidirect product */
  final val wreath: String = "≀"

  /** Semidirect product indicator */
  final val rightNormalFactor: String = "⋊"

  /** Semidirect product indicator (reverse) */
  final val leftNormalFactor: String = "⋉"

  /** Disjoint union, multiset sum */
  final val squareUnion: String = "⊔"

  /** Greatest lower bound in order theory */
  final val squareIntersection: String = "⊓"

  // -- Topology and lattice theory --

  /** Smash product in topology */
  final val wedge: String = "∧"

  /** Join operation in lattice theory */
  final val vee: String = "∨"

  // -- Delimiters and brackets --

  /** Standard grouping delimiter, stretches to content height */
  final val leftParenthesis: String = "("

  /** Standard grouping delimiter, stretches to content height */
  final val rightParenthesis: String = ")"

  /** Used for matrices, sequences, closed intervals */
  final val leftSquareBracket: String = "["

  /** Used for matrices, sequences, closed intervals */
  final val rightSquareBracket: String = "]"

  /** Used for sets, groups, and code blocks */
  final val leftCurlyBracket: String = "{"

  /** Used for sets, groups, and code blocks */
  final val rightCurlyBracket: String = "}"

  /** Used for inner products, bra-ket notation, expectation values */
  final val leftAngleBracket: String = "⟨"

  /** Used for inner products, bra-ket notation, expectation values */
  final val rightAngleBracket: String = "⟩"

  /** Absolute value, cardinality, determinant when paired */
  final val verticalBar: String = "|"

  /** Used for norms, parallel relation */
  final val doubleVerticalBar: String = "‖"

  /** Absolute value, cardinality, determinant when paired */
  final val pipe: String = "|"

  // -- Punctuation and notation --

  /** Colon for ratios, division, type annotations */
  final val colon: String = ":"

  /** Proportion, is to (as in ratios) */
  final val ratio: String = "∶"

  // -- Special values --

  /** Unbounded quantity, limit at infinity */
  final val infinity: String = "∞"

  // -- Spacing --

  /** Space that prevents line break, used for units and compound expressions */
  final val nonBreakingSpace: String = "\u00A0"

  /** Narrow space for fine-tuning mathematical layout */
  final val thinSpace: String = "\u2009"

  /** Very narrow space for subtle adjustments */
  final val hairSpace: String = "\u200A"

  /** Invisible space that allows line breaking */
  final val zeroWidthSpace: String = "\u200B"

}
