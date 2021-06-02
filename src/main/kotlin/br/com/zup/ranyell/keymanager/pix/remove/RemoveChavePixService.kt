package br.com.zup.ranyell.keymanager.pix.remove

import br.com.zup.ranyell.keymanager.compartilhado.excecao.RecursoNaoEncontradoException
import br.com.zup.ranyell.keymanager.pix.ChavePixRepository
import br.com.zup.ranyell.keymanager.sistemaitau.SistemaItauClient
import io.micronaut.validation.Validated
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class RemoveChavePixService(
    @Inject val repository: ChavePixRepository,
) {

    @Transactional
    fun remove(@Valid removeChavePix: RemoveChavePix) {
        //Verifica se o id da chave existe e se pertence ao cliente
        repository.findByIdAndContaTitularId(removeChavePix.idPix, removeChavePix.idCliente).orElseThrow {
            RecursoNaoEncontradoException("Chave pix não encontrada ou não pertence ao cliente")
        }
        //Remove a chave
        repository.deleteById(removeChavePix.idPix)
    }

}