package com.raquo.laminar.experimental.airstream.observation

class Observer[-A](val onNext: A => Unit)
