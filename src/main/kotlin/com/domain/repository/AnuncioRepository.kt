package com.domain.repository

import com.domain.models.Anuncio

interface AnuncioRepository {
    suspend fun getRandomAd(): Anuncio?
}
