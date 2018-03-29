package com.alexeymerov.unsplashviewer.utils

import android.support.design.widget.TextInputLayout

open class BaseErrorsString {
    open fun isShort(): String = "Field is short"
    open fun isLong(): String = "Field is long"
    open fun withoutDot(): String = "Field not contains dot"
    open fun withoutAtSymbol(): String = "Field not contains @"
    open fun notOnlyLatin(): String = "Field has non-latin symbols"
    open fun isEmpty(): String = "Field is empty"
    open fun containsSpace(): String = "Field contains space"
    open fun incorrect(): String = "Field incorrect"
    open fun notOnlyDigits(): String = "Field contains not only digits"
    open fun notFitCustomRegex(): String = "Field not fit custom regex"
}

class Validation {
    private val EMAIL_REGEX = ".+@.+\\..+".toRegex()
    private val ALL_NON_LATIN_REGEX = "[^a-zA-Z]".toRegex()
    private val ALL_LATIN_REGEX = "([a-zA-Z])+?".toRegex()
    private val ALL_DIGITS_REGEX = "([0-9])+?".toRegex()

    private var errorsString: BaseErrorsString = BaseErrorsString()
    private var onlyDigits: Boolean? = null //- латинкские буквы и цифры
    private var minLength: Int? = null //- минимальная длина
    private var maxLength: Int? = null //- максимальная длина
    private var onlyLatin: Boolean? = null //- латинкские буквы и цифры
    private var isNotEmpty: Boolean? = null //- пустота
    private var canContainSpaces: Boolean? = null //- пробелы
    private var checkForDot: Boolean? = null //- на точку
    private var checkForAtSymbol: Boolean? = null //- на @
    private var useEmailRegex: Boolean? = null //- регулярка
    private var customRegex: Regex? = null //- регулярка

    fun customErrors(errorsStrings: BaseErrorsString) = consume { errorsString = errorsStrings }
    fun setMinLength(int: Int) = consume { minLength = int }
    fun setMaxLength(int: Int) = consume { maxLength = int }
    fun hasDot() = consume { checkForDot = true }
    fun hasAtSymbol() = consume { checkForAtSymbol = true }
    fun onlyLatin() = consume { onlyLatin = true }
    fun notEmpty() = consume { isNotEmpty = true }
    fun canContainSpaces(boolean: Boolean) = consume { canContainSpaces = boolean }
    fun useEmailRegex() = consume { useEmailRegex = true }
    fun useOnlyDigits() = consume { onlyDigits = true }
    fun useCustomRegex(regex: Regex) = consume { customRegex = regex }

    private inline fun consume(f: () -> Unit): Validation {
        f.invoke()
        return this
    }

    fun getAllConditions(string: String): MutableList<Pair<Boolean, String>> {
        val conditionsList: MutableList<Pair<Boolean, String>> = ArrayList()
        with(conditionsList) {
            isNotEmpty?.let { addCustom(string.isNotEmpty()) { isEmpty() } }
            minLength?.let { addCustom(string.length >= minLength!!) { isShort() } }
            maxLength?.let { addCustom(string.length <= maxLength!!) { isLong() } }
            onlyDigits?.let { addCustom(string.matches(ALL_DIGITS_REGEX)) { notOnlyDigits() } }
            checkForDot?.let { addCustom(string.contains(".")) { withoutDot() } }
            checkForAtSymbol?.let { addCustom(string.contains("@")) { withoutAtSymbol() } }
            customRegex?.let { addCustom(string.matches(customRegex!!)) { notFitCustomRegex() } }
            useEmailRegex?.let { addCustom(string.matches(EMAIL_REGEX)) { incorrect() } }
            canContainSpaces?.let { addCustom(if (!canContainSpaces!!) !string.contains(" ") else true) { containsSpace() } }
            onlyLatin?.let { addCustom(string.replace(ALL_NON_LATIN_REGEX, "").matches(ALL_LATIN_REGEX)) { notOnlyLatin() } }
        }
        return conditionsList
    }

    private fun MutableList<Pair<Boolean, String>>.addCustom(condition: Boolean, errorString: BaseErrorsString.() -> String) = add(Pair(condition, errorString.invoke(errorsString)))
}

fun TextInputLayout.isFieldValid(validation: Validation): Boolean {
    editText?.apply {
        validation.getAllConditions(text.toString())
                .filterNot { it.first }
                .forEach { buildError(it.second) }

        return buildError(null)
    }
    return false
}

private fun TextInputLayout.buildError(errorString: String?): Boolean {
    error = errorString
    return errorString.isNullOrEmpty()
}