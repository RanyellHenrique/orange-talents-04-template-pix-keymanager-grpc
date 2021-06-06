package br.com.zup.ranyell.keymanager.pix.registra

import br.com.zup.ranyell.keymanager.KeyManagerRegistraGrpcServiceGrpc
import br.com.zup.ranyell.keymanager.RegistraChavePixRequest
import br.com.zup.ranyell.keymanager.TipoDeChave
import br.com.zup.ranyell.keymanager.TipoDeConta
import br.com.zup.ranyell.keymanager.pix.ChavePixRepository
import br.com.zup.ranyell.keymanager.sistemabcb.*
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
import org.mockito.Mockito.*
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class RegistraChavePixEndPointTest(
    val repository: ChavePixRepository,
    val grpcClient: KeyManagerRegistraGrpcServiceGrpc.KeyManagerRegistraGrpcServiceBlockingStub
) {

    @field:Inject
    lateinit var itauClient: SistemaItauClient

    @field:Inject
    lateinit var bcbClient: SistemaBCBClient

    @BeforeEach
    fun setup() {
        `when`(bcbClient.registra(any(CreatePixKeyRequest::class.java))).thenReturn(
            HttpResponse.created(
                createPixKeyResponse()
            )
        )
        `when`(
            itauClient.consulta(
                "ae93a61c-0652-43b3-bb8e-a17072295955",
                "CONTA_POUPANCA"
            )
        ).thenReturn(HttpResponse.ok(contaResponse()))
        repository.deleteAll()
    }

    @Test
    internal fun `deve cadastar uma nova chave pix`() {
        //ação
        val result = grpcClient.registra(registraChavePixRequest())
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
            .setClienteId("ae93a61c-0652-43b3-bb8e-a17072295955")
            .setTipoDeChave(TipoDeChave.CHAVE_ALEATORIA)
            .build()
        //ação
        val result = grpcClient.registra(novaChavePixAleatoria)
        //validação
        assertTrue(repository.existsById(result.pixId))
        assertNotNull(result.pixId)
        assertEquals(1, repository.count())
    }

    @Test
    internal fun `nao deve cadastrar uma nova chave quando o tipo de chave nao for informado`() {
        //cenário
        val novachavePixInvalida = RegistraChavePixRequest.newBuilder()
            .setChave("1233556")
            .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
            .setClienteId("ae93a61c-0652-43b3-bb8e-a17072295955")
            .build()
        //ação
        val result = assertThrows<StatusRuntimeException> {
            grpcClient.registra(novachavePixInvalida)
        }
        //validação
        with(result) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }
    }

    @Test
    internal fun `nao deve cadastrar uma nova chave quando o tipo de conta nao for informado`() {
        //cenário
        val novachavePixInvalida = RegistraChavePixRequest.newBuilder()
            .setChave("1233556")
            .setTipoDeChave(TipoDeChave.CHAVE_ALEATORIA)
            .setClienteId("ae93a61c-0652-43b3-bb8e-a17072295955")
            .build()
        //ação
        val result = assertThrows<StatusRuntimeException> {
            grpcClient.registra(novachavePixInvalida)
        }
        //validação
        with(result) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }
    }

    @Test
    internal fun `nao deve cadastrar uma nova chave pix quando a chave ja existir`() {
        //cenário
        grpcClient.registra(registraChavePixRequest())
        //ação
        val result = assertThrows<StatusRuntimeException> {
            grpcClient.registra(registraChavePixRequest())
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
            .setClienteId("ae93a61c-0652-43b3-bb8e-a17072295955")
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
            grpcClient.registra(registraChavePixRequest())
        }
        //validacao
        with(result) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals(0, repository.count())
        }
    }

    @Test
    internal fun `nao deve cadastrar uma nova chave quando o sistema BCB nao cadastrar uma nova chave`() {
        //cenário
        `when`(bcbClient.registra(any(CreatePixKeyRequest::class.java))).thenReturn(HttpResponse.badRequest())
        //ação
        val result = assertThrows<StatusRuntimeException> {
            grpcClient.registra(registraChavePixRequest())
        }
        //validação
        with(result) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Erro ao registrar a chave Pix no BCB", status.description)
            assertEquals(0, repository.count())
        }

    }

    @MockBean(SistemaItauClient::class)
    fun itauMock(): SistemaItauClient {
        return mock(SistemaItauClient::class.java)
    }


    @MockBean(SistemaBCBClient::class)
    fun bcbMock(): SistemaBCBClient {
        return mock(SistemaBCBClient::class.java)
    }

    @Factory
    class Clients {
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel)
                : KeyManagerRegistraGrpcServiceGrpc.KeyManagerRegistraGrpcServiceBlockingStub? {
            return KeyManagerRegistraGrpcServiceGrpc.newBlockingStub(channel)
        }
    }

    private fun createPixKeyResponse(): CreatePixKeyResponse {
        return CreatePixKeyResponse(
            keyType = KeyType.CPF,
            key = "23852310008",
            bankAccount = bankAccount(),
            owner = owner(),
            createdAt = LocalDateTime.now()
        )
    }

    private fun createPixKeyRequest(): CreatePixKeyRequest {
        return CreatePixKeyRequest(
            keyType = KeyType.CPF,
            key = "23852310008",
            bankAccount = bankAccount(),
            owner = owner()
        )
    }

    private fun bankAccount(): BankAccount {
        return BankAccount(
            participant = "123456",
            branch = "1218",
            accountNumber = "291900",
            accountType = AccountType.CACC
        )
    }

    private fun owner(): Owner {
        return Owner(
            name = "Rafael Ponte",
            taxIdNumber = "63657520325"
        )
    }

    private fun contaResponse(): ContaResponse {
        return ContaResponse(
            agencia = "0001",
            numero = "012345",
            titular = TitularResponse("ae93a61c-0652-43b3-bb8e-a17072295955", "N", "23852310008"),
            instituicao = IntituicaoResponse("itau", "123456"),
            tipo = TipoDeConta.CONTA_POUPANCA
        )
    }

    private fun registraChavePixRequest(): RegistraChavePixRequest {
        return RegistraChavePixRequest.newBuilder()
            .setChave("23852310008")
            .setTipoDeChave(TipoDeChave.CPF)
            .setTipoDeConta(TipoDeConta.CONTA_POUPANCA)
            .setClienteId("ae93a61c-0652-43b3-bb8e-a17072295955")
            .build()
    }

}