package br.com.zup.ranyell.keymanager.pix.consulta

import br.com.zup.ranyell.keymanager.ConsultaChavePixRequest
import br.com.zup.ranyell.keymanager.ConsultaChavePixRequest.*
import br.com.zup.ranyell.keymanager.KeyManagerConsultaGrpcServiceGrpc
import br.com.zup.ranyell.keymanager.TipoDeChave
import br.com.zup.ranyell.keymanager.conta.Conta
import br.com.zup.ranyell.keymanager.conta.Instituicao
import br.com.zup.ranyell.keymanager.conta.TipoDeConta
import br.com.zup.ranyell.keymanager.conta.Titular
import br.com.zup.ranyell.keymanager.pix.ChavePix
import br.com.zup.ranyell.keymanager.pix.ChavePixRepository
import br.com.zup.ranyell.keymanager.sistemabcb.*
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class ConsultaChavePixEndPointTest(
    val repository: ChavePixRepository,
    val grpcClient: KeyManagerConsultaGrpcServiceGrpc.KeyManagerConsultaGrpcServiceBlockingStub
) {

    @field:Inject
    lateinit var bcbClient: SistemaBCBClient

    lateinit var CHAVE_EXISTENTE: ChavePix

    @BeforeEach
    fun setUp() {
        CHAVE_EXISTENTE = chavePix()
        repository.save(CHAVE_EXISTENTE)
    }

    @AfterEach
    fun cleanUp() {
        repository.deleteAll()
    }

    @Test
    internal fun `deve retornar a chave pix quando existir no BCB`() {
        //cenário
        `when`(bcbClient.consulta("email@email.com")).thenReturn(HttpResponse.ok(pixKeyResponse()))
        //ação
        val result = grpcClient.consulta(consultaChavePixRequestPorChave())
        //validacao
        with(result) {
            assertEquals("email@email.com", chave)
            assertEquals("ITAÚ UNIBANCO S.A.", conta.nome)
            assertEquals(TipoDeChave.EMAIL.name, tipoDeChave.name)
            assertEquals("", clientId)
            assertEquals("", pixId)
        }
    }

    @Test
    internal fun `deve retornar a chave pix quando existir no sistema e no BCB`() {
        //cenário
        `when`(bcbClient.consulta(chavePix().chave))
            .thenReturn(HttpResponse.ok(pixKeyResponse()))
        //ação
        val result = grpcClient.consulta(consultaChavePixRequestPorPixId())
        //validação
        with(result) {
            assertEquals(CHAVE_EXISTENTE.conta.titular.id, clientId)
            assertEquals(CHAVE_EXISTENTE.id!!, pixId)
            assertEquals("email@email.com", chave)
            assertEquals("ITAÚ UNIBANCO S.A.", conta.nome)
            assertEquals(TipoDeChave.EMAIL.name, tipoDeChave.name)
        }
    }

    @Test
    internal fun `nao deve retornar a chave quando nao existir no BCB`() {
        //cenário
        `when`(bcbClient.consulta(chavePix().chave)).thenReturn(HttpResponse.notFound())
        //ação
        val result = assertThrows<StatusRuntimeException> {
            grpcClient.consulta(consultaChavePixRequestPorChave())
        }
        //validação
        with(result) {
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave não encontrada no BCB", status.description)
        }
    }

    @Test
    internal fun `nao deve retornar a chave quando existir no sistema, mas nao existir no BCB`() {
        //cenário
        `when`(bcbClient.consulta(chavePix().chave)).thenReturn(HttpResponse.notFound())
        //ação
        val result = assertThrows<StatusRuntimeException> {
            grpcClient.consulta(consultaChavePixRequestPorPixId())
        }
        //validação
        with(result) {
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave não encontrada no BCB", status.description)
        }
    }

    @Test
    internal fun `nao deve retornar a chave quando nao existir no sistema e no BCB`() {
        //cenário
        repository.deleteAll()
        `when`(bcbClient.consulta(chavePix().chave)).thenReturn(HttpResponse.notFound())
        //ação
        val result = assertThrows<StatusRuntimeException> {
            grpcClient.consulta(consultaChavePixRequestPorPixId())
        }
        //validação
        with(result) {
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave não encontrada", status.description)
        }
    }

    @Test
    internal fun `nao deve retornar a chave quando os argumentos forem invalidos`() {
        //ação
        val result = assertThrows<StatusRuntimeException> {
            grpcClient.consulta(consultaChavePixRequestInvalido())
        }
        //validação
        with(result) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertTrue("pixId: Não é um formato válido de UUID".toRegex().containsMatchIn(status.description!!))
            assertTrue("clienteId: Não é um formato válido de UUID".toRegex().containsMatchIn(status.description!!))
        }
    }

    @Test
    internal fun `nao deve retornar a chave quando a chave for invalida`() {
        //ação
        val result = assertThrows<StatusRuntimeException> {
            grpcClient.consulta(newBuilder().setChave("").build())
        }
        //validação
        with(result) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertTrue("não deve estar em branco".toRegex().containsMatchIn(status.description!!))
        }
    }

    fun consultaChavePixRequestInvalido(): ConsultaChavePixRequest {
        return newBuilder().setPixId(
            FiltroPorPixId
                .newBuilder()
                .setClienteId("Cliente id invalido")
                .setPixId("Pix id invalido")
                .build()
        ).build()
    }

    @MockBean(SistemaBCBClient::class)
    fun bcbMock(): SistemaBCBClient {
        return mock(SistemaBCBClient::class.java)
    }

    @Factory
    class Clients {
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel)
                : KeyManagerConsultaGrpcServiceGrpc.KeyManagerConsultaGrpcServiceBlockingStub? {
            return KeyManagerConsultaGrpcServiceGrpc.newBlockingStub(channel)
        }
    }

    private fun pixKeyResponse(): PixKeyResponse {
        return PixKeyResponse(
            keyType = KeyType.EMAIL,
            key = "email@email.com",
            bankAccount = BankAccount(
                participant = "60701190",
                branch = "0001",
                accountNumber = "01234",
                accountType = AccountType.SVGS
            ),
            owner = Owner(
                name = "Bob Brow",
                taxIdNumber = "63657520325"
            ),
            createdAt = LocalDateTime.now()
        )
    }

    private fun chavePix(): ChavePix {
        return ChavePix(
            chave = "email@email.com",
            tipoDeChave = br.com.zup.ranyell.keymanager.pix.TipoDeChave.EMAIL,
            conta = Conta(
                agencia = "0001",
                numero = "01234",
                tipo = TipoDeConta.CONTA_POUPANCA,
                instituicao = Instituicao("itau", "60701190"),
                titular = Titular("ae93a61c-0652-43b3-bb8e-a17072295955", "Bob Brow", "63657520325"),
            )
        )
    }

    fun consultaChavePixRequestPorChave(): ConsultaChavePixRequest {
        return newBuilder()
            .setChave("email@email.com")
            .build()
    }

    fun consultaChavePixRequestPorPixId(): ConsultaChavePixRequest {
        return newBuilder()
            .setPixId(
                FiltroPorPixId.newBuilder()
                    .setPixId(CHAVE_EXISTENTE.id!!)
                    .setClienteId(CHAVE_EXISTENTE.conta.titular.id)
                    .build()
            ).build()
    }

}
