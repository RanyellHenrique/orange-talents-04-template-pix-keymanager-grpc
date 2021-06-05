package br.com.zup.ranyell.keymanager.pix

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface ChavePixRepository: JpaRepository<ChavePix, String> {

    fun existsByChave(chave: String): Boolean
    fun findByIdAndContaTitularId(id: String, contaTitularId: String): Optional<ChavePix>
    fun deleteByChave(chave: String)
    fun findByContaTitularId(contaTitularId: String): List<ChavePix>
}