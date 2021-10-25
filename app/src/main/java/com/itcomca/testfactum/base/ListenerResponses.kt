package com.itcomca.testfactum.base

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Response

interface ListenerResponses {
    fun onSuccess(error: Boolean, operation: Operations, data: JsonObject)
    fun onError(error: Boolean, msg: String)
}