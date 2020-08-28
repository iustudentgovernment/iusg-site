package edu.indiana.iustudentgovernment.utils

fun String.nullifyEmpty() = if (isEmpty()) null else this.trim()