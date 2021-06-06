package br.com.zup.ranyell.keymanager.pix.lista

import br.com.zup.ranyell.keymanager.KeyManagerListaChavePixPorClienteGrpcServiceGrpc
import br.com.zup.ranyell.keymanager.ListaChavePixPorClienteRequest
import br.com.zup.ranyell.keymanager.ListaChavePixPorClienteResponse
import br.com.zup.ranyell.keymanager.compartilhado.excecao.handler.ErrorHandler
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@ErrorHandler
@Singleton
class ListaChavePixEndPoint(
    @Inject val listaService: ListaChavePixService
) : KeyManagerListaChavePixPorClienteGrpcServiceGrpc.KeyManagerListaChavePixPorClienteGrpcServiceImplBase() {

    override fun lista(
        request: ListaChavePixPorClienteRequest?,
        responseObserver: StreamObserver<ListaChavePixPorClienteResponse>?
    ) {
        val listaResponse = listaService.lista(request!!.clientId)
        val response = ListaChavePixPorClienteResponse.newBuilder().addAllChaves(listaResponse).build()
        responseObserver!!.onNext(response)
        responseObserver.onCompleted()
    }
}