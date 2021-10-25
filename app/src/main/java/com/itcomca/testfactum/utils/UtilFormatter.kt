package com.itcomca.testfactum.utils

import java.lang.Exception
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class UtilFormatter {
    companion object{
        fun formatDate(fecha: String?) : String{
            try {
                if(!fecha.equals("") && !fecha.isNullOrEmpty()){
                    val convertFecha = LocalDate.parse(fecha, DateTimeFormatter.ISO_DATE)
                    val formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy")
                    return formatter.format(convertFecha).capitalize()
                }else {
                    return " - "
                }
            }catch (e: Exception){
                return " - "
            }

        }
    }
}