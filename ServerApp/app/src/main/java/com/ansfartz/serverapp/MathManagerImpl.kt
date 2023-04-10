package com.ansfartz.serverapp

import java.io.IOException

class MathManagerImpl : IMathManager.Stub(){

    @Throws(IOException::class)
    override fun add(a: Int, b: Int): Int {
        return a + b
    }

    @Throws(IOException::class)
    override fun substract(a: Int, b: Int): Int {
        return a - b
    }
}