package net.francescogatto.catlog

import timber.log.Timber


class CatLogTree(private val catLog: CatLog) : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) =
            catLog.log(priority, tag, message)

    companion object {
        fun with(catLog: CatLog) = CatLogTree(catLog)
    }

}