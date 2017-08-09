package org.crypticmission.gurpslack

/**
 */

fun Int.toSignedString(): String =
        if (this > 0) "+${this}"
        else if (this < 0) "-${this}"
        else ""
