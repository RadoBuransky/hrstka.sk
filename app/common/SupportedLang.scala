package common

import java.util.Locale

import play.api.i18n.Lang

import scala.language.implicitConversions

object SupportedLang extends Enumeration {
  type SupportedLang = Value
  val en, sk = Value

  lazy val defaultLang = Lang("sk-SK")

  def apply(iso: String): SupportedLang = {
    try {
      SupportedLang.withName(iso.toLowerCase)
    }
    catch {
      case ex: Exception => throw new HEException("Unknown language! [" + iso + "]")
    }
  }

  implicit def localeToLang(locale: Locale): Lang = Lang(locale.toLanguageTag)
}