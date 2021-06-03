package br.com.zup.ranyell.keymanager.pix.remove

import br.com.zup.ranyell.keymanager.compartilhado.excecao.RecursoNaoEncontradoException
import br.com.zup.ranyell.keymanager.pix.ChavePixRepository
import br.com.zup.ranyell.keymanager.sistemabcb.DeletePixKeyRequest
import br.com.zup.ranyell.keymanager.sistemabcb.SistemaBCBClient
import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import java.lang.IllegalArgumentException
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class RemoveChavePixService(
    @Inject val repository: ChavePixRepository,
    @Inject val bcbClient: SistemaBCBClient
) {

    private val LOGGER = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun remove(@Valid removeChavePix: RemoveChavePix) {
        //Verifica se o id da chave existe e se pertence ao cliente
        val chavePix = repository.findByIdAndContaTitularId(removeChavePix.idPix, removeChavePix.idCliente)
            .orElseThrow { RecursoNaoEncontradoException("Chave pix não encontrada ou não pertence ao cliente") }
        //Remove a chave no BCB
        LOGGER.info("Deletando a chave ${chavePix.chave} no BCB")
        val responseBcb = bcbClient.deleta(
            DeletePixKeyRequest(chavePix.chave, chavePix.conta.instituicao.ispb),
            chavePix.chave)
        if(responseBcb.status != HttpStatus.OK) {
            throw IllegalArgumentException("Erro ao deletar chave no BCB")
        }
        val removeBcb = responseBcb.body()
        //Remove a chave
        repository.deleteByChave(removeBcb!!.key)
    }

}