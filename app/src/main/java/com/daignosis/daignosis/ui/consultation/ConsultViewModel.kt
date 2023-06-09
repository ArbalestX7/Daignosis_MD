package com.daignosis.daignosis.ui.consultation

import androidx.lifecycle.ViewModel
import com.daignosis.daignosis.data.repository.DaignosisRepository

class ConsultViewModel (private val repository: DaignosisRepository): ViewModel() {

    fun getSession() = repository.getSession()
    fun getToken() = repository.getToken()
    fun rmvSession() = repository.rmvSession()

    fun sendMsg(token:String, message:String, sessionId:String)
    = repository.message(token, message, sessionId)

    fun historyMsg(token: String,sessionId: String) = repository.historyMessage(token, sessionId)
}