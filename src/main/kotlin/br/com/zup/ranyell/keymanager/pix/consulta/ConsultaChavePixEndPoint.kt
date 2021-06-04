package br.com.zup.ranyell.keymanager.pix.consulta

import br.com.zup.ranyell.keymanager.ConsultaChavePixRequest
import br.com.zup.ranyell.keymanager.ConsultaChavePixResponse
import br.com.zup.ranyell.keymanager.KeyManagerConsultaGrpcServiceGrpc
import br.com.zup.ranyell.keymanager.compartilhado.excecao.handler.ErrorHandler
import br.com.zup.ranyell.keymanager.pix.ChavePixRepository
import br.com.zup.ranyell.keymanager.sistemabcb.SistemaBCBClient
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.Validator

@ErrorHandler
@Singleton
class ConsultaChavePixEndPoint(
    @Inject private val repository: ChavePixRepository,
    @Inject private val validator: Validator,
    @Inject private val bcbClient: SistemaBCBClient
) : KeyManagerConsultaGrpcServiceGrpc.KeyManagerConsultaGrpcServiceImplBase() {
    override fun consulta(
        request: ConsultaChavePixRequest?,
        responseObserver: StreamObserver<ConsultaChavePixResponse>?
    ) {
        val filtro = request!!.toModel(validator)
        val chaveInfo = filtro.filtra(repository, bcbClient)

        responseObserver!!.onNext(ConsultaChavePixResponseConverter().convert(chaveInfo))
        responseObserver.onCompleted()
    }
}