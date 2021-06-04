package br.com.zup.ranyell.keymanager.pix.remove

import br.com.zup.ranyell.keymanager.KeyManagerRemoveGrpcServiceGrpc
import br.com.zup.ranyell.keymanager.RemoveChavePixRequest
import br.com.zup.ranyell.keymanager.RemoveChavePixResponse
import br.com.zup.ranyell.keymanager.compartilhado.excecao.handler.ErrorHandler
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@ErrorHandler
@Singleton
class RemoveChavePixEndPoint(
    @Inject val removeChavePixService: RemoveChavePixService
) : KeyManagerRemoveGrpcServiceGrpc.KeyManagerRemoveGrpcServiceImplBase() {

    override fun remove(request: RemoveChavePixRequest?, responseObserver: StreamObserver<RemoveChavePixResponse>?) {
        removeChavePixService.remove(request!!.toModel())
        val response = RemoveChavePixResponse.newBuilder()
            .setClienteId(request.clienteId)
            .setPixId(request.pixId)
            .build()

        responseObserver!!.onNext(response)
        responseObserver.onCompleted()
    }
}