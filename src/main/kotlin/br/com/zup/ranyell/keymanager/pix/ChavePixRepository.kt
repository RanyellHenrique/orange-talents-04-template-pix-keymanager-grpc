package br.com.zup.ranyell.keymanager.pix

import br.com.zup.ranyell.keymanager.conta.Titular
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface ChavePixRepository: JpaRepository<ChavePix, String> {

    fun existsByChave(chave: String): Boolean
    fun findByIdAndContaTitularId(id: String, ContaTitularId: String): Optional<ChavePix>
    fun deleteByChave(chave: String)
}