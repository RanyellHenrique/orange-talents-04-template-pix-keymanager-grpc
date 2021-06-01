package br.com.zup.ranyell.keymanager.pix

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository

@Repository
interface PixRepository: JpaRepository<Pix, String> {

    fun existsByChave(chave: String): Boolean
}