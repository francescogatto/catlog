package net.francescogatto.catlog.internal

/**
 * @author Francesco Gatto
 */
interface LogWriter {

    fun log(log: LogEntity)
    fun delete()

}