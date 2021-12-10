package maliauka.sasha.yandexstocks.data.util

enum class Interval {
    WEEK {
        override val intervalInMillis: Long
            get() = 604_800_000L
    },
    MONTH {
        override val intervalInMillis: Long
            get() = 2_629_746_000L
    },
    SIX_MONTHS {
        override val intervalInMillis: Long
            get() = 15_778_476_000
    },
    YEAR {
        override val intervalInMillis: Long
            get() = 31_556_952_000L
    },
    ALL {
        override val intervalInMillis: Long
            get() = 0
    };

    abstract val intervalInMillis: Long

    fun getFromDate(): String {
        return if (intervalInMillis != 0L)
            getDateForMillis(System.currentTimeMillis() - intervalInMillis)
        else ""
    }

    fun getToDate(): String {
        return if (intervalInMillis != 0L)
            getCurrentDate()
        else ""
    }
}

