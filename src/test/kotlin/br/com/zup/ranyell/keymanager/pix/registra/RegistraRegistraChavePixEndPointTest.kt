package br.com.zup.ranyell.keymanager.pix.registra

import br.com.zup.ranyell.keymanager.KeyManagerRegistraGrpcServiceGrpc
import br.com.zup.ranyell.keymanager.RegistraChavePixRequest
import br.com.zup.ranyell.keymanager.TipoDeChave
import br.com.zup.ranyell.keymanager.TipoDeConta
import br.com.zup.ranyell.keymanager.pix.ChavePixRepository
import br.com.zup.ranyell.keymanager.sistemaitau.ContaResponse
import br.com.zup.ranyell.keymanager.sistemaitau.IntituicaoResponse
import br.com.zup.ranyell.keymanager.sistemaitau.SistemaItauClient
import br.com.zup.ranyell.keymanager.sistemaitau.TitularResponse
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class RegistraRegistraChavePixEndPointTest(
    val repository: ChavePixRepository,
    val grpcClient: KeyManagerRegistraGrpcServiceGrpc.KeyManagerRegistraGrpcServiceBlockingStub
) {

    @field:Inject
    lateinit var itauClient: SistemaItauClient

    lateinit var novaChavePixRequest: RegistraChavePixRequest

    lateinit var contaResponse: ContaResponse

    @BeforeEach
    fun setup() {
        contaResponse = ContaResponse(
            "0001",
            "0123",
            TitularResponse("ae93a61c-0652-43b3-bb8e-a17072295955", "N", "23852310008"),
            IntituicaoResponse("itau", "123456"),
            TipoDeConta.CONTA_POUPANCA
        )
        novaChavePixRequest = RegistraChavePixRequest.newBuilder()
            .setChave("23852310008")
            .setTipoDeChave(TipoDeChave.CPF)
            .setTipoDeConta(TipoDeConta.CONTA_POUPANCA)
            .setIdCliente("ae93a61c-0652-43b3-bb8e-a17072295955")
            .build()
        repository.deleteAll()
    }

    @Test
    internal fun `deve cadastar uma nova chave pix`() {
        //cenário
        `when`(itauClient.consulta("ae93a61c-0652-43b3-bb8e-a17072295955", "CONTA_POUPANCA"))
            .thenReturn(HttpResponse.ok(contaResponse))
        //ação
        val result = grpcClient.registra(novaChavePixRequest)
        //validação
        assertTrue(repository.existsById(result.pixId))
        assertNotNull(result.pixId)
        assertEquals(1, repository.count())
    }

    @Test
    internal fun `deve cadastrar uma nova chave quando nao for passado nenhuma chave e o tipo de chave for aleatoria`() {
        //cenário
        val novaChavePixAleatoria = RegistraChavePixRequest.newBuilder()
            .setTipoDeConta(TipoDeConta.CONTA_POUPANCA)
            .setIdCliente("ae93a61c-0652-43b3-bb8e-a17072295955")
            .setTipoDeChave(TipoDeChave.CHAVE_ALEATORIA)
            .build()

        `when`(itauClient.consulta("ae93a61c-0652-43b3-bb8e-a17072295955", "CONTA_POUPANCA"))
            .thenReturn(HttpResponse.ok(contaResponse))
        //ação
        val result = grpcClient.registra(novaChavePixAleatoria)
        //validação
        assertTrue(repository.existsById(result.pixId))
        assertNotNull(result.pixId)
        assertEquals(1, repository.count())
    }

    @Test
    internal fun `nao deve cadastrar uma nova chave pix quando a chave ja existir`() {
        //cenário
        `when`(itauClient.consulta("ae93a61c-0652-43b3-bb8e-a17072295955", "CONTA_POUPANCA"))
            .thenReturn(HttpResponse.ok(contaResponse))
        grpcClient.registra(novaChavePixRequest)
        //ação
        val result = assertThrows<StatusRuntimeException> {
            grpcClient.registra(novaChavePixRequest)
        }
        //validação
        with(result) {
            assertEquals(Status.ALREADY_EXISTS.code, status.code)
            assertEquals("ChavePix 23852310008 existente", status.description)
            assertEquals(1, repository.count())
        }
    }

    @Test
    internal fun `nao deve cadastrar uma nova chave pix quando  a chave nao tiver o padrao estabelecido`() {
        //cenário
        val novachavePixInvalida = RegistraChavePixRequest.newBuilder()
            .setChave("não tem i padrão")
            .setIdCliente("ae93a61c-0652-43b3-bb8e-a17072295955")
            .setTipoDeChave(TipoDeChave.EMAIL)
            .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
            .build()
        //ação
        val result = assertThrows<StatusRuntimeException> {
            grpcClient.registra(novachavePixInvalida)
        }
        //validação
        with(result) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals(0, repository.count())
        }
    }

    @Test
    internal fun `nao deve cadastrar uma nova chave quando  o id do cliente nao existir`() {
        //cenário
        `when`(itauClient.consulta("ae93a61c-0652-43b3-bb8e-a17072295955", "CONTA_POUPANCA"))
            .thenReturn(HttpResponse.notFound())
        //ação
        val result = assertThrows<StatusRuntimeException> {
            grpcClient.registra(novaChavePixRequest)
        }
        //validacao
        with(result) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals(0, repository.count())
        }
    }


    @MockBean(SistemaItauClient::class)
    fun itauMock(): SistemaItauClient {
        return mock(SistemaItauClient::class.java)
    }

    @Factory
    class Clients {
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel)
                : KeyManagerRegistraGrpcServiceGrpc.KeyManagerRegistraGrpcServiceBlockingStub? {
            return KeyManagerRegistraGrpcServiceGrpc.newBlockingStub(channel)
        }
    }

}