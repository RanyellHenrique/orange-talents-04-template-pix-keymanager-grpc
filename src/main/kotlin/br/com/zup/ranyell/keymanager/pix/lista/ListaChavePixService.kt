package br.com.zup.ranyell.keymanager.pix.lista

import br.com.zup.ranyell.keymanager.compartilhado.validacao.UUIDValid
import br.com.zup.ranyell.keymanager.pix.ChavePixRepository
import io.micronaut.validation.Validated
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@Validated
@Singleton
class ListaChavePixService(
    @Inject val repository: ChavePixRepository
) {

    fun lista(@Valid @UUIDValid @NotBlank clienteId: String): List<ListaChavePix>{
        return repository.findByContaTitularId(clienteId).map(::ListaChavePix)
    }
}