package dev.woos.toons_api.config.converter

import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import java.time.DayOfWeek

@ReadingConverter
class ReadingDayOfWeekConverter: Converter<String, DayOfWeek> {
    override fun convert(source: String): DayOfWeek {
        return DayOfWeek.valueOf(source)
    }
}

@WritingConverter
class WritingDayOfWeekConverter: Converter<DayOfWeek, String> {
    override fun convert(source: DayOfWeek): String {
        return source.name
    }
}