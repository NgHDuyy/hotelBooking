package com.example.myBookingHotel.viewmodel.utils.extension

fun<T> List<T>.copy(): MutableList<T> {
    val listCopy = mutableListOf<T>()
    forEach { listCopy.add(it) }
    return listCopy
}