package br.com.zup.ranyell.keymanager.pix.registra

import br.com.zup.ranyell.keymanager.KeyManagerRegistraGrpcServiceGrpc
import br.com.zup.ranyell.keymanager.RegistraChavePixRequest
import br.com.zup.ranyell.keymanager.RegistraChavePixResponse
import br.com.zup.ranyell.keymanager.compartilhado.excecao.handler.ErrorHandler
import br.com.zup.ranyell.keymanager.pix.toModel
import io.grpc.stub.StreamObserver
import javax.inject.Singleton

@ErrorHandler
@Singleton
class RegistraChavePixEndPoint(
    val chavePixService: RegistraChavePixService
) : KeyManagerRegistraGrpcServiceGrpc.KeyManagerRegistraGrpcServiceImplBase() {

    override fun registra(
        request: RegistraChavePixRequest?,
        responseObserver: StreamObserver<RegistraChavePixResponse>?
    ) {
        val pix = chavePixService.registra(request!!.toModel())
        val response = RegistraChavePixResponse.newBuilder()
            .setPixId(pix.id)
            .setClienteId(pix.conta.titular.id)
            .build()

        responseObserver!!.onNext(response)
        responseObserver.onCompleted()
    }
}