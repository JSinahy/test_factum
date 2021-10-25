package com.itcomca.testfactum.base

/**
 * Se crea clase enum para determinar cual es el tipo de llamado que se
 * esta haciendo con retrofit para saber a que tipo de dato convertir
 * la informacion regresada
 * **/
enum class Operations {
    PLAYING_MOVIES,
    POPULAR_MOVIES,
    DETAILS_MOVIE
}