package com.itcomca.testfactum.repositories.models

import java.io.Serializable

/** Clase creada para capturar los detalles de las peliculas al dar click **/
class MovieDetailModel(
    var movieId : String = "",
    var pathImage: String = "",
    var title: String = "",
    var subtitle: String = "",
    var sinopsis: String = ""
) : Serializable