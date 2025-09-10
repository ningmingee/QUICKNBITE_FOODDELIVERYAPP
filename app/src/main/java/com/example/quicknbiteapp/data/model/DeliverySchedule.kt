package com.example.quicknbiteapp.data.model

import java.time.LocalTime
import java.time.format.DateTimeFormatter

class DeliverySchedule(
    startTime: LocalTime = LocalTime.of(10, 0),
    endTime: LocalTime = LocalTime.of(18, 0),
    intervalMinutes: Int = 30
) {
    val timeSlots: List<String>
    var selectedTime: String = ""
        private set

    init {
        val formatter = DateTimeFormatter.ofPattern("hh:mm a")
        val slots = mutableListOf<String>()
        var current = startTime
        while (current.isBefore(endTime)) {
            val next = current.plusMinutes(intervalMinutes.toLong())
            slots.add("${current.format(formatter)} - ${next.format(formatter)}")
            current = next
        }
        timeSlots = slots
    }

    fun selectTime(slot: String) {
        if (timeSlots.contains(slot)) selectedTime = slot
    }
}