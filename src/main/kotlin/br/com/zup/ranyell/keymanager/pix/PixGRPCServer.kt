package br.com.zup.ranyell.keymanager.pix

import br.com.zup.ranyell.keymanager.ErrorDetails
import br.com.zup.ranyell.keymanager.KeyManagerServiceGrpc
import br.com.zup.ranyell.keymanager.NovaChavePixRequest
import br.com.zup.ranyell.keymanager.NovaChavePixResponse
import br.com.zup.ranyell.keymanager.sistemaitau.SistemaItauClient
import com.google.protobuf.Any
import com.google.rpc.Code
import com.google.rpc.Status as StatusGoogle
import io.grpc.Status
import io.grpc.protobuf.StatusProto
import io.grpc.stub.StreamObserver
import io.micronaut.http.client.exceptions.HttpClientResponseException
import org.valiktor.ConstraintViolationException
import org.valiktor.i18n.mapToMessage
import javax.inject.Singleton

@Singleton
class PixGRPCServer(
    val sistemaItauClient: SistemaItauClient,
    val pixRepository: PixRepository
) : KeyManagerServiceGrpc.KeyManagerServiceImplBase() {

    override fun adicionaNovaChavePix(
        request: NovaChavePixRequest?,
        responseObserver: StreamObserver<NovaChavePixResponse>?
    ) {
        try {
            val pixRequest = PixRequest(request!!)
            val contaResponse = sistemaItauClient.consulta(request.idCliente, request.tipoDeConta.name).body()
            if (pixRepository.existsByChave(request.chave)) {
                throw IllegalArgumentException("Chave já existe")
            }
            val pix = pixRepository.save(pixRequest.toModel(contaResponse.toModel()))
            val response = NovaChavePixResponse.newBuilder()
                .setPixId(pix.id)
                .build()

            responseObserver!!.onNext(response)
            responseObserver.onCompleted()

        } catch (ex: ConstraintViolationException) {
            val error = ex.constraintViolations
                .mapToMessage(baseName = "messages")
                .map {
                    Any.pack(
                        ErrorDetails.newBuilder()
                            .setCode(400)
                            .setMessage(it.message)
                            .build()
                    )
                }.toList()
            val statusProto = StatusGoogle.newBuilder()
                .setCode(Code.INVALID_ARGUMENT.number)
                .setMessage("Argumento(s) inválidos")
                .addAllDetails(error)
                .build()

            responseObserver?.onError(StatusProto.toStatusRuntimeException(statusProto))
        } catch (ex: IllegalArgumentException) {
            val erro = Status.ALREADY_EXISTS
                .withDescription("Chave inválida")
                .augmentDescription(ex.message)
                .asRuntimeException()

            responseObserver?.onError(erro)
        } catch (ex: HttpClientResponseException) {
            val erro = Status.NOT_FOUND
                .withDescription("Cliente não encontrado")
                .augmentDescription(ex.message)
                .asRuntimeException()
            responseObserver?.onError(erro)
        }
    }

}