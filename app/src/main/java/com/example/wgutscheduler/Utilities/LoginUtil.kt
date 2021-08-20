package com.example.wgutscheduler.Utilities

import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * Created by Thomas Hester on 8/18/21 in the Software Development program.
 */

fun hash(input: String): String? {
    return try {
        val md: MessageDigest = MessageDigest.getInstance("MD5")
        val md5Data = BigInteger(1, md.digest(input.toByteArray()))
        java.lang.String.format("%032X", md5Data)
    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
        null
    }
}
