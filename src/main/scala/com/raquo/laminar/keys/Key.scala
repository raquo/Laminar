package com.raquo.laminar.keys

/**
  * This class represents a Key typically found on the left hand side of the key-value pair `key := value`
  *
  * Example would be a particular attribute or a property (without the corresponding value), e.g. "href"
  */
abstract class Key {
  val name: String
}
