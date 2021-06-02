package br.com.zup.ranyell.keymanager.pix.registra

import br.com.zup.ranyell.keymanager.compartilhado.excecao.RecursoExistenteException
import br.com.zup.ranyell.keymanager.pix.ChavePix
import br.com.zup.ranyell.keymanager.pix.ChavePixRepository
import br.com.zup.ranyell.keymanager.sistemaitau.SistemaItauClient
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import java.lang.IllegalArgumentException
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class RegistraChavePixService(
    @Inject val repository: ChavePixRepository,
    @Inject val itauClient: SistemaItauClient
) {
    private val LOGGER = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun registra(@Valid registraChavePix: RegistraChavePix): ChavePix {
        //1 - Verifica se a chave existe
        if (repository.existsByChave(registraChavePix.chave!!)) {
            throw RecursoExistenteException("ChavePix ${registraChavePix.chave} existente")
        }
        //2 - Busca os dados no ITAU
        val response = itauClient.consulta(registraChavePix.idCliente, registraChavePix.tipoDeConta!!.name)
        val conta = response.body()?.toModel() ?: throw IllegalArgumentException("Cliente não encontrado no Itaú")
        //3 - Grava no banco
        return repository.save(registraChavePix.toModel(conta))
    }
}