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
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.`when`
import java.util.*
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class RemoveChavePixEndPointTest(
    val repository: ChavePixRepository,
    val grpcClient: KeyManagerRemoveGrpcServiceGrpc.KeyManagerRemoveGrpcServiceBlockingStub
) {

    lateinit var CHAVE_EXISTENTE: ChavePix

    @BeforeEach
    fun setup() {
        CHAVE_EXISTENTE = ChavePix(
            "email@email.com",
            TipoDeChave.EMAIL,
            Conta(
                "0001",
                "0123",
                TipoDeConta.CONTA_POUPANCA,
                Instituicao("itau", "123456"),
                Titular("ae93a61c-0642-43b3-bb8e-a17072295955", "N", "23852310008"),
            )
        )
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
            .setIdCliente(CHAVE_EXISTENTE.conta.titular.id)
            .setIdPix(CHAVE_EXISTENTE.id)
            .build()

        //ação
        val result = grpcClient.remove(request)
        //validação
        assertFalse(repository.existsByChave(CHAVE_EXISTENTE.chave))
        assertEquals(0, repository.count())
        assertEquals(request.idPix,result.idPix)
        assertEquals(request.idCliente, result.idCliente)
    }

    @Test
    internal fun `nao deve remover a chave quando a chave nao pertencer ao cliente`() {
        //cenario
        val request = RemoveChavePixRequest.newBuilder()
            .setIdCliente(UUID.randomUUID().toString())
            .setIdPix(CHAVE_EXISTENTE.id)
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
            .setIdCliente(UUID.randomUUID().toString())
            .setIdPix(UUID.randomUUID().toString())
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

    @Factory
    class Clients {
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel)
                : KeyManagerRemoveGrpcServiceGrpc.KeyManagerRemoveGrpcServiceBlockingStub? {
            return KeyManagerRemoveGrpcServiceGrpc.newBlockingStub(channel)
        }
    }

}