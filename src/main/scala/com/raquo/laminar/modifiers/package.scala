package com.raquo.laminar

import com.raquo.laminar.keys.SimpleKey
import com.raquo.laminar.nodes.ReactiveElement

package object modifiers {

  @deprecated("Use SimpleKeyUpdater[K, V, El] or CompositeKeyUpdater[V, El] instead - note both the new name and the different order of type params", "18.0.0-M1")
  type KeyUpdater[El <: ReactiveElement.Base, K <: SimpleKey[_, _, El], V] = SimpleKeyUpdater[K, V, El]
}
