package br.com.zup.ranyell.keymanager.pix.registra

import br.com.zup.ranyell.keymanager.KeyManagerServiceGrpc
import br.com.zup.ranyell.keymanager.NovaChavePixRequest
import br.com.zup.ranyell.keymanager.NovaChavePixResponse
import br.com.zup.ranyell.keymanager.compartilhado.excecao.handler.ErrorHandler
import br.com.zup.ranyell.keymanager.pix.toModel
import io.grpc.stub.StreamObserver
import javax.inject.Singleton

@ErrorHandler
@Singleton
class PixGRPCServer(
    val chavePixService: NovaChavePixService
) : KeyManagerServiceGrpc.KeyManagerServiceImplBase() {

    override fun adicionaNovaChavePix(
        request: NovaChavePixRequest?,
        responseObserver: StreamObserver<NovaChavePixResponse>?
    ) {
        val pix = chavePixService.registra(request!!.toModel())
        val response = NovaChavePixResponse.newBuilder()
            .setPixId(pix.id)
            .build()

        responseObserver!!.onNext(response)
        responseObserver.onCompleted()
    }

}