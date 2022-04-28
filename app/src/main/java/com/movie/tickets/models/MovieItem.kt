package com.movie.tickets.models

import java.io.Serializable

class MovieItem : Serializable{
    var id : String? = null
    var name : String? = null
    var imageLink : String = ""
    var maxSeats : String? = null
}