package br.com.zup.ranyell.keymanager.pix.consulta

import br.com.zup.ranyell.keymanager.compartilhado.excecao.RecursoNaoEncontradoException
import br.com.zup.ranyell.keymanager.compartilhado.validacao.UUIDValid
import br.com.zup.ranyell.keymanager.pix.ChavePixRepository
import br.com.zup.ranyell.keymanager.sistemabcb.CreatePixKeyResponse
import br.com.zup.ranyell.keymanager.sistemabcb.PixKeyResponse
import br.com.zup.ranyell.keymanager.sistemabcb.SistemaBCBClient
import io.micronaut.core.annotation.Introspected
import org.slf4j.LoggerFactory
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Introspected
sealed class Filtro {

    abstract fun filtra(repository: ChavePixRepository, bcbClient: SistemaBCBClient): ChavePixInfo

    @Introspected
    data class PorChave(
        @field:NotBlank @Size(max = 77) val chave: String
    ) : Filtro() {

        private val LOGGER = LoggerFactory.getLogger(this::class.java)

        override fun filtra(repository: ChavePixRepository, bcbClient: SistemaBCBClient): ChavePixInfo {
            LOGGER.info("Buscando a chave ${chave} no BCB")
            val bcbResponse = bcbClient.consulta(chave).body()
                ?: throw RecursoNaoEncontradoException("Chave não encontrada no BCB")
            return bcbResponse.toModel()
        }
    }

    @Introspected
    data class PorPixId(
        @field:UUIDValid
        @field:NotBlank
        val clienteId: String,
        @field:UUIDValid
        @field:NotBlank
        val pixId: String
    ) : Filtro() {

        private val LOGGER = LoggerFactory.getLogger(this::class.java)

        override fun filtra(repository: ChavePixRepository, bcbClient: SistemaBCBClient): ChavePixInfo {
            val chavePix = repository.findByIdAndContaTitularId(pixId, clienteId).orElseThrow {
                throw RecursoNaoEncontradoException("Chave não encontrada")
            }
            LOGGER.info("Buscando a chave ${chavePix.chave} no BCB")
            bcbClient.consulta(chavePix.chave).body()
                ?: throw RecursoNaoEncontradoException("Chave não encontrada no BCB")
            return ChavePixInfo.of(chavePix)
        }

    }

    class Invalido : Filtro() {
        override fun filtra(repository: ChavePixRepository, bcbClient: SistemaBCBClient): ChavePixInfo {
            throw IllegalArgumentException("Chave inválida ou não informada")
        }
    }

}
