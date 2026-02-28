/**
 * Contrato de repositorio de listas de canciones.
 * Incluye operaciones para gestionar listas y su relación con canciones.
 */
package com.domain.repository

import com.domain.models.Cancion
import com.domain.models.ListaCanciones

interface ListaCancionesInterface {
    suspend fun createLista(lista: ListaCanciones): ListaCanciones?
    suspend fun getListasByUsuario(idUsuario: Long): List<ListaCanciones>
    suspend fun getListaById(idLista: Long): ListaCanciones?
    suspend fun deleteLista(idLista: Long): Boolean
    suspend fun addCancionToLista(idLista: Long, idCancion: Int): Boolean
    suspend fun removeCancionFromLista(idLista: Long, idCancion: Int): Boolean
    suspend fun getCancionesByLista(idLista: Long): List<Cancion>
    suspend fun updateLista(id: Long, lista: ListaCanciones): ListaCanciones?
}
