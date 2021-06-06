package br.com.zup.ranyell.keymanager.pix.lista

import br.com.zup.ranyell.keymanager.KeyManagerListaChavePixPorClienteGrpcServiceGrpc
import br.com.zup.ranyell.keymanager.KeyManagerRegistraGrpcServiceGrpc
import br.com.zup.ranyell.keymanager.ListaChavePixPorClienteRequest
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
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class ListaChavePixEndPointTest(
    val repository: ChavePixRepository,
    val grpcClient: KeyManagerListaChavePixPorClienteGrpcServiceGrpc.KeyManagerListaChavePixPorClienteGrpcServiceBlockingStub
) {

    @BeforeEach
    fun setUp() {
        repository.save(chavePix())
        repository.save(chavePix())
    }

    @AfterEach
    fun cleanUp() {
        repository.deleteAll()
    }

    @Test
    internal fun `deve retornar a lista de chaves quando existir`() {
        //cenário
        val request = ListaChavePixPorClienteRequest.newBuilder().setClientId(chavePix().conta.titular.id).build()
        //ação
        val result = grpcClient.lista(request)
        //avaliação
        with(result) {
            assertEquals(2, result.chavesList.size)
            assertEquals(chavePix().conta.titular.id, result.chavesList[0].clienteId)
            assertEquals(br.com.zup.ranyell.keymanager.TipoDeConta.CONTA_POUPANCA, result.chavesList[0].tipoDeConta)
        }
    }

    @Test
    internal fun `deve retornar uma lista vazia quando nao existir chaves cadastradas`() {
        //cenário
        val request = ListaChavePixPorClienteRequest.newBuilder()
            .setClientId("ae93a61c-0652-43b3-bb8e-a17072295957")
            .build()
        //ação
        val result = grpcClient.lista(request)
        //avaliação
        with(result) {
            assertTrue(result.chavesList.isEmpty())
        }
    }

    @Test
    internal fun `nao deve retornar uma lista quando o id do cliente for invalido`() {
        //cenário
        val request = ListaChavePixPorClienteRequest.newBuilder()
            .setClientId("id inválido")
            .build()
        //ação
        val result = assertThrows<StatusRuntimeException> {
            grpcClient.lista(request)
        }
        //avaliação
        with(result) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("lista.clienteId: Não é um formato válido de UUID", status.description)
        }
    }

    @Factory
    class Clients {
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel)
                : KeyManagerListaChavePixPorClienteGrpcServiceGrpc.KeyManagerListaChavePixPorClienteGrpcServiceBlockingStub? {
            return KeyManagerListaChavePixPorClienteGrpcServiceGrpc.newBlockingStub(channel)
        }
    }

    private fun chavePix(): ChavePix {
        return ChavePix(
            chave = UUID.randomUUID().toString(),
            tipoDeChave = TipoDeChave.CHAVE_ALEATORIA,
            conta = Conta(
                agencia = "0001",
                numero = "01234",
                tipo = TipoDeConta.CONTA_POUPANCA,
                instituicao = Instituicao("itau", "60701190"),
                titular = Titular("ae93a61c-0652-43b3-bb8e-a17072295955", "Bob Brow", "63657520325"),
            )
        )
    }


}