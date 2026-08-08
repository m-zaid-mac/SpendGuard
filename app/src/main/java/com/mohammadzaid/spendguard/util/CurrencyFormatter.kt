package com.mohammadzaid.spendguard.util

import java.text.NumberFormat
import java.util.Locale

fun Double.asCurrency(): String =
    NumberFormat.getCurrencyInstance(Locale.US).format(this)
