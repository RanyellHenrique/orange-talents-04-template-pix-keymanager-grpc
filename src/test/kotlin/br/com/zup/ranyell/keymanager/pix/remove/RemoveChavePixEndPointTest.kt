package br.com.zup.ranyell.keymanager.pix.remove


import br.com.zup.ranyell.keymanager.KeyManagerRemoveGrpcServiceGrpc
import br.com.zup.ranyell.keymanager.RemoveChavePixRequest
import br.com.zup.ranyell.keymanager.RemoveChavePixResponse
import br.com.zup.ranyell.keymanager.conta.Conta
import br.com.zup.ranyell.keymanager.conta.Instituicao
import br.com.zup.ranyell.keymanager.conta.TipoDeConta
import br.com.zup.ranyell.keymanager.conta.Titular
import br.com.zup.ranyell.keymanager.pix.ChavePix
import br.com.zup.ranyell.keymanager.pix.ChavePixRepository
import br.com.zup.ranyell.keymanager.pix.TipoDeChave
import br.com.zup.ranyell.keymanager.sistemabcb.DeletePixKeyResponse
import br.com.zup.ranyell.keymanager.sistemabcb.SistemaBCBClient
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.*
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class RemoveChavePixEndPointTest(
    val repository: ChavePixRepository,
    val grpcClient: KeyManagerRemoveGrpcServiceGrpc.KeyManagerRemoveGrpcServiceBlockingStub
) {

    @field:Inject
    lateinit var bcbClient: SistemaBCBClient

    lateinit var CHAVE_EXISTENTE: ChavePix

    @BeforeEach
    fun setup() {
        CHAVE_EXISTENTE = chavePix()
        `when`(bcbClient.deleta(any(), any())).thenReturn(HttpResponse.ok(deletePixResponse()))
        repository.save(CHAVE_EXISTENTE)
    }

    @AfterEach
    fun cleanUp() {
        repository.deleteAll()
    }

    @Test
    internal fun `deve remover a chave quando a chave existir e pertencer ao cliente`() {
        //cenario
        val request = RemoveChavePixRequest.newBuilder()
            .setClienteId(CHAVE_EXISTENTE.conta.titular.id)
            .setPixId(CHAVE_EXISTENTE.id)
            .build()

        //ação
        val result = grpcClient.remove(request)
        //validação
        assertFalse(repository.existsByChave(CHAVE_EXISTENTE.chave))
        assertEquals(0, repository.count())
        assertEquals(request.pixId, result.pixId)
        assertEquals(request.clienteId, result.clienteId)
    }

    @Test
    internal fun `nao deve remover a chave quando a chave nao pertencer ao cliente`() {
        //cenario
        val request = RemoveChavePixRequest.newBuilder()
            .setClienteId(UUID.randomUUID().toString())
            .setPixId(CHAVE_EXISTENTE.id)
            .build()
        //ação
        val result = assertThrows<StatusRuntimeException> {
            grpcClient.remove(request)
        }
        //validação
        with(result) {
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave pix não encontrada ou não pertence ao cliente", status.description)
        }
    }

    @Test
    internal fun `nao deve remover a chave quando a chave nao existir`() {
        //cenario
        val request = RemoveChavePixRequest.newBuilder()
            .setClienteId(UUID.randomUUID().toString())
            .setPixId(UUID.randomUUID().toString())
            .build()
        //ação
        val result = assertThrows<StatusRuntimeException> {
            grpcClient.remove(request)
        }
        //validação
        with(result) {
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave pix não encontrada ou não pertence ao cliente", status.description)
        }
    }

    @Test
    internal fun `nao deve remover a chave quando nao for removida no servico BCB`() {
        //cenário
        val request = RemoveChavePixRequest.newBuilder()
            .setClienteId(CHAVE_EXISTENTE.conta.titular.id)
            .setPixId(CHAVE_EXISTENTE.id)
            .build()
        `when`(bcbClient.deleta(any(), any())).thenReturn(HttpResponse.badRequest())
        //ação
        val result = assertThrows<StatusRuntimeException> {
            grpcClient.remove(request)
        }
        //validação
        with(result) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Erro ao deletar chave no BCB", status.description)
        }
    }

    @MockBean(SistemaBCBClient::class)
    fun bcbMock(): SistemaBCBClient {
        return mock(SistemaBCBClient::class.java)
    }

    @Factory
    class Clients {
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel)
                : KeyManagerRemoveGrpcServiceGrpc.KeyManagerRemoveGrpcServiceBlockingStub? {
            return KeyManagerRemoveGrpcServiceGrpc.newBlockingStub(channel)
        }
    }

    private fun chavePix(): ChavePix {
        return ChavePix(
            chave = "email@email.com",
            tipoDeChave = TipoDeChave.EMAIL,
            conta = Conta(
                agencia = "0001",
                numero = "0123",
                tipo = TipoDeConta.CONTA_POUPANCA,
                instituicao = Instituicao("itau", "123456"),
                titular = Titular("ae93a61c-0642-43b3-bb8e-a17072295955", "N", "23852310008"),
            )
        )
    }

    private fun deletePixResponse(): DeletePixKeyResponse{
        return DeletePixKeyResponse(
            key = CHAVE_EXISTENTE.chave,
            participant = "0657123",
            deletedAt = LocalDateTime.now()
        )
    }

}