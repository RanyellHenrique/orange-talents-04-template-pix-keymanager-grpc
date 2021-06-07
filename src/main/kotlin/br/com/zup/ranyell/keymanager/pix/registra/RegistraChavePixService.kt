package br.com.zup.ranyell.keymanager.pix.registra

import br.com.zup.ranyell.keymanager.compartilhado.excecao.RecursoExistenteException
import br.com.zup.ranyell.keymanager.compartilhado.excecao.RecursoNaoEncontradoException
import br.com.zup.ranyell.keymanager.pix.ChavePix
import br.com.zup.ranyell.keymanager.pix.ChavePixRepository
import br.com.zup.ranyell.keymanager.sistemabcb.CreatePixKeyRequest
import br.com.zup.ranyell.keymanager.sistemabcb.SistemaBCBClient
import br.com.zup.ranyell.keymanager.sistemaitau.SistemaItauClient
import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class RegistraChavePixService(
    @Inject val repository: ChavePixRepository,
    @Inject val itauClient: SistemaItauClient,
    @Inject val bcbClient: SistemaBCBClient
) {
    private val LOGGER = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun registra(@Valid registraChavePix: RegistraChavePix): ChavePix {
        //1 - Verifica se a chave existe
        if (repository.existsByChave(registraChavePix.chave!!)) {
            throw RecursoExistenteException("ChavePix ${registraChavePix.chave} existente")
        }

        //2 - Busca os dados no ITAU
        val responseItau = itauClient.consulta(registraChavePix.idCliente, registraChavePix.tipoDeConta!!.name)
        val conta = responseItau.body()?.toModel() ?: throw RecursoNaoEncontradoException("Cliente não encontrado no Itaú")

        //3 - Grava no banco
        val chave = registraChavePix.toModel(conta)
        repository.save(chave)

        //4 - Registra a chave no BCB
        LOGGER.info("Registrando a chave ${registraChavePix.chave} no BCB")
        val responseBcb = bcbClient.registra(CreatePixKeyRequest(chave))
        if (responseBcb.status != HttpStatus.CREATED) {
            throw  IllegalArgumentException("Erro ao registrar a chave Pix no BCB")
        }
        chave.atualiza(responseBcb.body()!!.key)

        return chave
    }
}