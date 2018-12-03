package com.overflow.cash.utils

import com.bumptech.glide.load.Key
import java.nio.ByteBuffer
import java.security.MessageDigest

class IntegerVersionSignature(private val version:Int):Key {

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ByteBuffer.allocate(Integer.SIZE).putInt(version).array())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IntegerVersionSignature

        if (version != other.version) return false

        return true
    }

    override fun hashCode(): Int {
        return version
    }


}